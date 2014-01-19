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
#define _GNU_SOURCE

#include "bridj.hpp"
#include "jni.h"
#include "JNI.h"

#include "Exceptions.h"

#include <string.h>
#include <errno.h>

#ifdef _WIN32
#include "windows.h"
#define strerror_r(errno,buf,len) strerror_s(buf,len,errno)
#endif

#define STRERROR_BUFLEN 1024

// http://msdn.microsoft.com/en-us/library/ms679356(VS.85).aspx

extern jclass gSignalErrorClass;
extern jmethodID gSignalErrorThrowMethod;

extern jclass gWindowsErrorClass;
extern jmethodID gWindowsErrorThrowMethod;

void throwException(JNIEnv* env, const char* message) {
	if ((*env)->ExceptionCheck(env))
		return; // there is already a pending exception
	(*env)->ExceptionClear(env);
	(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/RuntimeException"), message ? message : "No message (TODO)");
}

#ifdef __GNUC__
void throwSignalError(JNIEnv* env, int signal, int signalCode, jlong address) {
	initMethods(env);
	(*env)->CallStaticVoidMethod(env, gSignalErrorClass, gSignalErrorThrowMethod, signal, signalCode, address);
}
#else
void throwWindowsError(JNIEnv* env, int code, jlong info, jlong address) {
	initMethods(env);
	(*env)->CallStaticVoidMethod(env, gWindowsErrorClass, gWindowsErrorThrowMethod, code, info, address);
}
#endif

jboolean assertThrow(JNIEnv* env, jboolean value, const char* message) {
	if (!value)
		throwException(env, message);
	return value;
}

//#if defined(ENABLE_PROTECTED_MODE)

#ifdef __GNUC__

//Signals gSignals;


void TrapSignals(Signals* s) 
{	
	struct sigaction act;
	memset(&act, 0, sizeof(struct sigaction));
	act.sa_sigaction = UnixExceptionHandler;
	act.sa_flags = SA_SIGINFO | SA_NOCLDSTOP | SA_NOCLDWAIT;
	
#define TRAP_SIG(sig) \
	sigaction(sig, &act, &s->fOld ## sig);
		
	TRAP_SIG(SIGSEGV)
	TRAP_SIG(SIGBUS)
	TRAP_SIG(SIGFPE)
	TRAP_SIG(SIGCHLD)
	TRAP_SIG(SIGILL)
	TRAP_SIG(SIGABRT)
	//TRAP_SIG(SIGTRAP)
}
void RestoreSignals(Signals* s) {
	#define UNTRAP_SIG(sig) \
		sigaction(sig, &s->fOld ## sig, NULL);
	
	UNTRAP_SIG(SIGSEGV)
	UNTRAP_SIG(SIGBUS)
	UNTRAP_SIG(SIGFPE)
	UNTRAP_SIG(SIGCHLD)
	UNTRAP_SIG(SIGILL)
	UNTRAP_SIG(SIGABRT)
	//UNTRAP_SIG(SIGTRAP)
}

void InitProtection() {
	//TrapSignals(&gSignals);
}

void CleanupProtection() {
	//RestoreSignals(&gSignals);
}

//void UnixExceptionHandler(int sig) {
void UnixExceptionHandler(int sig, siginfo_t* si, void * ctx)
{
  JNIEnv* env = GetEnv();
  CallTempStruct* call = getCurrentTempCallStruct(env);
  if (!call)
  	  return;
  
  call->signal = sig;
  call->signalCode = si->si_code;
  call->signalAddress = PTR_TO_JLONG(si->si_addr);
  
  longjmp(call->exceptionContext, sig);
}

#else

int WinExceptionFilter(LPEXCEPTION_POINTERS ex) {
	switch (ex->ExceptionRecord->ExceptionCode) {
		case 0x40010005: // Control+C
		case 0x80000003: // Breakpoint
			return EXCEPTION_CONTINUE_SEARCH;
	}
	return EXCEPTION_EXECUTE_HANDLER;
}
void WinExceptionHandler(JNIEnv* env, LPEXCEPTION_POINTERS ex) {
	int code = ex->ExceptionRecord->ExceptionCode;
	jlong info;
	void* address;

	if ((code == EXCEPTION_ACCESS_VIOLATION || code == EXCEPTION_IN_PAGE_ERROR) && ex->ExceptionRecord->NumberParameters >= 2) {
		info = ex->ExceptionRecord->ExceptionInformation[0];
		address = (void*)ex->ExceptionRecord->ExceptionInformation[1];
	} else {
		info = 0;
		address = ex->ExceptionRecord->ExceptionAddress;
	}

	throwWindowsError(env, code, info, PTR_TO_JLONG(address));
}

//#endif //defined(ENABLE_PROTECTED_MODE)

#endif
