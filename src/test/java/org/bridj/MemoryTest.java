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

import java.io.FileNotFoundException;

import java.util.Collection;

import java.util.concurrent.*;

import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.*;
import org.bridj.*;
import static org.bridj.BridJ.*;
import static org.bridj.Pointer.*;

@Library("test")
public class MemoryTest {
	static {
		BridJ.register();
	}
	public static abstract class CallbackType extends Callback<CallbackType> {
		abstract public void apply(Pointer<Byte > v1, Pointer<Byte > v2);
	}
	public static native void defineCallback(Pointer<CallbackType> callback);
	public static native void copyChar(Pointer<Byte> dest, Pointer<Byte> src);
	public static native void callCallback(long times, Pointer<Byte> value);


  static String[] strings = {"a", "b", "c"};
	@Test
	public void testGC() {
	    for (int i = 0; i < 10; i++) {
	        for (int j = 0; j < 100; j++) {
	            Pointer.allocateBytes(2);
	            Pointer.allocateSizeT();
	            Pointer.pointerToCStrings(strings);
	        }
	        System.gc();
	    }
	}
	@Test
	public void testRelease() {
	    for (int i = 0; i < 10; i++) {
	        for (int j = 0; j < 100; j++) {
	            Pointer.release(Pointer.allocateBytes(2));
	            Pointer.release(Pointer.allocateSizeT());
	            Pointer.release(Pointer.pointerToCStrings(strings));
	        }
	        System.gc();
	    }
	}

    @Test
    public void testCopy() {
        Pointer<Byte> dest = allocateByte(), src = pointerToByte((byte)10);
        copyChar(dest, src);
        assertEquals(dest.get(), src.get());
    }
	@Test
	public void testCallbacks() {
		final Pointer<Byte> v = pointerToByte((byte)1);
		CallbackType cb = new CallbackType() {
			public void apply(Pointer<Byte> v1, Pointer<Byte> v2) {
				assertEquals(v, v1);
				assertNotNull(v2);
			}
		};
	  defineCallback(getPointer(cb));
	  callCallback(1000000L, v);
	}

	
	
	static void parallelStress(int nThreads, long times, Runnable runnable) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		for (long i = 0; i < times; i++) {
			executor.execute(runnable);
		}
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}
	@Library("c") @SetsLastError
	public static native long strtol(@Ptr long str, @Ptr long endptr, int base) throws LastError;
	//public static native @org.bridj.ann.CLong long strtol(Pointer<Byte> str, Pointer<Pointer<Byte>> endptr, int base) throws LastError;
    
	@Library("test")
	public static native Pointer<Byte> incrPointer(Pointer<Byte> ptr);
	
    @Test
    public void testErrors() throws Exception {
    	final Pointer<Byte> s = pointerToCString("18446744073709551616");
    	parallelStress(20, 100000, new Runnable() { public void run() {
    		strtol(s.getPeer(), 0, 10);
			assertNull(new LastError(0, LastError.eLastErrorKindWindows).getDescription());
    		assertNull(new LastError(0, LastError.eLastErrorKindCLibrary).getDescription());
    		assertNotNull(new LastError(1, LastError.eLastErrorKindWindows).getDescription());
    		assertNotNull(new LastError(1, LastError.eLastErrorKindCLibrary).getDescription());
    		assertEquals(s.getPeer() + 1, incrPointer(s).getPeer());
    		//strtol(s, null, 10);
    		//LastError e = LastError.getLastError();
    		//assertNotNull(e);
		}});
    }
}
