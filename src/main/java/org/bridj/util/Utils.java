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
    
    public static boolean containsTypeVariables(Type type) {
        if (type instanceof TypeVariable)
            return true;
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            for (Type t : pt.getActualTypeArguments())
                if (containsTypeVariables(t))
                    return true;
        }
        return false;
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

    public static boolean parametersComplyToSignature(Object[] values, Class[] parameterTypes) {
        if (values.length != parameterTypes.length)
            return false;
        for (int i = 0, n = values.length; i < n; i++) {
            Object value = values[i];
            Class parameterType = parameterTypes[i];
            if (!parameterType.isInstance(value))
                return false;
        }
        return true;
    }
}
