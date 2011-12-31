package org.bridj;

/**
 * POSIX signal constants for use from {@link org.bridj.NativeError.SignalError}
 * @author ochafik
 */
class SignalConstants 
{
	public static final int SIGABRT = 6;
	public static final int SIGALRM = 14;
	public static final int SIGBUS = 10;
	public static final int SIGCHLD = 20;
	public static final int SIGCONT = 19;
	public static final int SIGEMT = 7;
	public static final int SIGFPE = 8;
	public static final int SIGHUP = 1;
	public static final int SIGILL = 4;
	public static final int SIGINFO = 29;
	public static final int SIGINT = 2;
	public static final int SIGIO = 23;
	public static final int SIGIOT = 6;
	public static final int SIGKILL = 9;
	public static final int SIGPIPE = 13;
	public static final int SIGPOLL = 7;
	public static final int SIGPROF = 27;
	public static final int SIGQUIT = 3;
	public static final int SIGSEGV = 11;
	public static final int SIGSTOP = 17;
	public static final int SIGSYS = 12;
	public static final int SIGTERM = 15;
	public static final int SIGTRAP = 5;
	public static final int SIGTSTP = 18;
	public static final int SIGTTIN = 21;
	public static final int SIGTTOU = 22;
	public static final int SIGURG = 16;
	public static final int SIGUSR1 = 30;
	public static final int SIGUSR2 = 31;
	public static final int SIGVTALRM = 26;
	public static final int SIGWINCH = 28;
	public static final int SIGXCPU = 24;
	public static final int SIGXFSZ = 25;
	public static final int BUS_ADRALN = 1;
	public static final int BUS_ADRERR = 2;
	public static final int BUS_OBJERR = 3;
	public static final int CLD_CONTINUED = 6;
	public static final int CLD_DUMPED = 3;
	public static final int CLD_EXITED = 1;
	public static final int CLD_KILLED = 2;
	public static final int CLD_STOPPED = 5;
	public static final int CLD_TRAPPED = 4;
	public static final int FPE_FLTDIV = 1;
	public static final int FPE_FLTINV = 5;
	public static final int FPE_FLTOVF = 2;
	public static final int FPE_FLTRES = 4;
	public static final int FPE_FLTSUB = 6;
	public static final int FPE_FLTUND = 3;
	public static final int FPE_INTDIV = 7;
	public static final int FPE_INTOVF = 8;
	public static final int ILL_BADSTK = 8;
	public static final int ILL_COPROC = 7;
	public static final int ILL_ILLADR = 5;
	public static final int ILL_ILLOPC = 1;
	public static final int ILL_ILLOPN = 4;
	public static final int ILL_ILLTRP = 2;
	public static final int ILL_PRVOPC = 3;
	public static final int ILL_PRVREG = 6;
	public static final int POLL_ERR = 4;
	public static final int POLL_HUP = 6;
	public static final int POLL_IN = 1;
	public static final int POLL_MSG = 3;
	public static final int POLL_OUT = 2;
	public static final int POLL_PRI = 5;
	public static final int SEGV_ACCERR = 2;
	public static final int SEGV_MAPERR = 1;
	public static final int TRAP_BRKPT = 1;
	public static final int TRAP_TRACE = 2;
}
