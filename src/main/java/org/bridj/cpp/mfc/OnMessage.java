/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp.mfc;
import static org.bridj.cpp.mfc.AFXSignature.*;
/**
 * http://msdn.microsoft.com/en-us/library/0812b0wa(VS.80).aspx
 * C:\Program Files\Microsoft Visual Studio 8\VC\ATLMFC\INCLUDE\afxwin.h
 */
public @interface OnMessage {

	/// C:\Program Files\Microsoft Visual Studio 8\VC\ATLMFC\INCLUDE\afxmsg_.h
	public enum Type {
		WM_KEYDOWN("OnKeyDown", AfxSig_vwww),
		WM_LBUTTONDOWN("OnLButtonDown", AfxSig_vwp);

		Type(String defaultName, AFXSignature afxSig) {
			this.afxSig = afxSig;
            this.defaultName = defaultName;
		}
        final String defaultName;
		final AFXSignature afxSig;
	}
	Type value();
}