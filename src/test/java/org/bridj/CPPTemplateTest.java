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
package org.bridj;

import org.junit.Test;

import org.bridj.ann.Constructor;
import org.bridj.ann.Template;
import org.bridj.ann.Library;
import org.bridj.ann.Runtime;
import org.bridj.cpp.*;

import java.lang.reflect.Type;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html
@Library("test")
@Runtime(CPPRuntime.class)
public class CPPTemplateTest {
	static {
		BridJ.register();
	}

	@Template({ Integer.class, Type.class })
	public static class InvisibleSourcesTemplate<T> extends CPPObject {
		public final int n;
        
		public InvisibleSourcesTemplate(int n, Type t) {
            super((Void)null, -1, n, t);
            this.n = n;
        }
		        
		@Constructor(0)
		public InvisibleSourcesTemplate(int n, Type t, int arg) {
			super((Void)null, 0, n, t, arg);
			this.n = n;
		}
		public native Pointer<T> createSome();
		public native void deleteSome(Pointer<T> pValue);
	}
	@Template({ Class.class })
	public static class Temp1<T> extends CPPObject { }
	
	@Template({ Class.class, Class.class })
	public static class Temp2<T1, T2> extends CPPObject { }
	
	@Template({ Class.class, Integer.class })
	public static class TempV<T> extends CPPObject { }
	
	@Test
	public void invisibleSourcesTemplateIntegerTest() {
		invisibleSourcesTemplateTest(Integer.class);
	}
	<T> void invisibleSourcesTemplateTest(Class<T> t) {
		InvisibleSourcesTemplate<T> ii = new InvisibleSourcesTemplate(10, t, 4);
		Pointer<T> p = ii.createSome();
        T v = p.as(t).get();
		System.out.println("Template created value : " + v);
		ii.deleteSome(p);
	}
}

