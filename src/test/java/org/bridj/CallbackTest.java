package org.bridj;
import static org.bridj.Pointer.*;
import org.junit.Test;
import org.bridj.ann.Runtime;
import static org.junit.Assert.*;

import org.bridj.ann.Library;

@Library("test")
@Runtime(CRuntime.class)
public class CallbackTest {
	static {
		BridJ.register();
	}
	
    @Test
    public void deleteTest() {
        MyCallback cb = new MyCallback() {
            @Override
            public int doSomething(int a, int b) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        pointerTo(cb).release();
    }
	@Test
	public void testJavaTargetIntCallbacks() {
		assertEquals(3, forwardCall(new MyCallback() {
			@Override
			public int doSomething(int a, int b) {
				return a + b;
			}
		}.toPointer(), 1, 2));
		
		assertEquals(21, forwardCall(new MyCallback() {
			@Override
			public int doSomething(int a, int b) {
				return a + b * 10;
			}
		}.toPointer(), 1, 2));
	}
	
	@Test
	public void testNativeTargetIntCallbacks() {
		MyCallback adder = getAdder().getNativeObject(MyCallback.class);
		assertEquals(3, adder.doSomething(1, 2));
	}
	
	static native int forwardCall(Pointer<MyCallback> cb, int a, int b);
	static native Pointer<MyCallback> getAdder();
	
	public static abstract class MyCallback extends Callback {
		public abstract int doSomething(int a, int b); 
	}
	
	
	
	
	@Test
	public void testJavaTargetPtrCallbacks() {
		assertEquals(3, forwardPtrCall(new MyPtrCallback() {
			@Override
			public Pointer doSomething(Pointer a, Pointer b) {
				return pointerToAddress(a.getPeer() + b.getPeer());
			}
		}.toPointer(), pointerToAddress(1), pointerToAddress(2)).getPeer());
		
		Pointer<Integer> pa = pointerToInt(1), pb = pointerToInt(2);
		
		assertEquals(4, forwardPtrCall(new MyPtrCallback() {
			@Override
			public Pointer<Integer> doSomething(Pointer<Integer> a, Pointer<Integer> b) {
				return pointerToInt(a.get() + b.get() + 1);
				//return pointerToInt(a.getInt() + b.getInt() + 1);
			}
		}.toPointer(), pa, pb).getInt()); // .get());
	}
	
	@Test
	public void testNativeTargetPtrCallbacks() {
		MyPtrCallback adder = getPtrAdder().getNativeObject(MyPtrCallback.class);
		assertEquals(3, adder.doSomething((Pointer)pointerToAddress(1), (Pointer)pointerToAddress(2)).getPeer());
	}
	static native Pointer<?> forwardPtrCall(Pointer<MyPtrCallback> cb, Pointer<?> a, Pointer<?> b);
	static native Pointer<MyPtrCallback> getPtrAdder();
	
	public static abstract class MyPtrCallback extends Callback {
		public abstract Pointer<Integer> doSomething(Pointer<Integer> a, Pointer<Integer> b); 
	}
	
	
}
