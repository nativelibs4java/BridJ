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

	followArgs(call, args, nParams, pParamTypes, JNI_FALSE, JNI_FALSE) 
	&&
	followCall(call, info->fInfo.fReturnType, result, callbackFn, JNI_FALSE, JNI_FALSE);

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
	
	followArgs(call, args, info->fInfo.nParams, info->fInfo.fParamTypes, JNI_FALSE, JNI_FALSE) 
	&&
	followCall(call, info->fInfo.fReturnType, result, info->fForwardedSymbol, JNI_FALSE, JNI_FALSE);
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

