/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp.mfc;

import org.bridj.StructObject;
import org.bridj.ann.Field;

/**
 *
 * @author Olivier
 */
public class CPoint extends StructObject {
    @Field(0)
    public native int x();
    @Field(0)
    public native void x(int x);
    @Field(0)
    public native int y();
    @Field(0)
    public native void y(int y);
}
