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

import org.bridj.NativeObject;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import org.bridj.BridJ;

@org.bridj.ann.Runtime(ObjectiveCRuntime.class)
public class ObjCObject extends NativeObject {

    static {
        BridJ.register();
    }
    ObjCObject type;

    //public native <T extends ObjCObject> Pointer<T> create();
    public native Pointer<ObjCObject> init();

    public native Pointer<NSString> stringValue();

    public native Pointer<NSString> description();

    public native int hash();

    public native boolean isEqual(Pointer<? extends ObjCObject> anObject);

    public native boolean isKindOf(Pointer<? extends ObjCObject> aClassObject);

    public native boolean isMemberOf(Pointer<? extends ObjCObject> aClassObject);

    public native boolean isKindOfClassNamed(Pointer<Byte> aClassName);

    public native boolean isMemberOfClassNamed(Pointer<Byte> aClassName);

    public native boolean respondsTo(SEL aSelector);

    public native IMP methodFor(SEL aSelector);

    public native Pointer<?> perform(SEL aSelector);

    public native Pointer<?> perform$with(SEL aSelector, Pointer<?> anObject);

    public native Pointer<?> perform$with$with(SEL aSelector, Pointer<?> object1, Pointer<?> object2);

    public ObjCObject(Pointer<? extends NativeObject> peer) {
        super(peer);
    }

    public ObjCObject() {
        super();
    }

    public ObjCObject(int constructorId, Object... args) {
        super(constructorId, args);
    }

    @Override
    public String toString() {
        Pointer<NSString> p = description();
        if (p == null) {
            p = stringValue();
        }

        return p.get().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjCObject)) {
            return false;
        }

        Pointer<ObjCObject> p = getPointer((ObjCObject) o);
        return isEqual(p);
    }

    @Override
    public int hashCode() {
        return hash();
    }
}
