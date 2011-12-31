package com.example.hellojni;

import org.bridj.*;
import org.bridj.ann.*;

@Library("hello-jni")
public class BridJLib {
	static {
		BridJ.register();
	}
	public static native int addTwoInts(int a, int b);
	/*
	public static abstract class passTwoIntsToCallback_cb extends Callback {
		public abstract int apply(int a, int b);
	}
	//public static native int passTwoIntsToCallback(int a, int b, Pointer<passTwoIntsToCallback_cb> cb);
	//*/
	public static native int passTwoIntsToCallback(int a, int b, Pointer<?> cb);
	
	@Library("c")
	public static native int access(Pointer<Byte> path, int amode);
}
