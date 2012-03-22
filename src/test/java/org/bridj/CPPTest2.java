package org.bridj;
import java.io.File;
import org.bridj.ann.Library;
import org.bridj.ann.Virtual;
import org.bridj.cpp.CPPObject;

import org.bridj.*;
import static org.bridj.Pointer.*;
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
        
        IModule m = new AModule(), i = pointerTo(m).as(IModule.class).get();
        
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
 		assertEquals(210, testIVirtualAdd(pointerTo(v), 1, 2));
 	}
}
