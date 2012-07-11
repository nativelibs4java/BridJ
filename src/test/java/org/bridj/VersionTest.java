package org.bridj;

import org.junit.*;
import static org.junit.Assert.*;

public class VersionTest {
	@Test
	public void testVersion() {
		assertTrue(Version.MAVEN_VERSION, 
			Version.MAVEN_VERSION.matches("\\d+\\.\\d+(\\.\\d+)?(-SNAPSHOT)?"));
		
		assertTrue(Version.VERSION_SPECIFIC_SUB_PACKAGE, 
			Version.VERSION_SPECIFIC_SUB_PACKAGE.matches("v\\d+\\_\\d+(_\\d+)?"));
	}
}
