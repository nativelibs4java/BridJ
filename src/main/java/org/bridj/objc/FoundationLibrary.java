/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    public static final int kCFStringEncodingASCII = 0x0600,
            kCFStringEncodingUnicode = 0x0100,
            kCFStringEncodingUTF8 = 0x08000100;

    public static native Pointer<NSString> CFStringCreateWithBytes(Pointer<?> alloc, Pointer<Byte> bytes, @Ptr long numBytes, int encoding, boolean isExternalRepresentation);

    public static Pointer<NSString> pointerToNSString(String s) {
        Pointer p = Pointer.pointerToString(s, StringType.C, Charset.forName("utf-8"));
        assert p != null;
        Pointer<NSString> ps = CFStringCreateWithBytes(null, p, p.getValidBytes() - 1 /* remove the trailing NULL */, kCFStringEncodingUTF8, false);
        assert ps != null;
        return ps;
    }
}
