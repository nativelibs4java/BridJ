package org.bridj;
import org.bridj.ann.*;
import org.junit.*;
import static org.junit.Assert.*;

@Library(value = "dependsOnTest", dependencies = { "test" })
public class DependencyTest {
	public DependencyTest() {
		BridJ.register();
	}
	public static native int addThatDependsOnTest(int a, int b);
	
	@Test
	public void testAdd() {
		assertEquals(4, addThatDependsOnTest(3, 1));
	}
}
