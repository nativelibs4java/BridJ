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
package org.bridj.cpp.com;

import java.util.Collections;
import java.util.Iterator;
import org.bridj.BridJ;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.ValuedEnum;

public enum VARENUM implements IntValuedEnum<VARENUM> {

    VT_EMPTY(0),
    VT_NULL(1),
    VT_I2(2),
    VT_I4(3),
    VT_R4(4),
    VT_R8(5),
    VT_CY(6),
    VT_DATE(7),
    VT_BSTR(8),
    VT_DISPATCH(9),
    VT_ERROR(10),
    VT_BOOL(11),
    VT_VARIANT(12),
    VT_UNKNOWN(13),
    VT_DECIMAL(14),
    VT_I1(16),
    VT_UI1(17),
    VT_UI2(18),
    VT_UI4(19),
    VT_I8(20),
    VT_UI8(21),
    VT_INT(22),
    VT_UINT(23),
    VT_VOID(24),
    VT_HRESULT(25),
    VT_PTR(26),
    VT_SAFEARRAY(27),
    VT_CARRAY(28),
    VT_USERDEFINED(29),
    VT_LPSTR(30),
    VT_LPWSTR(31),
    VT_FILETIME(64),
    VT_BLOB(65),
    VT_STREAM(66),
    VT_STORAGE(67),
    VT_STREAMED_OBJECT(68),
    VT_STORED_OBJECT(69),
    VT_BLOB_OBJECT(70),
    VT_CF(71),
    VT_CLSID(72),
    VT_VECTOR(0x1000),
    VT_ARRAY(0x2000),
    VT_BYREF(0x4000),
    VT_RESERVED(0x8000),
    VT_ILLEGAL(0xFFFF),
    VT_ILLEGALMASKED(0xFFF),
    VT_TYPEMASK(0xFFF);

    VARENUM(long value) {
        this.value = value;
    }
    public final long value;

    public long value() {
        return this.value;
    }

    public Iterator<VARENUM> iterator() {
        return Collections.singleton(this).iterator();
    }

    public static ValuedEnum<VARENUM> fromValue(long value) {
        return FlagSet.fromValue(value, values());
    }
};
