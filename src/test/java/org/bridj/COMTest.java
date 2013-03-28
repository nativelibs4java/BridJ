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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

import org.bridj.ann.*;
import static org.junit.Assert.*;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.IUnknown;
import org.bridj.cpp.com.VARIANT;
import org.bridj.cpp.com.CLSID;
import org.bridj.cpp.com.IID;
import org.bridj.cpp.com.shell.IShellWindows;

public class COMTest {

	static boolean hasCOM = Platform.isWindows();// || !Platform.is64Bits();
	

	@Test
	public void shellFolder() {
		if (!hasCOM)
            return;
        try {
            IShellWindows win = COMRuntime.newInstance(IShellWindows.class);
            assertNotNull(win);
            IUnknown iu = win.QueryInterface(IUnknown.class);
            assertNotNull(iu);
            win = iu.QueryInterface(IShellWindows.class);
            assertNotNull(win);
            win.Release();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(COMTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
	}
	
	static class SomeUnknown extends IUnknown {
		//public SomeUnknown() {}
		@Override
		public int QueryInterface(Pointer<Byte> riid, Pointer<Pointer<IUnknown>> ppvObject) {
			return 0;
		}
		int refs;
		@Override
		public synchronized int AddRef() { return ++refs; } 
		
		@Override
		public synchronized int Release() { return --refs; }
    }

    @Test
    public void testSomeUnknownInstantiation() {
    		new SomeUnknown();
    }
    
    
    @CLSID("62BE5D10-60EB-11d0-BD3B-00A0C911CE86") 
    @IID("29840822-5B84-11D0-BD3B-00A0C911CE86") 
    public static class ICreateDevEnum extends IUnknown { 
        @Virtual(0) 
        public native int CreateClassEnumerator(Pointer<?>  clsidDeviceClass, Pointer<Pointer<?>> enumerator, int flags); 
    } 
    
    @Test
    public void testICreateDevEnum() throws Exception {
    	if (!hasCOM) 
    		return;
    	
    	//Not needed, as it's called by COMRuntime.newInstance : COMRuntime.initialize(); 
    	ICreateDevEnum devEnumCreator = COMRuntime.newInstance(ICreateDevEnum.class);
    }
    
    @Test
    public void testVARIANTSize() {
    	if (!hasCOM)
    		return;
    	long size = BridJ.sizeOf(VARIANT.class);
    	long expectedSize = Platform.is64Bits() ? 24 : 16;
    	assertEquals("Invalid size for VARIANT", expectedSize, size);
    }
}
