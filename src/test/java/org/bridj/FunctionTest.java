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

import org.bridj.ann.Name;
import org.bridj.ann.Library;
//import com.sun.jna.Native;
import java.util.Collections;
import java.util.Iterator;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Olivier
 */
@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
public class FunctionTest {
	
	@Before
    public void register() {
		BridJ.register(getClass());
	}
    @After
    public void unregister() {
	//	BridJ.releaseAll();
		System.gc();
		try {
			Thread.sleep(200);
		} catch (Exception ex) {}
		System.gc();
	}
    public native int testAddDyncall(int a, int b);
    
    @Name("testAddDyncall")
    public native int fooBar(int a, int b);
    
    public enum ETest implements IntValuedEnum<ETest> {
    	eFirst(0),
    	eSecond(1),
    	eThird(2);
    	
    	ETest(int value) {
    		this.value = value;
    	}
    	final int value;
    	public long value() {
    		return value;
    	}
		public Iterator<ETest> iterator() {
			return Collections.singleton(this).iterator();
		}
		public static ValuedEnum<ETest> fromValue(long value) {
			return FlagSet.fromValue(value, values());
		}
    }
    public static native ValuedEnum<ETest> testEnum(ValuedEnum<ETest> e);
    @Name("testEnum")
    public static native IntValuedEnum<ETest> testIntEnum(IntValuedEnum<ETest> e);
    
    @Test
    public void add() {
		int res = testAddDyncall(10, 4);
		assertEquals(14, res);
    }

    @Test
    public void renamedAdd() {
		int res = fooBar(10, 4);
		assertEquals(14, res);
    }

    @Test
    public void testEnumCalls() {
        for (ETest e : ETest.values()) {
            ValuedEnum<ETest> r = testEnum(e);
            assertEquals(e.value(), r.value());
            
            r = testIntEnum(e);
            assertEquals(e.value(), r.value());
            //assertEquals(e, r);
        }
    }
    @Test
    public void enu() {
    	for (ETest v : ETest.values())
    	{
	    	FlagSet<ETest> e = FlagSet.fromValues(v);
	    	assertNotNull(e);
	    	assertEquals(v.value(), e.value());
	    	ETest[] values = e.getEnumClassValues();
	    	assertEquals(1, values.length);
	    	assertEquals(v, values[0]);
    	}
    }
}
