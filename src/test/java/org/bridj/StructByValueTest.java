package org.bridj;

import java.util.logging.Level;
import org.junit.Ignore;
import org.bridj.ann.Library;
import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.Field;
import org.bridj.ann.Array;


import static org.bridj.dyncall.DyncallLibrary.*;

@Ignore
@Library("test")
public class StructByValueTest {
    static {
        if (!BridJ.Switch.StructsByValue.enabled)
            BridJ.warning("Structs by value are not enabled (see " + BridJ.Switch.StructsByValue.getFullDescription() + ")");
        else
            BridJ.register();
    }
    public static class SimpleStruct extends StructObject {
		@Field(0)
		public int a;
		
		@Field(1)
        @Array(2)
		public Pointer<Long> b;
        
		@Field(2)
		public short c;
        
		@Field(3)
        @Array(3)
		public Pointer<Byte> d;
        
		@Field(4)
		public float e;
	}
    
    public static class S_int extends StructObject {
        @Field(0)
        public int a;
    }
    public static class S_int2 extends StructObject {
        @Field(0)
        public int a;
        @Field(1)
        public int b;
    }
    public static class S_jlong4 extends StructObject {
        @Field(0)
        public long a;
        @Field(1)
        public long b;
        @Field(2)
        public long c;
        @Field(3)
        public long d;
    }
    public static class S_jlong10 extends StructObject {
        @Field(0)
        @Array(10)
        public Pointer<Long> a;
    }
    
    public static native int incr(S_int s);
    public static native long sum(S_int2 s);
    public static native long sum(S_jlong4 s);
    public static native long sum(S_jlong10 s);
	
//    @Test
//    public void testSimpleStruct() {
//        Pointer<DCstruct> s = dcNewStruct(5, DEFAULT_ALIGNMENT);
//        try {
//            dcStructField(s, DC_SIGCHAR_INT, DEFAULT_ALIGNMENT, 1);
//            dcStructField(s, DC_SIGCHAR_LONGLONG, DEFAULT_ALIGNMENT, 2);
//            dcStructField(s, DC_SIGCHAR_SHORT, DEFAULT_ALIGNMENT, 1);
//            dcStructField(s, DC_SIGCHAR_CHAR, DEFAULT_ALIGNMENT, 3);
//            dcStructField(s, DC_SIGCHAR_FLOAT, DEFAULT_ALIGNMENT, 1);
//            dcCloseStruct(s);
//
//            long size = dcStructSize(s);
//            assertEquals(BridJ.sizeOf(SimpleStruct.class), size);
//        } finally {
//            dcFreeStruct(s);
//        }
//    }
            
    
    @Test
    public void testStructSizes() {
        if (!BridJ.Switch.StructsByValue.enabled)
            return;
        
        StructIO io = StructIO.getInstance(SimpleStruct.class);
        Pointer<DCstruct> struct = DyncallStructs.buildDCstruct(io);
        assertNotNull(struct);
    }
    
    //@Ignore
    @Test
    public void testIncrInt() {
        if (!BridJ.Switch.StructsByValue.enabled)
            return;
        
        int value = 12345;
        S_int s = new S_int();
        s.a = value;
        BridJ.writeToNative(s);
        assertEquals(value + 1, incr(s));
    }
    
    
    @Ignore
    @Test
    public void testIncrLong4() {
        if (!BridJ.Switch.StructsByValue.enabled)
            return;
        
        long value = 12345;
        S_jlong4 s = new S_jlong4();
        s.a = 10;
        s.b = 100;
        s.c = 1000;
        s.d = 10000;
        BridJ.writeToNative(s);
        assertEquals(s.a + s.b + s.c + s.d, sum(s));
    }
    
    @Test
    public void testIncrLong10() {
        if (!BridJ.Switch.StructsByValue.enabled)
            return;
        
        long tot = 0;
        S_jlong10 s = new S_jlong10();
        for (int i = 0; i < 10; i++) {
            long v = i + 1;
            tot += v;
            s.a.set(i, v);
        }
        BridJ.writeToNative(s);
        assertEquals(tot, sum(s));
    }
}