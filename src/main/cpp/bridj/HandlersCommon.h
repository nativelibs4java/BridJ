#include "bridj.hpp"
#include <jni.h>
#include "Exceptions.h"

jboolean followArgs(CallTempStruct* call, DCArgs* args, int nTypes, ValueType* pTypes, jboolean toJava, jboolean isVarArgs);

jboolean followCall(CallTempStruct* call, ValueType returnType, DCValue* result, void* callback, jboolean bCallingJava, jboolean forceVoidReturn);

jobject initCallHandler(DCArgs* args, CallTempStruct** callOut, JNIEnv* env, CommonCallbackInfo* info);
void cleanupCallHandler(CallTempStruct* call);

jboolean followArgsGenericJavaCallback(CallTempStruct* call, DCArgs* args, int nTypes, ValueType* pTypes) ;
jboolean followCallGenericJavaCallback(CallTempStruct* call, ValueType returnType, DCValue* result, void* callback); 
