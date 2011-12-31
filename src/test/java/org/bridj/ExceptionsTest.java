package org.bridj;

import org.junit.Test;

import org.bridj.ann.Library;
import static org.bridj.Pointer.*;
import static org.junit.Assert.*;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html

@Library("test")
public class ExceptionsTest {
	static {
		BridJ.register();
	}
	
	public static native void crashIllegalAccess() throws RuntimeException;
	public static native void throwMyExceptionByValue(Pointer<Byte> message) throws RuntimeException;
	public static native void throwNewMyException(Pointer<Byte> message) throws RuntimeException;
	public static native void throwInt(int value) throws RuntimeException;
	
	void throwExpectedIfNotSupported() {
		if (!BridJ.protectedMode) {
            System.out.println("Please run this test with protected mode enabled :\n\tBRIDJ_PROTECTED=1 mvn test -o -Dtest=" + getClass().getSimpleName());
			SignalError.throwNew(0, 0, 0);
        }
	}
	
	@Test(expected=NativeError.class)
	public void testCrashIllegalAccess() {
		throwExpectedIfNotSupported();
		
		try {
            crashIllegalAccess();
     	} catch (NativeError ex) {
			System.out.println(ex);
			throw ex;
		}
	}
	
	
	@Test(expected=NativeError.class)
	public void testThrowCPPException() {
        throwExpectedIfNotSupported();
		
		throwMyExceptionByValue(pointerToCString("Whatever"));
	}
}

