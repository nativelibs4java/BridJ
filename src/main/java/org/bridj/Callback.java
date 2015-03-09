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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj;

import org.bridj.Pointer;
import org.bridj.ann.Runtime;

/**
 * Native C callback (beware : don't let your callbacks be GC'd before they're
 * used).<br>
 * To protect a callback against the GC, you can keep a reference to your
 * callback or use {@link BridJ#protectFromGC(org.bridj.NativeObject) } / {@link BridJ#unprotectFromGC(org.bridj.NativeObject)
 * }.<br>
 * A callback is a Java object with only one abstract method exposed as a C
 * function pointer to the native world.<br>
 * Here's an example of callback definition (use JNAerator to generate them
 * automatically) :
 * <pre>{@code
 *  // typedef int (*MyCallback)(int a, int b);
 *  public static abstract class MyCallback extends Callback {
 * public abstract int doSomething(int a, int b);
 * }
 * }</pre>
 *
 * @author Olivier Chafik
 */
@Runtime(CRuntime.class)
public abstract class Callback<C extends Callback<C>> extends NativeObject implements CallbackInterface {

    public Pointer<C> toPointer() {
        return (Pointer) Pointer.getPointer(this);
    }
}
