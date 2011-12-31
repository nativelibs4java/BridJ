package org.bridj.examples;

import org.bridj.Pointer;
import org.bridj.CRuntime;

@org.bridj.ann.Runtime(CRuntime.class)
public class MyCallbackImpl extends MyCallback {
	public native long doSomething(int a, int b);
}
