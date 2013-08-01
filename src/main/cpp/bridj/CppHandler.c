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
#include "HandlersCommon.h"

void callSinglePointerArgVoidFunction(JNIEnv* env, void* constructor, void* thisPtr, int callMode)
{
	CallTempStruct* call;
	initCallHandler(NULL, &call, env, NULL);
	
	dcMode(call->vm, callMode);
	//dcReset(call->vm);
	
	dcArgPointer(call->vm, thisPtr);
	dcCallVoid(call->vm, constructor);

	cleanupCallHandler(call);
}

void* getNthVirtualMethodFromThis(JNIEnv* env, void* thisPtr, size_t virtualTableOffset, size_t virtualIndex) {
	// Get virtual pointer table
	void* ret;
	void** vptr = (void**)*((void**)thisPtr);
	if (!vptr) {
		throwException(env, "Null virtual pointer table !");
		return NULL;
	}
	ret = (void*)vptr[virtualIndex];
	if (!ret)
		throwException(env, "Failed to get the method pointer from the virtual table !");
		//THROW_EXCEPTION(env, "Failed to get the method pointer from the virtual table ! Virtual index = %lld, vtable ptr = 0x%llx", (long long int)virtualIndex, (long long unsigned int)PTR_TO_JLONG(vptr));
	
	return ret;
}

void JavaToVirtualMethodCallHandler_Sub(CallTempStruct* call, VirtualMethodCallInfo* info, jobject instance, DCArgs* args, DCValue* result)
{
	void* callbackFn;
	void* thisPtr;
	int nParams = info->fInfo.nParams;
	ValueType *pParamTypes = info->fInfo.fParamTypes;
	
	dcMode(call->vm, info->fInfo.fDCMode);
	//dcReset(call->vm);
	
	if (info->fHasThisPtrArg) {
		if (nParams == 0 || *pParamTypes != eSizeTValue) {
			throwException(call->env, "A C++ method must be bound with a method having a first argument of type long !");
			return;
		}
		thisPtr = dcbArgPointer(args);
		if (!thisPtr) {
			throwException(call->env, "Calling a method on a NULL C++ class pointer !");
			return;
		}
		nParams--;
		pParamTypes++;
		
	} else {
		thisPtr = getNativeObjectPointer(call->env, instance, info->fClass);
		if (!thisPtr) {
			throwException(call->env, "Failed to get the pointer to the target C++ instance of the method invocation !");
			return;
		}
		
		//nParams--;
		//pParamTypes++;
		
	}
	
	callbackFn = getNthVirtualMethodFromThis(call->env, thisPtr, info->fVirtualTableOffset, info->fVirtualIndex);
	if (!callbackFn) {
		throwException(call->env, "Virtual method pointer found in virtual table is NULL !");			
		return;
	}
		
	dcArgPointer(call->vm, thisPtr);

	followArgs(call, args, nParams, pParamTypes, NO_FLAGS) 
	&&
	followCall(call, info->fInfo.fReturnType, result, callbackFn, NO_FLAGS);

}
char __cdecl JavaToVirtualMethodCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	VirtualMethodCallInfo* info = (VirtualMethodCallInfo*)userdata;
	CallTempStruct* call;
	jobject instance = initCallHandler(args, &call, NULL, &info->fInfo);
	
	call->pCallIOs = info->fInfo.fCallIOs;
	
	BEGIN_TRY(call->env, call);

	JavaToVirtualMethodCallHandler_Sub(call, info, instance, args, result);
	
	END_TRY(info->fInfo.fEnv, call);
	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
}

void JavaToCPPMethodCallHandler_Sub(CallTempStruct* call, FunctionCallInfo* info, jobject instance, DCArgs* args, DCValue* result)
{
	void* thisPtr;
	
	dcMode(call->vm, info->fInfo.fDCMode);
	//dcReset(call->vm);
	
	thisPtr = getNativeObjectPointer(call->env, instance, info->fClass);
	if (!thisPtr) {
		throwException(call->env, "Failed to get the pointer to the target C++ instance of the method invocation !");
		return;
	}
	dcArgPointer(call->vm, thisPtr);
	
	callFunction(call, &info->fInfo, args, result, info->fForwardedSymbol, 0);
}
char __cdecl JavaToCPPMethodCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	FunctionCallInfo* info = (FunctionCallInfo*)userdata;
	CallTempStruct* call;
	jobject instance = initCallHandler(args, &call, NULL, &info->fInfo);
	
	call->pCallIOs = info->fInfo.fCallIOs;
	
	BEGIN_TRY(call->env, call);

	JavaToCPPMethodCallHandler_Sub(call, info, instance, args, result);

	END_TRY(info->fInfo.fEnv, call);
	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
}

