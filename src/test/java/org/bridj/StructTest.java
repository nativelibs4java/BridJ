package org.bridj;

import com.sun.jna.Memory;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Array;
import org.bridj.ann.Ptr;
import org.bridj.ann.Struct;
import org.bridj.ann.Optional;
import org.bridj.cpp.com.*;
import static org.bridj.Pointer.*;
import static org.bridj.BridJ.*;
import java.util.List;

import javolution.io.*;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html

@Library("test")
public class StructTest {
	static {
		BridJ.register();
	}

	@Optional
	public static native @Ptr long sizeOfVARIANT();
	@Optional
	public static native @Ptr long sizeOfDECIMAL();
	@Optional
	public static native @Ptr long sizeOfCAPDRIVERCAPS();


	@Test
	public void guidSize() {
        long s = BridJ.sizeOf(GUID.class);
		assertEquals(16, s);
	}

	@Test
	public void variantSize() {
		if (!Platform.isWindows())
			return;
		
		long s = BridJ.sizeOf(VARIANT.class);
            assertEquals(sizeOfVARIANT(), s);
	}
	
	@Test
	public void decimalSize() {
        long s = BridJ.sizeOf(DECIMAL.class);
		if (Platform.isWindows())
            assertEquals(sizeOfDECIMAL(), s);
        else
            assertEquals(16, s);
	}
	
	@Test
	public void testSizeOfCAPDRIVERCAPS() {
		long s = BridJ.sizeOf(CAPDRIVERCAPS.class);
		if (Platform.isUnix()) {
			if (Platform.is64Bits())
				assertEquals(64, s);
			else
				assertEquals(44, s);
		} else if (Platform.isWindows())
			assertEquals(sizeOfCAPDRIVERCAPS(), s); 
	}

