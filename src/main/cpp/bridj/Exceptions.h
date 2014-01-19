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
#pragma once
#ifndef _BRIDJ_EXCEPTIONS_H
#define _BRIDJ_EXCEPTIONS_H

#include <jni.h>

#define ENABLE_PROTECTED_MODE
#if defined(ENABLE_PROTECTED_MODE)

#include "bridj.hpp"
//#include "Protected.h"
extern jboolean gProtected;

#if defined(__GNUC__)

void throwSignalError(JNIEnv* env, int signal, int signalCode, jlong address);

static inline jboolean DoTrapSignals(CallTempStruct* call) {
	//call->signal = call->signalCode = 0;
	//call->signalAddress = 0;
	TrapSignals(&call->signals);
	return JNI_TRUE;
}

#define BEGIN_TRY_BASE(env, call, prot) \
	if (!prot || !DoTrapSignals(call) || (call->signal = setjmp(call->exceptionContext)) == 0) \
	{
		
#define END_TRY_BASE(env, call, prot, ifProt) \
	} else { \
		throwSignalError(env, call->signal, call->signalCode, call->signalAddress); \
	} \
	if (prot) { \
		RestoreSignals(&call->signals); \
		ifProt \
	}
	
#define BEGIN_TRY(env, call) BEGIN_TRY_BASE(env, call, gProtected)
		
#define BEGIN_TRY_CALL(env) \
	{ \
		jboolean _protected = gProtected; \
		{ \
			CallTempStruct* call = _protected ? getTempCallStruct(env) : NULL; \
			BEGIN_TRY_BASE(env, call, _protected);

#define END_TRY(env, call) END_TRY_BASE(env, call, gProtected, )

#define END_TRY_CALL(env) \
			END_TRY_BASE(env, call, _protected, releaseTempCallStruct(env, call);) \
		} \
	}

#else

// WINDOWS
#define BEGIN_TRY(env, call) \
	{ \
		LPEXCEPTION_POINTERS exceptionPointers = NULL; \
		__try \
		{
			
#define END_TRY(env, call) \
		} \
		__except (gProtected ? WinExceptionFilter(exceptionPointers = GetExceptionInformation()) : EXCEPTION_CONTINUE_SEARCH) \
		{ \
			WinExceptionHandler(env, exceptionPointers); \
		} \
	}

#define BEGIN_TRY_CALL(env) BEGIN_TRY(env,) 
#define END_TRY_CALL(env) END_TRY(env, )

#endif

#else

#define BEGIN_TRY(env, call) {
#define END_TRY(env, call) }

#define BEGIN_TRY_CALL(env) { 
#define END_TRY_CALL(env) } 

#endif // defined(ENABLE_PROTECTED_MODE)

#endif // _BRIDJ_EXCEPTIONS_H
