package org.bridj.objc;
import org.bridj.*;
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
public class NSNumber extends NSObject {
    static {
        BridJ.register();
    }

	public static native Pointer<NSNumber> numberWithBool(boolean value);
	public static native Pointer<NSNumber> numberWithInt(int value);
	public static native Pointer<NSNumber> numberWithDouble(double e);
	public static native Pointer<NSNumber> numberWithLong(long value);
	public static native Pointer<NSNumber> numberWithFloat(float value);
	
	public native short shortValue();
	public native int intValue();
	public native long longValue();
	public native float floatValue();
	public native double doubleValue();
	public native int compare(Pointer<NSNumber> another);
	
	public native boolean isEqualToNumber(Pointer<NSNumber> another);
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NSNumber))
			return false;
		
		NSNumber nn = (NSNumber)o;
		return isEqualToNumber(pointerTo(nn));
	}
}