	public class CAPDRIVERCAPS extends StructObject {
		public CAPDRIVERCAPS() {
			super();
		}
		public CAPDRIVERCAPS(Pointer pointer) {
			super(pointer);
		}
		/// C type : UINT
		@Field(0) 
		public int wDeviceIndex() {
			return this.io.getIntField(this, 0);
		}
		/// C type : UINT
		@Field(0) 
		public CAPDRIVERCAPS wDeviceIndex(int wDeviceIndex) {
			this.io.setIntField(this, 0, wDeviceIndex);
			return this;
		}
		/// C type : UINT
		public final int wDeviceIndex_$eq(int wDeviceIndex) {
			wDeviceIndex(wDeviceIndex);
			return wDeviceIndex;
		}
		@Field(1) 
		public int fHasOverlay() {
			return this.io.getIntField(this, 1);
		}
		@Field(1) 
		public CAPDRIVERCAPS fHasOverlay(int fHasOverlay) {
			this.io.setIntField(this, 1, fHasOverlay);
			return this;
		}
		public final int fHasOverlay_$eq(int fHasOverlay) {
			fHasOverlay(fHasOverlay);
			return fHasOverlay;
		}
		@Field(2) 
		public int fHasDlgVideoSource() {
			return this.io.getIntField(this, 2);
		}
		@Field(2) 
		public CAPDRIVERCAPS fHasDlgVideoSource(int fHasDlgVideoSource) {
			this.io.setIntField(this, 2, fHasDlgVideoSource);
			return this;
		}
		public final int fHasDlgVideoSource_$eq(int fHasDlgVideoSource) {
			fHasDlgVideoSource(fHasDlgVideoSource);
			return fHasDlgVideoSource;
		}
		@Field(3) 
		public int fHasDlgVideoFormat() {
			return this.io.getIntField(this, 3);
		}
		@Field(3) 
		public CAPDRIVERCAPS fHasDlgVideoFormat(int fHasDlgVideoFormat) {
			this.io.setIntField(this, 3, fHasDlgVideoFormat);
			return this;
		}
		public final int fHasDlgVideoFormat_$eq(int fHasDlgVideoFormat) {
			fHasDlgVideoFormat(fHasDlgVideoFormat);
			return fHasDlgVideoFormat;
		}
		@Field(4) 
		public int fHasDlgVideoDisplay() {
			return this.io.getIntField(this, 4);
		}
		@Field(4) 
		public CAPDRIVERCAPS fHasDlgVideoDisplay(int fHasDlgVideoDisplay) {
			this.io.setIntField(this, 4, fHasDlgVideoDisplay);
			return this;
		}
		public final int fHasDlgVideoDisplay_$eq(int fHasDlgVideoDisplay) {
			fHasDlgVideoDisplay(fHasDlgVideoDisplay);
			return fHasDlgVideoDisplay;
		}
		@Field(5) 
		public int fCaptureInitialized() {
			return this.io.getIntField(this, 5);
		}
		@Field(5) 
		public CAPDRIVERCAPS fCaptureInitialized(int fCaptureInitialized) {
			this.io.setIntField(this, 5, fCaptureInitialized);
			return this;
		}
		public final int fCaptureInitialized_$eq(int fCaptureInitialized) {
			fCaptureInitialized(fCaptureInitialized);
			return fCaptureInitialized;
		}
		@Field(6) 
		public int fDriverSuppliesPalettes() {
			return this.io.getIntField(this, 6);
		}
		@Field(6) 
		public CAPDRIVERCAPS fDriverSuppliesPalettes(int fDriverSuppliesPalettes) {
			this.io.setIntField(this, 6, fDriverSuppliesPalettes);
			return this;
		}
		public final int fDriverSuppliesPalettes_$eq(int fDriverSuppliesPalettes) {
			fDriverSuppliesPalettes(fDriverSuppliesPalettes);
			return fDriverSuppliesPalettes;
		}
		/// C type : HANDLE
		@Field(7) 
		public Pointer<? > hVideoIn() {
			return this.io.getPointerField(this, 7);
		}
		/// C type : HANDLE
		@Field(7) 
		public CAPDRIVERCAPS hVideoIn(Pointer<? > hVideoIn) {
			this.io.setPointerField(this, 7, hVideoIn);
			return this;
		}
		/// C type : HANDLE
		public final Pointer<? > hVideoIn_$eq(Pointer<? > hVideoIn) {
			hVideoIn(hVideoIn);
			return hVideoIn;
		}
		/// C type : HANDLE
		@Field(8) 
		public Pointer<? > hVideoOut() {
			return this.io.getPointerField(this, 8);
		}
		/// C type : HANDLE
		@Field(8) 
		public CAPDRIVERCAPS hVideoOut(Pointer<? > hVideoOut) {
			this.io.setPointerField(this, 8, hVideoOut);
			return this;
		}
		/// C type : HANDLE
		public final Pointer<? > hVideoOut_$eq(Pointer<? > hVideoOut) {
			hVideoOut(hVideoOut);
			return hVideoOut;
		}
		/// C type : HANDLE
		@Field(9) 
		public Pointer<? > hVideoExtIn() {
			return this.io.getPointerField(this, 9);
		}
		/// C type : HANDLE
		@Field(9) 
		public CAPDRIVERCAPS hVideoExtIn(Pointer<? > hVideoExtIn) {
			this.io.setPointerField(this, 9, hVideoExtIn);
			return this;
		}
		/// C type : HANDLE
		public final Pointer<? > hVideoExtIn_$eq(Pointer<? > hVideoExtIn) {
			hVideoExtIn(hVideoExtIn);
			return hVideoExtIn;
		}
		/// C type : HANDLE
		@Field(10) 
		public Pointer<? > hVideoExtOut() {
			return this.io.getPointerField(this, 10);
		}
		/// C type : HANDLE
		@Field(10) 
		public CAPDRIVERCAPS hVideoExtOut(Pointer<? > hVideoExtOut) {
			this.io.setPointerField(this, 10, hVideoExtOut);
			return this;
		}
		/// C type : HANDLE
		public final Pointer<? > hVideoExtOut_$eq(Pointer<? > hVideoExtOut) {
			hVideoExtOut(hVideoExtOut);
			return hVideoExtOut;
		}
	}
	public class Simple extends StructObject {
		public Simple() {
			super();
		}
		public Simple(Pointer pointer) {
			super(pointer);
		}
		@Field(0) 
		public boolean a() {
			return this.io.getBooleanField(this, 0);
		}
		@Field(0) 
		public Simple a(boolean a) {
			this.io.setBooleanField(this, 0, a);
			return this;
		}
		public final boolean a_$eq(boolean a) {
			a(a);
			return a;
		}
		@Field(1) 
		public boolean b() {
			return this.io.getBooleanField(this, 1);
		}
		@Field(1) 
		public Simple b(boolean b) {
			this.io.setBooleanField(this, 1, b);
			return this;
		}
		public final boolean b_$eq(boolean b) {
			b(b);
			return b;
		}
		/// C type : void*
		@Field(2) 
		public Pointer<? > i() {
			return this.io.getPointerField(this, 2);
		}
		/// C type : void*
		@Field(2) 
		public Simple i(Pointer<? > i) {
			this.io.setPointerField(this, 2, i);
			return this;
		}
		/// C type : void*
		public final Pointer<? > i_$eq(Pointer<? > i) {
			i(i);
			return i;
		}
	}
	
