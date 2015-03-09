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

import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Array;
import org.bridj.ann.Ptr;
import org.bridj.ann.Optional;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html

@Library("test")
public class Struct3Test {
	static {
		BridJ.register();
	}

	@Optional
	public static native @Ptr long sizeOf_message();
	@Optional
	public static native @Ptr long sizeOf_container();
	@Optional
	public static native @Ptr long sizeOf_stuff();
	
	public static class stuff extends StructObject {
		public stuff() {
			super();
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(0) 
		public Pointer<Byte > part1() {
			return this.io.getPointerField(this, 0);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(1) 
		public Pointer<Byte > part2() {
			return this.io.getPointerField(this, 1);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(2) 
		public Pointer<Byte > part3() {
			return this.io.getPointerField(this, 2);
		}
		public stuff(Pointer pointer) {
			super(pointer);
		}
	};
	public static class container extends StructObject {
		public container() {
			super();
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(0) 
		public Pointer<Byte > type() {
			return this.io.getPointerField(this, 0);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(1) 
		public Pointer<Byte > date() {
			return this.io.getPointerField(this, 1);
		}
		/// C type : char[100]
		@Array({100}) 
		@Field(2) 
		public Pointer<Byte > time() {
			return this.io.getPointerField(this, 2);
		}
		/// C type : stuff
		@Field(3) 
		public stuff stuff1() {
			return this.io.getNativeObjectField(this, 3);
		}
		/// C type : stuff
		@Field(3) 
		public container stuff1(stuff stuff1) {
			this.io.setNativeObjectField(this, 3, stuff1);
			return this;
		}
		/// C type : stuff
		@Field(4) 
		public stuff stuff2() {
			return this.io.getNativeObjectField(this, 4);
		}
		/// C type : stuff
		@Field(4) 
		public container stuff2(stuff stuff2) {
			this.io.setNativeObjectField(this, 4, stuff2);
			return this;
		}
		public container(Pointer pointer) {
			super(pointer);
		}
	};
	public static class message extends StructObject {
		public message() {
			super();
		}
		/// C type : char[512]
		@Array({512}) 
		@Field(0) 
		public Pointer<Byte > text() {
			return this.io.getPointerField(this, 0);
		}
		/// C type : container[1]
		@Array({1}) 
		@Field(1) 
		public Pointer<container > container() {
			return this.io.getPointerField(this, 1);
		}
		public message(Pointer pointer) {
			super(pointer);
		}
	};
	
    @Test
    public void testSizeOfMessage() {
    	assertEquals(sizeOf_stuff(), BridJ.sizeOf(stuff.class));
    	assertEquals(sizeOf_container(), BridJ.sizeOf(container.class));
    	assertEquals(sizeOf_message(), BridJ.sizeOf(message.class));
    }
}

