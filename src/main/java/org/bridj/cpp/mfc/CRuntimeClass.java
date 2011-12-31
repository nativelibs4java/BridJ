package org.bridj.cpp.mfc;

import org.bridj.Pointer;
import org.bridj.ann.Field;
import org.bridj.func.Fun0;

public class CRuntimeClass extends MFCObject {
	// Attributes
	@Field(0)
	public native Pointer<Byte>  m_lpszClassName();
	public native void  m_lpszClassName(Pointer<Byte> m_lpszClassName);
	@Field(1)
	public native int m_nObjectSize();
	public native void m_nObjectSize(int m_nObjectSize);
	@Field(2)
	public native int m_wSchema(); // schema number of the loaded class
	public native void m_wSchema(int m_wSchema); // schema number of the loaded class
	
	@Field(3)
	public native Pointer<Fun0<Pointer<CObject>>> m_pfnCreateObject(); // NULL => abstract class
	public native void m_pfnCreateObject(Pointer<Fun0<Pointer<CObject>>> m_pfnCreateObject); // NULL => abstract class

	/*#ifdef _AFXDLL
		CRuntimeClass* (PASCAL* m_pfnGetBaseClass)();
	#else
		CRuntimeClass* m_pBaseClass;
	#endif
	*/
	@Field(4)
	public native Pointer<CRuntimeClass> m_pBaseClass();
	public native void m_pBaseClass(Pointer<CRuntimeClass> m_pBaseClass);
	
// Operations
	public native Pointer<CObject> CreateObject();
	public native boolean IsDerivedFrom(Pointer<CRuntimeClass> pBaseClass);

	// dynamic name lookup and creation
	public native static Pointer<CRuntimeClass> FromName(Pointer<Byte> /*LPCSTR*/ lpszClassName);
	public native static Pointer<CRuntimeClass> FromName$2(Pointer<Character> lpszClassName);
	public native static Pointer<CObject> CreateObject(Pointer<Byte> lpszClassName);
	public native static Pointer<CObject> CreateObject$2(Pointer<Character> lpszClassName);

	// Implementation
	public native void Store(Pointer<CArchive> ar);
	public native static Pointer<CRuntimeClass> Load(Pointer<CArchive> ar, Pointer<Integer> pwSchemaNum);

	// CRuntimeClass objects linked together in simple list
	@Field(5)
	public native Pointer<CRuntimeClass> m_pNextClass();       // linked list of registered classes
	public native void m_pNextClass(Pointer<CRuntimeClass> m_pNextClass);       // linked list of registered classes
	@Field(6)
	public native Pointer<?> /*AFX_CLASSINIT*/ m_pClassInit();
	public native void m_pClassInit(Pointer<?> m_pClassInit);
}
