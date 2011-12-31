/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.bridj.Pointer.*;

/**
 * Generic C function which invocation involves a bit of Java reflection.<br>
 * To create a dynamic function, use {@link Pointer#asDynamicFunction(org.bridj.ann.Convention.Style, java.lang.reflect.Type, java.lang.reflect.Type[]) } or {@link CRuntime#getDynamicFunctionFactory(org.bridj.NativeLibrary, org.bridj.ann.Convention.Style, java.lang.reflect.Type, java.lang.reflect.Type[])  }.
 * @author ochafik
 * @param R Return type of the function (can be {@link java.lang.Void})
 */
public abstract class DynamicFunction<R> extends Callback {
    /// Don't GC the factory, which holds the native callback handle
    DynamicFunctionFactory factory;
    Method method;

    protected DynamicFunction() {}

    public R apply(Object... args) {
        try {
            return (R) method.invoke(this, args);
        } catch (Throwable th) {
            th.printStackTrace();
            throw new RuntimeException("Failed to invoke callback" + " : " + th, th);
        }
    }

    @Override
    public String toString() {
        return method.toString();
    }


}
