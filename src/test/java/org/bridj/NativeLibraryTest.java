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

import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.objc.*;
import org.bridj.ann.Library;
import org.bridj.ann.Runtime;
import java.io.*;

@Library("Foundation")
@Runtime(ObjectiveCRuntime.class)
public class NativeLibraryTest {
	
	static File tempDir() throws IOException {
		File f = File.createTempFile("bridj", "natlibtest");
		f.delete();
		f.mkdir();
		f.deleteOnExit();
		return f;
	}
	static void touch(File dir, String name) throws IOException {
		File f = new File(dir, name);
		f.deleteOnExit();
		new FileOutputStream(f).close();
	}
	
	@Test
	public void testVersionedLibrary() throws IOException {
		File d = new File(".");
		String[] files = new String[] { 
			"libc.so.0.1",
			"libc.so.1",
			"libc.so.13.0",
			"libc.so.2.1",
			"libc.so.1.0.0"
		};
		
		assertEquals("libc.so.13.0", BridJ.findFileWithGreaterVersion(d, files, "libc.so").getName());
	}
}
