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

import org.bridj.CRuntime.MethodCallInfoBuilder;
import org.bridj.ann.Convention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.bridj.Pointer.*;

/**
 * Factory that is able to create dynamic functions bindings with a given
 * signature
 */
public class DynamicFunctionFactory {

    final Constructor<? extends DynamicFunction> constructor;
    final Method method;
    final long callbackHandle;

    DynamicFunctionFactory(Class<? extends DynamicFunction> callbackClass, Method method, /*Convention.Style style,*/ MethodCallInfoBuilder methodCallInfoBuilder) {
        try {
            this.constructor = callbackClass.getConstructor();
            this.method = method;

            MethodCallInfo mci = methodCallInfoBuilder.apply(method);
            callbackHandle = JNI.bindJavaToCCallbacks(mci);
        } catch (Throwable th) {
            th.printStackTrace();
            throw new RuntimeException("Failed to instantiate callback" + " : " + th, th);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (BridJ.debugNeverFree) {
            return;
        }

        JNI.freeJavaToCCallbacks(callbackHandle, 1);
    }

    public DynamicFunction newInstance(Pointer<?> functionPointer) {
        if (functionPointer == null) {
            return null;
        }

        try {
            DynamicFunction dcb = constructor.newInstance();
            dcb.peer = (Pointer) functionPointer;
            dcb.method = method;
            dcb.factory = this;

            return dcb;
        } catch (Throwable th) {
            th.printStackTrace();
            throw new RuntimeException("Failed to instantiate callback" + " : " + th, th);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + method + ")";
    }
}
