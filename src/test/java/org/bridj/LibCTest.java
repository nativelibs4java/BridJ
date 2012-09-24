package org.bridj;

import java.io.IOException;
import java.io.FileNotFoundException;
import org.bridj.ann.*; // annotations such as Library...

import static org.bridj.Pointer.*;
import org.junit.*;
import static org.junit.Assert.*;

@Library("c")
@org.bridj.ann.Runtime(CRuntime.class)
public class LibCTest {
	static {
		if ("1".equals(System.getenv("JNA")))
			com.sun.jna.Native.register("c");
		else
			BridJ.register();
	}
	public static native void sprintf(Pointer<Byte> dest, Pointer<Byte> format, Object... values);
	
	@Library("m")
	public static native double fabs(double x);
	public static native int abs(int x);
	public static native int getpid();
	
    public static native @org.bridj.ann.CLong long strtol(Pointer<Byte> str, Pointer<Pointer<Byte>> endptr, int base) throws LastError;
	
    @Optional // only on Windows
    @Library("test")
    public static native void setLastWindowsError() throws LastError;
    
	@Test
	public void testFabs() {
		assertEquals(10.0, fabs(-10.0), 0.000001);
	}
	@Test(expected=LastError.class)
	public void testLastWindowsError() {
        if (!Platform.isWindows())
            throw new LastError(0, "");
        
        setLastWindowsError();
	}
	@Test
	public void testErrno() throws IOException {
		if (!Platform.isUnix())
			return;
		
		assertNotNull(BridJ.getNativeLibrary("c").getSymbolPointer("errno"));
	}
	
	public void testNoLastError() {
        long v = strtol(pointerToCString("1010"), null, 10);
        assertEquals(1010, v);
	}
	
	@Test(expected = LastError.class)
	public void testLastError() {
        strtol(pointerToCString("18446744073709551616"), null, 10);
	}
	
	@Test
	public void testAbs() {
		assertEquals(10, abs(-10));
	}
}
	

