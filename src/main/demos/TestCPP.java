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
package org.bridj.demos;

import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.Callback;
import org.bridj.demangling.Demangler;
import org.bridj.JNI;
import org.bridj.NativeLibrary;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import org.bridj.demangling.Demangler.Symbol;
import org.bridj.ann.Array;
import org.bridj.ann.Convention;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;
import org.bridj.ann.Name;
import org.bridj.ann.Virtual;
import org.bridj.cpp.CPPObject;
import org.bridj.cpp.CPPRuntime;
import org.bridj.demangling.VC9Demangler;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.IUnknown;
import org.bridj.cpp.com.shell.IShellFolder;
import org.bridj.cpp.com.shell.IShellWindows;
import org.bridj.cpp.com.shell.ITaskbarList3;
import org.bridj.demos.TaskbarListDemo;
import org.bridj.jawt.JAWTUtils;
import org.bridj.objc.NSAutoreleasePool;
//import org.bridj.objc.NSCalendar;
import org.bridj.objc.ObjCObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.Callback;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.NativeLibrary;
import org.bridj.Platform;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ValuedEnum;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html
//@Library("C:\\Users\\Olivier\\Prog\\nativelibs4java\\Runtime\\BridJ\\src\\test\\resources\\win64\\test.dll")
@Library("test")
@org.bridj.ann.Runtime(CPPRuntime.class)
public class TestCPP {
//	static String libraryPath = //BridJ.getNativeLibraryFile("test").toString()
//		Platform.isMacOSX() ? 
//				//"/Users/ochafik/nativelibs4java/Runtime/BridJ/src/test/resources/darwin_universal/libtest.dylib"
//				"/Users/ochafik/nativelibs4java/Runtime/BridJ/src/test/cpp/test/build_out/darwin_universal_gcc_debug/libtest.dylib" :
//		//"F:\\Experiments\\tmp\\key\\svn\\nativelibs4java\\Runtime\\BridJ\\src\\test\\resources\\win32\\test.dll" +
//        "C:\\Users\\Olivier\\Prog\\nativelibs4java\\Runtime\\BridJ\\src\\main\\cpp\\buildsys\\vs2008\\x64\\Debug\\test.dll"
//	;
	public enum Toto implements IntValuedEnum<Toto > {
		IBV_SYSFS_NAME_MAX(64),
		IBV_SYSFS_PATH_MAX(256);
		Toto(int value) {
			this.value = value;
		}
		public final int value;
		@java.lang.Override 
		public long value() {
			return this.value;
		}
		public Iterator<Toto> iterator() {
			return Collections.singleton(this).iterator();
		}
		public static ValuedEnum<Toto> fromValue(long value) {
			return FlagSet.fromValue(value, values());
		}
	}
	static {
		//BridJ.register();
	}
	
	static NativeLibrary library;


    @Library("test")
    @org.bridj.ann.Runtime(CRuntime.class)
    public static class FunctionTest {

    	public FunctionTest() {
            BridJ.register(getClass());
        }
    	public native int testAddDyncall(int a, int b);

        public void add() {
            int a = 10, b = 4, exp = a + b;
            int res = testAddDyncall(a, b);
            if (res != exp)
                throw new RuntimeException("Got " + res + " (" + Integer.toHexString(res) + ") instead of " + exp + " (" + Integer.toHexString(exp));
        }
    }
//    @Array(Toto.IBV_SYSFS_NAME_MAX.value)
	public static void print(String name, long addr, int n, int minI) {
		System.out.println(name);
		for (int i = -1; i < n; i++) {
			long v = getPtr(addr + i * Pointer.SIZE);
			Symbol sym = BridJ.getSymbolByAddress(v);
			String sname = sym == null ? null : sym.getName();
			System.out.println("\tOffset " + i + ":\t" + hex(v) + " \t('" + sname + "')");
			if (v == 0 && i >= minI)
				break;
		}
		System.out.println();
	}
    public static native char test_incr_char(char value);
	//public static native short test_incr_char(short value);
	public static native int testAddDyncall(int a, int b);


    public enum ETest implements ValuedEnum<ETest> {
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


	static int[] createExpectedInts(int n) {
		int[] expected = new int[n];
		for (int i = 0; i < n; i++)
			expected[i] = (int)(i + 1);
		return expected;
	}
	

    public static void testSetGetIntBuffer(int n) {
		int[] expected = createExpectedInts(n);
		Pointer<Integer> p = Pointer.pointerToInts(expected);
		long peer = p.getPeer();

		Iterator<Integer> it = p.iterator();
		for (int i = 0; i < n; i++) {
			Integer obVal = it.next();
			int val = obVal;
			if (val != expected[i])
                throw new RuntimeException("at position i = " + i);
		}
	}

