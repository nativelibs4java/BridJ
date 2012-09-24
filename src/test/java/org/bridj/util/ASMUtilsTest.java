/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.bridj.BridJ;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author ochafik
 */
public class ASMUtilsTest {
    
    public native void someNativeFunction();
    
    //public ClassSynchronizerTest() {}
    
    @Test
    public void test() throws IOException, InstantiationException, IllegalAccessException {
        Class<?> c = BridJ.subclassWithSynchronizedNativeMethods(ASMUtilsTest.class);
        Method[] declaredMethods = c.getDeclaredMethods();
        assertEquals(1, declaredMethods.length);
        
        Method nativeMethod = declaredMethods[0];
        assertTrue(Modifier.isSynchronized(nativeMethod.getModifiers()));
        
        c.newInstance();
    }
}
