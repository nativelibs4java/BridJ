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
import java.io.File;
import org.bridj.ann.Constructor;
import org.bridj.ann.Library;
import org.bridj.ann.Virtual;
import org.bridj.cpp.CPPObject;

import org.bridj.*;
import static org.bridj.Pointer.*;
import org.bridj.ann.Field;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

@Library("test") 
public class CPPTest2 {
    
    public static class Module extends CPPObject {
        @Virtual
        public native int add(int a, int b);
    }
    
    public static class IModule extends CPPObject {
        public IModule() {
            super();
        }
        @Virtual(2) 
        public native int add(int a, int b);
        @Virtual(3) 
        public native int subtract(int a, int b);
    }
    
    public static class AModule extends IModule {
        public AModule() {
            super();
        }
        //@Name("AModule") 
        //public native int AModule$2();
        @Virtual(2) 
        public native int add(int a, int b);
        @Virtual(3) 
        public native int subtract(int a, int b);
    }
    
    @Test
    public void test() {
        
        IModule m = new AModule(), i = getPointer(m).as(IModule.class).get();
        
		assertEquals("AModule.add failed", 3, m.add(1, 2));
		assertEquals("AModule.subtract failed", -1, m.subtract(1, 2));
        
        assertEquals("IModule.add failed", 3, i.add(1, 2));
		assertEquals("IModule.subtract failed", -1, i.subtract(1, 2));
		
        assertEquals("Module.add failed", 3, new Module().add(1, 2));
        
        
    }
    
    @Library("test") 
    public abstract class IVirtual extends CPPObject {
        public IVirtual() {
            super();
        }
        @Virtual(2)
        public native int add(int a, int b);
    }
    public static native int testIVirtualAdd(Pointer<IVirtual> pVirtual, int a, int b);
    
    @Ignore
    @Test
 	public void testPureVirtual() {
 		BridJ.register(getClass());
 		
 		IVirtual v = new IVirtual() {
 			@Override
 			public int add(int a, int b) {
 				return a * 10 + b * 100;	
 			}
 		};
 		assertEquals(210, testIVirtualAdd(getPointer(v), 1, 2));
 	}

  public static class Hello extends CPPObject {
    @Constructor(0)
    public Hello(Pointer<Integer> a, Pointer<Integer> b) {
      super((Void)null, 0, a, b);
    }
    public native int Sum();
    @Field(0)
    public int a() {
        return io.getIntField(this, 0);
    }
    @Field(1)
    public int b() {
        return io.getIntField(this, 1);
    }
  }

  @Test
  public void testHello() {
    Hello h = new Hello(pointerToInt(1), pointerToInt(2));
    assertEquals(1, h.a());
    assertEquals(2, h.b());
    assertEquals(3, h.Sum());
  }
}
