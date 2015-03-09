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
package org.bridj;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.bridj.cpp.CPPRuntime;
import org.bridj.util.Utils;

/**
 * Base class for implementation of runtimes
 *
 * @author Olivier
 */
public abstract class AbstractBridJRuntime implements BridJRuntime {
    //@Override

    public void unregister(Type type) {
        // TODO !!!
    }

    //@Override
    public Type getType(NativeObject instance) {
        if (instance == null) {
            return null;
        }
        return Utils.getClass(instance.getClass());
    }

    protected java.lang.reflect.Constructor findConstructor(Class<?> type, int constructorId, boolean onlyWithAnnotation) throws SecurityException, NoSuchMethodException {
        for (java.lang.reflect.Constructor<?> c : type.getDeclaredConstructors()) {
            org.bridj.ann.Constructor ca = c.getAnnotation(org.bridj.ann.Constructor.class);
            if (ca == null) {
                continue;
            }
            if (ca.value() == constructorId) {
                return c;
            }
        }
        if (constructorId < 0)// && args.length == 0)
        {
            return type.getConstructor();
        }
        Class<?> sup = type.getSuperclass();
        if (sup != null) {
            try {
                java.lang.reflect.Constructor c = findConstructor(sup, constructorId, onlyWithAnnotation);
                if (onlyWithAnnotation && c != null) {
                    return c;
                }

                Type[] params = c.getGenericParameterTypes();
                Constructor<?>[] ccs = type.getDeclaredConstructors();
                for (java.lang.reflect.Constructor cc : ccs) {
                    Type[] ccparams = cc.getGenericParameterTypes();
                    int overrideOffset = Utils.getEnclosedConstructorParametersOffset(cc);
                    if (isOverridenSignature(params, ccparams, overrideOffset)) {
                        return cc;
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        throw new NoSuchMethodException("Cannot find constructor with index " + constructorId);
    }

    public static boolean isOverridenSignature(Type[] parentSignature, Type[] overrideSignature, int overrideOffset) {
        int n = parentSignature.length;
        if (overrideSignature.length - overrideOffset != n) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (!isOverride(parentSignature[i], overrideSignature[overrideOffset + i])) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isOverride(Type parentSignature, Type overrideSignature) {
        return Utils.getClass(parentSignature).isAssignableFrom(Utils.getClass(overrideSignature));
    }
}
