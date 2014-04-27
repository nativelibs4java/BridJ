/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.util;

import org.bridj.Platform;
import org.bridj.Pointer;
import org.bridj.SizeT;
import static org.bridj.util.ReflectionUtils.makeFieldWritable;

/**
 *
 * @author ochafik
 */
public class PlatformTestUtils {
    private static void forcePointerSize(int size) {
        try {
            makeFieldWritable(Pointer.class, "SIZE").setInt(null, size);
            makeFieldWritable(SizeT.class, "SIZE").setInt(null, size);
            makeFieldWritable(Platform.class, "POINTER_SIZE").setInt(null, size);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }
    public static void force32Bits() {
        forcePointerSize(4);
    }
    public static void force64Bits() {
        forcePointerSize(8);
    }
    
}
