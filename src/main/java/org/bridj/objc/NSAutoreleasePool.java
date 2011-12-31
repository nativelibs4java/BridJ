package org.bridj.objc;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

@Library("Foundation")
public class NSAutoreleasePool extends NSObject {
    static {
        BridJ.register();
    }
    public static native Pointer<NSAutoreleasePool> new_();
	public native void drain();
}
