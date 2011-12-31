package org.bridj.cpp.com;

import org.bridj.Pointer;

/**
 * Implementing the IDispatch Interface : http://msdn.microsoft.com/en-us/library/ms221037.aspx
 * Simulating COM Interfaces : http://msdn.microsoft.com/en-us/library/111chfb8.aspx
 */
public class COMCallableWrapper extends IDispatch {
	Object instance;
	public COMCallableWrapper(Object instance) {
		this.instance = instance;
	}
//	public class IDispatchImpl extends IDispatch {
		@Override
		public int GetIDsOfNames(Pointer riid,
				Pointer<Pointer<Character>> rgszNames, int cNames,
				int lcid, Pointer<Integer> rgDispId) {
			
			// TODO
			return COMRuntime.E_NOTIMPL;
		}
		@Override
		public int Invoke(int dispIdMember, Pointer<Byte> riid, int lcid,
				short wFlags, Pointer<DISPPARAMS> pDispParams,
				Pointer<VARIANT> pVarResult, Pointer<EXCEPINFO> pExcepInfo,
				Pointer<Integer> puArgErr) {
			
			// TODO
			return COMRuntime.E_NOTIMPL;
		}
		@Override
		public int GetTypeInfo(int iTInfo, int lcid,
				Pointer<Pointer<ITypeInfo>> ppTInfo) {
			// TODO
			return COMRuntime.E_NOTIMPL;
		}
		@Override
		public int GetTypeInfoCount(Pointer<Integer> pctinfo) {
			// TODO
			return COMRuntime.E_NOTIMPL;
		}
//	}
}
