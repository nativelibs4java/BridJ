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

#ifdef BRIDJ_OBJC_SUPPORT

#include "HandlersCommon.h"

#include <objc/message.h>

char __cdecl JavaToObjCCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	JavaToObjCCallInfo* info = (JavaToObjCCallInfo*)userdata;
	CallTempStruct* call;
	jobject instance = initCallHandler(args, &call, NULL, &info->fInfo);
	JNIEnv* env = call->env;
	BEGIN_TRY(env, call);
	
	call->pCallIOs = info->fInfo.fCallIOs;
	
	void* targetId = info->fNativeClass ? JLONG_TO_PTR(info->fNativeClass) : getNativeObjectPointer(env, instance, NULL);
	void* callback = //objc_msgSend_stret;//
        objc_msgSend;
	
#if defined(DC__Arch_Intel_x86)
	switch (info->fInfo.fReturnType) {
    case eDoubleValue:
    case eFloatValue:
      callback = objc_msgSend_fpret;
      break;
    default:
      break;
	}
#endif

	dcMode(call->vm, info->fInfo.fDCMode);
	//dcReset(call->vm);
	
	dcArgPointer(call->vm, targetId);
	dcArgPointer(call->vm, info->fSelector);
	
	// TODO isVarArgs ?? 
	callFunction(call, &info->fInfo, args, result, callback, IS_VAR_ARGS);
	
	END_TRY(info->fInfo.fEnv, call);
	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
}

#endif // BRIDJ_OBJC_SUPPORT

