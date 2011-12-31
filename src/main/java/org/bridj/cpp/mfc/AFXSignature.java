package org.bridj.cpp.mfc;

import org.bridj.Pointer;

public enum AFXSignature {
	AfxSig_vwww(Void.TYPE, int.class, int.class, int.class), // void (UINT, UINT, UINT)
	AfxSig_vwp(Void.TYPE, Pointer.class, CPoint.class); // void (CWnd*, CPoint)

	AFXSignature(Class<?> returnType, Class<?>... paramTypes) {
		this.returnType = returnType;
		this.paramTypes = paramTypes;
	}
	final Class<?> returnType;
	final Class<?>[] paramTypes;

}
