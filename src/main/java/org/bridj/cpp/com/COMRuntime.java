package org.bridj.cpp.com;

import java.lang.reflect.Type;
import java.lang.reflect.Method;

import org.bridj.ValuedEnum;
import org.bridj.FlagSet;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.CRuntime;
import org.bridj.Platform;
import org.bridj.Pointer.StringType;
import org.bridj.ann.Convention;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;
import org.bridj.ann.Runtime;
import org.bridj.cpp.CPPRuntime;
import static org.bridj.cpp.com.VARENUM.*;
import org.bridj.cpp.com.VARIANT.__VARIANT_NAME_1_union;
import org.bridj.cpp.com.VARIANT.__VARIANT_NAME_1_union.__tagVARIANT;
import org.bridj.cpp.com.VARIANT.__VARIANT_NAME_1_union.__tagVARIANT.__VARIANT_NAME_3_union;
import org.bridj.util.Utils;
import static org.bridj.Pointer.*;
import static org.bridj.cpp.com.OLEAutomationLibrary.*;

/*
 * Adding Icons, Previews and Shortcut Menus :
 * http://msdn.microsoft.com/en-us/library/bb266530(VS.85).aspx
 * 
 * TODO CoCreateInstanceEx
 * TODO CoRegisterClassObject
 * TODO CoRevokeClassObject
 * TODO CoCreateGuid 
 * 
 * IDL syntax : 
 * http://caml.inria.fr/pub/old_caml_site/camlidl/htmlman/main002.html
 * 
 * Registering a Running EXE Server :
 * http://msdn.microsoft.com/en-us/library/ms680076(VS.85).aspx
 */
/**
 * Microsoft COM runtime, along with useful constants and methods.<br>
 * All COM classes must extends {@link org.bridj.cpp.com.IUnknown} and hence inherit from it the correct {@link org.bridj.ann.Runtime} annotation that references {@link org.bridj.cpp.com.COMRuntime}.
 */
@Library("Ole32")
@Runtime(CRuntime.class)
@Convention(Convention.Style.StdCall)
public class COMRuntime extends CPPRuntime {
	static {
		if (Platform.isWindows())
			BridJ.register();
	}
	public static final int 
	  CLSCTX_INPROC_SERVER            = 0x1,
	  CLSCTX_INPROC_HANDLER           = 0x2,
	  CLSCTX_LOCAL_SERVER             = 0x4,
	  CLSCTX_INPROC_SERVER16          = 0x8,
	  CLSCTX_REMOTE_SERVER            = 0x10,
	  CLSCTX_INPROC_HANDLER16         = 0x20,
	  CLSCTX_RESERVED1                = 0x40,
	  CLSCTX_RESERVED2                = 0x80,
	  CLSCTX_RESERVED3                = 0x100,
	  CLSCTX_RESERVED4                = 0x200,
	  CLSCTX_NO_CODE_DOWNLOAD         = 0x400,
	  CLSCTX_RESERVED5                = 0x800,
	  CLSCTX_NO_CUSTOM_MARSHAL        = 0x1000,
	  CLSCTX_ENABLE_CODE_DOWNLOAD     = 0x2000,
	  CLSCTX_NO_FAILURE_LOG           = 0x4000,
	  CLSCTX_DISABLE_AAA              = 0x8000,
	  CLSCTX_ENABLE_AAA               = 0x10000,
	  CLSCTX_FROM_DEFAULT_CONTEXT     = 0x20000,
	  CLSCTX_ACTIVATE_32_BIT_SERVER   = 0x40000,
	  CLSCTX_ACTIVATE_64_BIT_SERVER   = 0x80000,
	  CLSCTX_ENABLE_CLOAKING          = 0x100000,
	  CLSCTX_PS_DLL                   = 0x80000000;

