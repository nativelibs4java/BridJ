package org.bridj;

import com.sun.jna.Memory;
import java.nio.ByteBuffer;
import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Array;
import org.bridj.ann.Ptr;
import org.bridj.ann.Struct;
import org.bridj.cpp.com.*;
import static org.bridj.Pointer.*;
import static org.bridj.BridJ.*;
import java.util.List;

import javolution.io.*;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html

public class Struct2Test {
	public static class OrphanStruct extends StructObject {
		@Field(0)
		public int a;
		
		@Field(1)
		public int b;
	}
	@Test
	public void testOrphanStruct() {
		new OrphanStruct();
	}
}
