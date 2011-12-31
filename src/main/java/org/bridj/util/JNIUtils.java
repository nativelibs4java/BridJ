/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;

/**
 *
 * @author ochafik
 */
public class JNIUtils {
    
	public static String getNativeName(Class c) {
		return c.getName().replace('.', '/');	
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
