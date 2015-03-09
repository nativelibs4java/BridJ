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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import org.bridj.objc.*;
import org.bridj.ann.Library;
import org.bridj.ann.Runtime;
import org.bridj.ann.Ptr;
import static org.bridj.Pointer.*;
import static org.bridj.objc.FoundationLibrary.*;
import java.util.*;
import static org.bridj.ObjectiveCTest.TestLib.*;

@Library("Foundation")
@Runtime(ObjectiveCRuntime.class)
public class ObjectiveCTest {
	static boolean mac = Platform.isMacOSX() && (System.getenv("BRIDJ_NO_OBJC") == null);
	static {
        if (mac)
            try {
                BridJ.register();
            } catch (Throwable th) {
                if (mac)
                    throw new RuntimeException(th);
            }
	}
	
    protected Pointer<NSAutoreleasePool> pool;
    
    @Before
    public void init() {
    	if (!mac) return;
        pool = NSAutoreleasePool.new_();
		assertNotNull(pool);
    }

    @After
    public void cleanup() {
        if (!mac) return;
        pool.get().drain();
    }
    //*
	@Test 
	public void testNSNumber() {
		if (!mac) return;
        
		long n = 13;
		Pointer<NSNumber> pnn = NSNumber.numberWithLong(n);
		//System.out.println("pnn = " + pnn);
		NSNumber nn = pnn.get();
		//System.out.println("nn = " + nn);
		assertEquals(n + "", nn.toString());
		assertEquals(n, nn.shortValue());   
		assertEquals(n, nn.intValue());   
		assertEquals(n, nn.longValue());   
		assertEquals(n, nn.floatValue(), 0);    
		assertEquals(n, nn.doubleValue(), 0);       
	}
	
	@Library("Foundation")
	public static class NSWorkspace extends NSObject
	{
		public static native Pointer<NSWorkspace> sharedWorkspace();
		
		public native Pointer<?> runningApplications();
	}
	@Test
	public void testNSWorkspace() {
		if (!mac) return;
		
		BridJ.register(NSWorkspace.class);
		Pointer<NSWorkspace> pWorkspace = NSWorkspace.sharedWorkspace();
		assertNotNull(pWorkspace);
		
		NSWorkspace workspace = pWorkspace.get();
		assertNotNull(workspace);
	}
    
    @Test
    public void testNSString() {
        if (!mac) return;
        for (String s : new String[] { "", "1", "ha\nha\u1234" }) {
            assertEquals(s, pointerToNSString(s).get().toString());
            
            NSString ns = new NSString(s);
            assertEquals(s, ns.toString());
            assertEquals(s.length(), ns.length());
        }
    }
    
    @Test
    public void testSEL() {
        if (!mac) return;
        for (String s : new String[] { "", "1", "ha:ha" }) {
            SEL sel = SEL.valueOf(s);
            assertEquals(s, sel.getName());
        }
    }
    
    @Test
    public void testNSDictionary() {
        if (!mac) return;
        Map<String, NSObject> map = new HashMap<String, NSObject>(), map2;
    		for (String s : new String[] { "", "1", "ha\nha\u1234" })
    			map.put(s, NSString.valueOf(s + s));
    		
    		NSDictionary dic = NSDictionary.valueOf(map);
    		assertEquals(map.size(), dic.count());
    		
    		map2 = dic.toMap();
    		assertEquals(map.size(), map2.size());
    		
    		//assertEquals(map, map2);
    		for (Map.Entry<String, NSObject> e : map.entrySet()) {
    			String key = e.getKey();
    			NSObject expected = e.getValue();
    			NSObject got = map2.get(key);
    			assertEquals(expected, got);
    			//assertEquals(expected.toString(), got.toString());
    		}
    }
    
