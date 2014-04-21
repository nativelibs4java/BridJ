/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Bits;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Name;
import org.junit.Test;
import static org.junit.Assert.*;

public class BitFieldTest {
    static {
        BridJ.register();
    }
    /**
        struct S {
            char i:1;
            char j:2;
            char k:5;
        };
    */
    @Name("S") 
    public class S extends StructObject {
        @Field(0) 
        @Bits(1) 
        public byte i() {
            return this.io.getByteField(this, 0);
        }
        @Field(0) 
        @Bits(1) 
        public S i(byte i) {
            this.io.setByteField(this, 0, i);
            return this;
        }
        @Field(1) 
        @Bits(2) 
        public byte j() {
            return this.io.getByteField(this, 1);
        }
        @Field(1) 
        @Bits(2) 
        public S j(byte j) {
            this.io.setByteField(this, 1, j);
            return this;
        }
        @Field(2) 
        @Bits(5) 
        public byte k() {
            return this.io.getByteField(this, 2);
        }
        @Field(2) 
        @Bits(5) 
        public S k(byte k) {
            this.io.setByteField(this, 2, k);
            return this;
        }
        public S() {
            super();
        }
        public S(Pointer pointer) {
            super(pointer);
        }
    }

    @Test
    public void testSize() {
        assertEquals(1, BridJ.sizeOf(S.class));
    }

    @Test
    public void testFields() {
        //assertEquals(1, BridJ.sizeOf(S.class));
        S s = new S();
        s.j((byte) 7);
        assertEquals(0, s.i());
        assertEquals(3, s.j());
        assertEquals(0, s.k());
    }
}
