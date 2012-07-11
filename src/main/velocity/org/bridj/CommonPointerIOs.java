package org.bridj;
import java.util.concurrent.atomic.AtomicLong;
import org.bridj.util.*;
import java.util.*;
import java.nio.*;
import java.lang.reflect.Type;
import static org.bridj.util.DefaultParameterizedType.*;

class CommonPointerIOs {

	static class NativeObjectPointerIO<N extends NativeObject> extends PointerIO<N> {
		protected volatile long targetSize = -1, targetAlignment = -1;
		protected Type nativeObjectType;
		public NativeObjectPointerIO(Type nativeObjectType) {
			super(nativeObjectType, -1, null);
			this.nativeObjectType = nativeObjectType;
		}


		protected long computeTargetSize() {
			return BridJ.sizeOf(nativeObjectType);
		}
		protected long computeTargetAlignment() {
			return getTargetSize();
		}
        @Override
        public long getTargetSize() {
            if (targetSize < 0)
                targetSize = computeTargetSize();
                
            return targetSize;
        }
        @Override
        public long getTargetAlignment() {
            if (targetAlignment < 0)
                targetAlignment = computeTargetAlignment();
                
            return targetAlignment;
        }
		
		@Override
		public N get(Pointer<N> pointer, long index) {
			return (N)pointer.getNativeObjectAtOffset(index * getTargetSize(), nativeObjectType);
		}
		@Override
		public void set(Pointer<N> pointer, long index, N value) {
			Pointer<N> ps = Pointer.pointerTo(value);
			ps.copyTo(pointer.offset(index * getTargetSize()));
		}
	}
	static class StructPointerIO<S extends StructObject> extends NativeObjectPointerIO<S> {
		final StructIO structIO;
		public StructPointerIO(StructIO structIO) {
			super(structIO.getStructType());
			this.structIO = structIO;
		}
		
		@Override
        protected long computeTargetSize() {
			structIO.build();
            return structIO.getStructSize();
		}
		@Override
        protected long computeTargetAlignment() {
			structIO.build();
            return structIO.getStructAlignment();
		}
	}
	
	static class PointerPointerIO<T> extends PointerIO<Pointer<T>> {
		final PointerIO<T> underlyingIO;

		public PointerPointerIO(PointerIO<T> underlyingIO) {
			super(underlyingIO == null ? Pointer.class : paramType(Pointer.class, new Type[] {underlyingIO.getTargetType()}), Pointer.SIZE, null);
			this.underlyingIO = underlyingIO;
		}
		
		@Override
		public Pointer<T> get(Pointer<Pointer<T>> pointer, long index) {
			return pointer.getPointerAtOffset(index * Pointer.SIZE, underlyingIO);
		}

		@Override
		public void set(Pointer<Pointer<T>> pointer, long index, Pointer<T> value) {
			pointer.setPointerAtOffset(index * Pointer.SIZE, value);
		}
	}
	
	static class PointerArrayIO<T> extends PointerIO<Pointer<T>> {
		final PointerIO<T> underlyingIO;
		final long[] dimensions;
		final long totalRemainingDims;
		final int iDimension;

		static Type arrayPtrType(Type elementType, long... dimensions) {
			Type type = elementType;
			for (int i = 0; i < dimensions.length; i++)
				type = paramType(Pointer.class, type);
			return type;
		}
		static long getTotalRemainingDims(long[] dimensions, int iDimension) {
			long d = 1;
			for (int i = iDimension + 1; i < dimensions.length; i++)
				d *= dimensions[i];
			return d;
		}
		
		public PointerArrayIO(PointerIO<T> underlyingIO, long[] dimensions, int iDimension) {
			super(
				//underlyingIO.getTargetType(),//
				underlyingIO == null ? null : arrayPtrType(underlyingIO.getTargetType(), dimensions), 
				-1, 
				null
			);
			//if (iDimension >= dimensions.length) {
				this.underlyingIO = underlyingIO;
			/*} else {
				this.underlyingIO = new PointerArrayIO(underlyingIO, dimensions, iDimension + 1);
			}*/
			this.dimensions = dimensions;
			this.iDimension = iDimension;
			totalRemainingDims = getTotalRemainingDims(dimensions, iDimension);
		}
		
