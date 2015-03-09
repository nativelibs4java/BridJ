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

import java.io.IOException;
import org.bridj.cpp.CPPRuntime.CPPTypeInfo;
import org.junit.Test;
import static org.junit.Assert.*;

import org.bridj.cpp.*;
import org.bridj.cpp.std.*;
import static org.bridj.Pointer.*;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class STLTest {
    <T> DynamicFunction<T> getTestFunction(String name, Type ret, Type... args) {
        try {
            NativeLibrary lib = BridJ.getNativeLibrary("test");
            Pointer<?> sym = lib.getSymbolPointer(name);
            if (sym == null)
            sym = lib.getSymbolPointer("_" + name);
            assertNotNull("Symbol " + name + " not found", sym);
            Pointer<?> ptr = sym.getPointer();
            return ptr.asDynamicFunction(null, ret, args);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get test function " + name + ": " + ex, ex);
        }
    }
    DynamicFunction<Pointer<?>> new_int_vector = getTestFunction("new_int_vector", Pointer.class, int.class);
    DynamicFunction<Integer> int_vector_get = getTestFunction("int_vector_get", int.class, Pointer.class, int.class);
    DynamicFunction<Void> int_vector_push_back = getTestFunction("int_vector_push_back", void.class, Pointer.class, int.class);
    DynamicFunction<Void> int_vector_set = getTestFunction("int_vector_set", void.class, Pointer.class, int.class, int.class);
    DynamicFunction<Void> int_vector_resize = getTestFunction("int_vector_get", void.class, Pointer.class, int.class);
    DynamicFunction<SizeT> sizeof_int_vector = getTestFunction("sizeof_int_vector", SizeT.class);

    DynamicFunction<Pointer<?>> new_int_list = getTestFunction("new_int_list", Pointer.class);
    DynamicFunction<Void> int_list_push_back = getTestFunction("int_list_push_back", void.class, Pointer.class, int.class);

	@Test
	public void testVector() throws Exception {
		Type intVectorType = CPPType.getCPPType(vector.class, int.class);
		assertEquals("bad vector<int> size !",
					 sizeof_int_vector.apply().longValue(), 
					 BridJ.sizeOf(intVectorType));
        CPPTypeInfo<vector<Integer>> typeInfo = (CPPTypeInfo)CPPRuntime.getInstance().getCPPTypeInfo(intVectorType);
        System.out.println("Type info for " + intVectorType + ": " + typeInfo);
        
        int n = 10;
        Pointer<?> nativeVector = new_int_vector.apply(n);
        vector<Integer> bridjVector = new vector<Integer>((Pointer)nativeVector, Integer.class);
        assertEquals("bridj vector failed to compute size", n, bridjVector.size());
        for (int i = 0; i < 10; i++) {
            int v = int_vector_get.apply(nativeVector, i);
            assertEquals("native vector not built as expected", i, v);
            assertEquals("bridj vector failed to get element at index " + i, i, (int)bridjVector.get(i));
        }
        int_vector_push_back.apply(nativeVector, -1);
        assertEquals("bad size after push_back", n + 1, bridjVector.size());
        assertEquals("bad back", -1, (int)bridjVector.back());
        int_vector_push_back.apply(nativeVector, -2);
        assertEquals("bad size after push_back", n + 2, bridjVector.size());
        assertEquals("bad back", -2, (int)bridjVector.back());
    }
    
    @Test
	public void testList() throws Exception {
        int n = 10;
        Pointer<?> nativeList = new_int_list.apply();
        list<Integer> bridjList = new list<Integer>((Pointer)nativeList, Integer.class);
        assertTrue("bad empty()", bridjList.empty());
//            assertEquals("bad size()", 0, bridjList.size());
        int_list_push_back.apply(nativeList, 66);
        assertFalse("bad empty()", bridjList.empty());
        assertEquals("first front()", 66, (int)bridjList.front());
        assertEquals("first back()", 66, (int)bridjList.back());
        
        int_list_push_back.apply(nativeList, 65);
        assertEquals("second front()", 66, (int)bridjList.front());
        assertEquals("second back()", 65, (int)bridjList.back());

        for (int i = n; --i != 0;) {
            int_list_push_back.apply(nativeList, i);
            assertEquals("bad back() at index " + i, i, (int)bridjList.back());
        }
    }
}

