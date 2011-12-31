/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp.mfc;

import org.bridj.Pointer;
import org.bridj.ann.Virtual;

/**
 *
 * @author Olivier
 */
public class CWnd extends MFCObject {
    @Virtual
    public native int SendMessage(int message, int wParam, int lParam);

    @Virtual
    public native void SetWindowText(Pointer<CString> lpszString);
}