    public static final int
        CLSCTX_INPROC           = CLSCTX_INPROC_SERVER|CLSCTX_INPROC_HANDLER,
        CLSCTX_ALL              =(CLSCTX_INPROC_SERVER|
                                 CLSCTX_INPROC_HANDLER|
                                 CLSCTX_LOCAL_SERVER|
                                 CLSCTX_REMOTE_SERVER),
     CLSCTX_SERVER           = (CLSCTX_INPROC_SERVER|CLSCTX_LOCAL_SERVER|CLSCTX_REMOTE_SERVER);
    
	public static final int S_OK = 0,
			S_FALSE = 1,
            REGDB_E_CLASSNOTREG = 0x80040154,
            CLASS_E_NOAGGREGATION = 0x80040110,
            CO_E_NOTINITIALIZED = 0x800401F0;

    public static final int E_UNEXPECTED                    = 0x8000FFFF;
    public static final int E_NOTIMPL                       = 0x80004001;
    public static final int E_OUTOFMEMORY                   = 0x8007000E;
    public static final int E_INVALIDARG                    = 0x80070057;
    public static final int E_NOINTERFACE                   = 0x80004002;
    public static final int E_POINTER                       = 0x80004003;
    public static final int E_HANDLE                        = 0x80070006;
    public static final int E_ABORT                         = 0x80004004;
    public static final int E_FAIL                          = 0x80004005;
    public static final int E_ACCESSDENIED                  = 0x80070005;
    
    
	public static final int DISP_E_BADVARTYPE = -2147352568;
	public static final int DISP_E_NOTACOLLECTION = -2147352559;
	public static final int DISP_E_MEMBERNOTFOUND = -2147352573;
	public static final int DISP_E_ARRAYISLOCKED = -2147352563;
	public static final int DISP_E_EXCEPTION = -2147352567;
	public static final int DISP_E_TYPEMISMATCH = -2147352571;
	public static final int DISP_E_BADINDEX = -2147352565;
	public static final int DISP_E_BADCALLEE = -2147352560;
	public static final int DISP_E_OVERFLOW = -2147352566;
	public static final int DISP_E_UNKNOWNINTERFACE = -2147352575;
	public static final int DISP_E_DIVBYZERO = -2147352558;
	public static final int DISP_E_UNKNOWNLCID = -2147352564;
	public static final int DISP_E_PARAMNOTOPTIONAL = -2147352561;
	public static final int DISP_E_PARAMNOTFOUND = -2147352572;
	public static final int DISP_E_BADPARAMCOUNT = -2147352562;
	public static final int DISP_E_BUFFERTOOSMALL = -2147352557;
	public static final int DISP_E_UNKNOWNNAME = -2147352570;
	public static final int DISP_E_NONAMEDARGS = -2147352569;

    public static interface COINIT {
        public final int
            COINIT_APARTMENTTHREADED  = 0x2,      // Apartment model
            COINIT_MULTITHREADED      = 0x0,      // OLE calls objects on any thread.
            COINIT_DISABLE_OLE1DDE    = 0x4,      // Don't use DDE for Ole1 support.
            COINIT_SPEED_OVER_MEMORY  = 0x8;
    }
    
    @Deprecated
	public static native int CoCreateInstance(
		Pointer<Byte> rclsid,
		Pointer<IUnknown> pUnkOuter,
		int dwClsContext,
		Pointer<Byte> riid,
		Pointer<Pointer<?>> ppv
	);

    static native int CoInitializeEx(@Ptr long pvReserved, int dwCoInit);
    static native int CoInitialize(@Ptr long pvReserved);
    static native void CoUninitialize();

