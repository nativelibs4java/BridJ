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

import java.lang.reflect.Type;

/**
 * Interface that each specific pluggable native runtime must implement.<br>
 * A runtime is attached to a class via the {@link org.bridj.ann.Runtime}
 * annotation, so any runtime can be added in thirdparty libraries.<br>
 * A runtime typically defines NativeObject subclasses and deals with their
 * instances lifecycle through the type information metadata {@link TypeInfo}
 * class.<br>
 *
 * @author ochafik
 */
public interface BridJRuntime {

    /**
     * Type information metadata + lifecycle management methods.<br>
     * This class is not meant to be used by end users, it's used by runtimes.
     */
    public interface TypeInfo<T extends NativeObject> {

        T cast(Pointer peer);

        void initialize(T instance);

        void initialize(T instance, Pointer peer);

        void initialize(T instance, int constructorId, Object[] args);

        void destroy(T instance);

        T createReturnInstance();

        T clone(T instance) throws CloneNotSupportedException;

        BridJRuntime getRuntime();

        Type getType();

        boolean equal(T instance, T other);

        int compare(T instance, T other);

        long sizeOf();

        void writeToNative(T instance);

        String describe(T instance);

        String describe();

        void readFromNative(T instance);

        void copyNativeObjectToAddress(T instance, Pointer<T> ptr);
    }

    Type getType(NativeObject instance);

    void register(Type type);

    void unregister(Type type);

    <T extends NativeObject> TypeInfo<T> getTypeInfo(final Type type);

    Type getType(final Class<?> cls, Object[] targs, int[] typeParamCount);

    boolean isAvailable();

    <T extends NativeObject> Class<? extends T> getActualInstanceClass(Pointer<T> pInstance, Type officialType);
}
