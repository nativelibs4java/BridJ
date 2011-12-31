package org.bridj.objc;
import org.bridj.*;

public class ObjCJNI {
	
    @Deprecated
    public static synchronized native Pointer<? extends ObjCObject> createObjCProxyPeer(ObjCProxy javaInstance);
    
    static synchronized native long createObjCBlockWithFunctionPointer(long fptr);
    static synchronized native long getObjCBlockFunctionPointer(long blockPtr);
    static synchronized native void releaseObjCBlock(long blockPtr);
}
