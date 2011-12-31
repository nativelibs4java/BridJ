/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp.mfc;

import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.ann.Runtime;
import org.bridj.cpp.CPPObject;

@Library(value="mfc90u.dll", versionPattern = "mfc(?:(\\d)(\\d))?u")
@Runtime(MFCRuntime.class)
public class MFCObject extends CPPObject {
	protected MFCObject() {}
    protected MFCObject(Pointer<? extends MFCObject> peer) {
        super(peer);
    }
    protected MFCObject(Void voidArg, int constructorId, Object... args) {
        super(voidArg, constructorId, args);
    }
}