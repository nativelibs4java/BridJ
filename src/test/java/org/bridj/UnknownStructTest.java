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

import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.Constructor;
import org.bridj.ann.Convention;
import org.bridj.ann.Ptr;
import org.bridj.cpp.CPPObject;


import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Name;
import org.bridj.ann.Runtime;
import org.bridj.ann.Virtual;
import org.bridj.cpp.CPPRuntime;

import org.junit.After;
import static org.bridj.Pointer.*;

@Library("test")
@Runtime(CPPRuntime.class)
public class UnknownStructTest {
  static {
		BridJ.register();
	}
	public static native Pointer<MyUnknownStruct> newMyUnknownStruct(int a);
	public static native int deleteMyUnknownStruct(Pointer<MyUnknownStruct> s);
	public static interface MyUnknownStruct {};

	@Test
	public void test() {
		int a = 10;
		Pointer<MyUnknownStruct> ps = newMyUnknownStruct(a);
		assertNotNull(ps);
		int aa = deleteMyUnknownStruct(ps);
		assertEquals(a, aa);
	}
}
