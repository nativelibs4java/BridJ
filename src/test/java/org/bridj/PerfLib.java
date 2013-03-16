package org.bridj;

import org.bridj.ann.Library;
//import com.sun.jna.Native;

/**
 *
 * @author Olivier
 */
@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
public class PerfLib {
	static java.io.File libraryFile = BridJ.getNativeLibraryFile(BridJ.getNativeLibraryName(PerfLib.class));
    static {
        System.load(libraryFile.toString());
    }
    public static class DynCallTest {
        static {
        		BridJ.register();
        }
        public native int testAddDyncall(int a, int b);
        public native int testASinB(int a, int b);
    }

    public static class JNATest implements com.sun.jna.Library {
        static {
        	try {
        		com.sun.jna.Native.register(libraryFile.toString());
        	} catch (Exception ex) {
        		throw new RuntimeException("Failed to initialize test JNA library", ex);
        	}
        }
        public static native int testAddJNA(int a, int b);
        public static native int testASinB(int a, int b);
    }
    public interface JNAInterfaceTest extends com.sun.jna.Library {
        public static final JNAInterfaceTest INSTANCE = (JNAInterfaceTest)com.sun.jna.Native.loadLibrary(libraryFile.toString(), JNAInterfaceTest.class);
        int testAddJNA(int a, int b);
        int testASinB(int a, int b);
    }
    public static native int testAddJNI(int a, int b);
    public static native double testASinB(int a, int b);
}
