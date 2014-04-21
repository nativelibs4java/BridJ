/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bridj;

import org.bridj.util.Utils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.bridj.demangling.Demangler.Symbol;
import org.bridj.NativeEntities.Builder;
import org.bridj.ann.Convention;
import org.bridj.ann.JNIBound;
import org.bridj.util.ConcurrentCache;
import static org.bridj.BridJ.*;
import static org.bridj.util.AnnotationUtils.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.bridj.ann.Optional;

/**
 * C runtime (used by default when no {@link org.bridj.ann.Runtime} annotation
 * is found).<br>
 * Deals with registration and lifecycle of structs, functions, callbacks.<br>
 * A shared C runtime instance can be retrieved with {@link CRuntime#getInstance()
 * }.
 *
 * @author ochafik
 */
public class CRuntime extends AbstractBridJRuntime {

    final static Set<Type> registeredTypes = new HashSet<Type>();
    final AtomicReference<CallbackNativeImplementer> _callbackNativeImplementer = new AtomicReference<CallbackNativeImplementer>();

    /**
     * @deprecated use {@link CRuntime#getInstance() } instead
     */
    @Deprecated
    public CRuntime() {
    }

    public CallbackNativeImplementer getCallbackNativeImplementer() {
        CallbackNativeImplementer impl = _callbackNativeImplementer.get();
        if (impl == null) {
            CallbackNativeImplementer newImpl = new CallbackNativeImplementer(BridJ.getOrphanEntities(), this);
            if (_callbackNativeImplementer.compareAndSet(null, newImpl)) {
                impl = newImpl;
            } else {
                impl = _callbackNativeImplementer.get();
            }
        }
        return impl;
    }

    public boolean isAvailable() {
        return true;
    }

    public static CRuntime getInstance() {
        return BridJ.getRuntimeByRuntimeClass(CRuntime.class);
    }

    //@Override
    public <T extends NativeObject> Class<? extends T> getActualInstanceClass(Pointer<T> pInstance, Type officialType) {
        return Utils.getClass(officialType);
    }

    protected boolean shouldWarnIfNoFieldsInStruct() {
        return true;
    }

    public class CTypeInfo<T extends NativeObject> implements TypeInfo<T> {

        public CTypeInfo(Type type) {
            this.type = type;
            this.typeClass = Utils.getClass(type);
            this.structIO = StructIO.getInstance(typeClass, type);
            if (structIO != null) {
                structIO.desc.build();
                if (BridJ.verbose
                        && structIO.desc.getAggregatedFields().isEmpty()
                        && shouldWarnIfNoFieldsInStruct()) {
                    BridJ.info("No fields found in " + Utils.toString(type) + " (maybe they weren't declared as public ?)");
                }
            }
            this.pointerIO = (PointerIO<T>) PointerIO.getInstance(structIO);
            //this.castClass = getTypeForCast(typeClass);
            register(typeClass);
        }
        protected final Type type;
        protected final Class<T> typeClass;
        protected final StructIO structIO;
        protected final PointerIO<T> pointerIO;
        protected volatile Class<T> castClass;

        //@Override
        public long sizeOf() {
            return structIO.desc.getStructSize();
        }
        //@Override

        public boolean equal(T instance, T other) {
            if (structIO != null) {
                if (((StructObject) instance).io != structIO) {
                    throw new IllegalArgumentException("This is not this instance's StructIO");
                }

                if (((StructObject) other).io != structIO) {
                    return false;
                }

                return structIO.equal((StructObject) instance, (StructObject) other);
            } else {
                return instance.peer.equals(other.peer);
            }
        }
        //@Override

        public int compare(T instance, T other) {
            if (structIO != null) {
                if (((StructObject) instance).io != structIO) {
                    throw new IllegalArgumentException("This is not this instance's StructIO");
                }

                if (((StructObject) other).io != structIO) {
                    return 1;
                }

                return structIO.compare((StructObject) instance, (StructObject) other);
            } else {
                return instance.peer.compareTo(other.peer);
            }
        }

