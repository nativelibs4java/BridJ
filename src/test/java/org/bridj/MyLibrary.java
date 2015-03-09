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

import org.bridj.*;
import static org.bridj.Pointer.*;
import org.bridj.ann.*;

/**
 *
 * @author Olivier
 */
public class MyLibrary {
    public MyLibrary() {
		BridJ.register(getClass());
	}
    public static class MyCallback {
        
    }
    public static class MyTypedPtr extends TypedPointer {
        public MyTypedPtr(Pointer<?> ptr) {
            super(ptr.getPeer());
        }
        @Deprecated
        public MyTypedPtr(long peer) {
            super(peer);
        }
    }
	protected native @Ptr long someFunction_native(@Ptr long arg1);
	public MyCallback someFunction(MyTypedPtr arg1) {
		return null;//Callback.wrapCallback(someFunction_native(Pointer.getPeer(arg1)), MyCallback.class);
	}
	
	protected native int someFunction_native(@Ptr long stringArray, @Ptr long errOut);
	public int someFunction(String[] arg1, Pointer<Integer> errOut) {
		return someFunction_native(pointerToCStrings(arg1).getPeer(), errOut.getPeer());
	}
	
	protected native int someFunction2_native(@Ptr long size, @Ptr long sizeOut);
	public int someFunction2(long size, Pointer<SizeT> sizeOut) {
		return someFunction2_native(size, Pointer.getPeer(sizeOut));
	}
}