	@Test
	public void testSizeOfSimple() {
		if (Platform.is64Bits())
			assertEquals(16, BridJ.sizeOf(Simple.class));
		else
			assertEquals(8, BridJ.sizeOf(Simple.class));
	}

	public static class MyStruct extends StructObject {
        public MyStruct(Pointer<MyStruct> p) { super(p); }
        public MyStruct() { super(); }
        @Field(0)
		public int a() {
			return io.getIntField(this, 0);
        }
        public MyStruct a(int a) {
            io.setIntField(this, 0, a);
            return this;
        }
        
        @Field(1)
		public double b() {
			return io.getDoubleField(this, 1);
        }
        public MyStruct b(double b) {
            io.setDoubleField(this, 1, b);
            return this;
        }
	}

    public static class TestStruct extends MyStruct {
        public TestStruct(Pointer<MyStruct> p) { super(p); }
        public TestStruct() { super(); }

        @Array(10)
        @Field(2)
		public Pointer<Integer> values() {
			return io.getPointerField(this, 2);
        }

        @Field(3)
        public MyStruct sub() {
			return io.getNativeObjectField(this, 3);
        }
    }

	
	public static class TestStructWithFields extends StructObject {
        public TestStructWithFields(Pointer p) { super(p); }
        public TestStructWithFields() { super(); }
        @Field(0)
		public int a;
        
        @Field(1)
		public double b;

        @Array(10)
        @Field(2)
		public Pointer<Integer> values;

        @Field(3)
        public MyStruct sub;
	}
	
	@Test
	public void testJavaFieldStructs() {
		assertEquals(BridJ.sizeOf(TestStruct.class), BridJ.sizeOf(TestStructWithFields.class));
		
		TestStruct s = new TestStruct();
		
		s.a(10);
		s.b(20);
		
		TestStructWithFields fs = new TestStructWithFields(pointerTo(s));

        assertEquals(s.a(), 10); // no modification of original struct
		assertEquals(s.b(), 20, 0);
		assertEquals(s.a(), fs.a); // read fields upon creation
		assertEquals(s.b(), fs.b, 0);

		assertEquals(pointerTo(s.sub()), pointerTo(fs.sub));

        assertEquals(s.values(), fs.values);

        fs.a = 100;
		fs.b = 200;
		BridJ.writeToNative(fs);
		assertEquals(s.a(), fs.a); // did write succeed ?
		assertEquals(s.b(), fs.b, 0);
		s.a(1000);
		s.b(2000);
		BridJ.readFromNative(fs);
		assertEquals(s.a(), fs.a); // did read succeed ?
		assertEquals(s.b(), fs.b, 0);
	}
	
	@Test
	public void testEquality() {
		MyStruct x = new MyStruct(), y = new MyStruct(), z = new MyStruct();
		long len = sizeOf(MyStruct.class);
		int a = 10;
		double b = 12.0;
		pointerTo(x).clearBytesAtOffset(0, len, (byte)0xff);
		for (MyStruct s : new MyStruct[] { x, y })
			s.a(a).b(b);
		
		assertFalse(pointerTo(x).compareBytes(pointerTo(y), len) == 0);
		assertEquals(x, y);
		assertFalse(x.equals(z));
	}
	
