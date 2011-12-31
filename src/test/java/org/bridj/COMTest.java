package org.bridj;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

import org.bridj.ann.*;
import static org.junit.Assert.*;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.IUnknown;
import org.bridj.cpp.com.CLSID;
import org.bridj.cpp.com.IID;
import org.bridj.cpp.com.shell.IShellWindows;

public class COMTest {

	static boolean hasCOM = Platform.isWindows();// || !Platform.is64Bits();
	

	@Test
	public void shellFolder() {
		if (!hasCOM)
            return;
        try {
            IShellWindows win = COMRuntime.newInstance(IShellWindows.class);
            assertNotNull(win);
            IUnknown iu = win.QueryInterface(IUnknown.class);
            assertNotNull(iu);
            win = iu.QueryInterface(IShellWindows.class);
            assertNotNull(win);
            win.Release();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(COMTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
	}
	
	static class SomeUnknown extends IUnknown {
		//public SomeUnknown() {}
		@Override
		public int QueryInterface(Pointer<Byte> riid, Pointer<Pointer<IUnknown>> ppvObject) {
			return 0;
		}
		int refs;
		@Override
		public synchronized int AddRef() { return ++refs; } 
		
		@Override
		public synchronized int Release() { return --refs; }
    }

    @Test
    public void testSomeUnknownInstantiation() {
    		new SomeUnknown();
    }
    
    
    @CLSID("62BE5D10-60EB-11d0-BD3B-00A0C911CE86") 
    @IID("29840822-5B84-11D0-BD3B-00A0C911CE86") 
    public static class ICreateDevEnum extends IUnknown { 
        @Virtual(0) 
        public native int CreateClassEnumerator(Pointer<?>  clsidDeviceClass, Pointer<Pointer<?>> enumerator, int flags); 
    } 
    
    @Test
    public void testICreateDevEnum() throws Exception {
    	if (!hasCOM) 
    		return;
    	
    	//Not needed, as it's called by COMRuntime.newInstance : COMRuntime.initialize(); 
    	ICreateDevEnum devEnumCreator = COMRuntime.newInstance(ICreateDevEnum.class);
    }
}
