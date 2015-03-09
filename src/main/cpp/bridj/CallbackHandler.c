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

#if defined(DC__OS_Win32) && !defined(DC__OS_Win64)
#define JNI_CALL_MODE DC_CALL_C_X86_WIN32_STD
#else
#define JNI_CALL_MODE DC_CALL_C_DEFAULT
#endif

void __cdecl CToJavaCallHandler_Sub(CallTempStruct* call, NativeToJavaCallbackCallInfo* info, DCArgs* args, DCValue* result)
{
	dcMode(call->vm, JNI_CALL_MODE);
	//dcReset(call->vm);
	
	if (!info->fCallbackInstance)
	{
		throwException(call->env, "Trying to call a null callback instance !");
		return;
	}

	dcArgPointer(call->vm, (DCpointer)call->env);
	dcArgPointer(call->vm, info->fCallbackInstance);
	dcArgPointer(call->vm, info->fInfo.fMethodID);
	
	if (info->fIsObjCBlock)
		dcbArgPointer(args); // consume the pointer to the block instance ; TODO use it to reuse native callbacks !!!
	
	if (info->fIsGenericCallback) {
		callGenericFunction(call, &info->fInfo, args, result, (void*)(*call->env)->CallObjectMethod);
	} else {
		callFunction(call, &info->fInfo, args, result, info->fJNICallFunction, CALLING_JAVA | IS_VAR_ARGS);
	}
	
}

char __cdecl CToJavaCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	CallTempStruct* call;
	jthrowable exc;
	NativeToJavaCallbackCallInfo* info = (NativeToJavaCallbackCallInfo*)userdata;
	JNIEnv *env = GetEnv();
	initCallHandler(NULL, &call, env, &info->fInfo);
	
	call->pCallIOs = info->fInfo.fCallIOs;
	
	BEGIN_TRY(env, call);
	
	CToJavaCallHandler_Sub(call, info, args, result);
	
	END_TRY(info->fInfo.fEnv, call);

	exc = (*env)->ExceptionOccurred(env);
	if (exc) {
		(*env)->ExceptionDescribe(env);
        printStackTrace(env, exc);
		//(*env)->ExceptionClear(env);
	}
	
	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
	
}

void __cdecl CPPToJavaCallHandler_Sub(CallTempStruct* call, NativeToJavaCallbackCallInfo* info, DCArgs* args, DCValue* result)
{
	void* cppObject;
	jobject javaObject;
	
	dcMode(call->vm, JNI_CALL_MODE);
	//dcReset(call->vm);
	
	if (info->fCallbackInstance)
	{
		throwException(call->env, "Not expecting a callback instance here !");
		return;
	}
	
	cppObject = dcbArgPointer(args);
	javaObject = getJavaObjectForNativePointer(call->env, cppObject);
	dcArgPointer(call->vm, (DCpointer)call->env);
	dcArgPointer(call->vm, javaObject);
	dcArgPointer(call->vm, info->fInfo.fMethodID);
	
	callFunction(call, &info->fInfo, args, result, info->fJNICallFunction, CALLING_JAVA | IS_VAR_ARGS);
}

char __cdecl CPPToJavaCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	CallTempStruct* call;
	jthrowable exc;
	NativeToJavaCallbackCallInfo* info = (NativeToJavaCallbackCallInfo*)userdata;
	JNIEnv *env = GetEnv();
	initCallHandler(NULL, &call, env, &info->fInfo);
	
	call->pCallIOs = info->fInfo.fCallIOs;
	
	BEGIN_TRY(env, call);
	
	CPPToJavaCallHandler_Sub(call, info, args, result);

	END_TRY(info->fInfo.fEnv, call);

	exc = (*env)->ExceptionOccurred(env);
	if (exc) {
		(*env)->ExceptionDescribe(env);
        printStackTrace(env, exc);
		//(*env)->ExceptionClear(env);
		// TODO rethrow in native world ?
	}
	
	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
}

void __cdecl JavaToCCallHandler_Sub(CallTempStruct* call, JavaToNativeCallbackCallInfo* info, jobject instance, DCArgs* args, DCValue* result)
{
	void* callbackPtr;
	
	dcMode(call->vm, info->fInfo.fDCMode);
	//dcReset(call->vm);
	
	callbackPtr = getNativeObjectPointer(call->env, instance, NULL);
	
	// printf("doJavaToCCallHandler(callback = %d) !!!\n", callback);
	
	callFunction(call, &info->fInfo, args, result, callbackPtr, 0);
}
char __cdecl JavaToCCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	CallTempStruct* call;
	JavaToNativeCallbackCallInfo* info = (JavaToNativeCallbackCallInfo*)userdata;
	jobject instance = initCallHandler(args, &call, NULL, &info->fInfo);
	
	// printf("JavaToCCallHandler !!!\n");
	call->pCallIOs = info->fInfo.fCallIOs;
	
	BEGIN_TRY(env, call);
	
	JavaToCCallHandler_Sub(call, info, instance, args, result);

	END_TRY(info->fInfo.fEnv, call);
	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
}
