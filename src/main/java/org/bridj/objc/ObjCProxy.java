package org.bridj.objc;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.bridj.*;
import static org.bridj.Pointer.*;
import static org.bridj.objc.ObjCJNI.*;
import static org.bridj.Platform.*;
import static org.bridj.objc.ObjectiveCRuntime.*;
import java.util.*;
import org.bridj.util.Pair;
import org.bridj.util.Utils;

public class ObjCProxy extends ObjCObject {
	final Map<SEL, Pair<NSMethodSignature, Method>> signatures = new HashMap<SEL, Pair<NSMethodSignature, Method>>();
	final Object invocationTarget;
    final static String PROXY_OBJC_CLASS_NAME = "ObjCProxy";
	
	protected ObjCProxy() {
		super(null);
		peer = createObjCProxyPeer(this);
		assert getClass() != ObjCProxy.class;
		this.invocationTarget = this;
	}
	public ObjCProxy(Object invocationTarget) {
		super(null);
		peer = createObjCProxyPeer(this);
		assert invocationTarget != null;
		this.invocationTarget = invocationTarget;
	}
	
    public void addProtocol(String name) throws ClassNotFoundException {
        Pointer<? extends ObjCObject> protocol = objc_getProtocol(pointerToCString(name));
        if (protocol == null)
            throw new ClassNotFoundException("Protocol " + name + " not found !");
        Pointer<? extends ObjCObject> cls = getObjCClass(PROXY_OBJC_CLASS_NAME);
        if (!class_addProtocol(cls, protocol))
            throw new RuntimeException("Failed to add protocol " + name + " to class " + PROXY_OBJC_CLASS_NAME);
    }
	public Object getInvocationTarget() {
		return invocationTarget;
	}
	public Pointer<NSMethodSignature> methodSignatureForSelector(SEL sel) {
		Pair<NSMethodSignature, Method> sig = getMethodAndSignature(sel);
        return sig == null ? null : pointerTo(sig.getFirst());
	}
    public synchronized Pair<NSMethodSignature, Method> getMethodAndSignature(SEL sel) {
		Pair<NSMethodSignature, Method> sig = signatures.get(sel);
		if (sig == null) {
			try {
				sig = computeMethodAndSignature(sel);
				if (sig != null)
					signatures.put(sel, sig);
			} catch (Throwable th) {
				BridJ.error("Failed to compute Objective-C signature for selector " + sel + ": " + th, th);
			}
		}
		return sig;
	}
	Pair<NSMethodSignature, Method> computeMethodAndSignature(SEL sel) {
		String name = sel.getName();
		ObjectiveCRuntime rt = ObjectiveCRuntime.getInstance();
		for (Method method : invocationTarget.getClass().getMethods()) {
			String msel = rt.getSelector(method);
			//System.out.println("Selector for method " + method.getName() + " is '" + msel + "' (vs. sel = '" + sel + "')");
			if (msel.equals(name)) {
				String sig = rt.getMethodSignature(method);
				if (BridJ.debug)
					BridJ.info("Objective-C signature for method " + method + " = '" + sig + "'");
				NSMethodSignature ms = NSMethodSignature.signatureWithObjCTypes(pointerToCString(sig)).get();
                long nArgs = ms.numberOfArguments() - 2;
                if (nArgs != method.getParameterTypes().length)
                    throw new RuntimeException("Bad method signature (mismatching arg types) : '" + sig + "' for " + method);
				return new Pair<NSMethodSignature, Method>(ms, method);
			}
		}
		//if (BridJ.debug)
        BridJ.error("Missing method for " + sel + " in class " + classHierarchyToString(getInvocationTarget().getClass()));
				
		return null;
	}
    static String classHierarchyToString(Class c) {
        String s = Utils.toString(c);
        Type p = c.getGenericSuperclass();
        while (p != null && p != Object.class && p != ObjCProxy.class) {
            s += " extends " + Utils.toString(p);
            p = Utils.getClass(p).getGenericSuperclass();
        }
        return s;
    }
    /*
    static Type promote(Type type) {
        if (type == byte.class || type == short.class || type == char.class || type == boolean.class)
            return int.class;
        if (type == float.class)
            return double.class;
        return type;
    }
    */
    /*
    static final Map<Class, Class> wrapperClasses = new HashMap<Class, Class>();
    static {
        wrapperClasses.put(int.class, Integer.class);
        wrapperClasses.put(short.class, Short.class);
        wrapperClasses.put(long.class, Long.class);
        wrapperClasses.put(char.class, Character.class);
        wrapperClasses.put(byte.class, Byte.class);
        wrapperClasses.put(boolean.class, Boolean.class);
        wrapperClasses.put(double.class, Double.class);
        wrapperClasses.put(float.class, Float.class);
    }
    */
    /*
    static Object constrain(Object value, Type type) {
        Class c = Utils.getClass(type);
        if (c.isPrimitive())
            c = wrapperClasses.get(c);
        if (c.isInstance(value))
            return value;
        
        // Assume value is an Integer or a Double
        if (type == byte.class)
            return ((Integer)value).byteValue();
        if (type == short.class)
            return ((Integer)value).shortValue();
        if (type == char.class)
            return (char)((Integer)value).shortValue();
        if (type == boolean.class)
            return ((Integer)value).byteValue() != 0;
        if (type == float.class)
            return ((Double)value).floatValue();
        
        throw new UnsupportedOperationException("Don't know how to constrain value " + value + " to type " + Utils.toString(type));
    }
    */
	public synchronized void forwardInvocation(Pointer<NSInvocation> pInvocation) {
        NSInvocation invocation = pInvocation.get();
        SEL sel = invocation.selector();
		Pair<NSMethodSignature, Method> sigMet = getMethodAndSignature(sel);
        NSMethodSignature sig = sigMet.getFirst();
        Method method = sigMet.getSecond();
        
        //System.out.println("forwardInvocation(" + invocation + ") : sel = " + sel);
        Type[] paramTypes = method.getGenericParameterTypes();
        int nArgs = paramTypes.length;//(int)sig.numberOfArguments();
        Object[] args = new Object[nArgs];
        for (int i = 0; i < nArgs; i++) {
            Type paramType = paramTypes[i];
            PointerIO<?> paramIO = PointerIO.getInstance(paramType);//promote(paramType));
            Pointer<?> pArg = allocate(paramIO);
            invocation.getArgument_atIndex(pArg, i + 2);
            Object arg = pArg.get();
            args[i] = arg;//constrain(arg, paramType);
        }
		try {
            method.setAccessible(true);
            Object ret = method.invoke(getInvocationTarget(), args);
            //System.out.println("Invoked  " + method + " : " + ret);
            Type returnType = method.getGenericReturnType();
            if (returnType == void.class) {
                assert ret == null;
            } else {
                PointerIO<?> returnIO = PointerIO.getInstance(returnType);
                Pointer<Object> pRet = (Pointer)allocate(returnIO);
                pRet.set(ret);
                invocation.setReturnValue(pRet);
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to forward invocation from Objective-C to Java invocation target " + getInvocationTarget() + " for method " + method + " : " + ex, ex);
        }
	}
	
	
}

