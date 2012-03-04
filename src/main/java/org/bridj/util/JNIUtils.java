/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import org.bridj.BridJ;
import org.bridj.Version;

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
        public Set<String> getNames() {
            return methods.keySet();
        }
    }
    private static Map<Class, NativeMethodsCache> nativeMethodsCache = new WeakHashMap<Class, NativeMethodsCache>();
    private static synchronized NativeMethodsCache getNativeMethodsCache(Class cls) {
        NativeMethodsCache cache = nativeMethodsCache.get(cls);
        if (cache == null)
            nativeMethodsCache.put(cls, cache = new NativeMethodsCache(cls));
        return cache;
    }
    private static final String bridjPackage = BridJ.class.getPackage().getName();
    
    private static final String bridjNormalPackagePrefix = bridjPackage.endsWith(Version.VERSION_SPECIFIC_SUB_PACKAGE) ? bridjPackage.substring(0, bridjPackage.length() - Version.VERSION_SPECIFIC_SUB_PACKAGE.length()) : bridjPackage + ".";
    private static final String bridjVersionSpecificPackagePrefix = bridjPackage + ".";
    
    static int findLastNonEscapeUnderscore(String s) {
        int len = s.length(), i = len;
        do {
            i = s.lastIndexOf("_", i - 1);
            if (i >= 0 && (i == len - 1 || !Character.isDigit(s.charAt(i + 1))))
                return i;
        } while (i > 0);
        return -1;
    }
    public static String decodeVersionSpecificMethodNameClassAndSignature(String symbolName, Object[] nameAndSigArray) throws ClassNotFoundException, NoSuchMethodException {
        return decodeMethodNameClassAndSignature(symbolName, nameAndSigArray, bridjNormalPackagePrefix, bridjVersionSpecificPackagePrefix);
    }
    
    static String decodeMethodNameClassAndSignature(String symbolName, Object[] nameAndSigArray, String normalClassPrefix, String replacementClassPrefix) throws ClassNotFoundException, NoSuchMethodException {
        if (symbolName.startsWith("_"))
            symbolName = symbolName.substring(1);
        if (symbolName.startsWith("Java_"))
            symbolName = symbolName.substring("Java_".length());
        
        int i = findLastNonEscapeUnderscore(symbolName);
        String className = symbolName.substring(0, i).replace('_', '.');
        if (className.startsWith(normalClassPrefix))
            className = replacementClassPrefix + className.substring(normalClassPrefix.length());
        
        String methodName = symbolName.substring(i + 1).replaceAll("_1", "_");
        Class cls = Class.forName(className);
        NativeMethodsCache mc = getNativeMethodsCache(cls);
        Method method = mc.get(methodName);
        if (method == null)
            throw new NoSuchMethodException("Method " + methodName + " not found in class " + className + " : known method names = " + StringUtils.implode(mc.getNames(), ", "));
        
        nameAndSigArray[0] = method.getName();//cls.getSimpleName();
        nameAndSigArray[1] = getNativeSignature(method);
        
        String internalClassName = className.replace('.', '/');
        return internalClassName;//className;
        
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
