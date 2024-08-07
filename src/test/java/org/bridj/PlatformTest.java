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
import static org.bridj.Platform.is64Bits;
import static org.bridj.Platform.isUnix;
import static org.bridj.Platform.isWindows;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

public class PlatformTest {
	@Test
	public void testSizes() {
		int clong = isWindows() || !is64Bits() ? 4 : 8;
		int sizet = is64Bits() ? 8 : 4, ptr = sizet;
		int wchar = isUnix() ? 4 : 2;
		
		assertEquals(clong, Platform.CLONG_SIZE);
		assertEquals(sizet, Platform.SIZE_T_SIZE);
		assertTrue(Platform.TIME_T_SIZE > 0);
		assertEquals(wchar, Platform.WCHAR_T_SIZE);
		assertEquals(ptr, Platform.POINTER_SIZE);
		
		assertEquals(ptr, Pointer.SIZE);
		assertEquals(sizet, SizeT.SIZE);
		assertTrue(TimeT.SIZE > 0);
		assertEquals(clong, CLong.SIZE);
		
	}
	@Test
	public void testMachine() throws Exception {
		if (!isUnix())
			return;
		
		Process p = Runtime.getRuntime().exec(new String[] { "uname", "-m" });
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String uname = r.readLine().trim();
		String m = uname;
		assertTrue(m.length() > 0);
		
		if (m.matches("i\\d86"))
			m = "i386";
		else if (m.matches("i86pc"))
			m = "x86";
		
		if (m.equals("i386") && Platform.is64Bits())
			m = "x86_64";

		if (m.equals("aarch64") && Platform.isArm() && Platform.is64Bits())
			m = "arm64";
		
		assertEquals("uname = " + uname + ", Platform.getMachine = " + Platform.getMachine(), m, Platform.getMachine());
	}
}
