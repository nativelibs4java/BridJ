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
package org.bridj;

import java.lang.reflect.Field;
import static org.bridj.WinExceptionsConstants.*;

/**
 * Native Windows error as caught by a
 * <code>__try { ... } __except (...) { ... }</code> block. Not public yet.
 *
 * @author Olivier
 */
class WindowsError extends NativeError {

    final int code;
    final long info, address;

    WindowsError(int code, long info, long address) {
        super(computeMessage(code, info, address));
        this.code = code;
        this.info = info;
        this.address = address;
    }

    public static void throwNew(int code, long info, long address) {
        throw new WindowsError(code, info, address);
    }

    static String subMessage(long info, long address) {
        switch ((int) info) {
            case 0:
                return "Attempted to read from inaccessible address " + toHex(address);
            case 1:
                return "Attempted to write to inaccessible address " + toHex(address);
            case 8:
                return "Attempted to execute memory " + toHex(address) + " that's not executable  (DEP violation)";
            default:
                return "?";
        }
    }

    public static String computeMessage(int code, long info, long address) {
        switch (code) {
            case EXCEPTION_ACCESS_VIOLATION:
                return "EXCEPTION_ACCESS_VIOLATION : " + subMessage(info, address);
            case EXCEPTION_IN_PAGE_ERROR:
                return "EXCEPTION_IN_PAGE_ERROR : " + subMessage(info, address);
            default:
                try {
                    for (Field field : WinExceptionsConstants.class.getFields()) {
                        if (field.getName().startsWith("EXCEPTION_") && field.getType() == int.class) {
                            int value = (Integer) field.get(null);
                            if (value == code) {
                                return field.getName();
                            }
                        }
                    }
                } catch (Throwable th) {
                }
                return "Windows native error (code = " + code + ", info = " + info + ", address = " + address + ") !";
        }

    }
}