    static void error(int err) {
        switch (err) {
            case E_INVALIDARG:
            case E_OUTOFMEMORY:
            case E_UNEXPECTED:
                throw new RuntimeException("Error " + Integer.toHexString(err));
            case S_OK:
                return;
            case CO_E_NOTINITIALIZED:
                throw new RuntimeException("CoInitialized wasn't called !!");
            case E_NOINTERFACE:
                throw new RuntimeException("Interface does not inherit from class");
            case E_POINTER:
                throw new RuntimeException("Allocated pointer pointer is null !!");
            default:
                throw new RuntimeException("Unexpected COM error code : " + err);
        }
    }
    /** 
     * Get the IID declared for a class using the {@link IID} annotation.
     * @throws RuntimeException if the class isn't annotated with IID
     */
	public static <I extends IUnknown> Pointer<Byte> getIID(Class<I> type) {
		IID id = type.getAnnotation(IID.class);
		if (id == null)
			throw new RuntimeException("No " + IID.class.getName() + " annotation set on type " + type.getName() + " !");

        return (Pointer)parseGUID(id.value());
	}
	
	/** 
     * Get the CLSID declared for a class using the {@link CLSID} annotation.
     * @throws RuntimeException if the class isn't annotated with CLSID
     */
	public static <I extends IUnknown> Pointer<Byte> getCLSID(Class<I> type) {
		CLSID id = type.getAnnotation(CLSID.class);
		if (id == null)
			throw new RuntimeException("No " + CLSID.class.getName() + " annotation set on type " + type.getName() + " !");
        
		return (Pointer)parseGUID(id.value());
	}
    static ThreadLocal<Object> comInitializer = new ThreadLocal<Object>() {
        @Override
        protected Object initialValue() {
            error(CoInitializeEx(0, COINIT.COINIT_MULTITHREADED));
            return new Object() {
                @Override
                protected void finalize() throws Throwable {
                    CoUninitialize();
                }
            };
        }
    };
    
    @Override
    protected boolean isSymbolOptional(Method method) {
    		return true;
    }
    
    /**
     * Initialize COM the current thread (uninitialization is done automatically upon thread death).<br>
     * Calls CoInitialize with COINIT_MULTITHREADED max once per thread.<br>
     * This is called automatically in {@link COMRuntime#newInstance(Class)}, so you'll typically never need to call this method by hand.
     */
    public static void initialize() {
        comInitializer.get();
    }
	public static <I extends IUnknown> I newInstance(Class<I> type) throws ClassNotFoundException {
        return newInstance(type, type);
    }
    public static <T extends IUnknown, I extends IUnknown> I newInstance(Class<T> instanceClass, Class<I> instanceInterface) throws ClassNotFoundException {
        initialize();
        
		Pointer<Pointer<?>> p = Pointer.allocatePointer();
        Pointer<Byte> clsid = getCLSID(instanceClass), uuid = getIID(instanceInterface);
        try {
            int ret = CoCreateInstance(clsid, null, CLSCTX_ALL, uuid, p);
            if (ret == REGDB_E_CLASSNOTREG)
                throw new ClassNotFoundException("COM class is not registered : " + instanceClass.getSimpleName() + " (clsid = " + clsid.getCString() + ")");
            error(ret);

            Pointer<?> inst = p.getPointer();
            if (inst == null)
                throw new RuntimeException("Serious low-level issue : CoCreateInstance executed fine but we only retrieved a null pointer !");

            I instance = inst.getNativeObject(instanceInterface);
            return instance;
        } finally {
            Pointer.release(p, clsid, uuid);
        }
	}

    private static final String model = "00000000-0000-0000-0000-000000000000";

    // Need to parse as (int, short, short, char[8])
    public static Pointer<?> parseGUID(String descriptor) {
        Pointer<?> out = Pointer.allocateBytes(16 + 4);
        descriptor = descriptor.replaceAll("-", "");
        if (descriptor.length() != 32)
            throw new RuntimeException("Expected something like :\n" + model + "\nBut got instead :\n" +descriptor);

        out.setIntAtOffset(0, (int)Long.parseLong(descriptor.substring(0, 8), 16));
        out.setShortAtOffset(4, (short)Long.parseLong(descriptor.substring(8, 12), 16));
        out.setShortAtOffset(6, (short)Long.parseLong(descriptor.substring(12, 16), 16));
        for (int i = 0; i < 8; i++)
            out.setByteAtOffset(8 + i, (byte)Long.parseLong(descriptor.substring(16 + i * 2, 16 + i * 2 + 2), 16));

        return out;
    }

