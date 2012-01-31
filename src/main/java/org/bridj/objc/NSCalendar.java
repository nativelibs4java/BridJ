package org.bridj.objc;

import org.bridj.BridJ;
import org.bridj.NativeObject;
import org.bridj.Pointer;
import org.bridj.ann.Library;

@Library("Foundation")
public class NSCalendar extends ObjCObject {
    static {
        BridJ.register();
    }

//    public NSCalendar(Pointer<? extends NSCalendar> peer) {
//        super(peer);
//    }

    public NSCalendar() {
        super();
    }

    public static native Pointer<NSCalendar> currentCalendar();
    public native Pointer<NSString> calendarIdentifier();
}
