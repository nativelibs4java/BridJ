package org.bridj;
import org.junit.Test;

import org.bridj.ann.*;

@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
public class DummyTest {
	
	@Test
	public void dummy() {
		BridJ.register();
	}
}