	public static class MyJNAStruct extends com.sun.jna.Structure {
		public MyJNAStruct(com.sun.jna.Pointer p) {
			super(p);
		}
		public MyJNAStruct() {
			super();
		}
		public int a;
		public double b;
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("a", "b");
		}
	}
    public static class MyNIOStruct {
        final ByteBuffer p;
		public MyNIOStruct(ByteBuffer p) {
            this.p = p;
        }
        public MyNIOStruct() {
            this(ByteBuffer.allocateDirect(12));
        }
        public int a() {
			return p.getInt(0);
        }
        public void a(int a) {
            p.putInt(0, a);
        }
        
        public double b() {
			return p.getDouble(4);
        }
        public void b(double b) {
            p.putDouble(4, b);
        }
	}
    public static class MyOptimalStruct {
        //protected final com.sun.jna.Pointer pointer;
        ByteBuffer pointer;
        private static final long aOffset, bOffset;
        static {
            StructIO io = StructIO.getInstance(MyOptimalStruct.class, MyOptimalStruct.class);
            io.build();
            aOffset = io.fields[0].byteOffset;
            bOffset = io.fields[1].byteOffset;
        }
		public MyOptimalStruct(ByteBuffer p) {//com.sun.jna.Pointer p) {
            this.pointer = p;
        }
        public MyOptimalStruct() {
            //this(new Memory(16));
            //this(allocateBytes(16));
            this(ByteBuffer.allocateDirect(16));
        }
        @Field(0)
		public int a() {
			return pointer.getInt((int)aOffset);
        }
        public void a(int a) {
            pointer.putInt((int)aOffset, a);
        }
        
        @Field(1)
		public double b() {
			return pointer.getDouble((int)bOffset);
        }
        public void b(double b) {
            pointer.putDouble((int)bOffset, b);
        }
	}
	
	public static class MyJavolutionStruct extends javolution.io.Struct {
		public final Signed32 a = new Signed32();
		public final Float64 b = new Float64();
	}
	
	@Test
	public void trivial() {
		MyStruct s = new MyStruct();
		s.a(10);
        s.b(100.0);
		int a = s.a();
		double b = s.b();
		assertEquals(10, a);
		assertEquals(100.0, b, 0);
	}

	public static class CastStruct extends StructObject {
        public CastStruct(Pointer p) {
            super(p);
        }
		@Field(0)
		public int a() {
			return this.io.getIntField(this, 0);
		}
        public void a(int a) {
			this.io.setIntField(this, 0, a);
		}
		//public native CastStruct a(int a);

	}

    @Test
    public void testCast() {
        Pointer<?> m = Pointer.allocateBytes(100);
        CastStruct s = new CastStruct(m);
        s.a(10);
        //assertTrue(s == s.a(10));
        int a = s.a();
        assertEquals(10, a);
    }

    public static class ArrStruct extends StructObject {

		@Field(0)
		public int a() {
			return this.io.getIntField(this, 0);
		}
		public ArrStruct a(int a) {
			this.io.setIntField(this, 0, a);
			return this;
		}

	}
    @Test
    public void arrayCast() {
        Pointer<ArrStruct> formats = allocateArray(ArrStruct.class, 10);
        assertEquals(10, formats.getValidElements());
        for (ArrStruct s : formats) {
            assertNotNull(s);
            assertEquals(0, s.a());
            assertTrue(s == s.a(10));
        }
    }


    public static class ThisStruct extends StructObject {

		@Field(0)
		public int a() {
            return io.getIntField(this, 0);
        }
		public ThisStruct a(int a) {
            io.setIntField(this, 0, a);
            return this;
        }
        @Field(1)
        public SubStruct sub() {
        		return io.getNativeObjectField(this, 1);
        }

	}
	public static class SubStruct extends StructObject {

		@Field(0)
		public int a() {
            return io.getIntField(this, 0);
        }
		public SubStruct a(int a) {
            io.setIntField(this, 0, a);
            return this;
        }

	}
	
	@Test
    public void testThisStruct() {
        ThisStruct s = new ThisStruct();
        ThisStruct o = s.a(10);
        assertTrue(s == o);
        int a = s.a();
        assertEquals(10, a);
    }
    @Test
    public void testThisSubStruct() {
        ThisStruct s = new ThisStruct();
        assertEquals(2 * 4, BridJ.sizeOf(ThisStruct.class));
        SubStruct sub = s.sub();
        assertEquals("Invalid sub-struct !", pointerTo(s).offset(4), pointerTo(sub));
        
    }
    
    public static class ThisStructFields extends StructObject {

		@Field(0)
		public int a;
		
        @Field(1)
        public SubStructFields sub;

	}
	public static class SubStructFields extends StructObject {

		@Field(0)
		public int a;
	}
    
	@Test
    public void testThisStructFields() {
        ThisStructFields s = new ThisStructFields();
        s.a = 10;
        BridJ.writeToNative(s);
        s = pointerTo(s).get();
        int a = s.a;
        assertEquals(10, a);
    }
    @Test
    public void testThisSubStructFields() {
        ThisStructFields s = new ThisStructFields();
        assertEquals(2 * 4, BridJ.sizeOf(ThisStructFields.class));
        SubStructFields sub = s.sub;
        assertEquals("Invalid sub-struct !", pointerTo(s).offset(4), pointerTo(sub));
        sub.a = 100;
        BridJ.writeToNative(s);
        
        s = pointerTo(s).get();
        assertEquals(100, s.sub.a);
    }
    
    public static class StructWithArrays extends StructObject {
    		@Array(10)
    		@Field(0)
    		public Pointer<Integer> ints() {
    			return io.getPointerField(this, 0);
    		}
    }
    @Test
    public void testStructWithArrays() {
        StructWithArrays s = new StructWithArrays();
        Pointer<Integer> pInts = s.ints();
        assertEquals("Invalid array field pointer type", Integer.class, pInts.getTargetType());
        assertEquals("Invalid sub array", pointerTo(s).getPeer(), pInts.getPeer());
    }
    
    public static class CLongSize extends StructObject {
    	@Field(0)
    	@org.bridj.ann.CLong
    	public long v;
    }
    
    @Test
    public void testCLongSize() {
    	assertEquals(CLong.SIZE, BridJ.sizeOf(CLongSize.class));
    }
    public static class SizeTSize extends StructObject {
    	@Field(0)
    	@Ptr
    	public long v;
    }
    
    @Test
    public void testSizeTSize() {
    	assertEquals(SizeT.SIZE, BridJ.sizeOf(SizeTSize.class));
    }
    
    public static class NoLoop extends StructObject {
    	@Field(0)
    	public int value;
    	
    	@Field(1)
    	public Pointer<NoLoop> next;
    }
    
    @Test
    public void testNoLoop() {
    	long s = BridJ.sizeOf(NoLoop.class);
    	assertEquals(2 * Pointer.SIZE, s);
    	//System.out.println("sizeof = " + s);
    	NoLoop l = new NoLoop();
    	Pointer<NoLoop> p = pointerTo(l);
    	//System.out.println("NoLoop = " + p);
    	//System.out.println("valid bytes = " + p.getValidBytes() + ", sizeof = " + s);
    	l = p.get();
    }

	/// see vfw.h
    public static class VIDEOHDR extends StructObject
    {
        @Field(0)
        public Pointer<Byte> lpData;
        @Field(1)
        public int dwBufferLength;
        @Field(2)
        public int dwBytesUsed;
        @Field(3)
        public int dwTimeCaptured;
        @Field(4)
        public Pointer<Integer> dwUser;
        @Field(5)
        public int dwFlags;
        @Field(6) @Array(4)
        public Pointer<Pointer<Integer>> dwReserved;
    }
    @Test
    public void testSizeOfVIDEOHDR() {
        if (!Platform.isWindows())
            return;

        long s = BridJ.sizeOf(VIDEOHDR.class);
        long expected = Platform.is64Bits() ? 72 : 40;
        assertEquals(expected, s);
    }
    
    @Struct(customizer = TinyStructCustomizer.class)
	public static class TinyStruct extends StructObject {
		@Field(0) 
		public long a;
		@Field(1)
		public long b;
	}
	public static class ExpectedTinyStruct extends StructObject {
		@Field(0) 
		public byte a;
		@Field(1)
		public byte b;
	}

	public static class TinyStructCustomizer extends StructIO.DefaultCustomizer {
		@Override
		public void beforeLayout(StructIO io, List<StructIO.AggregatedFieldDesc> aggregatedFields) {
			for (StructIO.AggregatedFieldDesc field : aggregatedFields) {
				field.byteLength = 1;
				field.alignment = 1;
			}
		}
	}
    @Test
    public void testTinyStructCustomization() {
    		assertEquals("Invalid customized struct size !", BridJ.sizeOf(ExpectedTinyStruct.class), BridJ.sizeOf(TinyStruct.class));
    		ExpectedTinyStruct s = new ExpectedTinyStruct();
    		s.a = 10;
    		s.b = -20;
    		BridJ.writeToNative(s);
    		TinyStruct t = pointerTo(s).as(TinyStruct.class).get();
    		assertEquals(s.a, t.a);
    		assertEquals(s.b, t.b);
    }
    
    @Optional
	@Ptr
    public static native long sizeOfTimeT();
    @Optional
	@Ptr
    public static native long sizeOfTimeval();

    @Test
    public void testSizeOfTimeval() {
    		assertEquals(sizeOfTimeval(), BridJ.sizeOf(TimeT.timeval.class));
    }
    @Test
    public void testTimeval() {
    		TimeT.timeval tv = new TimeT.timeval();
    		Pointer<TimeT.timeval> ptv = pointerTo(tv);
    		tv = ptv.get();
    }
    	
    
