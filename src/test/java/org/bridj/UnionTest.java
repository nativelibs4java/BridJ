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

import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.Field;
import org.bridj.ann.Union;

/**
 *
 * @author ochafik
 */
public class UnionTest {
    public static class MyUnionStruct extends StructObject {
        public MyUnionStruct(Pointer<MyUnionStruct> p) { super(p); }
        public MyUnionStruct() { super(); }

        @Field(0)
		public int a() {
			return io.getIntField(this, 0);
        }
        public MyUnionStruct a(int a) {
            io.setIntField(this, 0, a);
            return this;
        }

        @Field(value = 1, unionWith = 0)
		public double aa() {
			return io.getDoubleField(this, 0);
        }
        public MyUnionStruct aa(double a) {
            io.setDoubleField(this, 0, a);
            return this;
        }

        @Field(2)
		public double b() {
			return io.getDoubleField(this, 1);
        }
        public MyUnionStruct b(double b) {
            io.setDoubleField(this, 1, b);
            return this;
        }

        @Field(value = 3, unionWith = 2)
		public double bb() {
			return io.getDoubleField(this, 1);
        }
        public MyUnionStruct bb(double b) {
            io.setDoubleField(this, 1, b);
            return this;
        }
	}
    @Test
    public void unionSize() {
        assertEquals(16, BridJ.sizeOf(MyUnionStruct.class));
    }
    @Test
    public void unionValues() {
        MyUnionStruct us = new MyUnionStruct();
        us.b(10);
        assertEquals(10.0, us.bb(), 0.0);
    }
    
    @Union
    public static class Mixed extends StructObject {
        
        @Field(0) 
        public byte singleByte() {
            return this.io.getByteField(this, 0);
        }
        @Field(0) 
        public Mixed singleByte(byte single) {
            this.io.setByteField(this, 0, single);
            return this;
        }
        @Field(1) 
        public int fourBytes() {
            return this.io.getIntField(this, 1);
        }
        @Field(1) 
        public Mixed fourBytes(int fourBytes) {
            this.io.setIntField(this, 1, fourBytes);
            return this;
        }
    }
    @Test
    public void testMixedUnion() {
        assertEquals(4, BridJ.sizeOf(Mixed.class));
        Pointer<Mixed> p = Pointer.allocate(Mixed.class).order(ByteOrder.BIG_ENDIAN);
        Mixed m = p.get();
        m.singleByte((byte)1);
        assertEquals(1, m.singleByte());
        assertEquals(1 << (3 * 8), m.fourBytes());
    }
}