    static ValuedEnum<VARENUM> getType(VARIANT v) {
        __VARIANT_NAME_1_union v1 = v.__VARIANT_NAME_1();
        __tagVARIANT v2 = v1.__VARIANT_NAME_2();
        short vt = v2.vt();
        return FlagSet.fromValue(vt, VARENUM.class);
    }
    static VARIANT setType(VARIANT v, ValuedEnum<VARENUM> vt) {
        __VARIANT_NAME_1_union v1 = v.__VARIANT_NAME_1();
        __tagVARIANT v2 = v1.__VARIANT_NAME_2();
        v2.vt((short)vt.value());
        return v;
    }
    static VARIANT.__VARIANT_NAME_1_union.__tagVARIANT.__VARIANT_NAME_3_union getValues(VARIANT v) {
        __VARIANT_NAME_1_union v1 = v.__VARIANT_NAME_1();
        __tagVARIANT v2 = v1.__VARIANT_NAME_2();
        __VARIANT_NAME_3_union v3 = v2.__VARIANT_NAME_3();
        return v3;
    }

    /**
	 * Convert the VARIANT value to an equivalent Java value.
	 * @throws UnsupportedOperationException if the VARIANT type is not handled yet
	 * @throws RuntimeException if the VARIANT is invalid
	 */
	public static Object getValue(VARIANT v) {

		FlagSet<VARENUM> vt = FlagSet.fromValue(getType(v));
        __VARIANT_NAME_3_union values = getValues(v);
		if (vt.has(VT_BYREF)) {
			switch (vt.without(VT_BYREF).toEnum()) {
				case VT_DISPATCH:
					return values.ppdispVal();
				case VT_UNKNOWN:
					return values.ppunkVal();
				case VT_VARIANT:
					return values.pvarVal();
				case VT_I1       :
				case VT_UI1      :
					return values.pbVal();
				/* UINT16        */
				case VT_I2      :
				case VT_UI2      :
					return values.piVal();
				/* UINT32        */
				case VT_I4      :
				case VT_UI4      :
					return values.plVal();
				case VT_R4:
					return values.pfltVal();
				case VT_R8:
					return values.pdblVal();
				/* UINT64        */
				case VT_I8      :
				case VT_UI8      :
					return values.pllVal();
				/* BOOL          */
				case VT_BOOL     :
					return values.pbVal().as(Boolean.class);

				case VT_BSTR:
					return values.pbstrVal();
				case VT_LPSTR:
					return values.byref().getCString();
				case VT_LPWSTR:
					return values.byref().getWideCString();
				case VT_PTR:
				default:
					return values.byref();
			}
		}
		switch (vt.toEnum()) {
			/* UINT8         */
			case VT_I1       :
			case VT_UI1      :
				return values.bVal();
			/* UINT16        */
			case VT_I2      :
			case VT_UI2      :
				return values.uiVal();
			/* UINT32        */
			case VT_I4      :
			case VT_UI4      :
				return values.ulVal();
			/* UINT64        */
			case VT_I8      :
			case VT_UI8      :
				return values.ullVal();
			/* BOOL          */
			case VT_BOOL     :
				return values.bVal() != 0;
			case VT_R4:
				return values.fltVal();
			case VT_R8:
				return values.dblVal();
			case VT_BSTR:
				return values.bstrVal().getString(StringType.BSTR);
                        case VT_EMPTY:
                                return null;
			default:
				throw new UnsupportedOperationException("Conversion not implemented yet from VARIANT type " + vt + " to Java !");
		}
	}

