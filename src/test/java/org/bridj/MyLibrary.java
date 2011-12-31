package org.bridj;

import org.bridj.*;
import static org.bridj.Pointer.*;
import org.bridj.ann.*;

/**
 *
 * @author Olivier
 */
public class MyLibrary {
    public MyLibrary() {
		BridJ.register(getClass());
	}
    public static class MyCallback {
        
    }
    public static class MyTypedPtr extends TypedPointer {
        public MyTypedPtr(Pointer<?> ptr) {
            super(ptr.getPeer());
        }
        @Deprecated
        public MyTypedPtr(long peer) {
            super(peer);
        }
    }
	protected native @Ptr long someFunction_native(@Ptr long arg1);
	public MyCallback someFunction(MyTypedPtr arg1) {
		return null;//Callback.wrapCallback(someFunction_native(Pointer.getPeer(arg1)), MyCallback.class);
	}
	
	protected native int someFunction_native(@Ptr long stringArray, @Ptr long errOut);
	public int someFunction(String[] arg1, Pointer<Integer> errOut) {
		return someFunction_native(pointerToCStrings(arg1).getPeer(), errOut.getPeer());
	}
	
	protected native int someFunction2_native(@Ptr long size, @Ptr long sizeOut);
	public int someFunction2(long size, Pointer<SizeT> sizeOut) {
		return someFunction2_native(size, Pointer.getPeer(sizeOut));
	}
}