        //@Override
        public BridJRuntime getRuntime() {
            return CRuntime.this;
        }
        //@Override

        public Type getType() {
            return type;
        }

        protected Class<T> getCastClass() {
            if (castClass == null) {
                castClass = (Class<T>) getTypeForCast(typeClass);
            }
            return castClass;
        }

        protected T newCastInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Class<?> cc = getCastClass();
            try {
                return (T) cc.newInstance();
            } catch (IllegalAccessException ex) {
                Constructor<T> constructor = (Constructor<T>) cc.getConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            }
        }

        //@Override
        public T cast(Pointer peer) {
            try {
                T instance = newCastInstance();
                // TODO template parameters here !!!
                initialize(instance, peer);
                return instance;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to cast pointer " + peer + " to instance of type " + Utils.toString(type), ex);
            }
        }
        //@Override

        public T createReturnInstance() {
            try {
                T instance = newCastInstance();
                initialize(instance);
                return instance;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create return instance for type " + Utils.toString(type), ex);
            }
        }
        //@Override

        public void writeToNative(T instance) {
            if (instance instanceof StructObject) {
                structIO.writeFieldsToNative((StructObject) instance);
            }
        }
        //@Override

        public void readFromNative(T instance) {
            if (instance instanceof StructObject) {
                structIO.readFieldsFromNative((StructObject) instance);
            }
        }

        public void copyNativeObjectToAddress(T instance, Pointer<T> ptr) {
            if (instance instanceof StructObject) {
                // TODO override in C++ to call operator=
                ((StructObject) instance).peer.copyBytesTo(ptr, structIO.desc.getStructSize());
            }
        }
        //@Override

        public String describe(T instance) {
            if (instance instanceof StructObject) {
                return structIO.describe((StructObject) instance);
            } else {
                return instance.toString();
            }
        }
        //@Override

        public String describe() {
            if (structIO != null) {
                return structIO.desc.describe();
            } else {
                return Utils.toString(typeClass);
            }
        }

        //@Override
        public void initialize(T instance) {
            if (!BridJ.isCastingNativeObjectInCurrentThread()) {
                if (instance instanceof CallbackInterface) {
                    if (!(instance instanceof DynamicFunction)) {
                        setNativeObjectPeer(instance, registerCallbackInstance((CallbackInterface) instance));
                    }
                } else {
                    initialize(instance, -1);
                }

                if (instance instanceof StructObject) {
                    structIO.readFieldsFromNative((StructObject) instance);
                }
            } else if (instance instanceof StructObject) {
                ((StructObject) instance).io = structIO;
            }
        }
        //@Override

        public void initialize(T instance, Pointer peer) {
            instance.peer = peer;
            if (instance instanceof StructObject) {
                ((StructObject) instance).io = structIO;
                structIO.readFieldsFromNative((StructObject) instance);
            }
        }

        protected <V> Pointer<V> allocateStructMemory(PointerIO<V> pointerIO) {
            return Pointer.allocate(pointerIO);
        }

        //@Override
        public void initialize(T instance, int constructorId, Object... args) {
            StructObject s = (StructObject) instance;
            if (constructorId < 0) {
                s.io = structIO;
                if (instance.peer == null) {
                    instance.peer = allocateStructMemory(pointerIO);
                }
            } else {
                throw new UnsupportedOperationException("TODO implement structs constructors !");
            }
        }

        //@Override
        public T clone(T instance) throws CloneNotSupportedException {
            if (instance == null) {
                return null;
            }

            try {
                T clone = (T) typeClass.newInstance();
                Pointer<T> p = allocateStructMemory(pointerIO);
                Pointer.getPointer(instance).copyTo(p);
                initialize(clone, p);
                return clone;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to clone instance of type " + getType());
            }
        }