	static void change(VARIANT v, ValuedEnum<VARENUM> vt) {
		Pointer<VARIANT> pv = pointerTo(v);
        int res = VariantChangeType(pv, pv, (short)0, (short)vt.value());
		assert res == S_OK;
	}
    public static VARIANT setValue(VARIANT v, Object value) {
    		//ValuedEnum<VARENUM> vt;
        __VARIANT_NAME_3_union values = getValues(v);
        if (value == null) {
        		change(v, VT_EMPTY);
            //values.byref(null);
        } else if (value instanceof Integer) {
            change(v, VT_I4);
            values.lVal((Integer)value);
        } else if (value instanceof Long) {
            change(v, VT_I8);
            values.llval((Long)value);
        } else if (value instanceof Short) {
            change(v, VT_I2);
            values.iVal((Short)value);
        } else if (value instanceof Byte) {
            change(v, VT_I1);
            values.bVal((Byte)value);
        } else if (value instanceof Float) {
            change(v, VT_R4);
            values.fltVal((Float)value);
        } else if (value instanceof Double) {
            change(v, VT_I8);
            values.dblVal((Double)value);
        } else if (value instanceof Character) {
            change(v, VT_I2);
            values.iVal((short)((Character)value).charValue());
        } else if (value instanceof String) {
            change(v, VT_BSTR);
            /*String str = (String)value;
            int len = str.length();
            int capacity = SysStringLen(values.bstrVal());
            /Pointer<Character> chars = 
            if (len > capacity)
            		SysReAllocStringLen values.bstrVal()
            	SysReAllocString(values.bstrVal().getReference(),
            	*/
            values.bstrVal().setString((String)value, StringType.BSTR);
        } else if (value instanceof Pointer) {
            Pointer ptr = (Pointer)value;
            Type targetType = ptr.getTargetType();
            Class targetClass = Utils.getClass(targetType);
            if (targetClass == null)
                change(v, VT_PTR);
            else {
                VARENUM ve;
                if (targetClass == Integer.class || targetClass == int.class)
                    ve = VT_I4;
                else if(targetClass == Long.class || targetClass == long.class)
                    ve = VT_I8;
                else if(targetClass == Short.class || targetClass == short.class)
                    ve = VT_I2;
                else if(targetClass == Byte.class || targetClass == byte.class)
                    ve = VT_I1;
                else if(targetClass == Character.class || targetClass == char.class)
                    ve = VT_LPWSTR; // TODO
                else if(targetClass == Boolean.class || targetClass == boolean.class)
                    ve = VT_BOOL;
                else if(targetClass == Float.class || targetClass == float.class)
                    ve = VT_R4;
                else if(targetClass == Double.class || targetClass == double.class)
                    ve = VT_R8;
                else if(Pointer.class.isAssignableFrom(targetClass))
                    ve = VT_PTR;
                else
                    ve = null; // TODO

                change(v, FlagSet.fromValues(VT_BYREF, ve));
            }
        } else
            throw new UnsupportedOperationException("Unable to convert an object of type " + value.getClass().getName() + " to a COM VARIANT object !");

        //setType(v, vt);
        return v;
    }

    public static String toString(VARIANT v) {
        StringBuilder b = new StringBuilder("Variant(value = ");
        try {
            b.append(getValue(v));
        } catch (Throwable th) {
            b.append("?");
        }
        b.append(", type = ").append(getType(v)).append(")");

        return b.toString();
    }
    public static VARIANT clone(VARIANT v) {
		VARIANT c = new VARIANT();
		int res = VariantCopy(pointerTo(v), pointerTo(c));
		switch (res) {
		case S_OK:
			break;
		case DISP_E_ARRAYISLOCKED:
			throw new RuntimeException("The variant contains an array that is locked.");
		case DISP_E_BADVARTYPE:
			throw new RuntimeException("The source and destination have an invalid variant type (usually uninitialized).");
		case E_OUTOFMEMORY:
			throw new RuntimeException("Memory could not be allocated for the copy.");
		case E_INVALIDARG:
			throw new RuntimeException("One of the arguments is invalid.");
		default:
			throw new RuntimeException("Grave error : unexpected error code for VariantCopy : " + res);
		}
		return c;
	}
}
