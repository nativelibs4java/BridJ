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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp.com;

import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.ValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Virtual;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author Olivier
 */
@IID("00020400-0000-0000-C000-000000000046")
public class IDispatch extends IUnknown {
	public static class DISPPARAMS extends StructObject {
		@Field(0)
		public native Pointer<VARIANT> rgvarg();
		@Field(1)
		public native Pointer<Integer> rgdispidNamedArgs();
		@Field(2)
		public native int cArgs();
        @Field(3)
		public native int cNamedArgs();
	}
	
	
	public static class EXCEPINFO extends StructObject {
		@Field(0)
		public native short wCode();
		@Field(1)
		public native short wReserved();
		@Field(2)
		public native Pointer<Character> bstrSource();
		@Field(3)
		public native Pointer<Character> bstrDescription();
		@Field(4)
		public native Pointer<Character> bstrHelpFile();
		@Field(5)
		public native int dwHelpContext();
		@Field(6)
		public native Pointer<?> pvReserved();
		@Field(7)
		public native Pointer<?> pfnDeferredFillIn();//HRESULT (__stdcall *pfnDeferredFillIn)(struct tagEXCEPINFO *);
		@Field(8)
		public native int scode();
	}
	@Virtual(0)
	public native int GetTypeInfoCount(Pointer<Integer> pctinfo);

	@Virtual(1)
	public native int GetTypeInfo(int iTInfo, int lcid, Pointer<Pointer<ITypeInfo>> ppTInfo);

	@Virtual(2) 
	public native int GetIDsOfNames(
		Pointer riid,//REFIID riid,
		Pointer<Pointer<Character>> rgszNames,
		int cNames,
		int lcid, //LCID lcid,
		Pointer<Integer> rgDispId); //DISPID *rgDispId);

	@Virtual(3)
	public native int Invoke(
		int dispIdMember,
		Pointer<Byte> riid,
		int lcid,
		short wFlags,
		Pointer<DISPPARAMS> pDispParams,
		Pointer<VARIANT> pVarResult,
		Pointer<EXCEPINFO> pExcepInfo,
		Pointer<Integer> puArgErr
	);
}
