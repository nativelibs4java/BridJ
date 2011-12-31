package org.bridj.objc;

import org.bridj.Pointer;
import org.bridj.NativeObject;
import org.bridj.objc.*;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Runtime;
import org.bridj.ann.Array;
import org.bridj.ann.Ptr;
import org.bridj.cpp.com.*;
import static org.bridj.Pointer.*;
import static org.bridj.BridJ.*;

@Library("Foundation")
public class NSObject extends ObjCObject {
    public NSObject(Pointer<? extends NSObject> peer) {
        super(peer);
    }

    public NSObject() {
        super();
    }

    
}
