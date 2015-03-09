/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Default implementation of {@link java.lang.reflect.ParameterizedType}
 *
 * @author Olivier
 */
public class DefaultParameterizedType implements ParameterizedType {

    private final Type[] actualTypeArguments;
    private final Type ownerType;
    private final Type rawType;

    public DefaultParameterizedType(Type ownerType, Type rawType, Type[] actualTypeArguments) {
        this.ownerType = ownerType;
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
    }

    public DefaultParameterizedType(Type rawType, Type... actualTypeArguments) {
        this(null, rawType, actualTypeArguments);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (ownerType != null) {
            b.append(Utils.toString(ownerType)).append(".");
        }
        b.append(rawType);
        if (actualTypeArguments.length > 0) {
            b.append("<");
            for (int i = 0; i < actualTypeArguments.length; i++) {
                if (i > 0) {
                    b.append(", ");
                }
                b.append(Utils.toString(actualTypeArguments[i]));
            }
            b.append(">");
        }
        return b.toString();
    }

    /**
     * Builds a parameterized type with the provided raw type and type
     * arguments.<br>
     * For instance,
     * <code>paramType(Pointer.class, Integer.class)</code> gives you the type
     * of
     * <code>Pointer&lt;Integer&gt;</code>.
     */
    public static Type paramType(Type rawType, Type... actualTypeArguments) {
        return new DefaultParameterizedType(rawType, actualTypeArguments);
    }

    //@Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments.clone();
    }

    //@Override
    public java.lang.reflect.Type getOwnerType() {
        return ownerType;
    }

    //@Override
    public java.lang.reflect.Type getRawType() {
        return rawType;
    }

    //@Override
    public int hashCode() {
        int h = getRawType().hashCode();
        if (getOwnerType() != null) {
            h ^= getOwnerType().hashCode();
        }
        for (int i = 0, n = actualTypeArguments.length; i < n; i++) {
            Type arg = actualTypeArguments[i];
            if (arg != null) {
                h ^= arg.hashCode();
            }
        }
        return h;
    }

    static boolean eq(Object a, Object b) {
        if ((a == null) != (b == null)) {
            return false;
        }
        if (a != null && !a.equals(b)) {
            return false;
        }
        return true;
    }
    //@Override

    public boolean equals(Object o) {
        if (o == null || !(o instanceof DefaultParameterizedType)) {
            return false;
        }

        DefaultParameterizedType t = (DefaultParameterizedType) o;
        if (!eq(getRawType(), t.getRawType())) {
            return false;
        }
        if (!eq(getOwnerType(), t.getOwnerType())) {
            return false;
        }

        Object[] tp = t.actualTypeArguments;
        if (actualTypeArguments.length != tp.length) {
            return false;
        }

        for (int i = 0, n = actualTypeArguments.length; i < n; i++) {
            if (!eq(actualTypeArguments[i], tp[i])) {
                return false;
            }
        }

        return true;
    }
}