	public static void main(String[] args) throws IOException {
        try {
            {
                JFrame f = new JFrame();
                f.setVisible(true);
                long hwndVal = JAWTUtils.getNativePeerHandle(f);//TODO com.sun.jna.Native.getComponentID(this);
                System.out.println("#\n# hwndVal = " + hwndVal + "\n#");
                
                BridJ.register();
                ValuedEnum<ETest> t = testEnum(ETest.eFirst);
                if (t != ETest.eFirst)
                    throw new RuntimeException();
            }
            testSetGetIntBuffer(10);
//			if (Platform.isMacOSX()) {
//				new NSCalendar();
//				new NSAutoReleasePool();
//			}

//        	NativeLibrary lib = BridJ.getNativeLibrary("OpenCL", new File("/System/Library/Frameworks/OpenCL.framework/OpenCL"));
//        	NativeLibrary lib = BridJ.getNativeLibrary("OpenCL", new File("/usr/lib/libobjc.dylib"));
//        	NativeLibrary lib = BridJ.getNativeLibrary("OpenCL", new File("/Users/ochafik/nativelibs4java/Runtime/BridJ/src/test/cpp/test/build_out/darwin_universal_gcc_debug/libtest.dylib"));
        	
        	
//        	Collection<Symbol> symbols = lib.getSymbols();
        	BridJ.register(MyCallback.class);
            BridJ.register();

			int ra = testAddDyncall(10, 4);
			if (ra != 14)
				throw new RuntimeException("Expected 14, got " + ra);
			ra = testAddDyncall(10, 4);
			if (ra != 14)
				throw new RuntimeException("Expected 14, got " + ra);
			
            testNativeTargetCallbacks();
            testJavaTargetCallbacks();

            if (true)
                return;

            long crea = Ctest.createTest();
            crea = Pointer.pointerToAddress(crea).getPointer().getPeer();
            print("Ctest.createTest()", crea, 10, 0);
            Ctest test = new Ctest();
            //long thisPtr = test.$this.getPeer();
            //System.out.println(hex(thisPtr));
            print("Ctest.this", Pointer.getPointer(test, Ctest.class).getPointer().getPeer(), 10, 2);
            int res = test.testAdd(1, 2);
            System.out.println("res = " + res);
            res = test.testVirtualAdd(1, 2);
            System.out.println("res = " + res);

            res = test.testVirtualAddStdCall(null, 1, 2);
            System.out.println("res = " + res);
            res = test.testAddStdCall(null, 1, 2);
            System.out.println("res = " + res);

            TaskbarListDemo.main(null);

            IShellWindows win = COMRuntime.newInstance(IShellWindows.class);
                IUnknown iu = win.QueryInterface(IUnknown.class);
                if (iu == null)
                    throw new RuntimeException("Interface does not handle IUnknown !");
                win.Release();

            try {
                new FunctionTest().add();
                
                MyStruct s = new MyStruct();
                s.a(10);
                System.out.println("Created MyStruct and set it to 10");
                int a = Pointer.getPointer(s).getInt();
                a = s.a();
                Pointer.getPointer(s).setInt(10);
                a = Pointer.getPointer(s).getInt();
                a = s.a();
                if (s.a() != 10)
                    throw new RuntimeException("invalid value = " + a);
                s.b(100.0);
                if (s.b() != 100.0)
                    throw new RuntimeException("invalid value = " + a);

            } catch (Throwable ex) {
                ex.printStackTrace();
            }


            library = BridJ.getNativeLibrary("test");
                //NativeLibrary.load(libraryPath);

            new VC9Demangler(null, "?sinInt@@YANH@Z").parseSymbol();
            new VC9Demangler(null, "?forwardCall@@YAHP6AHHH@ZHH@Z").parseSymbol();

            BridJ.register();

            //new VC9Demangler(null, "??0Ctest2@@QEAA@XZ").parseSymbol();
            
            for (Demangler.Symbol symbol : library.getSymbols()) {
                String name = symbol.getName();
                long addr = symbol.getAddress();
                System.out.println(name + " = \t" + hex(addr));

                if (name.startsWith("_ZTV") || name.startsWith("_ZTI") || name.startsWith("??_")) {
                    print("vtable", addr, 10, 1);
                } else
                    System.out.println("'" + name + "'");
            }

            boolean is64 = Platform.is64Bits();

        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            System.in.read();
        }
        /*double dres = PerfLib.testASinB(1, 2);
        res = PerfLib.testAddJNI(1, 2);
        System.out.println("Done");*/
	}

