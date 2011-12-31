#pragma once
#ifndef _BRIDJ_PROTECTED_H
#define _BRIDJ_PROTECTED_H

#include <jni.h>

#if defined(__GNUC__)

#include <signal.h>
#include <setjmp.h>

typedef struct Signals {
	struct sigaction fOldSIGSEGV;
	struct sigaction fOldSIGBUS;
	struct sigaction fOldSIGFPE; 
	struct sigaction fOldSIGCHLD;
	struct sigaction fOldSIGABRT;
	struct sigaction fOldSIGILL;	
	struct sigaction fOldSIGTRAP;	
} Signals;

void TrapSignals(Signals* s);
void RestoreSignals(Signals* s);
//void UnixExceptionHandler(int sig);
void UnixExceptionHandler(int, siginfo_t*, void*);

#else

// WINDOWS
#include <windows.h>

void WinExceptionHandler(JNIEnv* env, LPEXCEPTION_POINTERS ex);
int WinExceptionFilter(LPEXCEPTION_POINTERS ex);

#endif

#endif // _BRIDJ_PROTECTED_H
