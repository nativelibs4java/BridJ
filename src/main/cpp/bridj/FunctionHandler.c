/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
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
#include "HandlersCommon.h"
#include "Errors.h"
#include "string.h"

void __cdecl JavaToFunctionCallHandler_Sub(CallTempStruct* call, FunctionCallInfo* info, DCArgs* args, DCValue* result, jboolean setsLastError)
{
	dcMode(call->vm, info->fInfo.fDCMode);
	//dcReset(call->vm);
	
	callFunction(call, &info->fInfo, args, result, info->fForwardedSymbol, setsLastError ? SETS_LASTERROR : 0);
}
char __cdecl JavaToFunctionCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	FunctionCallInfo* info = (FunctionCallInfo*)userdata;
	CallTempStruct* call;
	JNIEnv* env;
	LastError lastError = { 0, 0 };
	jboolean setsLastError = info->fInfo.fSetsLastError;
	initCallHandler(args, &call, NULL, &info->fInfo);
	env = call->env;
	
	call->pCallIOs = info->fInfo.fCallIOs;
	
	BEGIN_TRY(env, call);
	
	if (setsLastError) {
		clearLastError(info->fInfo.fEnv);
	}

	JavaToFunctionCallHandler_Sub(call, info, args, result, setsLastError);

	if (setsLastError) {
	  lastError = call->lastError;
	  //memcpy(&lastError, &call->lastError, sizeof(LastError));
	}
	
	END_TRY(info->fInfo.fEnv, call);

	cleanupCallHandler(call);

  if (setsLastError) {
    setLastError(env, lastError, info->fInfo.fThrowsLastError);
  }
	return info->fInfo.fDCReturnType;
}
