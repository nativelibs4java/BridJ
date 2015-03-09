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

import org.bridj.ann.Library;
//import com.sun.jna.Native;

/**
 *
 * @author Olivier
 */
@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
public class PerfLib {
	static java.io.File libraryFile = BridJ.getNativeLibraryFile(BridJ.getNativeLibraryName(PerfLib.class));
    static {
        System.load(libraryFile.toString());
    }
    public static class DynCallTest {
        static {
        		BridJ.register();
        }
        public native int testAddDyncall(int a, int b);
        public native int testASinB(int a, int b);
    }

    public static class JNATest implements com.sun.jna.Library {
        static {
        	try {
        		com.sun.jna.Native.register(libraryFile.toString());
        	} catch (Exception ex) {
        		throw new RuntimeException("Failed to initialize test JNA library", ex);
        	}
        }
        public static native int testAddJNA(int a, int b);
        public static native int testASinB(int a, int b);
    }
    public interface JNAInterfaceTest extends com.sun.jna.Library {
        public static final JNAInterfaceTest INSTANCE = (JNAInterfaceTest)com.sun.jna.Native.loadLibrary(libraryFile.toString(), JNAInterfaceTest.class);
        int testAddJNA(int a, int b);
        int testASinB(int a, int b);
    }
    public static native int testAddJNI(int a, int b);
    public static native double testASinB(int a, int b);
}
