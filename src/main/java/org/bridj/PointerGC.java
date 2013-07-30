/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj;

import org.bridj.Pointer;

/**
 *
 * @author ochafik
 */
public class PointerGC {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000000; i++) {
            Pointer<Double> p = Pointer.allocateDoubles(2);
            Pointer.release(p);
        }

        synchronized (PointerGC.class) {
            PointerGC.class.wait();
        }
    }
}
