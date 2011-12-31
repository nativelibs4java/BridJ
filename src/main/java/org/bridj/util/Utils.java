/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.*;

/**
 * Miscellaneous utility methods.
 * @author ochafik
 */
public class Utils {
    public static int getEnclosedConstructorParametersOffset(Constructor c) {
        Class<?> enclosingClass = c.getDeclaringClass().getEnclosingClass();
        Class[] params = c.getParameterTypes();
        int overrideOffset = params.length > 0 && enclosingClass != null && enclosingClass == params[0] ? 1 : 0;
        return overrideOffset;
    }
    public static boolean isDirect(Buffer b) {
    	if (b instanceof ByteBuffer)
    		return ((ByteBuffer)b).isDirect();
    	if (b instanceof IntBuffer)
    		return ((IntBuffer)b).isDirect();
    	if (b instanceof LongBuffer)
    		return ((LongBuffer)b).isDirect();
    	if (b instanceof DoubleBuffer)
    		return ((DoubleBuffer)b).isDirect();
    	if (b instanceof FloatBuffer)
    		return ((FloatBuffer)b).isDirect();
	if (b instanceof ShortBuffer)
    		return ((ShortBuffer)b).isDirect();
    	if (b instanceof CharBuffer)
    		return ((CharBuffer)b).isDirect();
    	return false;
    }

    public static boolean isSignedIntegral(Type tpe) {
		return 
			tpe == int.class || tpe == Integer.class || 
			tpe == long.class || tpe == Long.class ||
			tpe == short.class || tpe == Short.class || 
			tpe == byte.class || tpe == Byte.class;
	}
	
    public static String toString(Type t) {
    		if (t == null)
    			return "?";
    		if (t instanceof Class)
			return ((Class)t).getName();
		return t.toString();
	}
    public static String toString(Throwable th) {
    		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		return sw.toString();
	}
			
    public static boolean eq(Object a, Object b) {
        if ((a == null) != (b == null))
            return false;
        return !(a != null && !a.equals(b));
    }
    public static <T> Class<T> getClass(Type type) {
        if (type == null)
            return null;
		if (type instanceof Class<?>)
			return (Class<T>)type;
		if (type instanceof ParameterizedType)
			return getClass(((ParameterizedType)type).getRawType());
        if (type instanceof GenericArrayType)
            return (Class)Array.newInstance(getClass(((GenericArrayType)type).getGenericComponentType()), 0).getClass();
        if (type instanceof WildcardType)
            return null;
        if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable)type).getBounds();
            return getClass(bounds[0]);
        }
		throw new UnsupportedOperationException("Cannot infer class from type " + type);
	}

    public static Type getParent(Type type) {
        if (type instanceof Class)
            return ((Class)type).getSuperclass();
        else
            // TODO handle templates !!!
            return getParent(getClass(type));
    }

    public static Class[] getClasses(Type[] types) {
        int n = types.length;
        Class[] ret = new Class[n];
        for (int i = 0; i < n; i++)
            ret[i] = getClass(types[i]);
        return ret;
    }
	
    public static Type getUniqueParameterizedTypeParameter(Type type) {
    		return (type instanceof ParameterizedType) ? ((ParameterizedType)type).getActualTypeArguments()[0] : null;
    }
}
