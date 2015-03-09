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

import java.io.IOException;
import java.io.FileNotFoundException;
import org.bridj.ann.*; // annotations such as Library...

import static org.bridj.Pointer.*;
import org.junit.*;
import static org.junit.Assert.*;

@Library("c")
@org.bridj.ann.Runtime(CRuntime.class)
public class LibCTest {
	static {
		if ("1".equals(System.getenv("JNA")))
			com.sun.jna.Native.register("c");
		else
			BridJ.register();
	}
	public static native void sprintf(Pointer<Byte> dest, Pointer<Byte> format, Object... values);
	
	@Library("m")
	public static native double fabs(double x);
	public static native int abs(int x);
	public static native int getpid();
	
    public static native @org.bridj.ann.CLong long strtol(Pointer<Byte> str, Pointer<Pointer<Byte>> endptr, int base) throws LastError;
	
    @Optional // only on Windows
    @Library("test")
    public static native void setLastWindowsError() throws LastError;
    
	@Test
	public void testFabs() {
		assertEquals(10.0, fabs(-10.0), 0.000001);
	}
	@Test(expected=LastError.class)
	public void testLastWindowsError() {
        if (!Platform.isWindows())
            throw new LastError(0, 0);
        
        setLastWindowsError();
	}
	@Test
	public void testErrno() throws IOException {
		if (!Platform.isUnix())
			return;
		
		assertNotNull(BridJ.getNativeLibrary("c").getSymbolPointer("errno"));
	}
	
	public void testNoLastError() {
        long v = strtol(pointerToCString("1010"), null, 10);
        assertEquals(1010, v);
	}
	
	@Test(expected = LastError.class)
	public void testLastError() {
        strtol(pointerToCString("18446744073709551616"), null, 10);
	}
	
	@Test
	public void testAbs() {
		assertEquals(10, abs(-10));
	}
}
	

