/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author ochafik
 */
public class JNIUtils {
    
    private static class NativeMethodsCache {
        Map<String, Method> methods = new HashMap<String, Method>();
        public NativeMethodsCache(Class cls) {
            for (Method method : cls.getDeclaredMethods()) {
                if (Modifier.isNative(method.getModifiers()))
                    methods.put(method.getName(), method);
            }
        }
        public Method get(String name) {
            return methods.get(name);
        }
    }
    private static Map<Class, NativeMethodsCache> nativeMethodsCache = new WeakHashMap<Class, NativeMethodsCache>();
    private static synchronized NativeMethodsCache getNativeMethodsCache(Class cls) {
        NativeMethodsCache cache = nativeMethodsCache.get(cls);
        if (cache == null)
            nativeMethodsCache.put(cls, cache = new NativeMethodsCache(cls));
        return cache;
    }
    public static String decodeMethodNameClassAndSignature(String symbolName, Object[] nameAndSigArray) throws ClassNotFoundException {
        if (symbolName.startsWith("_"))
            symbolName = symbolName.substring(1);
        if (symbolName.startsWith("Java_"))
            symbolName = symbolName.substring("Java_".length());
//        symbolName = symbolName.replace('_', '.');
        int i = symbolName.lastIndexOf("_");
        String className = symbolName.substring(0, i).replace('_', '/');
        String methodName = symbolName.substring(i + 1);
        Class cls = Class.forName(className.replace('/', '.'));
        Method method = getNativeMethodsCache(cls).get(methodName);
        nameAndSigArray[0] = cls.getSimpleName();
        nameAndSigArray[1] = getNativeSignature(method);
        return className;
        
    }
	public static String getNativeName(Class c) {
		return c.getName().replace('.', '/');	
	} 
	public static String getNativeSignature(Method m) {
        StringBuffer b = new StringBuffer();
        b.append('(');
        for (Class c : m.getParameterTypes())
            b.append(getNativeSignature(c));
        b.append(')');
        b.append(getNativeSignature(m.getReturnType()));
        return b.toString();
    }
	public static String getNativeSignature(Class c) {
		if (c.isPrimitive()) {
            if (c == int.class)
                return "I";
            if (c == long.class)
                return "J";
            if (c == short.class)
                return "S";
            if (c == byte.class)
                return "B";
            if (c == boolean.class)
                return "Z";
            if (c == double.class)
                return "D";
            if (c == float.class)
                return "F";
            if (c == char.class)
                return "C";
            if (c == void.class)
                return "V";
            
            throw new RuntimeException("unexpected case");
        }
		if (c.isArray())
			return "[" + getNativeSignature(c.getComponentType());
		return "L" + getNativeName(c) + ";";
	}
}
