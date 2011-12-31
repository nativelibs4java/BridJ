package org.bridj.objc;

import org.bridj.*;

public class ObjCClass extends ObjCObject {
	static {
		BridJ.register();
	}
	public native <T extends ObjCObject> Pointer<T> alloc();
	@Selector("new")
	public native <T extends ObjCObject> Pointer<T> new$();
	
	public native boolean instancesRespondTo(SEL sel);
	//public native boolean respondsTo(SEL sel);
	public native IMP instanceMethodFor(SEL aSelector);
}
