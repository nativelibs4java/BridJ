package org.bridj;

import org.bridj.ann.*; // annotations such as Library...

import static org.bridj.Pointer.*;
import org.junit.*;
import static org.junit.Assert.*;

import static org.bridj.LibCTest.*;

@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
public class VarArgsTest {
	static {
		BridJ.register();
	}
	public static native void passVarArgs(Pointer<Byte> out, boolean isInt, Object... args);
		
	@Test
	public void testSPrintf() {
		Pointer<Byte> dest = allocateBytes(100);
		String fmtString = "Hello %d !";
		int value = 10;
		sprintf(dest, pointerToCString(fmtString), value);
		assertEquals(String.format(fmtString, value), dest.getCString());
	}
	
	@Test
	public void testPassBools() {
		Pointer<Byte> out = allocateBytes(8);
		for (boolean value : new boolean[] { true, false }) {
			passVarArgs(out, true, value);
			assertEquals(value, out.getSizeT() != 0);
		}
	}
	int[] intValues = new int[] { 0, 1, -1, 33 };
	@Test
	public void testPassInts() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassLongs() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (long)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassShorts() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (short)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassChars() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (char)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassBytes() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, true, (byte)value);
			assertEquals(value, out.getSizeT());
		}
	}
	@Test
	public void testPassDouble() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, false, (double)value);
			assertEquals(value, out.getDouble(), 0);
		}
	}		
	@Test
	public void testPassFloats() {
		Pointer<Byte> out = allocateBytes(8);
		for (int value : intValues) {
			passVarArgs(out, false, (float)value);
			assertEquals(value, out.getDouble(), 0);
		}
	}
	
}