        //@Override
        public void destroy(T instance) {
            if (instance instanceof CallbackInterface) {
                return;
            }
        }
    }
    /// Needs not be fast : TypeInfo will be cached in BridJ anyway !
    //@Override

    public <T extends NativeObject> TypeInfo<T> getTypeInfo(final Type type) {
        return new CTypeInfo(type);
    }

    public Type getType(Class<?> cls, Object[] targs, int[] typeParamCount) {
        return cls;
    }

    //@Override
    public void register(Type type) {
        register(type, null, null);
    }

    public static class MethodCallInfoBuilder {

        public MethodCallInfo apply(Method method) throws FileNotFoundException {
            return new MethodCallInfo(method);
        }
    }

    @Override
    public void unregister(Type type) {
        Class typeClass = Utils.getClass(type);
        registeredTypes.remove(typeClass);
    }

    synchronized void register(Type type, NativeLibrary forcedLibrary, MethodCallInfoBuilder methodCallInfoBuilder) {
        Class typeClass = Utils.getClass(type);
        if (!BridJ.getRuntimeClass(typeClass).isInstance(this)) {
            BridJ.register(typeClass);
            return;
        }
        synchronized (registeredTypes) {
            if (!registeredTypes.add(typeClass)) {
                return;
            }
        }

        if (methodCallInfoBuilder == null) {
            methodCallInfoBuilder = new MethodCallInfoBuilder();
        }

        if (verbose) {
            info("Registering type " + Utils.toString(type));
        }

        int typeModifiers = typeClass.getModifiers();

        NativeLibrary typeLibrary = null;
        try {
            typeLibrary = forcedLibrary == null ? getNativeLibrary(typeClass) : forcedLibrary;
        } catch (Throwable th) {
        }

        ConcurrentCache<NativeEntities, NativeEntities.Builder> builders = new ConcurrentCache<NativeEntities, NativeEntities.Builder>(NativeEntities.Builder.class);
        try {
            Set<Method> handledMethods = new HashSet<Method>();
            /*if (StructObject.class.isAssignableFrom(typeClass)) {
             StructIO io = StructIO.getInstance(typeClass, type, this); // TODO handle differently with templates...
             io.build();
             StructIO.FieldIO[] fios = io == null ? null : io.getFields();
             if (fios != null)
             for (StructIO.FieldIO fio : fios) {
             NativeEntities.Builder builder = builders.get(BridJ.getOrphanEntities());

             try {
             {
             MethodCallInfo getter = new MethodCallInfo(fio.getter);
             getter.setIndex(fio.index);
             builder.addGetter(getter);
             handledMethods.add(fio.getter);
             }
             if (fio.setter != null) {
             MethodCallInfo setter = new MethodCallInfo(fio.setter);
             setter.setIndex(fio.index);
             builder.addSetter(setter);
             handledMethods.add(fio.setter);
             }
             } catch (Exception ex) {
             BridJ.error("Failed to register field " + fio.name + " in struct " + type);
             ex.printStackTrace();
             }
             }
             }*/

            if (CallbackInterface.class.isAssignableFrom(typeClass)) {
                if (rootCallbackClasses.contains(type)) {
                    return;
                }

                if (Modifier.isAbstract(typeModifiers)) {
                    getCallbackNativeImplementer().getCallbackImplType((Class) type, forcedLibrary);
                }
            }


//		for (; type != null && type != Object.class; type = type.getSuperclass()) {
            List<Method> nativeMethods = new ArrayList<Method>();
            for (Method method : typeClass.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (Modifier.isNative(modifiers)) {
                    if (!isAnnotationPresent(JNIBound.class, method)) {
                        nativeMethods.add(method);
                    }
                }
            }

            if (!nativeMethods.isEmpty()) {
                try {
                    for (Method method : nativeMethods) {
                        if (!handledMethods.add(method)) {
                            continue;
                        }

                        try {
                            NativeLibrary methodLibrary = forcedLibrary == null ? BridJ.getNativeLibrary(method) : forcedLibrary;
                            NativeEntities nativeEntities = methodLibrary == null ? BridJ.getOrphanEntities() : methodLibrary.getNativeEntities();
                            NativeEntities.Builder builder = builders.get(nativeEntities);

                            registerNativeMethod(typeClass, typeLibrary, method, methodLibrary, builder, methodCallInfoBuilder);
                        } catch (Exception ex) {
                            if (verbose) {
                                error("Method " + method.toGenericString() + " cannot be mapped : " + ex, ex);
                            }
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to register class " + Utils.toString(type), ex);
                }
            }
//		}
        } finally {
            for (Map.Entry<NativeEntities, NativeEntities.Builder> e : builders.entrySet()) {
                e.getKey().addDefinitions(typeClass, e.getValue());
            }
            registerFamily(type, forcedLibrary, methodCallInfoBuilder);
        }
    }

    protected void registerFamily(Type type, NativeLibrary forcedLibrary, MethodCallInfoBuilder methodCallInfoBuilder) {
        Class typeClass = Utils.getClass(type);

        for (Class<?> child : typeClass.getClasses()) {
            register(child, forcedLibrary, methodCallInfoBuilder);
        }

        typeClass = typeClass.getSuperclass();
        if (typeClass != null && typeClass != Object.class) {
            register(typeClass, forcedLibrary, methodCallInfoBuilder);
        }
    }

    protected NativeLibrary getNativeLibrary(Class<?> type) throws IOException {
        return BridJ.getNativeLibrary(type);
    }

    protected boolean isSymbolOptional(Method method) {
        return getInheritableAnnotation(Optional.class, method) != null;
    }

    protected void registerNativeMethod(
            Class<?> type,
            NativeLibrary typeLibrary,
            Method method,
            NativeLibrary methodLibrary,
            Builder builder,
            MethodCallInfoBuilder methodCallInfoBuilder) throws FileNotFoundException {
        MethodCallInfo mci;
        try {
            mci = methodCallInfoBuilder.apply(method);
            if (mci == null) {
                return;
            }
            //System.out.println("method.dcCallingConvention = " + mci.dcCallingConvention + " (for method " + type.getName() + ", method " + method + ", type = " + type.getName() + ", enclosingClass = " + method.getDeclaringClass().getName() + ")");
        } catch (Throwable th) {
            error("Unable to register " + method + " : " + th);
            th.printStackTrace();
            return;
        }
        if (CallbackInterface.class.isAssignableFrom(type)) {
            if (debug) {
                info("Registering java -> native callback : " + method);
            }
            builder.addJavaToNativeCallback(mci);
        } else {
            Symbol symbol = methodLibrary == null ? null : methodLibrary.getSymbol(method);
            if (symbol == null) {
//                for (Demangler.Symbol symbol : methodLibrary.getSymbols()) {
//                    if (symbol.matches(method)) {
//                        address = symbol.getAddress();
//                        break;
//                    }
//                }
//                if (address == null) {
                if (!isSymbolOptional(method)) {
                    error("Failed to get address of method " + method);
                }
                return;
//                }
            }
            mci.setForwardedPointer(symbol.getAddress());
            if (!mci.hasCallingConvention()) {
                Convention.Style cc = symbol.getInferredCallingConvention();
                if (cc != null) {
                    mci.setCallingConvention(cc);
                }
            }
            builder.addFunction(mci);
            if (debug) {
                info("Registering " + method + " as C function " + symbol.getName() + " (address = 0x" + Long.toHexString(symbol.getAddress()) + ")");
            }
        }
    }

    public <T extends NativeObject> Pointer<T> allocate(Class<T> type, int constructorId, Object... args) {
        if (CallbackInterface.class.isAssignableFrom(type)) {
            if (constructorId != -1 || args.length != 0) {
                throw new RuntimeException("Callback should have a constructorId == -1 and no constructor args !");
            }
            return null;//newCallbackInstance(type);
        }
        throw new RuntimeException("Cannot allocate instance of type " + type.getName() + " (unhandled NativeObject subclass)");
    }
    static final int defaultObjectSize = Platform.is64Bits() ? 8 : 4;
    public static final String PROPERTY_bridj_c_defaultObjectSize = "bridj.c.defaultObjectSize";

    public int getDefaultStructSize() {
        String s = System.getProperty(PROPERTY_bridj_c_defaultObjectSize);
        if (s != null) {
            try {
                return Integer.parseInt(s);
            } catch (Throwable th) {
                error("Invalid value for property " + PROPERTY_bridj_c_defaultObjectSize + " : '" + s + "'");
            }
        }
        return defaultObjectSize;
    }

    public long sizeOf(Type structType, StructIO io) {
        if (io == null) {
            io = StructIO.getInstance(Utils.getClass(structType), structType);
        }
        long size;
        if (io == null || (size = io.desc.getStructSize()) <= 0) {
            return getDefaultStructSize();
        }
        return size;
    }
    protected Set<Class> rootCallbackClasses = new HashSet<Class>(Arrays.asList(Callback.class, DynamicFunction.class));

    public Method getUniqueCallbackMethod(Class type) {
        return getCallbackMethod(type, true);
    }
    public Method getFastestCallbackMethod(Class type) {
        return getCallbackMethod(type, false);
    }

    private Method getSingleAbstractMethodMethod(Class type) {
        assert Modifier.isAbstract(type.getModifiers());
        Method method = null;
        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method dm : declaredMethods) {
            int modifiers = dm.getModifiers();
            if (!Modifier.isAbstract(modifiers)) {
                continue;
            }

            if (method == null) {
                method = dm;
            } else {
                throw new RuntimeException("Callback " + type.getName() + " has more than one abstract method (" + dm + " and " + method + ")");
            }
            //break;
        }
        return method;
    }
    
    private boolean sameBindings(Method m1, Method m2) {
        Class<?>[] params1 = m1.getParameterTypes(), params2 = m2.getParameterTypes();
        Class<?> r1 = m1.getReturnType(), r2 = m2.getReturnType();
        if (!sameBindings(r1, r2) || params1.length != params2.length) {
            return false;
        }
        for (int i = 0; i < params1.length; i++) {
            Class p1 = params1[i], p2 = params2[i];
            if (!sameBindings(p1, p2)) {
                return false;
            }
        }
        return true;
    }
    
    private static int getSignatureObjectCount(Class t) {
        return t.isPrimitive() ? 0 : 1;
    }
    private int getSignatureObjectCount(Method m) {
        int count = getSignatureObjectCount(m.getReturnType());
        for (Class<?> param : m.getParameterTypes()) {
            count += getSignatureObjectCount(param);
        }
        return count;
    }

    public List<Method> getApplyMethods(Class type) {
        List<Method> ret = new ArrayList<Method>();
        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().equals("apply")) {
                ret.add(method);
            }
        }
        return ret;
    }
    
    public Class<?> getAbstractCallback(Class type) {
        Class<?> parent = null;
        while ((parent = type.getSuperclass()) != null && !rootCallbackClasses.contains(parent)) {
            type = parent;
        }
        if (!Modifier.isAbstract(type.getModifiers())) {
            throw new RuntimeException("Callback definition " + type.getName() + " must be abstract.");
        }
        return type;
    }
    
    public Method getCallbackMethod(Class<?> type, boolean expectUniqueMethod) {
        Class<?> abstractCallback = getAbstractCallback(type);
        Method singleAbstractMethod = getSingleAbstractMethodMethod(abstractCallback);
        if (singleAbstractMethod != null) {
            return singleAbstractMethod;
        }

        List<Method> applyList = getApplyMethods(type);
        if (applyList.isEmpty()) {
            throw new RuntimeException("Type doesn't have any abstract method nor any 'apply' method: " + abstractCallback.getName());
        }
        
        Method m0 = applyList.get(0);
        for (int i = 1, n = applyList.size(); i < n; i++) {
            Method mi = applyList.get(i);
            if (!sameBindings(m0, mi)) {
                throw new RuntimeException("Callback apply methods don't match: " + m0 + " vs. " + mi);
            }
        }

        boolean overridesOnlyOneMethod = applyList.size() == 1;
        if (expectUniqueMethod) {
            if (!overridesOnlyOneMethod) {
                throw new RuntimeException("Expected only one overridden apply method in " + type.getName() + ", but got " + applyList);
            }
        }
        if (overridesOnlyOneMethod) {
            return applyList.get(0);
        } else {
            int bestCount = Integer.MAX_VALUE;
            Method best = null;
            for (Method m : applyList) {
                int count = getSignatureObjectCount(m);
                if (count < bestCount) {
                    bestCount = count;
                    best = m;
                }
            }
            return best;
        }
    }
    private static boolean sameBindings(Class t1, Class t2) {
        return t1.equals(t2) ||
            t1 == long.class && Pointer.class.isAssignableFrom(t2) ||
            t2 == long.class && Pointer.class.isAssignableFrom(t1) ||
            t1 == int.class && IntValuedEnum.class.isAssignableFrom(t2) ||
            t2 == int.class && IntValuedEnum.class.isAssignableFrom(t1);
    }
    
    public <T extends NativeObject> Class<? extends T> getTypeForCast(Type type) {
        Class<?> typeClass = Utils.getClass(type);
        if (CallbackInterface.class.isAssignableFrom(typeClass)) {
            return getCallbackNativeImplementer().getCallbackImplType((Class) typeClass, null);
        } else {
            return (Class<? extends T>) typeClass;
        }
    }

    /**
     * Get a shared factory of native function wrappers that have a given
     * signatures.
     *
     * @param library library to which the allocated native thunks will be bound
     * (can be null, in which case the native allocations will be bound to {@link BridJ#getOrphanEntities()
     * })
     * @param callingConvention calling convention used by the functions (if
     * null, default is typically {@link org.bridj.ann.Convention.Style#CDecl})
     * @param returnType return type of the functions
     * @param parameterTypes parameter types of the functions Also see
     * {@link DynamicFunction} and {@link Pointer#asDynamicFunction(org.bridj.ann.Convention.Style, java.lang.reflect.Type, java.lang.reflect.Type[])
     * }.
     */
    public DynamicFunctionFactory getDynamicFunctionFactory(NativeLibrary library, Convention.Style callingConvention, Type returnType, Type... parameterTypes) {
        return getCallbackNativeImplementer().getDynamicCallback(library, callingConvention, returnType, parameterTypes);
    }

    public static <T> Pointer<T> createCToJavaCallback(MethodCallInfo mci, Type t) {
        mci.prependCallbackCC();
        final long handle = JNI.createCToJavaCallback(mci);
        long peer = JNI.getActualCToJavaCallback(handle);

        final Throwable stackTrace = BridJ.debugPointers
                ? new RuntimeException().fillInStackTrace() : null;

        return (Pointer) Pointer.pointerToAddress(peer, t, new Pointer.Releaser() {
            //@Override
            public void release(Pointer<?> p) {
                if (BridJ.debugPointers) {
                    BridJ.info("Freeing callback pointer " + p + "\n(Creation trace = \n\t" + Utils.toString(p.creationTrace).replaceAll("\n", "\n\t") + "\n)", new RuntimeException().fillInStackTrace());
                }

                if (BridJ.debugNeverFree) {
                    return;
                }

                JNI.freeCToJavaCallback(handle);//p.getPeer());
            }
        });
    }

    protected <T extends CallbackInterface> Pointer<T> registerCallbackInstance(T instance) {
        try {
            Class<?> c = instance.getClass();
            MethodCallInfo mci = new MethodCallInfo(getFastestCallbackMethod(c));
            mci.setDeclaringClass(c);
            mci.setJavaCallback(instance);
            return createCToJavaCallback(mci, c);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register callback instance of type " + instance.getClass().getName(), e);
        }
    }

    protected void setNativeObjectPeer(NativeObjectInterface instance, Pointer<? extends NativeObjectInterface> peer) {
        ((NativeObject) instance).peer = (Pointer<NativeObject>) peer;
    }
}
