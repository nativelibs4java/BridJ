package org.bridj.objc;
import org.bridj.Pointer;
import org.bridj.Pointer.StringType;
import org.bridj.ann.Library;
import java.nio.charset.*;
import static org.bridj.objc.FoundationLibrary.*;

@Library("Foundation")
public class NSString extends NSObject {

    public native int length();
    public native boolean isAbsolutePath();
    public native Pointer<Byte> UTF8String();

    public NSString() {
        super();
    }
    public NSString(String s) {
        super(pointerToNSString(s));
    }
    public String toString() {
    		return UTF8String().getString(StringType.C, Charset.forName("utf-8"));
    }
    public int hashCode() {
    		return toString().hashCode();
    }
    
    /*
    public boolean equals(Object o) {
    		if (!(o instanceof NSString))
    			return false;
    		return o.toString().equals(toString());
    }
    */
    
    public static NSString valueOf(String s) {
        return pointerToNSString(s).get();
    }
    
}
