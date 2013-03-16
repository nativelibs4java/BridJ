package org.bridj.cpp.mfc;

import org.bridj.ann.Virtual;


public class CCmdUI extends MFCObject {

	@Virtual
	public native void Enable(boolean bOn);
}