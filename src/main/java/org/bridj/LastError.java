/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bridj;

import java.io.FileNotFoundException;
import org.bridj.ann.*;

//import static org.bridj.LastError.Windows.*;
//import static org.bridj.LastError.Unix.*;
import static org.bridj.Pointer.*;

/**
 * Native error that correspond to the <a
 * href="http://en.wikipedia.org/wiki/Errno.h">errno</a> or <a
 * href="http://msdn.microsoft.com/en-us/library/ms679360(v=vs.85).aspx">GetLastError()</a>
 * mechanism.<br>
 * Some C functions declare errors by marking an error code in <a
 * href="http://en.wikipedia.org/wiki/Errno.h">errno</a> or through <a
 * href="http://msdn.microsoft.com/en-us/library/ms680627(v=vs.85).aspx">SetLastError(int)</a>.<br>
 * If you want their corresponding bindings to throw an exception whenever such
 * an error was marked, simply make them throw this exception explicitly.<br>
 * On Windows, BridJ will first check <a
 * href="http://msdn.microsoft.com/en-us/library/ms679360(v=vs.85).aspx">GetLastError()</a>,
 * then if no error was found it will check <a
 * href="http://en.wikipedia.org/wiki/Errno.h">errno</a> (on the other platforms
 * only <a href="http://en.wikipedia.org/wiki/Errno.h">errno</a> is
 * available).<br>
 * For instance, look at the following binding of the C-library <a
 * href="http://www.cplusplus.com/reference/clibrary/cstdlib/strtoul/">strtoul</a>
 * function :
 * <pre>
 * &#064;Library("c")
 * {@code
 * public static native long strtoul(Pointer<Byte> str, Pointer<Pointer<Byte>> endptr, int base) throws LastError;
 * }</pre>
 *
 * @author Olivier Chafik
 */
public class LastError extends NativeError {

    final int code, kind;
    String description;

    LastError(int code, int kind) {
        super(null);
        this.code = code;
        this.kind = kind;
        /*
        if (BridJ.verbose) {
            BridJ.info("Last error detected : " + getMessage());
        }*/
    }

    /**
     * Native error code (as returned by <a
     * href="http://en.wikipedia.org/wiki/Errno.h">errno</a> or <a
     * href="http://msdn.microsoft.com/en-us/library/ms679360(v=vs.85).aspx">GetLastError()</a>).
     */
    public int getCode() {
        return code;
    }

    /**
     * Native error description (as returned by <a
     * href="http://www.cplusplus.com/reference/clibrary/cstring/strerror/">strerror</a>
     * or <a
     * href="http://msdn.microsoft.com/en-us/library/ms680582(v=vs.85).aspx">FormatMessage</a>).
     */
    public String getDescription() {
    	if (description == null) {
    		description = getDescription(code, kind);
    	}
    	return description;
    }
    
    @Override
    public String getMessage() {
    	String description = getDescription();
    	return (description == null ? "?" : description.trim()) + " (error code = " + code + ")";
    }
    
    private static native String getDescription(int value, int kind);

    private static final ThreadLocal<LastError> lastError = new ThreadLocal<LastError>();

    public static LastError getLastError() {
        return lastError.get();
    }
    
    private static void setLastError(int code, int kind, boolean throwLastError) {
        if (code == 0) {
            return;
        }
        LastError err = new LastError(code, kind);
        err.fillInStackTrace();
        lastError.set(err);
        if (throwLastError) {
            throw err;
        }
    }
}
