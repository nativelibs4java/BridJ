package org.bridj;
import static org.bridj.Platform.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

public class PlatformTest {
	@Test
	public void testSizes() {
		int clong = isWindows() || !is64Bits() ? 4 : 8;
		int sizet = is64Bits() ? 8 : 4, ptr = sizet;
		int wchar = isUnix() ? 4 : 2;
		
		assertEquals(clong, Platform.CLONG_SIZE);
		assertEquals(sizet, Platform.SIZE_T_SIZE);
		assertTrue(Platform.TIME_T_SIZE > 0);
		assertEquals(wchar, Platform.WCHAR_T_SIZE);
		assertEquals(ptr, Platform.POINTER_SIZE);
		
		assertEquals(ptr, Pointer.SIZE);
		assertEquals(sizet, SizeT.SIZE);
		assertTrue(TimeT.SIZE > 0);
		assertEquals(clong, CLong.SIZE);
		
	}
	@Test
	public void testMachine() throws Exception {
		if (!isUnix())
			return;
		
		Process p = Runtime.getRuntime().exec(new String[] { "uname", "-m" });
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String m = r.readLine().trim();
		assertTrue(m.length() > 0);
		
		if (m.matches("i\\d86"))
			m = "i386";
		else if (m.matches("i86pc"))
			m = "x86";
		
		assertEquals(m, Platform.getMachine());
	}
}
