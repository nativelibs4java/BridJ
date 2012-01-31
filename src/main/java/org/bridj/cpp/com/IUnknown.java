package org.bridj.cpp.com;

import org.bridj.*;
import org.bridj.ann.*;
import org.bridj.ann.Runtime;
import org.bridj.cpp.CPPObject;
import org.bridj.cpp.mfc.MFCRuntime;

import static org.bridj.cpp.com.COMRuntime.*;

@Convention(Convention.Style.StdCall)
@IID("00000000-0000-0000-C000-000000000046")
@Runtime(COMRuntime.class)
public class IUnknown extends CPPObject {
	protected boolean autoRelease;

	public static IUnknown wrap(Object object) {
		if (object instanceof IUnknown)
			return (IUnknown)object;
		
		return new COMCallableWrapper(object);
	}
    @Override
    protected void finalize() throws Throwable {
        if (autoRelease)
            Release();
        super.finalize();
    }


	@Virtual(0)
	@Deprecated
	public native int QueryInterface(
		Pointer<Byte> riid,
		Pointer<Pointer<IUnknown>> ppvObject
	);
	
	public <I extends IUnknown> I QueryInterface(Class<I> type) {
		Pointer<Pointer<IUnknown>> p = Pointer.allocatePointer(IUnknown.class);
		int ret = QueryInterface(getIID(type), p);
		if (ret != S_OK)
			return null;
		
		return p.get().getNativeObject(type);
	}
	
	@Virtual(1)
	public native int AddRef();
	
	@Virtual(2)
	public native int Release();
	
}