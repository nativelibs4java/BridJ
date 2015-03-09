/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
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

import java.util.HashMap;
import org.bridj.Pointer;
import java.util.Map;
import org.bridj.BridJ;
import static org.bridj.objc.FoundationLibrary.*;
import static org.bridj.Pointer.*;

/**
 *
 * @author ochafik
 */
public class NSDictionary extends NSObject {

    static {
        BridJ.register();
    }

    public NSDictionary() {
        super();
    }
//    public NSDictionary(Map<String, NSObject> map) {
//        super(pointerToNSDictionary(map));
//    }

    public native Pointer<NSObject> valueForKey(Pointer<NSString> key);

    public native Pointer<NSObject> objectForKey(Pointer<NSObject> key);

    public native int count();

    public native void getObjects_andKeys(Pointer<Pointer<NSObject>> objects, Pointer<Pointer<NSObject>> keys);

    public static native Pointer<NSDictionary> dictionaryWithContentsOfFile(Pointer<NSString> path);

    public static native Pointer<NSDictionary> dictionaryWithObjects_forKeys_count(Pointer<Pointer<NSObject>> objects, Pointer<Pointer<NSObject>> keys, int count);

    public static Pointer<NSDictionary> pointerToNSDictionary(Map<String, NSObject> map) {
        int n = map.size();
        Pointer<Pointer<NSObject>> objects = allocatePointers(NSObject.class, n);
        Pointer<Pointer<NSObject>> keys = allocatePointers(NSObject.class, n);

        int i = 0;
        for (Map.Entry<String, NSObject> e : map.entrySet()) {
            keys.set(i, (Pointer) pointerToNSString(e.getKey()));
            objects.set(i, getPointer(e.getValue()));
            i++;
        }

        return dictionaryWithObjects_forKeys_count(objects, keys, n);
    }

    public static NSDictionary valueOf(Map<String, NSObject> map) {
        return pointerToNSDictionary(map).get();
    }

    public Map<String, NSObject> toMap() {
        int n = count();
        Pointer<Pointer<NSObject>> objects = allocatePointers(NSObject.class, n);
        Pointer<Pointer<NSString>> keys = allocatePointers(NSString.class, n);

        getObjects_andKeys(objects, (Pointer) keys);

        Map<String, NSObject> ret = new HashMap<String, NSObject>();
        for (int i = 0; i < n; i++) {
            Pointer<NSString> key = keys.get(i);
            Pointer<NSObject> value = objects.get(i);

            ret.put(key.get().toString(), value == null ? null : value.get());
        }
        return ret;
    }
}
