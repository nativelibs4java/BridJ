/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

import java.io.IOException;
import java.io.FileNotFoundException;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author ochafik
 */
public class DynamicCallbackTest {

    public DynamicCallbackTest() {
    }


    @Test
    public void testAddDynamicFunction() throws IOException {
        NativeLibrary lib = BridJ.getNativeLibrary("test");
        DynamicFunction i = lib.getSymbolPointer("testAddDyncall").asDynamicFunction(null, int.class, int.class, int.class);
        int res = (Integer)i.apply(1, 2);
        assertEquals(3, res);
    }
    
    @Test
    public void testDynamicFunctionCallback() throws FileNotFoundException {
        Pointer dc = Pointer.allocateDynamicCallback(
            new DynamicCallback<Integer>() {

                public Integer apply(Object... args) {
                    int a = (Integer)args[0];
                    int b = (Integer)args[1];
                    return a + b;
                }
                
            }, null, int.class, int.class, int.class
        );
        DynamicFunction<Integer> df = dc.asUntyped().asDynamicFunction(null, int.class, int.class, int.class);
        int ret = df.apply(1, 2);
        assertEquals(3, ret);
    }
}