//    @Library("SmallPt") 
    public static class S extends StructObject {
        @Field(0)
        public int a;
        @Field(1) 
        public byte b;
        @Field(2) 
        public byte c;
    }

    @Struct(pack = 1)
    public static class PackedS extends S {
        
    }
    
    @Test
    public void testPacked() {
        long s = sizeOf(S.class), ps = sizeOf(PackedS.class);
        //System.out.println("testPacked " + ps + " vs. " + s);
        assertEquals("Invalid packed struct size", 6, ps);
        assertTrue("Packed size (" + ps + ") same as size (" + s + ") !", s != ps); 
        //assertTrue(ps < s);
    }
    
    public static class EnumsStruct extends StructObject {
        public enum E implements IntValuedEnum<E > {
            A(0),
            B(1),
            C(2);
            E(long value) {
                this.value = value;
            }
            public final long value;
            public long value() {
                return this.value;
            }
            public Iterator<E > iterator() {
                return Collections.singleton(this).iterator();
            }
            public static ValuedEnum<E > fromValue(long value) {
                return FlagSet.fromValue(value, values());
            }
        };
        @Array(5)
        @Field(0)
        public Pointer<IntValuedEnum<E>> p;
    }
    @Test
    public void testEnumsStruct() {
        assertEquals(5 * BridJ.sizeOf(IntValuedEnum.class), BridJ.sizeOf(EnumsStruct.class));
    }
    
    public enum cec_device_type implements IntValuedEnum<cec_device_type> {

        A, B;

        public long value() {
            return ordinal();
        }

        public Iterator<cec_device_type> iterator() {
            return FlagSet.fromValue(value(), cec_device_type.class).iterator();
        }
        
    }
    public static class cec_device_type_list extends StructObject {
       public cec_device_type_list() {
               super();
       }
       /// C type : cec_device_type[5]
       @Array({5})
       @Field(0)
       public Pointer<IntValuedEnum<cec_device_type > > types() {
               return this.io.getPointerField(this, 0);
       }
    }
    
    @Test
    public void testEnumsFieldRegression() {
        Pointer<cec_device_type_list> pDeviceTypeList = Pointer.pointerTo(new cec_device_type_list());
        cec_device_type_list deviceTypeList = pDeviceTypeList.get();
        Pointer<IntValuedEnum<cec_device_type>> pDeviceTypes = deviceTypeList.types();
    }
    
    public static class SBytes100 extends StructObject {
       @Field(0) @Array({100})
       public byte field() {
		   return this.io.getByteField(this, 0);
       }
    }
    public static class SBytes100x4 extends StructObject {
       @Field(0)
       public int first() {
       	   return this.io.getIntField(this, 0);
       }
       @Field(1) @Array({4})
       public Pointer<SBytes100> second() {
		   return this.io.getPointerField(this, 0);
       }
    }
    
    @Test
    public void testLargeAlignments() {
    	assertEquals(100, BridJ.sizeOf(SBytes100.class));
    	assertEquals(100 * 4 + 4, BridJ.sizeOf(SBytes100x4.class));
    }
}

