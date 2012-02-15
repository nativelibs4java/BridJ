package org.bridj;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.nio.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.bridj.util.Utils;

/**
 * Helper class that knows how to read/write data from/to a {@link Pointer}.<br>
 * End users don't need to use this class directly as ({@link Pointer} lets you work with {@link java.lang.reflect.Type} and {@link Class}).
 * @author Olivier
 */
public abstract class PointerIO<T> {
	final Type targetType;
	final Class<?> typedPointerClass;
	final int targetSize, targetAlignment = -1;
	
	public PointerIO(Type targetType, int targetSize, Class<?> typedPointerClass) {
		this.targetType = targetType;
		this.targetSize = targetSize;
		this.typedPointerClass = typedPointerClass;
	}
	abstract T get(Pointer<T> pointer, long index);
	abstract void set(Pointer<T> pointer, long index, T value);
	public Object getArray(Pointer<T> pointer, long byteOffset, int length) {
		return pointer.offset(byteOffset).toArray(length);
	}
	public <B extends Buffer> B getBuffer(Pointer<T> pointer, long byteOffset, int length) {
		throw new UnsupportedOperationException("Cannot create a Buffer instance of elements of type " + getTargetType());
	}
	public void setArray(Pointer<T> pointer, long byteOffset, Object array) {
		Object[] a = (Object[])array;
		for (int i = 0, n = a.length; i < n; i++)
			set(pointer, i, (T)a[i]);
	}
	
	public T castTarget(long peer) {
		throw new UnsupportedOperationException("Cannot cast pointer to " + targetType);
	}
	
	PointerIO<Pointer<T>> getReferenceIO() {
		return new CommonPointerIOs.PointerPointerIO<T>(this);
	}
	public long getTargetSize() {
		return targetSize;
	}
	public long getTargetAlignment() { 
		return targetAlignment < 0 ? getTargetSize() : targetAlignment;
	}
	public boolean isTypedPointer() {
		return typedPointerClass != null;
	}
	public Class<?> getTypedPointerClass() {
		return typedPointerClass;
	}
	public Type getTargetType() {
		return targetType;
	}
	
	static Class<?> getClass(Type type) {
		if (type instanceof Class<?>)
			return (Class<?>)type;
		if (type instanceof ParameterizedType)
			return getClass(((ParameterizedType)type).getRawType());
		return null;
	}
	
	public static <T> PointerIO<Pointer<T>> getPointerInstance(Type target) {
		return getPointerInstance((PointerIO<T>)getInstance(target));
	}
	public static <T> PointerIO<Pointer<T>> getPointerInstance(PointerIO<T> targetIO) {
		return new CommonPointerIOs.PointerPointerIO<T>(targetIO);
	}
	public static <T> PointerIO<Pointer<T>> getArrayInstance(PointerIO<T> targetIO, long[] dimensions, int iDimension) {
		return new CommonPointerIOs.PointerArrayIO<T>(targetIO, dimensions, iDimension);
	}
	
	static <T> PointerIO<T> getArrayIO(Object array) {
        #foreach ($prim in $primitives)
		if (array instanceof ${prim.Name}[])
			return (PointerIO)PointerIO.get${prim.CapName}Instance();
		#end
		return PointerIO.getInstance(array.getClass().getComponentType());
	}   
	
