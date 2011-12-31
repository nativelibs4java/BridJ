package org.bridj.objc;
import org.bridj.ann.Ptr;
import org.bridj.*;
import org.bridj.Pointer.StringType;
import org.bridj.ann.Library;
import java.nio.charset.*;
import static org.bridj.objc.FoundationLibrary.*;

@Library("Foundation")
public class NSMethodSignature extends NSObject {
	static { BridJ.register(); }

	public static native Pointer<NSMethodSignature> signatureWithObjCTypes(Pointer<Byte> types);	
    
    public native Pointer<Byte> methodReturnType();
    public native @Ptr long numberOfArguments();
    public native boolean isOneway();
    public native Pointer<Byte> getArgumentTypeAtIndex(@Ptr long index);
    public native @Ptr long frameLength();
}
