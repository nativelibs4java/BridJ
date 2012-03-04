/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import org.bridj.BridJ;
import org.bridj.Platform;
import org.bridj.Version;

/**
 *
 * @author ochafik
 */
public class JNIUtils {
    
    private static class NativeMethodsCache {
        Map<String, String> signatures = new HashMap<String, String>();
        public NativeMethodsCache(String internalClassName) throws IOException {
            for (String[] sig : BytecodeAnalyzer.getNativeMethodSignatures(internalClassName, Platform.getClassLoader())) {
                signatures.put(sig[1], sig[2]);
            }
        }
        public String get(String name) {
            return signatures.get(name);
        }
        public Set<String> getNames() {
            return signatures.keySet();
        }
    }
    private static Map<String, NativeMethodsCache> nativeMethodsCache = new WeakHashMap<String, NativeMethodsCache>();
    private static synchronized NativeMethodsCache getNativeMethodsCache(String internalClassName) throws IOException {
        NativeMethodsCache cache = nativeMethodsCache.get(internalClassName);
        if (cache == null)
            nativeMethodsCache.put(internalClassName, cache = new NativeMethodsCache(internalClassName));
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
    public static String decodeVersionSpecificMethodNameClassAndSignature(String symbolName, Object[] nameAndSigArray) throws NoSuchMethodException, IOException {
        return decodeMethodNameClassAndSignature(symbolName, nameAndSigArray, bridjNormalPackagePrefix, bridjVersionSpecificPackagePrefix);
    }
    
    static String decodeMethodNameClassAndSignature(String symbolName, Object[] nameAndSigArray, String normalClassPrefix, String replacementClassPrefix) throws NoSuchMethodException, IOException {
        if (symbolName.startsWith("_"))
            symbolName = symbolName.substring(1);
        if (symbolName.startsWith("Java_"))
            symbolName = symbolName.substring("Java_".length());
        
        int i = findLastNonEscapeUnderscore(symbolName);
        String className = symbolName.substring(0, i).replace('_', '.');
        if (normalClassPrefix != null) {
            if (className.startsWith(normalClassPrefix) && !className.startsWith(replacementClassPrefix))
                className = replacementClassPrefix + className.substring(normalClassPrefix.length());
        }
        String methodName = symbolName.substring(i + 1).replaceAll("_1", "_");
        
        NativeMethodsCache mc = getNativeMethodsCache(className.replace('.', '/'));
        String sig = mc.get(methodName);
        if (sig == null)
            throw new NoSuchMethodException("Method " + methodName + " not found in class " + className + " : known method names = " + StringUtils.implode(mc.getNames(), ", "));
        
        nameAndSigArray[0] = methodName;
        nameAndSigArray[1] = sig;
        
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