	private static final ConcurrentHashMap<StructIO, PointerIO<?>> structIOs = new ConcurrentHashMap<StructIO, PointerIO<?>>();
	public static <S extends StructObject> PointerIO<S> getInstance(StructIO s) {
        PointerIO io = structIOs.get(s);
        if (io == null) {
            io = new CommonPointerIOs.StructPointerIO(s);
            PointerIO previousIO = structIOs.putIfAbsent(s, io);
            if (previousIO != null)
                io = previousIO;
        }
        return io;
    }
    private static final ConcurrentHashMap<Type, PointerIO<?>> ios = new ConcurrentHashMap<Type, PointerIO<?>>();
	public static <P> PointerIO<P> getInstance(Type type) {
        if (type == null)
            return null;
        
		PointerIO io = ios.get(type);
        if (io == null) {
            final Class<?> cl = Utils.getClass(type);
    	
            #foreach ($prim in $primitives)
            #if ($velocityCount > 1) else #end
            if (type == ${prim.WrapperName}.TYPE || type == ${prim.WrapperName}.class)
                io = CommonPointerIOs.${prim.Name}IO;
            #end
            else if (cl != null) {
            	    if (TypedPointer.class.isAssignableFrom(cl))
					io = new CommonPointerIOs.TypedPointerPointerIO((Class<? extends TypedPointer>)cl);
				else if (Pointer.class.isAssignableFrom(cl)) {
					if (Pointer.class.equals(type) || !(type instanceof ParameterizedType))
						io = getPointerInstance((PointerIO<?>)null);
					else
						io = getPointerInstance(((ParameterizedType)type).getActualTypeArguments()[0]);
				}
				else if (SizeT.class.isAssignableFrom(cl))
					io = CommonPointerIOs.sizeTIO;
				else if (TimeT.class.isAssignableFrom(cl))
					io = CommonPointerIOs.timeTIO;
				else if (CLong.class.isAssignableFrom(cl))
					io = CommonPointerIOs.clongIO;
				else if (StructObject.class.isAssignableFrom(cl))
					io = getInstance(StructIO.getInstance((Class)cl, type));
				else if (Callback.class.isAssignableFrom(cl))
					io = new CommonPointerIOs.CallbackPointerIO(cl);
				else if (NativeObject.class.isAssignableFrom(cl))
					io = new CommonPointerIOs.NativeObjectPointerIO(type);
				else if (IntValuedEnum.class.isAssignableFrom(cl)) {
					if (type instanceof ParameterizedType) {
						Type enumType = ((ParameterizedType)type).getActualTypeArguments()[0];
						if (enumType instanceof Class)
							io = new CommonPointerIOs.IntValuedEnumPointerIO((Class)enumType);
					}
				}
			}
            //else
            //throw new UnsupportedOperationException("Cannot create pointer io to type " + type + ((type instanceof Class) && ((Class)type).getSuperclass() != null ? " (parent type : " + ((Class)type).getSuperclass().getName() + ")" : ""));
            	//return null; // TODO throw here ?

            //if (io == null)
            //	throw new RuntimeException("Failed to create pointer io to type " + type);
            if (io != null) {
                PointerIO previousIO = ios.putIfAbsent(type, io);
                if (previousIO != null)
                    io = previousIO; // created io twice : not important in general (expecially not compared to cost of contention on non-concurrent map)
            }
        }
        return io;
    }

    private static PointerIO atomicInstance(AtomicReference ref, Type type) {
        PointerIO io = (PointerIO)ref.get();
        if (io != null)
            return io;

        if (ref.compareAndSet(null, io = getInstance(type)))
            return io;
               
        return (PointerIO)ref.get();
    }

#foreach ($prim in $bridJPrimitives)
    private static final AtomicReference<PointerIO<${prim.WrapperName}>> ${prim.Name}Instance = new AtomicReference<PointerIO<${prim.WrapperName}>>();
	public static PointerIO<${prim.WrapperName}> get${prim.CapName}Instance() {
        return atomicInstance(${prim.Name}Instance, ${prim.WrapperName}.class);
    }
    /*    PointerIO<${prim.WrapperName}> io = ${prim.Name}Instance.get();
        if (io != null)
            return io;

        if (${prim.Name}Instance.compareAndSet(null, io = getInstance(${prim.WrapperName}.class)))
            return io;
               
        return ${prim.Name}Instance.get();
	}*/
#end
	
    public static <P> PointerIO<P> getBufferPrimitiveInstance(Buffer buffer) {
        #foreach ($prim in $primitivesNoBool)
		if (buffer instanceof ${prim.BufferName})
            return (PointerIO<P>)get${prim.CapName}Instance();
		#end
        throw new UnsupportedOperationException();
    }

    private static final AtomicReference<PointerIO> stringInstance = new AtomicReference<PointerIO>();
    public static PointerIO<String> getStringInstance() {
        return atomicInstance(stringInstance, String.class);/*
        PointerIO io = stringInstance.get();
        if (io != null)
            return io;

        if (stringInstance.compareAndSet(null, io = getInstance(String.class)))
            return io;
               
        return stringInstance.get();*/
    }

}
