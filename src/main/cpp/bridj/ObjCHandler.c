#include "HandlersCommon.h"

#ifdef BRIDJ_OBJC_SUPPORT
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
	}
#endif

	dcMode(call->vm, info->fInfo.fDCMode);
	//dcReset(call->vm);
	
	dcArgPointer(call->vm, targetId);
	dcArgPointer(call->vm, info->fSelector);
	
	followArgs(call, args, info->fInfo.nParams, info->fInfo.fParamTypes, JNI_FALSE, JNI_TRUE)// TODO isVarArgs ?? 
	&&
	followCall(call, info->fInfo.fReturnType, result, callback, JNI_FALSE, JNI_FALSE);

	END_TRY(info->fInfo.fEnv, call);
	cleanupCallHandler(call);
	
	return info->fInfo.fDCReturnType;
}
#endif

