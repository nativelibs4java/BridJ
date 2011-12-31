/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.objc;

import java.nio.charset.Charset;
import java.util.Map;
import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import org.bridj.Pointer.StringType;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;

@Library("Foundation")
@org.bridj.ann.Runtime(CRuntime.class)
public class FoundationLibrary {
    static {
        BridJ.register();
    }
    
    public static final int 
        kCFStringEncodingASCII = 0x0600,
        kCFStringEncodingUnicode = 0x0100,
        kCFStringEncodingUTF8 = 0x08000100;

    public static native Pointer<NSString> CFStringCreateWithBytes(Pointer<?> alloc, Pointer<Byte> bytes, @Ptr long  numBytes, int encoding, boolean isExternalRepresentation);
    
    
    public static Pointer<NSString> pointerToNSString(String s) {
        Pointer p = Pointer.pointerToString(s, StringType.C, Charset.forName("utf-8"));
        assert p != null;
        Pointer<NSString> ps = CFStringCreateWithBytes(null, p, p.getValidBytes() - 1 /* remove the trailing NULL */, kCFStringEncodingUTF8, false);
        assert ps != null;
        return ps;
    }
}
