package org.bridj.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.Arrays;
import org.bridj.util.BytecodeAnalyzer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ochafik
 */
public class BytecodeAnalyzerTest {
    public static class MyStruct {
        int a, b, c;
        
        public static class SubStruct {
            int sa, sb, sc;
        }
        public static class SubStructDeriv extends SubStruct {
            int sa2, sb2, sc2;
        }
    }
    
    @Test
    public void testFields() throws IOException {
        assertEquals(Arrays.asList("a", "b", "c"), BytecodeAnalyzer.getFieldNames(MyStruct.class, Object.class));
        assertEquals(Arrays.asList("sa", "sb", "sc"), BytecodeAnalyzer.getFieldNames(MyStruct.SubStruct.class, Object.class));
        
        assertEquals(Arrays.asList("sa2", "sb2", "sc2"), BytecodeAnalyzer.getFieldNames(MyStruct.SubStructDeriv.class, MyStruct.SubStructDeriv.class));
        assertEquals(Arrays.asList("sa", "sb", "sc", "sa2", "sb2", "sc2"), BytecodeAnalyzer.getFieldNames(MyStruct.SubStructDeriv.class, Object.class));
        
    }
    
    @Test
    public void testMethods() throws IOException {
        assertEquals(Arrays.asList("<init>"), BytecodeAnalyzer.getMethodNames(MyStruct.class, Object.class));
    }
}
