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
#include "bridj.hpp"
#include "jni.h"
#include "JNI.h"

#include "Exceptions.h"

#include <string.h>
#include <errno.h>

#ifdef _WIN32
#include "windows.h"
#endif

// http://msdn.microsoft.com/en-us/library/ms679356(VS.85).aspx

extern jclass gLastErrorClass;
extern jmethodID gSetLastErrorMethod;

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

void clearLastError(JNIEnv* env) {
#ifdef _WIN32
	SetLastError(0);
#endif
	errno = 0;
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

#ifdef _WIN32
jstring formatWin32ErrorMessage(JNIEnv* env, int errorCode)
{
	jstring message = NULL;
	// http://msdn.microsoft.com/en-us/library/ms680582(v=vs.85).aspx
	LPVOID lpMsgBuf;
	int res;
	res = FormatMessageA(
		FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL,
		errorCode,
		MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
		(LPSTR) &lpMsgBuf,
		0, 
		NULL 
	);
	if (res) {
		message = (*env)->NewStringUTF(env, (LPCSTR)lpMsgBuf);
		LocalFree(lpMsgBuf);
	} else {
#define MESSAGE_BUF_SIZE 2048
		char lpMsgBuf[MESSAGE_BUF_SIZE + 1];
		//sprintf(lpMsgBuf, "Last Error Code = %d", errorCode);
		message = (*env)->NewStringUTF(env, lpMsgBuf);
	}
	return message;
}
#endif

void setLastError(JNIEnv* env, LastError lastError, jboolean throwsLastError) {
	if (lastError.value) {
		jobject err = (*env)->CallStaticObjectMethod(env, gLastErrorClass, gSetLastErrorMethod, 
			lastError.value,
			lastError.kind);
		if (err && throwsLastError) {
			(*env)->Throw(env, err);
		}
	}
}

LastError getLastError() {
	{
	int errnoCopy = errno;
	LastError ret;
#ifdef _WIN32
	int errorCode = GetLastError();
	if (errorCode) {
	  ret.value = errorCode;
	  ret.kind = eLastErrorKindWindows;
	  return ret;
	}
#endif
	ret.value = errnoCopy;
	ret.kind = eLastErrorKindCLibrary;
	return ret;
	}
}

JNIEXPORT jstring JNICALL Java_org_bridj_LastError_getDescription(JNIEnv* env, jclass clazz, jint code, jint kind) {
  if (!code) {
    return NULL;
  }
  switch ((LastErrorKind)kind) {
#ifdef _WIN32
  case eLastErrorKindWindows:
    return formatWin32ErrorMessage(env, code);
#endif
  case eLastErrorKindCLibrary:
    {
      const char* msg = strerror(code);
      return msg ? (*env)->NewStringUTF(env, msg) : NULL;
    }
  default:
    return NULL; // TODO throw something?
  }
}

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
