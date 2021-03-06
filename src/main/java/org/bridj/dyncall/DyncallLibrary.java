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
package org.bridj.dyncall;

import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.Pointer;
import org.bridj.ann.CLong;
import org.bridj.ann.Library;
import org.bridj.ann.Optional;
import org.bridj.ann.Ptr;
import org.bridj.ann.Runtime;

/**
 * Wrapper for library <b>dyncall</b><br>
 * This file was autogenerated by <a
 * href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a
 * href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few
 * opensource projects.</a>.<br>
 * For help, please visit <a
 * href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a
 * href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("bridj")
@Runtime(CRuntime.class)
public class DyncallLibrary {

    static {
        BridJ.register();
    }
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_FLOAT = (char) 'f';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_DEFAULT = (int) 0;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_PPC32_OSX = (int) 9;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_PPC32_SYSV = (int) 13;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_PPC32_DARWIN = (int) 9;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_ERROR_UNSUPPORTED_MODE = (int) -1;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_ELLIPSIS_VARARGS = (int) 101;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X86_WIN32_THIS_MS = (int) 5;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_ARM_ARM_EABI = (int) 10;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_SYS_X86_INT80H_BSD = (int) 202;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_CC_ELLIPSIS = (char) 'e';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X64_SYSV = (int) 8;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_POINTER = (char) 'p';
    /// <i>native declaration : dyncall.h</i>
    public static final int DEFAULT_ALIGNMENT = (int) 0;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_CC_FASTCALL_GNU = (char) 'f';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_UINT = (char) 'I';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_ENDARG = (char) ')';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_VOID = (char) 'v';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_UCHAR = (char) 'C';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_MIPS32_O32 = (int) 16;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_INT = (char) 'i';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_SYS_X86_INT80H_LINUX = (int) 201;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_DOUBLE = (char) 'd';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X64_WIN64 = (int) 7;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_SPARC32 = (int) 20;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_ARM_THUMB_EABI = (int) 11;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_STRUCT = (char) 'T';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X86_WIN32_THIS_GNU = (int) 6;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_SYS_DEFAULT = (int) 200;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_CC_STDCALL = (char) 's';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_ELLIPSIS = (int) 100;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X86_PLAN9 = (int) 19;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_ARM_THUMB = (int) 15;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_CC_FASTCALL_MS = (char) 'F';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_STRING = (char) 'Z';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_MIPS32_EABI = (int) 12;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X86_WIN32_FAST_GNU = (int) 4;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_LONGLONG = (char) 'l';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_SHORT = (char) 's';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_ULONGLONG = (char) 'L';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_ERROR_NONE = (int) 0;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_SPARC64 = (int) 21;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_PPC32_LINUX = (int) 13;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_ULONG = (char) 'J';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_CHAR = (char) 'c';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_CC_PREFIX = (char) '_';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_LONG = (char) 'j';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_MIPS64_N32 = (int) 17;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X86_WIN32_STD = (int) 2;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_CC_THISCALL_MS = (char) '+';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X86_CDECL = (int) 1;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_X86_WIN32_FAST_MS = (int) 3;
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_ARM_ARM = (int) 14;
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_USHORT = (char) 'S';
    /// <i>native declaration : dyncall_signature.h</i>
    public static final char DC_SIGCHAR_BOOL = (char) 'B';
    /// <i>native declaration : dyncall.h</i>
    public static final int DC_CALL_C_MIPS64_N64 = (int) 18;

    public static native Pointer<DyncallLibrary.DCCallVM> dcNewCallVM(@Ptr long size);

    public static native void dcFree(Pointer<DyncallLibrary.DCCallVM> vm);

    public static native void dcReset(Pointer<DyncallLibrary.DCCallVM> vm);

    public static native void dcMode(Pointer<DyncallLibrary.DCCallVM> vm, int mode);

    public static native void dcArgBool(Pointer<DyncallLibrary.DCCallVM> vm, int value);

    public static native void dcArgChar(Pointer<DyncallLibrary.DCCallVM> vm, byte value);

    public static native void dcArgShort(Pointer<DyncallLibrary.DCCallVM> vm, short value);

    public static native void dcArgInt(Pointer<DyncallLibrary.DCCallVM> vm, int value);

    public static native void dcArgLong(Pointer<DyncallLibrary.DCCallVM> vm, @CLong long value);

    public static native void dcArgLongLong(Pointer<DyncallLibrary.DCCallVM> vm, long value);

    public static native void dcArgFloat(Pointer<DyncallLibrary.DCCallVM> vm, float value);

    public static native void dcArgDouble(Pointer<DyncallLibrary.DCCallVM> vm, double value);

    public static native void dcArgPointer(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> value);

    @Optional
    public static native void dcArgStruct(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<DyncallLibrary.DCstruct> s, Pointer<?> value);

    public static native void dcCallVoid(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native int dcCallBool(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native byte dcCallChar(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native short dcCallShort(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native int dcCallInt(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    @CLong
    public static native long dcCallLong(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native long dcCallLongLong(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native float dcCallFloat(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native double dcCallDouble(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    public static native Pointer<?> dcCallPointer(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr);

    @Optional
    public static native void dcCallStruct(Pointer<DyncallLibrary.DCCallVM> vm, Pointer<?> funcptr, Pointer<DyncallLibrary.DCstruct> s, Pointer<?> returnValue);

    public static native int dcGetError(Pointer<DyncallLibrary.DCCallVM> vm);

    @Optional
    public static native Pointer<DyncallLibrary.DCstruct> dcNewStruct(@Ptr long fieldCount, int alignment);

    @Optional
    public static native void dcStructField(Pointer<DyncallLibrary.DCstruct> s, int type, int alignment, @Ptr long arrayLength);

    @Optional
    public static native void dcSubStruct(Pointer<DyncallLibrary.DCstruct> s, @Ptr long fieldCount, int alignment, @Ptr long arrayLength);

    @Optional
    public static native void dcCloseStruct(Pointer<DyncallLibrary.DCstruct> s);

    @Optional
    @Ptr
    public static native long dcStructSize(Pointer<DyncallLibrary.DCstruct> s);

    @Optional
    @Ptr
    public static native long dcStructAlignment(Pointer<DyncallLibrary.DCstruct> s);

    @Optional
    public static native void dcFreeStruct(Pointer<DyncallLibrary.DCstruct> s);

    @Optional
    public static native Pointer<DyncallLibrary.DCstruct> dcDefineStruct(Pointer<Byte> signature);

    /// Undefined type
    public static interface DCstruct {
    };
    /// Undefined type

    public static interface DCCallVM {
    };
}
