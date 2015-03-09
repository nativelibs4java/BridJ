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

import java.lang.reflect.Method;
import org.bridj.ann.Ptr;
import org.junit.Test;

import static org.junit.Assert.*;

public class CRuntimeTest {
    
	public static abstract class SimpleObjectCallback extends Callback<SimpleObjectCallback > {
		public abstract void apply(Pointer<? > voidPtr1);
	};
    
	public static abstract class SimpleRawCallback extends Callback<SimpleRawCallback > {
		public abstract void apply(@Ptr long voidPtr1);
	};
    
	public static abstract class MixedCallback extends Callback<MixedCallback > {
		public void apply(Pointer<? > voidPtr1) {
			apply(Pointer.getPeer(voidPtr1));
		}
		public void apply(@Ptr long voidPtr1) {
            apply(Pointer.pointerToAddress(voidPtr1));
        }
	};
    
    @Test(expected = RuntimeException.class)
    public void notExtendedMixedCallback() {
        // Accept either:
        // - Only 1 abstract method, 
        // - Two methods with same name, implemented once together and only one of them reimplemented below in the hierarchy
        CRuntime.getInstance().getUniqueCallbackMethod(MixedCallback.class);
    }
    
    @Test
    public void notExtendedMixedCallback2() {
        assertNotNull(CRuntime.getInstance().getFastestCallbackMethod(MixedCallback.class));
    }

    @Test
    public void objectMixedCallback() {
        testCalls(new CallsTester() {
            public Callback createCallback(final Runnable notify) {
                return new MixedCallback() {
                    @Override
                    public void apply(Pointer<?> voidPtr1) {
                        notify.run();
                    }
                };
            }
        });
    }
    @Test
    public void rawMixedCallback() {
        testCalls(new CallsTester() {
            public Callback createCallback(final Runnable notify) {
                return new MixedCallback() {
                    @Override
                    public void apply(@Ptr long voidPtr1) {
                        notify.run();
                    }
                };
            }
        });
    }

    @Test
    public void objectCallback() {
        testCalls(new CallsTester() {
            public Callback createCallback(final Runnable notify) {
                return new SimpleObjectCallback() {
                    public void apply(Pointer<?> voidPtr1) {
                        notify.run();
                    }
                };
            }
        });
    }
    @Test
    public void rawCallback() {
        testCalls(new CallsTester() {
            public Callback createCallback(final Runnable notify) {
                return new SimpleRawCallback() {
                    @Override
                    public void apply(@Ptr long voidPtr1) {
                        notify.run();
                    }
                };
            }
        });
    }
    
    public static interface CallsTester {
        Callback createCallback(Runnable notify);
    }
    void testCalls(CallsTester c) {
        final int[] calls = new int[1];
        Callback cb = c.createCallback(new Runnable() {
            public void run() {
                calls[0]++;
            }
        });
        assertNotNull(CRuntime.getInstance().getUniqueCallbackMethod(cb.getClass()));
        Pointer.getPointer(cb).asDynamicFunction(null, void.class, SizeT.class).apply(new SizeT(0L));
        assertEquals(1, calls[0]);
    }

	public static abstract class AbstractCallback extends Callback<AbstractCallback> {
        public abstract int apply(int foo);
    }

    private static class FooCallback extends AbstractCallback {
        @Override
        public int apply(int foo) {
            return foo;
        }

        private void m1() { }
        private void m2() { }
    }

    @Test
    public void testSimpleOldStyleCallback() {
        new FooCallback();
    }
}
