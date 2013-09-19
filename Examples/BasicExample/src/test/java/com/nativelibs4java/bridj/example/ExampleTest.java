package com.nativelibs4java.bridj.example;

import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.*;
import static org.bridj.Pointer.*;
import static com.nativelibs4java.bridj.example.ExampleLibrary.*;

public class ExampleTest {
	@Test
	public void testMessages() {
		Pointer<Byte> message = pointerToCString("Message from Java");
		someFunction(message);

		SomeClass c = new SomeClass(1234);
		int value = c.someMethod(message);
		assertEquals(1234, value);
	}
}
