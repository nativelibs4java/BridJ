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

import java.io.FileNotFoundException;

import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.*;
import org.bridj.*;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html
public class TypedPointersTest {
	
	public static class MyPtr extends TypedPointer {
		public MyPtr(long peer) {
			super(peer);
		}
		public MyPtr(Pointer peer) {
			super(peer);
		}
	}
	
	@Library("test")
	public static class MyStruct extends StructObject {
		@Field(0)
		//public native Pointer<Integer> a();
		//public native MyStruct a(MyPtr a);
        public MyPtr a() {
            return io.getTypedPointerField(this, 0);
        }
        public MyStruct a(MyPtr a) {
            io.setPointerField(this, 0, a);
            return this;
        }
        //public native void a(MyPtr a);
	}
	
	@Test
	public void testDummyPtrs() {
		int nPtrs = 3;
		Pointer<MyPtr> pptrs = Pointer.allocateTypedPointers(MyPtr.class, nPtrs), pptr = Pointer.allocateTypedPointer(MyPtr.class);
		assertEquals(nPtrs, pptrs.getValidElements());
		assertEquals(nPtrs * Pointer.SIZE, pptrs.getValidBytes());
		assertEquals(1, pptr.getValidElements());
		for (Pointer<MyPtr> ptrs : new Pointer[] { pptr, pptrs }) {;
			ptrs.setSizeT(10);
			MyPtr ptr = ptrs.get();
			assertTrue(ptr instanceof MyPtr);
			assertEquals(10, ptr.getPeer());
		}
	}
	
	@Test
	public void testStructTypedPtrField() {
		MyStruct s = new MyStruct();
        assertNull(s.a());
		Pointer<MyStruct> ps = Pointer.getPointer(s);
		ps.setSizeT(10);
		MyPtr ptr = s.a();
		assertTrue(ptr instanceof MyPtr);
		assertEquals(10, ptr.getPeer());
	}
	
	@Test
	public void testStringPointer() {
		assertNull(Pointer.pointerToCString(null));
		Pointer<Byte> p = Pointer.pointerToCString("test");
		assertEquals("test", p.getCString());
	}
	
	@Test
	public void testStringsPointer() {
		assertNull(Pointer.pointerToCStrings((String[])null));
		
		Pointer<Pointer<Byte>> p = Pointer.pointerToCStrings(null, null);
		assertNull(p.get(0));
		assertNull(p.get(1));
		
		p = Pointer.pointerToCStrings("test1", "test2");
		assertEquals("test1", p.get(0).getCString());
		assertEquals("test2", p.get(1).getCString());
	}
	
	@Test
	public void testEquals() {
		Pointer m1 = Pointer.allocateBytes(2), m2 = Pointer.allocateBytes(2);
		assertNotNull(m1);
		assertNotNull(m2);
		assertTrue(!m1.equals(m2));
		
		long addr1 = m1.getPeer(), addr2 = m2.getPeer();
		Pointer[] ps1 = new Pointer[] {
			m1,
			new MyPtr(addr1),
			Pointer.pointerToAddress(addr1)
		};
		Pointer[] ps2 = new Pointer[] {
			m2,
			new MyPtr(addr2),
			Pointer.pointerToAddress(addr2)
		};
		for (Pointer p1 : ps1) {
			assertNotNull(p1);
			assertEquals(m1, p1);
			assertEquals(p1, m1);	
		}
		for (Pointer p2 : ps2) {
			assertNotNull(p2);
			assertEquals(m2, p2);
			assertEquals(p2, m2);	
		}
		
		for (Pointer p1 : ps1) {
			for (Pointer p2 : ps2) {
				assertTrue(!p1.equals(p2));
			}	
		}
	}
}

