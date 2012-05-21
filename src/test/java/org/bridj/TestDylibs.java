package org.bridj;

import java.io.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TestDylibs {
	@Test
	public void test() throws IOException {
		if (!Platform.isMacOSX())
			return;
		
        Process p = Runtime.getRuntime().exec(new String[] { "find", "/usr/lib", "-name", "*.dylib" });
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
		String path;
        while ((path = in.readLine()) != null) {
        	path = path.trim();
			if (path.contains("libSystem"))
				continue;
			
            File f = new File(path);
            try {
                System.out.println("Loading '" + f + "'");
                NativeLibrary lib = BridJ.getNativeLibrary(f.getName(), f);
                assertNotNull(lib);
                System.out.println("\t" + lib.getSymbols().size() + " symbols");
                lib.release();
            } catch (Throwable th) {
                System.err.println("Failed to load '" + path + "' : " + th);
            }
        }
	}
}