    public static class NSNonExistentTestClass extends NSObject {
            public native int add2(int a, Pointer<Integer> p);
			public native float incf(float v);
            public native double add8(byte a, short b, int c, char d, long e, double f, Pointer<Integer> p);
    }
    static void test_NSNonExistentTestClass_add(ObjCObject proxy) {
    	if (!mac) return;
        NSNonExistentTestClass p = getPointer(proxy).as(NSNonExistentTestClass.class).get();
        Pointer<Integer> ptr = pointerToInt(64);
        assertEquals(1 + ptr.get(), p.add2(1, ptr));
		assertEquals(127, p.add8((byte)1, (short)2, (int)4, (char)8, (long)16, (double)32, ptr), 0);
    }
    String DESCRIPTION = "WHATEVER !!!";
    @Test
    public void testProxy() {
        if (!mac) return;
        ObjCProxy proxy = new ObjCProxy() {
			public Pointer<NSString> description() {
				return pointerToNSString(DESCRIPTION);
			}
            public int add2(int a, Pointer<Integer> p) {
                return a + p.get();
            }
			public float incf(float v) {
                return v + 1;
            }
			public double add8(long a, int b, short c, byte d, char e, double f, Pointer<Integer> p) {
				return a + b + c + d + e + f + p.get();
			}
		};
		test_NSNonExistentTestClass_add(proxy);
        test_NSNonExistentTestClass_add(new ObjCProxy(proxy));
    }
    
    @Ignore
    @Test
    public void testProxyFloat() {
        if (!mac) return;
        Object proxy = new Object() {
            public float incf(float v) {
                return v + 1;
            }
		};
        NSNonExistentTestClass p = getPointer(new ObjCProxy(proxy)).as(NSNonExistentTestClass.class).get();
        System.out.println(p.description().get());
        assertEquals(11, p.incf(10), 0);
    }
    //*/
    public static class NSEvent extends NSObject {
       //@Selector("addGlobalMonitorForEventsMatchingMask:handler:")
	   public static native Pointer addGlobalMonitorForEventsMatchingMask_handler(long mask, Pointer<NSEventGlobalCallback> handler);
	}
	
	public abstract static class NSEventGlobalCallback extends ObjCBlock {
		public abstract void callback(Pointer<NSEvent> event);
	}

    @Ignore
    @Test
    public void testGlobalNSEventHook() throws Exception {
    	if (!mac) return;
        BridJ.register(NSEvent.class);

        final boolean called[] = new boolean[1];
        NSEventGlobalCallback handler = new NSEventGlobalCallback() {
			@Override
			public void callback(Pointer<NSEvent> event) {
				System.out.println("Event: " + event);
				called[0] = true;
			}
		};

        //System.out.println("handler: " + handler);

        Pointer hook = NSEvent.addGlobalMonitorForEventsMatchingMask_handler(-1L/*1 << 1*/, getPointer(handler));

        //System.out.println("hook: " + hook);
        
        Thread.sleep(10000);
        
        assertTrue(called[0]);
   }
   //*
   @Library("test")
   public static class TestLib {
       public static interface Delg extends ObjCDelegate {
           int add_to(int a, int b);
       }
       public static class DelgImpl extends NSObject implements Delg {
           public native int add_to(int a, int b);
       }
       public static class DelgHolder extends NSObject {
           public native void setDelegate(Pointer<Delg> delegate);
           public native int outerAdd_to(int a, int b);
       }
       public static abstract class FwdBlock extends ObjCBlock {
           public abstract int apply(int a, int b);
       }
       public static native int forwardBlockCallIntIntInt(Pointer<FwdBlock> block, int a, int b);
   }
   
   static void testDelegate(Delg delegate) {
       DelgHolder holder = new DelgHolder();
       holder.setDelegate(getPointer(delegate));
       
       int a = 10, b = 20, expected = a + b;
       int res = holder.outerAdd_to(a, b);
       //System.out.println(Delg.class.getName() + ".add[through delegate](" + a + ", " + b + ") = " + res);
       assertEquals(expected, res);
   }
   
   @Test
   public void testNativeDelegate() {
        if (!mac) return;
       testDelegate(new DelgImpl());
   }
   static class MyDelg extends ObjCProxy implements Delg {
        @Override
        public int add_to(int a, int b) {
            return a + b;
        }
   }
   @Test
   public void testJavaDelegate() {
        if (!mac) return;
       testDelegate(new MyDelg());
   }
   
   @Test
   public void testBlock() {
        if (!mac) return;
       FwdBlock block = new FwdBlock() {
            @Override
            public int apply(int a, int b) {
                return a + b;
            }
       };
       int a = 10, b = 20, expected = a + b;
       int res = forwardBlockCallIntIntInt(getPointer(block), a, b);
       assertEquals(expected, res);
   }
   //*/
}
