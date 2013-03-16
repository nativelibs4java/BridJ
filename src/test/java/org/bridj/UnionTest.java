package org.bridj;

import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.Field;

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
}
