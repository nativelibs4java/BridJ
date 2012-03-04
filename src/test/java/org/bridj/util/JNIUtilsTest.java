package org.bridj.util;

import java.io.IOException;
import org.bridj.demangling.Demangler;
import java.util.Collection;

import org.junit.Test;


import static org.junit.Assert.*;


public class JNIUtilsTest {
    @Test
    public void testUnderscore() {
        assertEquals(5, JNIUtils.findLastNonEscapeUnderscore("o_b_c_m_1m"));
    }
    
    public static native void test_method_1(int i);
    
    @Test
    public void testDecode() throws NoSuchMethodException, IOException {
        String className = getClass().getName();
        Object[] nameAndSigArray = new Object[2];
        String enclosingClassName = JNIUtils.decodeMethodNameClassAndSignature("Java_" + className.replaceAll("_", "_1").replace('.', '_') + "_test_1method_11", nameAndSigArray, "", "");
        
        assertEquals(className, enclosingClassName.replace('/', '.'));
        assertEquals("test_method_1", nameAndSigArray[0]);
        assertEquals("(I)V", nameAndSigArray[1]);
    }
}
