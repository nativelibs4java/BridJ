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
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

import org.bridj.ann.*;
import org.bridj.cpp.CPPObject;
import org.bridj.cpp.CPPRuntime;
import org.bridj.cpp.CPPType;
import static org.junit.Assert.*;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.IUnknown;
import org.bridj.cpp.com.VARIANT;
import org.bridj.cpp.com.CLSID;
import org.bridj.cpp.com.IID;
import org.bridj.cpp.com.shell.IShellWindows;

public class CPPRuntimeTest {
    CPPRuntime runtime;
    public CPPRuntimeTest() {
        runtime = new CPPRuntime();
    }
    
    @Template({ Type.class, Type.class })
    public static class Temp<A, B> extends CPPObject {}
    
    @Test
    public void testType() {
        int[] targsCount = new int[1];
        Object[] targsAndArgs = new Object[] { int.class, double.class };
        Type expected = CPPType.getCPPType(Temp.class, int.class, double.class);
        
        Type actual = runtime.getType(Temp.class, targsAndArgs, targsCount);
        assertEquals(expected, actual);
        assertEquals(2, targsCount[0]);
        
        actual = runtime.getType(Temp.class, targsAndArgs, null);
        assertEquals(expected, actual);
    }
}