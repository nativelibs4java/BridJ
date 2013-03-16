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

import org.bridj.ann.*; // annotations such as Library...

import static org.bridj.Pointer.*;
import org.junit.*;
import static org.junit.Assert.*;

import static org.bridj.LibCTest.*;

@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
public class VarArgsTest {
	static {
		BridJ.register();
	}
	public static native void passVarArgs(Pointer<Byte> out, boolean isInt, Object... args);
		
	@Test
	public void testSPrintf() {
		Pointer<Byte> dest = allocateBytes(100);
		String fmtString = "Hello %d !";
		int value = 10;
		sprintf(dest, pointerToCString(fmtString), value);
		assertEquals(String.format(fmtString, value), dest.getCString());
	}
	
	@Test
	public void testPassBools() {
		Pointer<Byte> out = allocateBytes(8);
		for (boolean value : new boolean[] { true, false }) {
			passVarArgs(out, true, value);
			assertEquals(value, out.getSizeT() != 0);
		}
	}
	int[] intValues = new int[] { 0, 1, -1, 33 };
	@Test
	public void testPassInts() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassLongs() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (long)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassShorts() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (short)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassChars() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (char)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassBytes() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (byte)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassDouble() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, false, (double)value);
			assertEquals(value, out.getDouble(), 0);
		}
	}		
	@Test
	public void testPassFloats() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, false, (float)value);
			assertEquals(value, out.getDouble(), 0);
		}
	}
	
}