		@Override
		public long getTargetSize() {
			long subSize = underlyingIO.getTargetSize();
			return dimensions[iDimension + 1] * subSize;// * totalRemainingDims;
		}
		
		@Override
		public Pointer<T> get(Pointer<Pointer<T>> pointer, long index) {
			//long offset = getOffset(index);
			long targetSize = getTargetSize();//underlyingIO.getTargetSize();
			return pointer.offset(index * targetSize).as(underlyingIO);
		}

		long getOffset(long index) {
			assert iDimension < dimensions.length;
			return index * totalRemainingDims;
		}
		@Override
		public void set(Pointer<Pointer<T>> pointer, long index, Pointer<T> value) {
			throw new RuntimeException("Cannot set a multi-dimensional array's sub-arrays pointers !");
		}
	}
	
	static class CallbackPointerIO<T extends CallbackInterface> extends PointerIO<T> {
		final Class<T> callbackClass;

		public CallbackPointerIO(Class<T> callbackClass) {
			super(callbackClass, Pointer.SIZE, null);
			this.callbackClass = callbackClass;
		}
		
		@Override
		public T get(Pointer<T> pointer, long index) {
			if (index != 0)
				throw new UnsupportedOperationException("Cannot get function pointer at index different from 0");
			//return pointer.getPointerAtOffset(index * Pointer.SIZE, (Class<T>)null).getNativeObject(0, callbackClass);
			return (T)pointer.getNativeObjectAtOffset(0, callbackClass);
		}

		@Override
		public void set(Pointer<T> pointer, long index, T value) {
			throw new UnsupportedOperationException("Cannot write to body of function");
			//pointer.setPointer(index * Pointer.SIZE, Pointer.getPointer(value, callbackClass));
		}
	}
	
	static class IntValuedEnumPointerIO<E extends Enum<E>> extends PointerIO<IntValuedEnum<E>> {
		final Class<E> enumClass;

		public IntValuedEnumPointerIO(Class<E> enumClass) {
			super(IntValuedEnum.class, 4, null);
			this.enumClass = enumClass;
		}
		
		@Override
		public IntValuedEnum<E> get(Pointer<IntValuedEnum<E>> pointer, long index) {
			return FlagSet.fromValue(pointer.getIntAtOffset(4 * index), enumClass);
		}

		@Override
		public void set(Pointer<IntValuedEnum<E>> pointer, long index, IntValuedEnum<E> value) {
			pointer.setIntAtOffset(4 * index, (int)value.value());
		}
	}
	
	static class TypedPointerPointerIO<P extends TypedPointer> extends PointerIO<P> {
		final java.lang.reflect.Constructor cons;
		//final java.lang.reflect.Constructor cons2;
		final Class<P> pointerClass;
		public TypedPointerPointerIO(Class<P> pointerClass) {
			super(pointerClass, Pointer.SIZE, null);
			this.pointerClass = pointerClass;
			try {
				cons = pointerClass.getConstructor(long.class);
				//cons2 = pointerClass.getConstructor(Pointer.class);
			} catch (Exception ex) {
				throw new RuntimeException("Cannot find constructor for " + pointerClass.getName(), ex);
			}
		}
		
		@Override
		public P castTarget(long peer) {
			if (peer == 0)
				return null;
			try {
				return (P)cons.newInstance(peer);
			} catch (Exception ex) {
				throw new RuntimeException("Cannot create pointer of type " + pointerClass.getName(), ex);
			}
		}
		
		@Override
		public P get(Pointer<P> pointer, long index) {
			return castTarget(pointer.getSizeTAtOffset(index * Pointer.SIZE));
		}

