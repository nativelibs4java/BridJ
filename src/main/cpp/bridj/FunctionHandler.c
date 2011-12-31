#include "HandlersCommon.h"

void __cdecl JavaToFunctionCallHandler_Sub(CallTempStruct* call, FunctionCallInfo* info, DCArgs* args, DCValue* result)
{
	dcMode(call->vm, info->fInfo.fDCMode);
	//dcReset(call->vm);
	
	followArgs(call, args, info->fInfo.nParams, info->fInfo.fParamTypes, JNI_FALSE, JNI_FALSE) 
	&&
	followCall(call, info->fInfo.fReturnType, result, info->fForwardedSymbol, JNI_FALSE, JNI_FALSE);

}
char __cdecl JavaToFunctionCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata)
{
	FunctionCallInfo* info = (FunctionCallInfo*)userdata;
	CallTempStruct* call;
	JNIEnv* env;
	initCallHandler(args, &call, NULL, &info->fInfo);
	env = call->env;
	
	call->pCallIOs = info->fInfo.fCallIOs;
	
	BEGIN_TRY(env, call);
	
	if (info->fCheckLastError)
		clearLastError(info->fInfo.fEnv);
	
	JavaToFunctionCallHandler_Sub(call, info, args, result);

	if (info->fCheckLastError)
		throwIfLastError(info->fInfo.fEnv);
	
	END_TRY(info->fInfo.fEnv, call);

	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
}
