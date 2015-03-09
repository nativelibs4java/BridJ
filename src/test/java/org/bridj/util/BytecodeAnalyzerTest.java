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
package org.bridj.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.bridj.util.BytecodeAnalyzer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ochafik
 */
public class BytecodeAnalyzerTest {
    public static class MyStruct {
        int a, b, c;
        
        public static class SubStruct {
            int sa, sb, sc;
            public native void subNative();
        }
        public static class SubStructDeriv extends SubStruct {
            int sa2, sb2, sc2;
            public native void subSubNative();
        }
    }
    
    @Test
    public void testFields() throws IOException {
        assertEquals(Arrays.asList("a", "b", "c"), BytecodeAnalyzer.getFieldNames(MyStruct.class, Object.class));
        assertEquals(Arrays.asList("sa", "sb", "sc"), BytecodeAnalyzer.getFieldNames(MyStruct.SubStruct.class, Object.class));
        
        assertEquals(Arrays.asList("sa2", "sb2", "sc2"), BytecodeAnalyzer.getFieldNames(MyStruct.SubStructDeriv.class, MyStruct.SubStructDeriv.class));
        assertEquals(Arrays.asList("sa", "sb", "sc", "sa2", "sb2", "sc2"), BytecodeAnalyzer.getFieldNames(MyStruct.SubStructDeriv.class, Object.class));
        
        List<String[]> sigs = BytecodeAnalyzer.getNativeMethodSignatures(MyStruct.SubStructDeriv.class);
        assertEquals(2, sigs.size());
        assertEquals("subNative", sigs.get(0)[1]);
        assertEquals("subSubNative", sigs.get(1)[1]);
    }
    
    @Test
    public void testMethods() throws IOException {
        assertEquals(Arrays.asList("<init>"), BytecodeAnalyzer.getMethodNames(MyStruct.class, Object.class));
    }
}
