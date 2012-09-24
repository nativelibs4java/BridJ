package org.bridj;

import java.io.FileNotFoundException;
import org.bridj.ann.*;

//import static org.bridj.LastError.Windows.*;
//import static org.bridj.LastError.Unix.*;
import static org.bridj.Pointer.*;

/**
 * Native error that correspond to the <a href="http://en.wikipedia.org/wiki/Errno.h">errno</a> or <a href="http://msdn.microsoft.com/en-us/library/ms679360(v=vs.85).aspx">GetLastError()</a> mechanism.<br>
 * Some C functions declare errors by marking an error code in <a href="http://en.wikipedia.org/wiki/Errno.h">errno</a> or through <a href="http://msdn.microsoft.com/en-us/library/ms680627(v=vs.85).aspx">SetLastError(int)</a>.<br> 
 * If you want their corresponding bindings to throw an exception whenever such an error was marked, simply make them throw this exception explicitly.<br>
 * On Windows, BridJ will first check <a href="http://msdn.microsoft.com/en-us/library/ms679360(v=vs.85).aspx">GetLastError()</a>, then if no error was found it will check <a href="http://en.wikipedia.org/wiki/Errno.h">errno</a> (on the other platforms only <a href="http://en.wikipedia.org/wiki/Errno.h">errno</a> is available).<br>
 * For instance, look at the following binding of the C-library <a href="http://www.cplusplus.com/reference/clibrary/cstdlib/strtoul/">strtoul</a> function :
 * <pre>
 * &#064;Library("c")
 * {@code
 * public static native long strtoul(Pointer<Byte> str, Pointer<Pointer<Byte>> endptr, int base) throws LastError;
 * }</pre> 
 * @author Olivier Chafik
 */
public class LastError extends NativeError {
    final int code;
    final String description;
    
    LastError(int code, String description) {
    		super((description == null ? "?" : description.trim()) + " (error code = " + code + ")");//toString(code));
    		this.code = code;
            this.description = description;
            if (BridJ.verbose)
                BridJ.info("Last error detected : " + getMessage());
    }

    /**
     * Native error code (as returned by <a href="http://en.wikipedia.org/wiki/Errno.h">errno</a> or <a href="http://msdn.microsoft.com/en-us/library/ms679360(v=vs.85).aspx">GetLastError()</a>).
     */
    public int getCode() {
        return code;
    }

    /**
     * Native error description (as returned by <a href="http://www.cplusplus.com/reference/clibrary/cstring/strerror/">strerror</a> or <a href="http://msdn.microsoft.com/en-us/library/ms680582(v=vs.85).aspx">FormatMessage</a>).
     */
    public String getDescription() {
        return description;
    }
    
    static void throwNewInstance(int code, String description) {
        if (code == 0)
            return;
        
        throw new LastError(code, description);
    }
}
