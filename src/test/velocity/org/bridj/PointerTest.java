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
import org.bridj.cpp.*;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.*;
import java.util.Iterator;
import java.util.Arrays;
import static java.util.Arrays.asList;
import org.bridj.ann.Ptr;
import org.bridj.ann.Library;
import org.bridj.ann.Runtime;
import static org.junit.Assert.*;
import static org.bridj.Pointer.*;

#set($v1 = "(1 << 32 | 1)")
#set($v2 = "(2 << 32 | 2)")
#set($v3 = "(3 << 32 | 3)")

@Library("test")
@Runtime(CPPRuntime.class)
public class PointerTest {
	static {
		BridJ.register();
	}
	int n = 3;
	static final ByteOrder[] orders = new ByteOrder[] { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
    
	@Test
	public void testValidity() {
		Pointer<Short> p = allocateShorts(10), p2 = p.next(2), p2w = p2.withoutValidityInformation();
		Pointer<?> p2u = p2.asUntyped();
		
		assertEquals(p.getPeer() + 4, p2.getPeer());
		assertEquals(p2.getPeer(), p2u.getPeer());
		assertEquals(p2.getPeer(), p2w.getPeer());
		
		assertEquals(p.getValidStart(), p2.getValidStart());
		assertEquals(p.getValidEnd(), p2.getValidEnd());
		
		assertEquals(p.getValidStart(), p2u.getValidStart());
		assertEquals(p.getValidEnd(), p2u.getValidEnd());
		
		assertEquals(p.getIO(), p2w.getIO());
	}
	
	@Test
	public void testRelease() {
		final boolean called[] = new boolean[1]; 
		Pointer<Integer> p = allocateInts(3).withReleaser(new Releaser() {
			public void release(Pointer<?> p) {
				called[0] = true; 
			}
		});
		Pointer p2 = p.offset(1);
		p2.release();
		assertTrue(called[0]);
		Pointer.release(p, p2);
	}
	@Test
	public void testStaticGet() {
		assertNull(Pointer.get(null));
		assertNotNull(Pointer.get(allocateInt()));
	}
	@Test
	public void testMisc() {
		Pointer<Integer> p = allocateInt();
		assertFalse(p.equals(null));
		assertFalse(p.equals("toto"));
		assertSame(p, p.validBytes(p.getValidBytes()));
		assertSame(p, p.offset(0));
		assertEquals(p.getPeer(), p.offset(0, PointerIO.getDoubleInstance()).getPeer());
	}
	@Test(expected = IllegalArgumentException.class)
	public void testNegSize() {
		allocateInts(-1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidValidBytes() {
		allocateBytes(1).validBytes(2);
	}
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInvalidOffset() {
		allocateBytes(1).offset(2);
	}
	@Test
	public void testPointerPointerPointer() {
		Pointer<Integer> p = allocateInt();
		Pointer<Pointer<Integer>> pp = allocatePointer(Integer.class);
		Pointer<Pointer<Pointer<Integer>>> ppp = allocatePointerPointer(Integer.class);
		p.set(10);
		pp.set(p);
		ppp.set(pp);
		assertEquals(10, (int)(Integer)ppp.get().get().get());
	}
	
	@Test
	public void testNext() {
		int n = 3;
		Pointer<Integer> ints = allocateInts(n), p = ints;
		
		for (int i = 0; i < n; i++) {
			p.set(i);
			p = p.next();
		}
		assertEquals(asList(0, 1, 2), ints.asList());
	}
	@Test
	public void testClone() {
		Pointer<Integer> p = pointerToInts(1, 2, 3, 4);
		Pointer<Integer> c = p.clone();
		assertTrue(p.getPeer() != c.getPeer());
		for (int i = 0; i < 4; i++)
			assertEquals(i + 1, (int)c.get(i));
	
		assertEquals(0, p.compareBytes(c, p.getValidBytes()));
	}
	@Test
	public void testIdentities() {
		Pointer<Integer> p = allocateInt();
		assertTrue(p == (Pointer)p.offset(0));
		assertTrue(p == (Pointer)p.next(0));
		assertTrue(p == (Pointer)p.as(p.getIO()));
		assertTrue(p == (Pointer)p.as(p.getIO().getTargetType()));
		assertTrue(p == (Pointer)p.order(p.order()));
	}
	
	@Test
	public void testFind() {
		Pointer<Integer> p = pointerToInts(1, 2, 3, 4);
		assertEquals(null, p.find(null));
		assertEquals(null, p.find(pointerToInts(1, 4)));
		assertEquals(p, p.find(p));
		assertEquals(p.next(2), p.find(pointerToInts(3, 4)));
	}
	@Test
	public void testFindLast() {
		Pointer<Integer> p = pointerToInts(1, 2, 3, 4, 1, 2);
		assertEquals(null, p.findLast(null));
		assertEquals(null, p.findLast(pointerToInts(1, 4)));
		assertEquals(p, p.findLast(p));
		assertEquals(p.next(4), p.findLast(pointerToInts(1, 2)));
	}
	@Test
	public void testList() {
		NativeList<Integer> list = allocateList(int.class, 10);
		assertEquals(asList(), list);
		list.add(10);
		list.add(20);
		assertEquals(asList(10, 20), list);
		list.remove(0);
		assertEquals(asList(20), list);
		list.clear();
		assertEquals(asList(), list);
		
	}
	
	@Test
	public void testDebugBulkPut() {
		long s = BridJ.sizeOf(int.class);
		Pointer<Integer> a = pointerToInts(-1, 2, -3);
		Pointer<Integer> b = allocateInts(4);
		
		b.getByteBufferAtOffset(1 * s, 3 * s).put(a.getByteBuffer());
		
		assertEquals(Arrays.asList(0, -1, 2, -3), b.asList());
	}
	
	@Test
	public void testDebugFloatEndian() {
		float value = 10.0f;
		for (ByteOrder order : new ByteOrder[] { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN }) {
			ByteBuffer b = ByteBuffer.allocateDirect(20).order(order);
			b.asFloatBuffer().put(0, value);
			Pointer<Float> p = allocateFloat().order(order);
			p.setFloat(value);
			
			//System.out.println("Order = " + order);
			//System.out.println(Integer.toHexString(b.asIntBuffer().get())); 
			//System.out.println(Integer.toHexString(p.getInt()));
			assertEquals(value, b.asFloatBuffer().get(0), 0);
			assertEquals(value, p.getFloat(), 0);
			
			assertEquals(value, pointerToBuffer(b).getFloat(), 0);
			assertEquals(value, p.getFloatBuffer().get(), 0);
		}
	}
	
	@Test
	public void assumptionsOnDoubleBuffers() {
		double v = 13579000030.5336163;
		ByteBuffer b = ByteBuffer.allocateDirect(8);
		DoubleBuffer d1 = b.order(ByteOrder.BIG_ENDIAN).asDoubleBuffer();
		DoubleBuffer d2 = b.order(ByteOrder.BIG_ENDIAN).asDoubleBuffer();
		DoubleBuffer d3 = b.order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer();
		
		d1.put(0, v);
		
		LongBuffer l1 = b.order(ByteOrder.BIG_ENDIAN).asLongBuffer();
		LongBuffer l3 = b.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
	
		//System.out.println("ORDER 1 = " + Long.toHexString(l1.get(0)));
		//System.out.println("ORDER 2 = " + Long.toHexString(l3.get(0)));

		assertTrue(d1.get(0) == d2.get(0));
		assertTrue(d1.get(0) != d3.get(0));
	}
	@Test
	public void assumptionsOnDoublePointers() {
		double v = 13579000030.5336163;
		Pointer<Double> b = allocateDouble();
		Pointer<Double> d1 = b.order(ByteOrder.BIG_ENDIAN);
		Pointer<Double> d2 = b.order(ByteOrder.BIG_ENDIAN);
		Pointer<Double> d3 = b.order(ByteOrder.LITTLE_ENDIAN);
		
		d1.set(v);

		Pointer<Long> l1 = b.order(ByteOrder.BIG_ENDIAN).as(Long.class);
		Pointer<Long> l3 = b.order(ByteOrder.LITTLE_ENDIAN).as(Long.class);
	
		//System.out.println("Ptr ORDER 1 = " + Long.toHexString(l1.get(0)));
		//System.out.println("Ptr ORDER 2 = " + Long.toHexString(l3.get(0)));

		assertEquals("d1 = " + d1.get(0) + ", d2 = " + d2.get(0), d1.get(0), d2.get(0), 0.0);
		assertTrue("d1 = " + d1.get(0) + ", d3 = " + d3.get(0), d1.get(0) != d3.get(0));		
	}
	@Test
	public void manualTestDoubleEndian() {
	
		Pointer<Double> b = allocateDouble();
		for (ByteOrder bo : new ByteOrder[] { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN }) {
			b.order(bo).setDouble(10.0);
			assertEquals(10.0, b.order(bo).getDoubles(1)[0], 0);
			assertEquals(10.0, b.order(bo).getDoubleBuffer().get(0), 0);
		}
	}
	@Test(expected=UnsupportedOperationException.class)
	public void noRef() {
		allocateBytes(10).getReference();
	}
	@Test(expected=UnsupportedOperationException.class)
	public void noRemoveIt() {
		Iterator<Byte> it = allocateBytes(10).iterator();
		assertTrue(it.hasNext());
		it.next();
		it.remove();
	}
	@Test(expected=RuntimeException.class)
	public void untypedGet() {
		allocateBytes(10).asUntyped().get(0);
	}
	
	@Test
	public void findByte() {
		Pointer<Byte> p = pointerToBytes((byte)1, (byte)2, (byte)3, (byte)4);
		assertNotNull(p.findByte(0, (byte)2, 4));
		assertNull(p.findByte(0, (byte)5, 4));
		assertNull(p.findByte(0, (byte)2, 1));
	}
	
	@Test
	public void testAligned() {
		Pointer<Integer> p = allocateInts(2);
		assertTrue(p.isAligned());
		assertTrue(!p.offset(1).isAligned());
		assertTrue(!p.offset(2).isAligned());
		assertTrue(p.offset(2).isAligned(2));
		assertTrue(!p.offset(3).isAligned());
		assertTrue(p.offset(4).isAligned());
	}
	@Test
	public void testExplicitAlignment() {
		for (int alignment = 2; alignment < 20; alignment++) {
			Pointer<Integer> p = allocateAlignedArray(int.class, 3, alignment);
			assertEquals(0, p.getPeer() % alignment);
			p.release();
		}
	}
	
	
	
	@Test
	public void iterate() {
		int i = 0;
		for (int v : pointerToInts(0, 1, 2, 3, 4)) {
			assertEquals(i, v);
			i++;
		}
	}
	
	@Test
	public void basicTest() {
		Pointer<Byte> p = allocateBytes(10);
		assertTrue(p == p.offset(0));
		assertEquals(p, p);
		
		assertTrue(!p.equals(p.offset(1)));
		assertEquals(p, p.offset(1).offset(-1));
		
		assertEquals(new Long(p.getPeer()).hashCode(), p.hashCode());
		
		assertEquals(1, p.compareTo(null));
		assertEquals(-1, p.compareTo(p.offset(1)));
		assertEquals(0, p.compareTo(p.offset(1).offset(-1)));
		assertEquals(1, p.offset(1).compareTo(p.offset(1).offset(-1)));
		
		assertTrue(!allocateBytes(10).equals(allocateBytes(10)));
	}
	
	@Test
	public void refTest() {
		Pointer<Pointer<?>> pp = allocatePointers(10);
		Pointer<?> pa = allocateBytes(5);
		pp.set(2, pa);
		Pointer<?> p = pp.get(2);
		assertEquals(p, pa);
		Pointer ref = p.getReference();
		assertNotNull(ref);
		assertEquals(pp.offset(2 * Pointer.SIZE), ref);
	}
	
	void testAlignment(int alignment) {
		for (long byteSize : new long[] { 1, 2, 3, 4, 5, 10, 130 }) {
			Pointer<Integer> p = allocateAlignedBytes(PointerIO.getIntInstance(), byteSize, alignment, null);
			assertTrue(p.isAligned(alignment));
			if (alignment > 1)
				assertTrue((((int)p.getPeer()) % alignment) == 0);
			assertEquals(byteSize, p.getValidBytes());
		}
	}
	
#foreach ($align in [0, 1, 2, 4, 8, 16, 32, 64])
	@Test
	public void testAlignment${align}() {
		testAlignment($align);
	}
#end
	@Test
	public void testDefaultAlignment() {
		testAlignment(-1);
	}
	
#macro (testString $string $eltWrapper)
	@Test
    public void test${string}String() {
    		String s = "Hello, World !";
    		Pointer<$eltWrapper> p = pointerTo${string}String(s);
    		assertEquals(s, p.get${string}String());
	}
#end
#testString("C", "Byte")
#testString("WideC", "Character")

	@Test
    public void testStrings() {
		String s = "Hello, World !";
		String s2 = "Hello you !";
		Charset charset = null;
		for (int offset : new int[] { 0, 1, 4, 10 }) {
			for (Pointer.StringType type : Pointer.StringType.values()) {
				if (!type.canCreate)
					continue;
				
				Pointer<?> p = pointerToString(s, type, charset);
				assertEquals("Failed alloc / set of string type " + type, s, p.getString(type));
				
				p.setString(s2, type);
				assertEquals("Failed set / get of string type " + type, s2, p.getString(type));
			}
		}
	}
	
	/*
	public static native Pointer<?> newString();
	public static native Pointer<?> newWString();

	public static native void deleteWString(Pointer<?> s);
	public static native void appendToWString(Pointer<?> s, Pointer<Character> a);
	public static native void resizeWString(Pointer<?> s, @Ptr long newSize);
	public static native void reserveWString(Pointer<?> s, @Ptr long newCapacity);
	public static native Pointer<Character> wstringCStr(Pointer<?> s);
	
	public static native void deleteString(Pointer<?> s);
	public static native void appendToString(Pointer<?> s, Pointer<Byte> a);
	public static native void resizeString(Pointer<?> s, @Ptr long newSize);
	public static native void reserveString(Pointer<?> s, @Ptr long newCapacity);
	public static native Pointer<Byte> stringCStr(Pointer<?> s);
	
	@Test
	public void stlTestTest() {
		//if (true) return;
		String s1 = "Test !";
		String s2 = "Test, yeah man ! Test, yeah man 2 ! Test, yeah man 3 !";
		Pointer<?> p = newString();
		System.err.println("Created new string : " + p);
		appendToString(p, pointerToCString(s1));
		assertEquals(s1, stringCStr(p).getCString());
		System.out.println("Created string just fine !");
		
		resizeString(p, 0);
		appendToString(p, pointerToCString(s2));
		assertEquals(s2, stringCStr(p).getCString());
	}
	*/

#foreach ($prim in $bridJPrimitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end

	@Test
	public void testAllocateArrayPrim_${prim.Name}() {
		for (int type = 0; type < 4; type++) {
			Pointer ptr = null;
			switch (type) {
			case 0:
				ptr = Pointer.allocate${prim.CapName}();
				break;
			case 1:
				ptr = Pointer.allocate${prim.CapName}s(2);
				break;
			case 2: 
				ptr = Pointer.allocateArray(${prim.Name}.class, 2);
				break;
			case 3: 
				ptr = Pointer.allocateArray(${prim.WrapperName}.class, 2);
				break;
			};
			assertTrue("approach " + type + " failed", ptr.getIO() == (PointerIO)PointerIO.get${prim.CapName}Instance());
			assertTrue("approach " + type + " failed", ptr.getIO() == (PointerIO)PointerIO.getInstance(${prim.Name}.class));
		}
	}
#end
	
	Pointer<?> someUntypedPtr() {
		Pointer<Integer> p = allocateInt();
		return pointerToAddress(p.getPeer());
	}
	@Test(expected=RuntimeException.class)
	public void testUntypedSize() {
		someUntypedPtr().getTargetSize();
	}
	@Test(expected=RuntimeException.class)
	public void testUntypedNext() {
		someUntypedPtr().next();
	}
	@Test(expected=RuntimeException.class)
	public void testUntypedGetArray() {
		someUntypedPtr().getArray();
	}
	@Test(expected=RuntimeException.class)
	public void testUntypedGetBuffer() {
		someUntypedPtr().getBuffer();
	}
	
	@Test
	public void testPointerToNulls() {
		assertEquals(0, getPeer(null));
		assertEquals(null, allocateBytes(null, 0, null));
		assertEquals(null, allocateArray(int.class, 0));
		assertEquals(null, pointerToAddress(0));
		assertEquals(null, pointerToArray(null));
		assertEquals(null, pointerToBuffer(null));
		
#foreach ($prim in $bridJPrimitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end
		{
			assertTrue(pointerTo${prim.CapName}s((${prim.Name}[])null) == null);
#if ($prim.Name == "SizeT" || $prim.Name == "CLong")
			assertTrue(pointerTo${prim.CapName}s((int[])null) == null);
			assertTrue(pointerTo${prim.CapName}s((long[])null) == null);
#end	
		}
#end
#foreach ($prim in $primitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end
		{
			// TODO implement 2D and 3D arrays for CLong, SizeT !
			assertTrue(pointerTo${prim.CapName}s((${prim.Name}[][])null) == null);
			assertTrue(pointerTo${prim.CapName}s((${prim.Name}[][][])null) == null);
		}
#end
#foreach ($prim in $primitivesNoBool)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end
		{
			assertTrue(pointerTo${prim.CapName}s((${prim.BufferName})null) == null);
		}
#end
	}

	@Test
	public void testSignedIntegrals() {
		long value = 124;
		for (int sizeof : new int[] { 1, 2, 4, 8 }) {
			Pointer p = allocateBytes(sizeof);
			p.setSignedIntegralAtOffset(0, value, sizeof);
			assertEquals(value, p.getSignedIntegralAtOffset(0, sizeof));
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void testUpdateDifferentDirectBuffer() {
		ByteBuffer b = ByteBuffer.allocateDirect(4);
		Pointer p = pointerToBytes(b);
		p.updateBuffer(ByteBuffer.allocateDirect(4));
	}
	@Test(expected = RuntimeException.class)
	public void testUpdateDirectBufferOnNonBufferBoundPointer() {
		allocateInt().updateBuffer(ByteBuffer.allocateDirect(4));
	}
	
#foreach ($prim in $bridJPrimitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end

#if ($prim.Name == "double" || $prim.Name == "float")
#set ($precisionArg = ", (" + $prim.Name + ")0.00001")
#else
#set ($precisionArg = "")
#end

#if ($prim.Name != "SizeT" && $prim.Name != "CLong" && $prim.Name != "Pointer" && $prim.Name != "boolean" && $prim.Name != "char")	

	
	@Test
	public void testCopy${prim.CapName}s() {
		$prim.Name 
			zero = ${prim.value("0")}, 
			one = ${prim.value("1")}, 
			two = ${prim.value("2")},
			three = ${prim.value("3")};
		
		Pointer<${prim.WrapperName}> src = pointerTo${prim.CapName}s(one, two, three);
		Pointer<${prim.WrapperName}> dest = allocate${prim.CapName}s(4);
		
		dest.clearValidBytes();
		src.copyTo(dest, 1);
		assertEquals(one, (${prim.Name})dest.get(0)$precisionArg);
		assertEquals(zero, (${prim.Name})dest.get(1)$precisionArg);
		assertEquals(zero, (${prim.Name})dest.get(2)$precisionArg);
		assertEquals(zero, (${prim.Name})dest.get(3)$precisionArg);
		
		dest.clearValidBytes();
		src.copyTo(dest, 2);
		assertEquals(one, (${prim.Name})dest.get(0)$precisionArg);
		assertEquals(two, (${prim.Name})dest.get(1)$precisionArg);
		assertEquals(zero, (${prim.Name})dest.get(2)$precisionArg);
		assertEquals(zero, (${prim.Name})dest.get(3)$precisionArg);
		
		dest.clearValidBytes();
		src.copyTo(dest);
		assertEquals(one, (${prim.Name})dest.get(0)$precisionArg);
		assertEquals(two, (${prim.Name})dest.get(1)$precisionArg);
		assertEquals(three, (${prim.Name})dest.get(2)$precisionArg);
		assertEquals(zero, (${prim.Name})dest.get(3)$precisionArg);
	}
	
	@Test
	public void test${prim.BufferName}Update() {
		// Non-direct buffer
		${prim.BufferName} b = ${prim.BufferName}.allocate(1);
		Pointer<	${prim.WrapperName}> p = pointerTo${prim.CapName}s(b);
		for (${prim.Name} value : new ${prim.Name}[] { ${prim.value($v1)}, ${prim.value($v2)}, ${prim.value($v3)} }) { 
			p.set(value);
			assertEquals(value, (${prim.Name})p.get()$precisionArg);
			p.updateBuffer(b);
			assertEquals(value, b.get(0)$precisionArg);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegal${prim.BufferName}() {
		allocate${prim.CapName}().set${prim.CapName}sAtOffset(0, (${prim.BufferName})null, 0, 0);
	}
	@Test
	public void setBufferSet${prim.CapName}s() {
		for (${prim.Name} value : new ${prim.Name}[] { ${prim.value("-1")}, ${prim.value("-2")}, ${prim.value("0")}, ${prim.value($v1)} }) {
		//for (${prim.Name} value : new ${prim.Name}[] { ${prim.value($v1)}, ${prim.value($v2)}, ${prim.value($v3)} }) {
			Pointer<${prim.WrapperName}> p = allocateBytes(${primSize}).as(${prim.WrapperName}.class);
			ByteBuffer bb = ByteBuffer.allocateDirect(${primSize}).order(ByteOrder.nativeOrder());
			assertEquals(bb.order(), p.order());
			bb = bb.order(ByteOrder.LITTLE_ENDIAN);
			${prim.BufferName} b;
			#if ($prim.Name == "byte")
			b = bb;
			#else
			b = bb.as${prim.BufferName}();
			#end
			b.put(0, value);
			p.set${prim.CapName}s(b);
			${prim.BufferName} b2 = p.get${prim.BufferName}();
			${prim.Name} gotBuf = b2.get(0), gotPtr = p.get();
			assertEquals(value, gotBuf$precisionArg);
			assertEquals(value, gotPtr$precisionArg);
		}
	}
	
#end
	
#if ($prim.Name == "SizeT" || $prim.Name == "CLong")
#set ($rawType = "long")
#set ($rawCapName = "Long")
#else
#set ($rawType = $prim.Name)
#set ($rawCapName = $prim.CapName)
#end

	static ${prim.Name}[] createExpected${prim.CapName}s(int n) {
		${prim.Name}[] expected = new ${prim.Name}[n];
		expected[0] = ${prim.value($v1)};
		expected[1] = ${prim.value($v2)};
		expected[2] = ${prim.value($v3)};
		//for (int i = 0; i < n; i++)
		//	expected[i] = (${prim.Name})(i + 1);
		return expected;
	}
	
	@Test 
    public void test${prim.CapName}sIterator() {
		${prim.Name}[] expected = createExpected${prim.CapName}s(n);
		Pointer<${prim.typeRef}> p = Pointer.pointerTo${prim.CapName}s(expected);
		long peer = p.getPeer();
		
		Iterator<${prim.typeRef}> it = p.iterator();
		for (int i = 0; i < n; i++) {
			assertTrue(it.hasNext());
			${prim.typeRef} obVal = it.next();
			assertNotNull(obVal);
			${prim.Name} val = obVal;
			assertEquals("at position i = " + i, (Object)expected[i], (Object)val);
		}
		assertTrue(!it.hasNext());
	}
	
	@Test 
    public void testPointerTo_${prim.Name}_Values() {
		// Test pointerToInts(int...)
		Pointer<${prim.rawTypeRef}> p = Pointer.pointerTo${prim.CapName}s(${prim.value($v1)}, ${prim.value($v2)}, ${prim.value($v3)});
		assertEquals(${prim.value($v1)}, (${prim.Name})p.get(0)$precisionArg);
		assertEquals(${prim.value($v2)}, (${prim.Name})p.get(1)$precisionArg);
		assertEquals(${prim.value($v3)}, (${prim.Name})p.get(2)$precisionArg);
		
		p = Pointer.pointerTo${prim.CapName}s(${prim.rawValue($v1)}, ${prim.rawValue($v2)}, ${prim.rawValue($v3)});
		assertEquals(${prim.rawValue($v1)}, p.get${prim.CapName}AtOffset(0 * ${primSize})$precisionArg);
		assertEquals(${prim.rawValue($v2)}, p.get${prim.CapName}AtOffset(1 * ${primSize})$precisionArg);
		assertEquals(${prim.rawValue($v3)}, p.get${prim.CapName}AtOffset(2 * ${primSize})$precisionArg);
		assertEquals(${prim.rawValue($v1)}, p.get${prim.CapName}AtIndex(0)$precisionArg);
		assertEquals(${prim.rawValue($v2)}, p.get${prim.CapName}AtIndex(1)$precisionArg);
		assertEquals(${prim.rawValue($v3)}, p.get${prim.CapName}AtIndex(2)$precisionArg);
		
		${rawType}[] arr = p.get${prim.CapName}s();
		assertEquals(${prim.rawValue($v1)}, arr[0]$precisionArg);
		assertEquals(${prim.rawValue($v2)}, arr[1]$precisionArg);
		assertEquals(${prim.rawValue($v3)}, arr[2]$precisionArg);
		
	}
	@Test 
    public void testPointerTo_${prim.Name}_Value() {
		Pointer<${prim.rawTypeRef}> p = Pointer.pointerTo${prim.CapName}(${prim.value($v1)});
		assertEquals(${prim.value($v1)}, (${prim.Name})p.get(0)$precisionArg);
		
		p = Pointer.pointerTo${prim.CapName}(${prim.rawValue($v1)});
		assertEquals(${prim.rawValue($v1)}, p.get${prim.CapName}()$precisionArg);
	}

	
	@Test 
    public void testGet${prim.CapName}s() {
	
    		for (int type = 0; type <= 5; type++) {
			Pointer<${prim.typeRef}> p = Pointer.allocate${prim.CapName}s(n);
			${rawType}[] expected = createExpected${rawCapName}s(n);
			${rawType}[] values = null;
			
			switch (type) {
			case 0:
				p.set${prim.CapName}s(expected);
				values = p.get${prim.CapName}s();
				break;
			case 1:
				p.set${prim.CapName}sAtOffset(0, expected);
				values = p.get${prim.CapName}s();
				break;
			case 2:
				p.set${prim.CapName}sAtOffset(0, expected, 0, expected.length);
				values = p.get${prim.CapName}sAtOffset(0, expected.length);
				break;
			case 3:
				values = new ${rawType}[n];
				for (int i = 0; i < n; i++) {
#if ($prim.Name == "SizeT" || $prim.Name == "CLong")
					p.set(i, new ${prim.Name}(expected[i]));
					values[i] = p.get(i).longValue(); 
#else
					p.set(i, expected[i]);
					values[i] = p.get(i); 
#end
				}
				break;
			case 4:
				values = new ${rawType}[n];
				for (int i = 0; i < n; i++) {
					p.set${prim.CapName}AtOffset(i * ${primSize}, expected[i]);
					values[i] = p.get${prim.CapName}AtOffset(i * ${primSize}); 
				}
				break;
			case 5:
				values = new ${rawType}[n];
				for (int i = 0; i < n; i++) {
					p.set${prim.CapName}AtIndex(i, expected[i]);
					values[i] = p.get${prim.CapName}AtIndex(i); 
				}
				break;
			}
			assertNotNull(values);
			assertEquals("approach " + type + " failed", expected.length, values.length);
			for (int i = 0; i < n; i++) {
				assertEquals("approach " + type + " failed", expected[i], values[i]$precisionArg);
			}
		}
	}
	
	#foreach ($order in [ "LITTLE_ENDIAN", "BIG_ENDIAN"])
	@Test 
    public void simpleSetGet${prim.CapName}s_$order() {
    		Pointer<${prim.typeRef}> p = allocate${prim.CapName}s(3).order(ByteOrder.$order);

			p.set(2, ${prim.value($v3)});
			assertEquals(${prim.value($v3)}, (${prim.Name})p.get(2)$precisionArg);
			
			p.set${prim.CapName}(${prim.value($v1)});
			assertEquals(${prim.rawValue($v1)}, ($rawType)p.get${prim.CapName}()$precisionArg);
			
			p.set${prim.CapName}AtOffset(${primSize}, ${prim.value("-2")});
			assertEquals(${prim.rawValue("-2")}, ($rawType)p.get${prim.CapName}AtOffset(${primSize})$precisionArg);
			
			p.set${prim.CapName}sAtOffset(${primSize}, new ${prim.Name}[] { ${prim.value("5")}, ${prim.value("6")} });
			assertEquals(${prim.value("5")}, (${prim.Name})p.get(1)$precisionArg);
			assertEquals(${prim.value("6")}, (${prim.Name})p.get(2)$precisionArg);
			${rawType}[] a = p.get${prim.CapName}sAtOffset(${primSize}, 2);
			assertEquals(2, a.length);
			assertEquals(${prim.rawValue("5")}, a[0]$precisionArg);
			assertEquals(${prim.rawValue("6")}, a[1]$precisionArg);
	}
	#end
	
	@Test
	public void testPointerToArray_${prim.Name}() {
		${prim.Name}[] original = new ${prim.Name}[] { ${prim.value($v1)}, ${prim.value($v2)}, ${prim.value($v3)} };
		Pointer<${prim.typeRef}> p = pointerToArray(original);
		assertEquals(3, p.getValidElements());
		assertEquals(${prim.value($v1)}, (${prim.Name})p.get(0)$precisionArg);
		assertEquals(${prim.value($v2)}, (${prim.Name})p.get(1)$precisionArg);
		assertEquals(${prim.value($v3)}, (${prim.Name})p.get(2)$precisionArg);
		
		${prim.Name}[] values = (${prim.Name}[])p.getArray();
		assertEquals(original.length, values.length);
		for (int i = 0; i < original.length; i++)
			assertEquals(original[i], values[i]$precisionArg);
	}
	
#end

#foreach ($prim in $primitivesNoBool)	
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end

#if ($prim.Name == "double" || $prim.Name == "float")
#set ($precisionArg = ", 0")
#else
#set ($precisionArg = "")
#end
	
	@Test 
    public void testAllocateBounds_${prim.Name}_ok() {
		assertEquals(${prim.value("0")}, (${prim.Name})Pointer.allocate${prim.CapName}().get(0)$precisionArg);
		assertEquals(${prim.value("0")}, (${prim.Name})Pointer.allocate${prim.CapName}s(1).get(0)$precisionArg);
		assertEquals(${prim.value("0")}, (${prim.Name})Pointer.allocate${prim.CapName}s(2).offset(${primSize}).get(-1)$precisionArg);
		
		//TODO slide, slideBytes
	}
	
	//@Test(expected=UnsupportedOperationException.class)
	public void testPointerTo_${prim.Name}_IndirectBuffer() {
		${prim.BufferName} b = ${prim.BufferName}.wrap(new ${prim.Name}[3]);
		b.put(0, ${prim.value($v1)});
		b.put(1, ${prim.value($v2)});
		b.put(2, ${prim.value($v3)});
		
		Pointer<${prim.typeRef}> p;
		
		for (boolean generic : new boolean[] { false, true }) {
			if (generic)
				p = (Pointer<${prim.typeRef}>)Pointer.pointerToBuffer(b);
			else
				p = Pointer.pointerTo${prim.CapName}s(b);
			
			assertEquals(3 * ${primSize}, p.getValidBytes());
			assertEquals(${prim.value($v1)}, (${prim.Name})p.get(0)$precisionArg);
			assertEquals(${prim.value($v2)}, (${prim.Name})p.get(1)$precisionArg);
			assertEquals(${prim.value($v3)}, (${prim.Name})p.get(2)$precisionArg);
		}
		
		p = (Pointer<${prim.typeRef}>)Pointer.pointerToBuffer(b);
		
		p.set(1, ${prim.value("22")});
		p.updateBuffer(b);
		assertEquals(${prim.value("22")}, (${prim.Name})b.get(1)$precisionArg);
	}
	
	#if ($prim.Name != "char")
	
	@Test 
    public void testGet${prim.BufferName}s() {
		for (int type = 0; type < 6; type++) {
			Pointer<${prim.typeRef}> p = Pointer.allocate${prim.CapName}s(n);
			${prim.Name}[] expected = createExpected${prim.CapName}s(n);
			${prim.BufferName} buf = ${prim.BufferName}.wrap(expected);
			${prim.BufferName} values = null;
			
			switch (type) {
			case 0:
				p.setValues(buf);
				values = (${prim.BufferName})p.getBuffer();
				break;
			case 1:
				p.setValuesAtOffset(0, buf);
				values = (${prim.BufferName})p.getBuffer();
				break;
			case 2:
				p.setValuesAtOffset(0, buf, 0, n);
				values = (${prim.BufferName})p.getBufferAtOffset(0, n);
				break;
			case 3:
				p.set${prim.CapName}s(buf);
				values = p.get${prim.BufferName}();
				break;
			case 4:
				p.set${prim.CapName}sAtOffset(0, buf);
				values = p.get${prim.BufferName}();
				break;
			case 5:
				p.set${prim.CapName}sAtOffset(0, buf, 0, n);
				values = p.get${prim.BufferName}AtOffset(0, n);
				break;
			}
			assertEquals("approach " + type + " failed", n, values.capacity());
			
			for (int i = 0; i < n; i++) {
				assertEquals("approach " + type + " failed", expected[i], values.get(i)$precisionArg);
			}
		}
	}
	
	@Test 
    public void testPointerTo_${prim.Name}_DirectBuffer() {
    		Pointer<${prim.typeRef}> p = Pointer.allocate${prim.CapName}s(3);
    		assertEquals(3 * ${primSize}, p.getValidBytes());
		p.set(0, ${prim.value($v1)});
		p.set(1, ${prim.value($v2)});
		p.set(2, ${prim.value($v3)});
		${prim.BufferName} b = p.get${prim.BufferName}();
		assertEquals(3, b.capacity());
		
		for (boolean generic : new boolean[] { false, true }) {
			if (generic)
				p = (Pointer<${prim.typeRef}>)Pointer.pointerToBuffer(b);
			else
				p = Pointer.pointerTo${prim.CapName}s(b);
			
			assertEquals(3 * ${primSize}, p.getValidBytes());
			assertEquals(${prim.value($v1)}, (${prim.Name})p.get(0)$precisionArg);
			assertEquals(${prim.value($v2)}, (${prim.Name})p.get(1)$precisionArg);
			assertEquals(${prim.value($v3)}, (${prim.Name})p.get(2)$precisionArg);
		}
	}
	#end
	
	public void testPointerTo_${prim.Name}_Values2D(${prim.Name}[][] values, Pointer<Pointer<${prim.typeRef}>> p, int dim1, int dim2) {
		for (int i = 0; i < dim1; i++)
			for (int j = 0; j < dim2; j++)
				assertEquals(values[i][j], (${prim.Name})p.get(i).get(j)$precisionArg);
	}
	@Test 
    public void testPointerTo_${prim.Name}_Values2D() {
		${prim.Name}[][] values = new ${prim.Name}[][] {
				{${prim.value($v1)}, ${prim.value($v2)}},
				{${prim.value($v1)}, ${prim.value($v2)}},
				{${prim.value($v1)}, ${prim.value($v2)}}
		};
		Pointer<Pointer<${prim.typeRef}>> p = Pointer.pointerTo${prim.CapName}s(values);
		int dim1 = values.length;
		int dim2 = values[0].length;
		testPointerTo_${prim.Name}_Values2D(values, p, dim1, dim2);
		
		Pointer<Pointer<${prim.typeRef}>> p2 = allocate${prim.CapName}s(dim1, dim2);
		p.copyTo(p2);
		testPointerTo_${prim.Name}_Values2D(values, p2, dim1, dim2);
	}
	
	@Test 
    public void testPointerTo_${prim.Name}_Values3D() {
		${prim.Name}[][][] values = new ${prim.Name}[][][] {
			{
				{${prim.value($v1)}, ${prim.value($v2)}},
				{${prim.value($v1)}, ${prim.value($v2)}},
				{${prim.value($v1)}, ${prim.value($v2)}}
			},
			{
				{${prim.value($v1)}, ${prim.value($v2)}},
				{${prim.value($v1)}, ${prim.value($v2)}},
				{${prim.value($v1)}, ${prim.value($v2)}}
			}
		};
		Pointer<Pointer<Pointer<${prim.typeRef}>>> ppp = Pointer.pointerTo${prim.CapName}s(values);
		int dim1 = values.length;
		int dim2 = values[0].length;
		int dim3 = values[0][0].length;
		
		int subSize = dim2 * dim3 * ${primSize};
		for (int i = 0; i < dim1; i++) {
			for (int j = 0; j < dim2; j++) {
				for (int k = 0; k < dim3; k++) {
					Object o = values[i][j][k];
					//System.out.println(o);
					
					Pointer<Pointer<${prim.typeRef}>> pp = ppp.get(i);
					assertEquals(i * subSize, pp.getPeer() - ppp.getPeer());
					
					Pointer<${prim.typeRef}> p = pp.get(j);
					${prim.Name} value = p.get(k);
					${prim.Name} expected = values[i][j][k];
					
					//System.out.println("ppp.get(i) = " + pp);
					//System.out.println("ppp.get(i).get(j) = " + p);
					//System.out.println("ppp.get(i).get(j).get(k) = " + value);
					assertEquals("expected " + expected + ", got " + value + " (i = " + i + ", j = " + j + ", k = " + k + ")", expected, value$precisionArg);
				}
			}
		}
	}
	
	@Test 
    public void testAllocateRemaining_${prim.Name}_ok() {
    	Pointer<${prim.typeRef}> p = Pointer.allocate${prim.CapName}s(2);
    	assertEquals(2, p.getValidElements());
		assertEquals(2 * ${primSize}, p.getValidBytes());
		
		Pointer<${prim.typeRef}> n = p.next();
		Pointer<${prim.typeRef}> o = p.offset(${primSize});
		assertEquals(n, o);
		
		assertEquals(1, n.getValidElements());
		assertEquals(${primSize}, n.getValidBytes());
		assertEquals(1, o.getValidElements());
		assertEquals(${primSize}, o.getValidBytes());
		
		//TODO slide, slideBytes
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
    public void testAllocateBounds_${prim.Name}_failAfter() {
		Pointer.allocate${prim.CapName}().get(1);
	}
	@Test(expected=IndexOutOfBoundsException.class)
    public void testAllocateBounds_${prim.Name}_failBefore() throws IndexOutOfBoundsException {
		Pointer.allocate${prim.CapName}().get(-1);
	}
	

	@Test
    public void test${prim.CapName}Order() {
    	for (ByteOrder order : orders) {
    		boolean isOrdered = order.equals(ByteOrder.nativeOrder());
    		Pointer<${prim.typeRef}> p = Pointer.allocate${prim.CapName}().order(order);
    		assertEquals(order, p.order());
    		assertEquals(isOrdered, p.isOrdered());
    	}
    }
    #if (($prim.Name == "short") || ($prim.Name == "int") || ($prim.Name == "long") || ($prim.Name == "double") || ($prim.Name == "float"))
    
   	#foreach ($order in [ "LITTLE_ENDIAN", "BIG_ENDIAN"])

	@Test
	public void test${prim.CapName}_Endianness_$order() {
		for (${prim.Name} value : new ${prim.Name}[] { ${prim.value($v1)}, ${prim.value("-1")}, ${prim.value("-2")}, ${prim.value("0")} }) {
			Pointer<${prim.typeRef}> p = Pointer.allocate${prim.CapName}().order(ByteOrder.$order);
			p.set(value);
		    assertEquals(ByteOrder.$order, p.order());
		    assertEquals(ByteOrder.$order, p.get${prim.BufferName}AtOffset(0, 1).order());
		    assertEquals((${prim.Name})p.get${prim.BufferName}AtOffset(0, 1).get(), p.getByteBufferAtOffset(0, ${primSize}).order(ByteOrder.$order).as${prim.BufferName}().get()$precisionArg); // always true (?) : NIO consistency
		    
			assertEquals(value, (${prim.Name})p.get${prim.BufferName}AtOffset(0, 1).get()$precisionArg); // check that the NIO buffer was created with the correct order by default
			assertEquals(value, p.getByteBufferAtOffset(0, ${primSize}).order(ByteOrder.$order).as${prim.BufferName}().get()$precisionArg);
		}
	}
	#end
	#end
#end
}
