package org.bridj;


import org.bridj.demangling.Demangler;
import java.util.Collection;

import org.junit.Test;


import static org.junit.Assert.*;

public class BridJTest {
	
	@Test
	public void testLongToIntCast() {
		for (long value : new long[] { 1, -1, -2, 100 }) {
			assertEquals((int)value, SizeT.safeIntCast(value));
		}
	}
	@Test
	public void loadPthread() throws Exception {
		if (!Platform.isUnix())
			return;
		
		assertNotNull(BridJ.getNativeLibrary("pthread"));
	}
	@Test
	public void symbolsTest() throws Exception {
		NativeLibrary lib = BridJ.getNativeLibrary("test");
		Collection<Demangler.Symbol> symbols = lib.getSymbols();
		
		assertTrue("Not enough symbols : found only " + symbols.size(), symbols.size() > 20);
		boolean found = false;
		for (Demangler.Symbol symbol : symbols) {
			if (symbol.getName().contains("Ctest")) {
				found = true;
				break;
			}
		}
		assertTrue("Failed to find any Ctest-related symbol !", found);
	}
}
