package org.bridj;

import static org.bridj.SignalConstants.*;
import static java.lang.Long.toHexString;
/**
 * Native error encapsulated as a Java error.
 * @author ochafik
 */
public abstract class NativeError extends Error {
    protected NativeError(String message) {
        super(message);
    }
    static String toHex(long address) {
        return "0x" + Long.toHexString(address);
    }
}
