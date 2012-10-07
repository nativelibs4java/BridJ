package org.bridj;
import java.io.File;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;
import org.bridj.ann.Field;
import org.bridj.ann.Virtual;
import org.bridj.ann.Constructor;
import org.bridj.cpp.CPPObject;

import org.bridj.*;
import static org.bridj.Pointer.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

@Library("test") 
public class CPPTest3 {
	static {
		BridJ.register();
	}
	public static class Constructed extends CPPObject {
		@Field(0) 
		public int m_i() {
			return this.io.getIntField(this, 0);
		}
		@Field(0) 
		public Constructed m_i(int m_i) {
			this.io.setIntField(this, 0, m_i);
			return this;
		}
		@Field(1) 
		public float m_b() {
			return this.io.getFloatField(this, 1);
		}
		@Field(1) 
		public Constructed m_b(float m_b) {
			this.io.setFloatField(this, 1, m_b);
			return this;
		}
		@Field(2) 
		public byte m_c() {
			return this.io.getByteField(this, 2);
		}
		@Field(2) 
		public Constructed m_c(byte m_c) {
			this.io.setByteField(this, 2, m_c);
			return this;
		}
		/// C type : const char*
		@Field(3) 
		public Pointer<Byte > m_x() {
			return this.io.getPointerField(this, 3);
		}
		/// C type : const char*
		@Field(3) 
		public Constructed m_x(Pointer<Byte > m_x) {
			this.io.setPointerField(this, 3, m_x);
			return this;
		}
		/// C type : const char*
		@Field(4) 
		public Pointer<Byte > m_y() {
			return this.io.getPointerField(this, 4);
		}
		/// C type : const char*
		@Field(4) 
		public Constructed m_y(Pointer<Byte > m_y) {
			this.io.setPointerField(this, 4, m_y);
			return this;
		}
		/// Original signature : <code>int Constructed(int, float, char)</code>
		@Constructor(0)
		public Constructed(int i, float b, byte c, Pointer<Pointer<Byte>> result) {
			super((Void)null, 0, i, b, c, result);
		}
		
		@Constructor(1)
		public Constructed(Pointer<Byte > x, Pointer<Byte > y, Pointer<Pointer<Byte>> result) {
			super((Void)null, 1, x, y, result);
		}
		public Constructed(Pointer pointer) {
			super(pointer);
		}
		
		@Ptr
		public native static long sizeOf();
	}
	@Test
	public void testSize() {
		assertEquals(Constructed.sizeOf(), BridJ.sizeOf(Constructed.class));
	}
	
	@Test
	public void testConstructor1() {
		Pointer<Pointer<Byte>> result = allocatePointer(Byte.class);
		Constructed c = new Constructed(-1, 2f, (byte)-3, result);
		assertNotNull(result.get());
		assertEquals("constructor1", result.get().getCString());
		assertEquals(-1, c.m_i());
		assertEquals(2f, c.m_b(), 0);
		assertEquals((byte)-3, c.m_c());
		assertEquals(null, c.m_x());
		assertEquals(null, c.m_y());
	}
	
	@Test
	public void testConstructor2() {
		Pointer<Pointer<Byte>> result = allocatePointer(Byte.class);
		Pointer<Byte> x = pointerToCString("x"), y = pointerToCString("y");
		Constructed c = new Constructed(x, y, result);
		assertNotNull(result.get());
		assertEquals("constructor2", result.get().getCString());
		assertEquals(0, c.m_i());
		assertEquals(0f, c.m_b(), 0);
		assertEquals((byte)0, c.m_c());
		assertEquals(x, c.m_x());
		assertEquals(y, c.m_y());
	}
}