	public static class MyStruct extends StructObject {
		@Field(0)
		public native int a();
		public native void a(int a);

        @Field(1)
		public native double b();
		public native void b(double b);
	}

	@Library("test")
	static class Ctest extends CPPObject {
		static {
			BridJ.register();//Ctest.class);
		}

//		@Constructor
//		private static native void Ctest(@This long thisPtr);
		
		static native @Ptr long createTest();
		
		
		@Virtual
        public native int testVirtualAdd(int a, int b);

//		protected static native int testAdd(@This long thisPtr, int a, int b);
		public native int testAdd(int a, int b);


        @Virtual
        @Convention(Convention.Style.StdCall)
		public native int testVirtualAddStdCall(Pointer<?> ptr, int a, int b);

		@Convention(Convention.Style.StdCall)
		public native int testAddStdCall(Pointer<?> ptr, int a, int b);
        
//		public int testAdd(int a, int b) {
//			//print("this", Pointer.getAddress(this, Ctest.class), 10, 10);
//			//print("*this", $this.getPointer().getPeer(), 10, 10);
//			return testAdd(Pointer.getAddress(this, Ctest.class), a, b);
//		}
	}
	
	static String hex(long v) {
		return Long.toHexString(v);
		/*
		String s = Long.toString(v, 16);
		while ((s.length() < 16))
			s = "0" + s;
		return s;*/
	}
	static long getPtr(long peer) {
		Pointer<?> ptr = Pointer.pointerToAddress(peer);
		return ptr.getSizeT();
	}
	
	
	public static void testJavaTargetCallbacks() {
		int res;

        res = forwardCall(new MyCallback() {
			@Override
			public int doSomething(int a, int b) {
				return a + b * 10;
			}
		}.toPointer(), 1, 2);
        if (res != (1 + 2 * 10))
            throw new RuntimeException();

        res = forwardCall(new MyCallback() {
			@Override
			public int doSomething(int a, int b) {
				return a + b;
			}
		}.toPointer(), 1, 2);
        if (res != (1 + 2))
            throw new RuntimeException();

	}
	
	public static void testNativeTargetCallbacks() {
		long rawadder = getAdder_raw();
		if (rawadder != 0)
			System.out.println("Returned non null raw adder, cool (" + Long.toHexString(rawadder) + ") !");
		else
			System.out.println("Returned null raw adder !");
		long longadder = getAdder_long();
		if (longadder != 0)
			System.out.println("Returned non null longadder, cool (" + Long.toHexString(longadder) + ") !");
		else
			System.out.println("Returned null longadder !");
		long getAdder_pvoid = getAdder_pvoid();
		if (getAdder_pvoid != 0)
			System.out.println("Returned non null getAdder_pvoid, cool (" + Long.toHexString(getAdder_pvoid) + ") !");
		else
			System.out.println("Returned null getAdder_pvoid !");
		Pointer<org.bridj.demos.TestCPP.MyCallback> ptr = getAdder();
        if (ptr == null)
            throw new RuntimeException("getAdder returned null adder !!!");
		MyCallback adder = ptr.getNativeObject(MyCallback.class);
		int res = adder.doSomething(1, 2);

        if (res != 3)
            throw new RuntimeException("Expected 3, got "+ res);
	}
	
	static native int forwardCall(Pointer<MyCallback> cb, int a, int b);
	static native Pointer<MyCallback> getAdder();
	@Name("getAdder")
	static native @Ptr long getAdder_raw();
	
	static native @Ptr long getAdder_pvoid();
	static native long getAdder_long();
	
	public static abstract class MyCallback extends Callback {
		public abstract int doSomething(int a, int b); 
	}
	
}

/*

@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
class PerfLib {
    static {
        String f = BridJ.getNativeLibraryFile(BridJ.getNativeLibraryName(PerfLib.class)).toString();
        System.load(f);
    }
    public static class DynCallTest {
        public DynCallTest() throws FileNotFoundException {
            BridJ.register(getClass());
        }
        public native int testAddDyncall(int a, int b);
        public native int testASinB(int a, int b);
    }

    public static class JNATest implements com.sun.jna.Library {
        static {
        	try {
        		com.sun.jna.Native.register(JNI.extractEmbeddedLibraryResource("test").toString());
        	} catch (Exception ex) {
        		throw new RuntimeException("Failed to initialize test JNA library", ex);
        	}
        }
        public static native int testAddJNA(int a, int b);
        public static native int testASinB(int a, int b);
    }
    public static native int testAddJNI(int a, int b);
    public static native double testASinB(int a, int b);
}*/
