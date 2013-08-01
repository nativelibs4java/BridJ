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
package org.bridj.cpp.mfc;

import org.bridj.Pointer;
import org.bridj.ann.Field;
import org.bridj.func.Fun0;

public class CRuntimeClass extends MFCObject {
    // Attributes

    @Field(0)
    public native Pointer<Byte> m_lpszClassName();

    public native void m_lpszClassName(Pointer<Byte> m_lpszClassName);

    @Field(1)
    public native int m_nObjectSize();

    public native void m_nObjectSize(int m_nObjectSize);

    @Field(2)
    public native int m_wSchema(); // schema number of the loaded class

    public native void m_wSchema(int m_wSchema); // schema number of the loaded class

    @Field(3)
    public native Pointer<Fun0<Pointer<CObject>>> m_pfnCreateObject(); // NULL => abstract class

    public native void m_pfnCreateObject(Pointer<Fun0<Pointer<CObject>>> m_pfnCreateObject); // NULL => abstract class

    /*#ifdef _AFXDLL
     CRuntimeClass* (PASCAL* m_pfnGetBaseClass)();
     #else
     CRuntimeClass* m_pBaseClass;
     #endif
     */
    @Field(4)
    public native Pointer<CRuntimeClass> m_pBaseClass();

    public native void m_pBaseClass(Pointer<CRuntimeClass> m_pBaseClass);

// Operations
    public native Pointer<CObject> CreateObject();

    public native boolean IsDerivedFrom(Pointer<CRuntimeClass> pBaseClass);

    // dynamic name lookup and creation
    public native static Pointer<CRuntimeClass> FromName(Pointer<Byte> /*LPCSTR*/ lpszClassName);

    public native static Pointer<CRuntimeClass> FromName$2(Pointer<Character> lpszClassName);

    public native static Pointer<CObject> CreateObject(Pointer<Byte> lpszClassName);

    public native static Pointer<CObject> CreateObject$2(Pointer<Character> lpszClassName);

    // Implementation
    public native void Store(Pointer<CArchive> ar);

    public native static Pointer<CRuntimeClass> Load(Pointer<CArchive> ar, Pointer<Integer> pwSchemaNum);

    // CRuntimeClass objects linked together in simple list
    @Field(5)
    public native Pointer<CRuntimeClass> m_pNextClass();       // linked list of registered classes

    public native void m_pNextClass(Pointer<CRuntimeClass> m_pNextClass);       // linked list of registered classes

    @Field(6)
    public native Pointer<?> /*AFX_CLASSINIT*/ m_pClassInit();

    public native void m_pClassInit(Pointer<?> m_pClassInit);
}
