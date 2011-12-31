package org.bridj;

/**
 * POSIX signal error, such as "Segmentation fault" or "Bus error".<br>
 * Not public yet.
 */
class SignalError extends NativeError {
    final int signal;
    final int code;
    final long address;

    SignalError(int signal, int code, long address) {
        super(getFullSignalMessage(signal, code, address));
        this.signal = signal;
        this.code = code;
        this.address = address;
    }

    /**
     * POSIX signal associated with this error
     */
    public int getSignal() {
        return signal;
    }

    /**
     * POSIX signal code associated with this error
     */
    public int getCode() {
        return code;
    }

    /**
     * Memory address that caused the SIGBUS or SIGSEGV signal, or zero for other signals
     */
    public long getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SignalError)) {
            return false;
        }
        SignalError e = (SignalError) obj;
        return signal == e.signal && code == e.code;
    }

    @Override
    public int hashCode() {
        return ((Integer) signal).hashCode() ^ ((Integer) code).hashCode() ^ ((Long) address).hashCode();
    }

    public static String getFullSignalMessage(int signal, int code, long address) {
        String simple = getSignalMessage(signal, 0, address);
        if (code == 0) {
            return simple;
        }
        String sub = getSignalMessage(signal, code, address);
        if (sub.equals(simple)) {
            return simple;
        }
        return simple + " (" + sub + ")";
    }    
    public static void throwNew(int signal, int code, long address) {
        throw new SignalError(signal, code, address);
    }
    /**
     * http://pubs.opengroup.org/onlinepubs/7908799/xsh/signal.h.html
     */
    public static String getSignalMessage(int signal, int code, long address) {
        switch (signal) {
            case SignalConstants.SIGSEGV:
                switch (code) {
                    case SignalConstants.SEGV_MAPERR:
                        return "Address not mapped to object";
                    case SignalConstants.SEGV_ACCERR:
                        return "Invalid permission for mapped object";
                    default:
                        return "Segmentation fault : " + toHex(address);
                }
            case SignalConstants.SIGBUS:
                switch (code) {
                    case SignalConstants.BUS_ADRALN:
                        return "Invalid address alignment";
                    case SignalConstants.BUS_ADRERR:
                        return "Nonexistent physical address";
                    case SignalConstants.BUS_OBJERR:
                        return "Object-specific HW error";
                    default:
                        return "Bus error : " + toHex(address);
                }
            case SignalConstants.SIGABRT:
                return "Native exception (call to abort())";
            case SignalConstants.SIGFPE:
                switch (code) {
                    case SignalConstants.FPE_INTDIV:
                        return "Integer divide by zero";
                    case SignalConstants.FPE_INTOVF:
                        return "Integer overflow";
                    case SignalConstants.FPE_FLTDIV:
                        return "Floating point divide by zero";
                    case SignalConstants.FPE_FLTOVF:
                        return "Floating point overflow";
                    case SignalConstants.FPE_FLTUND:
                        return "Floating point underflow";
                    case SignalConstants.FPE_FLTRES:
                        return "Floating point inexact result";
                    case SignalConstants.FPE_FLTINV:
                        return "Invalid floating point operation";
                    case SignalConstants.FPE_FLTSUB:
                        return "Subscript out of range";
                    default:
                        return "Floating point error";
                }
            case SignalConstants.SIGSYS:
                return "Bad argument to system call";
            case SignalConstants.SIGTRAP:
                switch (code) {
                    case SignalConstants.TRAP_BRKPT:
                        return "Process breakpoint";
                    case SignalConstants.TRAP_TRACE:
                        return "Process trace trap";
                    default:
                        return "Trace trap";
                }
            case SignalConstants.SIGILL:
                switch (code) {
                    case SignalConstants.ILL_ILLOPC:
                        return "Illegal opcode";
                    case SignalConstants.ILL_ILLTRP:
                        return "Illegal trap";
                    case SignalConstants.ILL_PRVOPC:
                        return "Privileged opcode";
                    case SignalConstants.ILL_ILLOPN:
                        return "Illegal operand";
                    case SignalConstants.ILL_ILLADR:
                        return "Illegal addressing mode";
                    case SignalConstants.ILL_PRVREG:
                        return "Privileged register";
                    case SignalConstants.ILL_COPROC:
                        return "Coprocessor error";
                    case SignalConstants.ILL_BADSTK:
                        return "Internal stack error";
                    default:
                        return "Illegal instruction";
                }
            default:
                return "Native error";
        }
    }
    
}