		@Override
		public void set(Pointer<P> pointer, long index, P value) {
			pointer.setPointerAtOffset(index * Pointer.SIZE, value);
		}
	}
	
#foreach ($prim in $primitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end

	public static final PointerIO<${prim.WrapperName}> ${prim.Name}IO = new PointerIO<${prim.WrapperName}>(${prim.WrapperName}.class, ${primSize}, null) {
		@Override
		public ${prim.WrapperName} get(Pointer<${prim.WrapperName}> pointer, long index) {
			return pointer.get${prim.CapName}AtOffset(index * ${primSize});
		}

		@Override
		public void set(Pointer<${prim.WrapperName}> pointer, long index, ${prim.WrapperName} value) {
			pointer.set${prim.CapName}AtOffset(index * ${primSize}, value);
		}
		
		@Override
		public <B extends Buffer> B getBuffer(Pointer<${prim.WrapperName}> pointer, long byteOffset, int length) {
			#if ($prim.Name == "char")
			throw new UnsupportedOperationException("Creating direct char buffers in a cross-platform way is tricky, so it's currently disabled");
			#else
			return (B)pointer.get${prim.BufferName}AtOffset(byteOffset, length);
			#end
		}
		
		@Override
		public Object getArray(Pointer<${prim.WrapperName}> pointer, long byteOffset, int length) {
			return pointer.get${prim.CapName}sAtOffset(byteOffset, length);
		}
		
		@Override
		public void setArray(Pointer<${prim.WrapperName}> pointer, long byteOffset, Object array) {
			if (array instanceof ${prim.Name}[])
				pointer.set${prim.CapName}sAtOffset(byteOffset, (${prim.Name}[])array);
			else
				super.setArray(pointer, byteOffset, array);
		}
	
	};

#end

	public static final PointerIO<SizeT> sizeTIO = new PointerIO<SizeT>(SizeT.class, SizeT.SIZE, null) {
		@Override
		public SizeT get(Pointer<SizeT> pointer, long index) {
			return new SizeT(pointer.getSizeTAtOffset(index * SizeT.SIZE));
		}
		@Override
		public void set(Pointer<SizeT> pointer, long index, SizeT value) {
			pointer.setSizeTAtOffset(index * SizeT.SIZE, value == null ? 0 : value.longValue());
		}		
	};
	
	public static final PointerIO<TimeT> timeTIO = new PointerIO<TimeT>(TimeT.class, TimeT.SIZE, null) {
		@Override
		public TimeT get(Pointer<TimeT> pointer, long index) {
			long offset = index * TimeT.SIZE;
			return new TimeT(TimeT.SIZE == 4 ? pointer.getIntAtOffset(offset) : pointer.getLongAtOffset(offset));
		}
		@Override
		public void set(Pointer<TimeT> pointer, long index, TimeT value) {
			long offset = index * TimeT.SIZE;
			if (TimeT.SIZE == 4)
				pointer.setIntAtOffset(offset, value == null ? 0 : value.intValue());
			else
				pointer.setLongAtOffset(offset, value == null ? 0 : value.longValue());
		}		
	};
	
	public static final PointerIO<CLong> clongIO = new PointerIO<CLong>(CLong.class, CLong.SIZE, null) {
		@Override
		public CLong get(Pointer<CLong> pointer, long index) {
			return new CLong(pointer.getCLongAtOffset(index * CLong.SIZE));
		}
		@Override
		public void set(Pointer<CLong> pointer, long index, CLong value) {
			pointer.setCLongAtOffset(index * CLong.SIZE, value == null ? 0 : value.longValue());
		}		
	};
	
	

	/*public static final PointerIO<Integer> intIO = new PointerIO<Integer>(Integer.class, 4, null) {
		@Override
		public Integer get(Pointer<Integer> pointer, long index) {
			return pointer.getIntAtOffset(index * 4);
		}

		@Override
		public void set(Pointer<Integer> pointer, long index, Integer value) {
			pointer.setIntAtOffset(index * 4, value);
		}		
	};*/

}

