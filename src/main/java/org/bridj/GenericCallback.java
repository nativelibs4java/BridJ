/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.bridj.Pointer.*;

/**
 * Generic Java callback to be called from C.
 * @author ochafik
 */
public interface GenericCallback {
    public abstract Object apply(Object... args);
}
