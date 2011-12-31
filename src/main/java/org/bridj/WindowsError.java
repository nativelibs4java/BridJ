package org.bridj;
import java.lang.reflect.Field;
import static org.bridj.WinExceptionsConstants.*;

/**
 * Native Windows error as caught by a <code>__try { ... } __except (...) { ... }</code> block.
 * Not public yet.
 * @author Olivier
 */
class WindowsError extends NativeError {
    final int code;
    final long info, address;
    WindowsError(int code, long info, long address) {
        super(computeMessage(code, info, address));
        this.code = code;
        this.info = info;
        this.address = address;
    }
    public static void throwNew(int code, long info, long address) {
        throw new WindowsError(code, info, address);
    }
    static String subMessage(long info, long address) {
        switch ((int)info) {
            case 0: return "Attempted to read from inaccessible address " + toHex(address);
            case 1: return "Attempted to write to inaccessible address " + toHex(address);
            case 8: return "Attempted to execute memory " + toHex(address) + " that's not executable  (DEP violation)";
            default: return "?";
        }
    }
    public static String computeMessage(int code, long info, long address) {
        switch (code) {
            case EXCEPTION_ACCESS_VIOLATION:
                return "EXCEPTION_ACCESS_VIOLATION : " + subMessage(info, address);
            case EXCEPTION_IN_PAGE_ERROR:
                return "EXCEPTION_IN_PAGE_ERROR : " + subMessage(info, address);
            default:
                try {
                    for (Field field : WinExceptionsConstants.class.getFields()) {
                        if (field.getName().startsWith("EXCEPTION_") && field.getType() == int.class) {
                            int value = (Integer)field.get(null);
                            if (value == code)
                                return field.getName();
                        }
                    }
                } catch (Throwable th) {}
                return "Windows native error (code = " + code + ", info = " + info + ", address = " + address + ") !";
        }
        
    }
}
