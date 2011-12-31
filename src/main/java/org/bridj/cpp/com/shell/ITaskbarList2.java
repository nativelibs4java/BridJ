/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp.com.shell;

import org.bridj.Pointer;
import org.bridj.ann.Virtual;

/**
 *
 * @author Olivier
 */
public class ITaskbarList2  extends ITaskbarList {
	@Virtual(0) public native int MarkFullscreenWindow(Pointer<Integer> hWnd, boolean fFullscreen);
}