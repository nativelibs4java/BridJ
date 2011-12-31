/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp.mfc;

import org.bridj.ann.Virtual;


public class CCmdUI extends MFCObject {

	@Virtual
	public native void Enable(boolean bOn);
}