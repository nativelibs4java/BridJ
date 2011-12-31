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
		followArgsGenericJavaCallback(call, args, info->fInfo.nParams, info->fInfo.fParamTypes)
		&&
		followCallGenericJavaCallback(call, info->fInfo.fReturnType, result, (void*)(*call->env)->CallObjectMethod);
	} else {
		followArgs(call, args, info->fInfo.nParams, info->fInfo.fParamTypes, JNI_TRUE, JNI_TRUE)
		&&
		followCall(call, info->fInfo.fReturnType, result, info->fJNICallFunction, JNI_TRUE, JNI_FALSE);
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
	
	followArgs(call, args, info->fInfo.nParams, info->fInfo.fParamTypes, JNI_TRUE, JNI_TRUE)
	&&
	followCall(call, info->fInfo.fReturnType, result, info->fJNICallFunction, JNI_TRUE, JNI_FALSE);

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
	followArgs(call, args, info->fInfo.nParams, info->fInfo.fParamTypes, JNI_FALSE, JNI_FALSE)
	&&
	followCall(call, info->fInfo.fReturnType, result, callbackPtr, JNI_FALSE, JNI_FALSE);
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
