#include "org_bridj_JNI.h"
#include "JNI.h"

#include "dyncallback/dyncall_callback.h"
#include "dynload/dynload.h"
#include "RawNativeForwardCallback.h"

#include "bridj.hpp"
#include <string.h>
#include <wchar.h>
#include <math.h>
#include <time.h>
#include "Exceptions.h"
#include <stdlib.h>

#pragma warning(disable: 4152)
#pragma warning(disable: 4189) // local variable initialized but unreferenced // TODO remove this !

jboolean gLog = JNI_FALSE;
jboolean gProtected = JNI_FALSE;

jclass gObjectClass = NULL;
jclass gPointerClass = NULL;
//jclass gFlagSetClass = NULL;
jclass gValuedEnumClass = NULL;
jclass gBridJClass = NULL;
jclass gCallIOClass = NULL;
jclass gLastErrorClass = NULL;
jclass gRunnableClass = NULL;
jmethodID gRunnableRunMethod = NULL;
jmethodID gAddressMethod = NULL;
jmethodID gGetPeerMethod = NULL;
jmethodID gCreatePeerMethod = NULL;
jmethodID gGetValuedEnumValueMethod = NULL;
jmethodID gGetJavaObjectFromNativePeerMethod = NULL;
//jmethodID gNewFlagSetMethod = NULL;
jmethodID gThrowNewLastErrorMethod = NULL;
jmethodID gGetCallIOsMethod = NULL;
jmethodID gGetCallIOStructMethod = NULL;
jmethodID gCallIOGetPeerMethod = NULL;
jmethodID gNewCallIOInstance = NULL;
jmethodID gLogCallMethod = NULL;
jfieldID gLogCallsField = NULL;
jfieldID gProtectedModeField = NULL;

jclass 		gMethodCallInfoClass 		 = NULL;
jfieldID 	gFieldId_javaSignature 		 = NULL;
jfieldID 	gFieldId_dcSignature 		 = NULL;    
jfieldID 	gFieldId_paramsValueTypes 	 = NULL;
jfieldID 	gFieldId_returnValueType 	 = NULL;    
jfieldID 	gFieldId_forwardedPointer 	 = NULL;
jfieldID 	gFieldId_virtualIndex 		 = NULL;
jfieldID 	gFieldId_virtualTableOffset	 = NULL;
jfieldID 	gFieldId_javaCallback 		 = NULL;
jfieldID 	gFieldId_isGenericCallback    = NULL;
jfieldID 	gFieldId_isObjCBlock    = NULL;
jfieldID 	gFieldId_direct		 		 = NULL;
jfieldID 	gFieldId_startsWithThis 	     = NULL;
jfieldID 	gFieldId_isCPlusPlus 	     = NULL;
jfieldID 	gFieldId_isStatic    	     = NULL;
jfieldID 	gFieldId_bNeedsThisPointer 	 = NULL;
jfieldID 	gFieldId_bThrowLastError 	 = NULL;
jfieldID 	gFieldId_dcCallingConvention = NULL;
jfieldID 	gFieldId_symbolName			 = NULL;
jfieldID 	gFieldId_nativeClass			 = NULL;
jfieldID 	gFieldId_methodName			 = NULL;
jfieldID 	gFieldId_method    			 = NULL;
jfieldID 	gFieldId_declaringClass		 = NULL;

#ifdef __GNUC__
jclass gSignalErrorClass = NULL;
jmethodID gSignalErrorThrowMethod = NULL;
#else
jclass gWindowsErrorClass = NULL;
jmethodID gWindowsErrorThrowMethod = NULL;
#endif

/*jclass gCLongClass = NULL;
jclass gSizeTClass = NULL;
jmethodID gCLongValueMethod = NULL;
jmethodID gSizeTValueMethod = NULL;
jlong UnboxCLong(JNIEnv* env, jobject v) {
	return (*env)->CallLongMethod(env, v, gCLongValueMethod);
}
jlong UnboxSizeT(JNIEnv* env, jobject v) { \
	return (*env)->CallLongMethod(env, v, gSizeTValueMethod);
}*/

#define BOX_METHOD_IMPL(prim, shortName, methShort, type, letter) \
jclass g ## shortName ## Class = NULL; \
jmethodID g ## shortName ## ValueOfMethod = NULL; \
jmethodID g ## shortName ## ValueMethod = NULL; \
jobject Box ## shortName(JNIEnv* env, type v) { \
	return (*env)->CallStaticObjectMethod(env, g ## shortName ## Class, g ## shortName ## ValueOfMethod, (jlong)v); \
} \
type Unbox ## shortName(JNIEnv* env, jobject v) { \
	HACK_REFETCH_ENV(); \
	return (type)(*env)->Call ## methShort ## Method(env, v, g ## shortName ## ValueMethod); \
}
//
			
BOX_METHOD_IMPL("org/bridj/TimeT", TimeT, Long, time_t, "J");
BOX_METHOD_IMPL("org/bridj/SizeT", SizeT, Long, jlong, "J");
BOX_METHOD_IMPL("org/bridj/CLong", CLong, Long, long, "J");
BOX_METHOD_IMPL("java/lang/Integer", Int, Int, jint, "I");
BOX_METHOD_IMPL("java/lang/Long", Long, Long, jlong, "J");
BOX_METHOD_IMPL("java/lang/Short", Short, Short, jshort, "S");
BOX_METHOD_IMPL("java/lang/Byte", Byte, Byte, jbyte, "B");
BOX_METHOD_IMPL("java/lang/Boolean", Boolean, Boolean, jboolean, "Z");
BOX_METHOD_IMPL("java/lang/Character", Char, Char, jchar, "C");
BOX_METHOD_IMPL("java/lang/Float", Float, Float, jfloat, "F");
BOX_METHOD_IMPL("java/lang/Double", Double, Double, jdouble, "D");    
		
int main() {}

void printStackTrace(JNIEnv* env, jthrowable ex) {
	jthrowable cause;
	jclass thClass = (*env)->FindClass(env, "java/lang/Throwable");
	jmethodID printMeth = (*env)->GetMethodID(env, thClass, "printStackTrace", "()V");
	jmethodID causeMeth = (*env)->GetMethodID(env, thClass, "getCause", "()Ljava/lang/Throwable;");
	if (!ex) {
		jclass exClass = (*env)->FindClass(env, "java/lang/RuntimeException");
		jmethodID initMeth = (*env)->GetMethodID(env, exClass, "<init>", "()V");
		ex = (jthrowable)(*env)->NewObject(env, exClass, initMeth);
	}
	(*env)->CallVoidMethod(env, (jobject)ex, printMeth);
	cause = (jthrowable)(*env)->CallObjectMethod(env, ex, causeMeth);
	if (cause)
		printStackTrace(env, cause);
}

JavaVM* gJVM = NULL;
#define JNI_VERSION JNI_VERSION_1_4

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* x) {
  gJVM = jvm;
  return JNI_VERSION;
}

JNIEnv* GetEnv() {
  JNIEnv* env = NULL;
  if ((*gJVM)->GetEnv(gJVM, (void*)&env, JNI_VERSION) != JNI_OK) {
    if ((*gJVM)->AttachCurrentThreadAsDaemon(gJVM, (void*)&env, NULL) != JNI_OK) {
	  printf("BridJ: Cannot attach current JVM thread !\n");
      return NULL;
    }
  }
  return env;
}

void InitProtection();
void initPlatformMethods(JNIEnv* env);

void initMethods(JNIEnv* env) {
	//InitProtection();
	
	if (!gAddressMethod)
	{
		gObjectClass = FIND_GLOBAL_CLASS("java/lang/Object");
		gRunnableClass = FIND_GLOBAL_CLASS("java/lang/Runnable");
		
		#define INIT_PRIM(prim, shortName, methShort, type, letter) \
			g ## shortName ## Class = FIND_GLOBAL_CLASS(prim); \
			g ## shortName ## ValueMethod = (*env)->GetMethodID(env, g ## shortName ## Class, #type "Value", "()" letter); \
			g ## shortName ## ValueOfMethod = (*env)->GetStaticMethodID(env, g ## shortName ## Class, "valueOf", "(" letter ")L" prim ";");
			
		INIT_PRIM("org/bridj/SizeT", SizeT, Long, long, "J");
		INIT_PRIM("org/bridj/TimeT", TimeT, Long, long, "J");
		INIT_PRIM("org/bridj/CLong", CLong, Long, long, "J");
		INIT_PRIM("java/lang/Integer", Int, Int, int, "I");
		INIT_PRIM("java/lang/Long", Long, Long, long, "J");
		INIT_PRIM("java/lang/Short", Short, Short, short, "S");
		INIT_PRIM("java/lang/Byte", Byte, Byte, byte, "B");
		INIT_PRIM("java/lang/Boolean", Boolean, Boolean, boolean, "Z");
		INIT_PRIM("java/lang/Character", Char, Char, char, "C");
		INIT_PRIM("java/lang/Float", Float, Float, float, "F");
		INIT_PRIM("java/lang/Double", Double, Double, double, "D");    
		
		gBridJClass = FIND_GLOBAL_CLASS("org/bridj/BridJ");
		//gFlagSetClass = FIND_GLOBAL_CLASS("org/bridj/FlagSet");
		gValuedEnumClass = FIND_GLOBAL_CLASS("org/bridj/ValuedEnum");
		//gStructFieldsIOClass = FIND_GLOBAL_CLASS("org/bridj/StructFieldsIO");
		gPointerClass = FIND_GLOBAL_CLASS("org/bridj/Pointer");
		gMethodCallInfoClass = FIND_GLOBAL_CLASS("org/bridj/MethodCallInfo");
		gCallIOClass = FIND_GLOBAL_CLASS("org/bridj/CallIO");
		gLastErrorClass = FIND_GLOBAL_CLASS("org/bridj/LastError");
		
		gRunnableRunMethod = (*env)->GetMethodID(env, gRunnableClass, "run", "()V");
		//gGetTempCallStruct = (*env)->GetStaticMethodID(env, gBridJClass, "getTempCallStruct", "()J"); 
		//gReleaseTempCallStruct = (*env)->GetStaticMethodID(env, gBridJClass, "releaseTempCallStruct", "(J)V"); 
		gGetValuedEnumValueMethod = (*env)->GetMethodID(env, gValuedEnumClass, "value", "()J");
		gGetJavaObjectFromNativePeerMethod = (*env)->GetStaticMethodID(env, gBridJClass, "getJavaObjectFromNativePeer", "(J)" OBJECT_SIG);
		//gNewFlagSetMethod = (*env)->GetStaticMethodID(env, gFlagSetClass, "fromValue", "(J" CLASS_SIG ")Lorg/bridj/FlagSet;"); 
		gAddressMethod = (*env)->GetStaticMethodID(env, gPointerClass, "getAddress", "(Lorg/bridj/NativeObject;" CLASS_SIG ")J");
		gGetPeerMethod = (*env)->GetMethodID(env, gPointerClass, "getPeer", "()J");
		gCreatePeerMethod = (*env)->GetStaticMethodID(env, gPointerClass, "pointerToAddress", "(JLjava/lang/Class;)" POINTER_SIG);
		gThrowNewLastErrorMethod = (*env)->GetStaticMethodID(env, gLastErrorClass, "throwNewInstance", "(I" STRING_SIG ")V");
		gGetCallIOsMethod = (*env)->GetMethodID(env, gMethodCallInfoClass, "getCallIOs", "()[Lorg/bridj/CallIO;");
		gNewCallIOInstance = (*env)->GetMethodID(env, gCallIOClass, "newInstance", "(J)" OBJECT_SIG);
		gGetCallIOStructMethod = (*env)->GetMethodID(env, gCallIOClass, "getDCStruct", "()J");
		gCallIOGetPeerMethod = (*env)->GetMethodID(env, gCallIOClass, "getPeer", "(" OBJECT_SIG ")J");
		
		gLogCallMethod = (*env)->GetStaticMethodID(env, gBridJClass, "logCall", "(" METHOD_SIG ")V");
		gLogCallsField = (*env)->GetStaticFieldID(env, gBridJClass, "logCalls", "Z");
		gProtectedModeField = (*env)->GetStaticFieldID(env, gBridJClass, "protectedMode", "Z");
		
#ifdef __GNUC__
		gSignalErrorClass = FIND_GLOBAL_CLASS("org/bridj/SignalError");
		gSignalErrorThrowMethod = (*env)->GetStaticMethodID(env, gSignalErrorClass, "throwNew", "(IIJ)V");
#else
		gWindowsErrorClass = FIND_GLOBAL_CLASS("org/bridj/WindowsError");
		gWindowsErrorThrowMethod = (*env)->GetStaticMethodID(env, gWindowsErrorClass, "throwNew", "(IJJ)V");
#endif

#define GETFIELD_ID(out, name, sig) \
		if (!(gFieldId_ ## out = (*env)->GetFieldID(env, gMethodCallInfoClass, name, sig))) \
			throwException(env, "Failed to get the field " #name " in MethodCallInfo !");
		
	
		GETFIELD_ID(javaSignature 		,	"javaSignature"		,	STRING_SIG						);
		GETFIELD_ID(dcSignature 			,	"dcSignature" 		,	STRING_SIG						);
		GETFIELD_ID(symbolName 			,	"symbolName" 		,	STRING_SIG						);
		GETFIELD_ID(nativeClass 			,	"nativeClass" 		,	"J"								);
		GETFIELD_ID(methodName 			,	"methodName" 		,	STRING_SIG						);
		GETFIELD_ID(method     			,	"method"     		,	METHOD_SIG						);
		GETFIELD_ID(declaringClass		,	"declaringClass" 	,	CLASS_SIG						);
		GETFIELD_ID(paramsValueTypes 		,	"paramsValueTypes"	,	"[I"							);
		GETFIELD_ID(returnValueType 		,	"returnValueType" 	,	"I"								);
		GETFIELD_ID(forwardedPointer 		,	"forwardedPointer" 	,	"J"							);
		GETFIELD_ID(virtualIndex 		,	"virtualIndex"		,	"I"								);
		GETFIELD_ID(virtualTableOffset	,	"virtualTableOffset"	,	"I"								);
		//GETFIELD_ID(javaCallback 		,	"javaCallback" 		,	"Lorg/bridj/Callback;"			);
		GETFIELD_ID(javaCallback 		,	"javaCallback" 		,	OBJECT_SIG						);
		GETFIELD_ID(isGenericCallback 	,	"isGenericCallback"	,	"Z"								);
		GETFIELD_ID(isObjCBlock 			,	"isObjCBlock"		,	"Z"								);
		GETFIELD_ID(direct		 		,	"direct"	 			,	"Z"								);
		GETFIELD_ID(isCPlusPlus	 		,	"isCPlusPlus"		,	"Z"								);  		
		GETFIELD_ID(isStatic		 		,	"isStatic"			,	"Z"								);
		GETFIELD_ID(startsWithThis		,	"startsWithThis"		,	"Z"								);
		GETFIELD_ID(bNeedsThisPointer	,	"bNeedsThisPointer"		,	"Z"							);
		GETFIELD_ID(bThrowLastError	,	"bThrowLastError"		,	"Z"								);
		GETFIELD_ID(dcCallingConvention,	"dcCallingConvention"	,	"I"								);
		
		gLog = (*env)->GetStaticBooleanField(env, gBridJClass, gLogCallsField);
		gProtected = (*env)->GetStaticBooleanField(env, gBridJClass, gProtectedModeField);
		
		initPlatformMethods(env);
	}
}

jlong getFlagValue(JNIEnv *env, jobject valuedEnum)
{
	initMethods(env);
	return valuedEnum ? (*env)->CallLongMethod(env, valuedEnum, gGetValuedEnumValueMethod) : 0;	
}

/*
jobject newFlagSet(JNIEnv *env, jlong value, jobject enumClass)
{
	env = GetEnv();
	//initMethods(env);
	return (*env)->CallStaticObjectMethod(env, gFlagSetClass, gNewFlagSetMethod, value, enumClass);	
}
*/

//void main() {}
jmethodID GetMethodIDOrFail(JNIEnv* env, jclass declaringClass, const char* methName, const char* javaSig)
{
	jmethodID id = (*env)->GetStaticMethodID(env, declaringClass, methName, javaSig);
	if (!id) {
		(*env)->ExceptionClear(env);
		id = (*env)->GetMethodID(env, declaringClass, methName, javaSig);
	}
	if (!id)
		throwException(env, "Couldn't find this method !");
	
	return id;
}


jobject createPointerFromIO(JNIEnv *env, void* ptr, jobject callIO) {
	jobject instance;
	jlong addr;
	if (!callIO)
		return NULL;
	initMethods(env);
	addr = PTR_TO_JLONG(ptr);
	instance = (*env)->CallObjectMethod(env, callIO, gNewCallIOInstance, addr);
	return instance;
}
DCstruct* getStructFromIO(JNIEnv *env, jobject callIO) {
	jlong peer = (*env)->CallLongMethod(env, callIO, gGetCallIOStructMethod);
	return (DCstruct*)JLONG_TO_PTR(peer);
}
DCstruct* getNativeObjectPointerWithIO(JNIEnv *env, jobject instance, jobject callIO) {
	jlong peer = (*env)->CallLongMethod(env, callIO, gCallIOGetPeerMethod, instance);
	return (DCstruct*)JLONG_TO_PTR(peer);
}

void* getPointerPeer(JNIEnv *env, jobject pointer) {
	initMethods(env);
	return pointer ? JLONG_TO_PTR((*env)->CallLongMethod(env, pointer, gGetPeerMethod)) : NULL;
}

void* getNativeObjectPointer(JNIEnv *env, jobject instance, jclass targetClass) {
	initMethods(env);
	return JLONG_TO_PTR((*env)->CallStaticLongMethod(env, gPointerClass, gAddressMethod, instance, targetClass));
}


jobject getJavaObjectForNativePointer(JNIEnv *env, void* nativeObject) {
	initMethods(env);
	return (*env)->CallStaticObjectMethod(env, gBridJClass, gGetJavaObjectFromNativePeerMethod, PTR_TO_JLONG(nativeObject));
}

JNIEXPORT void JNICALL Java_org_bridj_Platform_init(JNIEnv *env, jclass clazz)
{
	initThreadLocal(env);
	//initMethods(env);
}

jlong JNICALL Java_org_bridj_JNI_getEnv(JNIEnv *env, jclass clazz)
{
	return PTR_TO_JLONG(env);
}

jlong JNICALL Java_org_bridj_JNI_getJVM(JNIEnv *env, jclass clazz)
{
	return PTR_TO_JLONG(gJVM);
}

jobject JNICALL Java_org_bridj_JNI_refToObject(JNIEnv *env, jclass clazz, jlong refPeer)
{
	return JLONG_TO_PTR(refPeer);
}

void logCall(JNIEnv *env, jobject method) {
	initMethods(env);
	(*env)->CallStaticObjectMethod(env, gBridJClass, gLogCallMethod, method);
}

jlong JNICALL Java_org_bridj_JNI_newGlobalRef(JNIEnv *env, jclass clazz, jobject obj)
{
	return obj ? PTR_TO_JLONG(GLOBAL_REF(obj)) : 0;
}

void JNICALL Java_org_bridj_JNI_deleteGlobalRef(JNIEnv *env, jclass clazz, jlong ref)
{
	if (ref)
		DEL_GLOBAL_REF((jobject)JLONG_TO_PTR(ref));
}
jlong JNICALL Java_org_bridj_JNI_newWeakGlobalRef(JNIEnv *env, jclass clazz, jobject obj)
{
	return obj ? PTR_TO_JLONG(WEAK_GLOBAL_REF(obj)) : 0;
}

void JNICALL Java_org_bridj_JNI_deleteWeakGlobalRef(JNIEnv *env, jclass clazz, jlong ref)
{
	if (ref)
		DEL_WEAK_GLOBAL_REF((jobject)JLONG_TO_PTR(ref));
}
void JNICALL Java_org_bridj_JNI_callSinglePointerArgVoidFunction(JNIEnv *env, jclass clazz, jlong constructor, jlong thisPtr, jint callMode)
{
	callSinglePointerArgVoidFunction(env, JLONG_TO_PTR(constructor), JLONG_TO_PTR(thisPtr), callMode);
}

jlong JNICALL Java_org_bridj_JNI_getDirectBufferAddress(JNIEnv *env, jobject jthis, jobject buffer) {
	jlong ret;
	ret = !buffer ? 0 : PTR_TO_JLONG((*env)->GetDirectBufferAddress(env, buffer));
	return ret;
}
jlong JNICALL Java_org_bridj_JNI_getDirectBufferCapacity(JNIEnv *env, jobject jthis, jobject buffer) {
	jlong ret;
	ret = !buffer ? 0 : (*env)->GetDirectBufferCapacity(env, buffer);
	return ret;
}

jlong JNICALL Java_org_bridj_JNI_getObjectPointer(JNIEnv *env, jclass clazz, jobject object)
{
	return PTR_TO_JLONG(object);
}
 
#if defined(DC_UNIX)
char* dlerror();
#else
jstring formatWin32ErrorMessage(JNIEnv* env, int errorCode);
#endif

#if defined(DC_WINDOWS)
wchar_t* ConvertStringToWide(JNIEnv* env, jstring javaString) {
	const char* utfStr = GET_CHARS(javaString);
	int len = (*env)->GetStringLength(env, javaString);
	wchar_t* wideStr = (wchar_t*)malloc((len + 1) * sizeof(wchar_t));
	wideStr[len] = L'\0';
	MultiByteToWideChar(CP_UTF8, 0, utfStr, -1, wideStr, len);	
	RELEASE_CHARS(javaString, utfStr);
	return wideStr;
}
#endif

jlong JNICALL Java_org_bridj_JNI_loadLibrary(JNIEnv *env, jclass clazz, jstring pathStr)
{
	jlong ret = 0;
#if defined(DC_WINDOWS)
	wchar_t* widePath = ConvertStringToWide(env, pathStr);
	const char* path = (char*)(void*)widePath;
	//int pathStrLength = (*env)->GetStringLength(env, pathStr);
	//wchar_t* widePath = (wchar_t*)malloc((pathStrLength + 1) * sizeof(wchar_t));
	//widePath[pathStrLength] = L'\0';
	//MultiByteToWideChar(CP_UTF8, 0, rawPath, -1, widePath, pathStrLength);
	//ret = PTR_TO_JLONG((DLLib*) LoadLibraryW(widePath));
#else
	const char* rawPath = GET_CHARS(pathStr);
	const char* path = rawPath;
	//ret = PTR_TO_JLONG(dlLoadLibrary(path));
#endif
	ret = PTR_TO_JLONG(dlLoadLibrary(path));
	if (!ret) {
#if defined(DC_UNIX)
		printf("# BridJ: dlopen error when loading %s : %s\n", path, dlerror());
#elif defined(DC_WINDOWS)
		jstring message = formatWin32ErrorMessage(env, GetLastError());
		wchar_t* msg = ConvertStringToWide(env, message);
		//const char* msg = GET_CHARS(message);
		wprintf(L"# BridJ: LoadLibrary error when loading %s : %s\n", widePath, msg);
		//RELEASE_CHARS(message, msg);
#endif
	}
#if defined(DC_WINDOWS)
	free(widePath);
#else
	RELEASE_CHARS(pathStr, rawPath);
#endif
	return ret;
}

void JNICALL Java_org_bridj_JNI_freeLibrary(JNIEnv *env, jclass clazz, jlong libHandle)
{
	dlFreeLibrary((DLLib*)JLONG_TO_PTR(libHandle));
}

jlong JNICALL Java_org_bridj_JNI_loadLibrarySymbols(JNIEnv *env, jclass clazz, jstring libPath)
{
	DLSyms* pSyms = NULL;
	const char* libPathStr;
	wchar_t* widePath;

	// Force protection (override global protection switch)
	jboolean gProtected = JNI_TRUE;
	BEGIN_TRY_CALL(env);
	
#if defined(DC_WINDOWS)
	widePath = ConvertStringToWide(env, libPath);
	libPathStr = (char*)(void*)widePath;
	pSyms = dlSymsInit(libPathStr);
	free(widePath);
#else
	libPathStr = GET_CHARS(libPath);
	pSyms = dlSymsInit(libPathStr);
	RELEASE_CHARS(libPath, libPathStr);
#endif
	END_TRY_CALL(env);	
	return PTR_TO_JLONG(pSyms);
	
}
void JNICALL Java_org_bridj_JNI_freeLibrarySymbols(JNIEnv *env, jclass clazz, jlong symbolsHandle)
{
	DLSyms* pSyms = (DLSyms*)JLONG_TO_PTR(symbolsHandle);
	dlSymsCleanup(pSyms);
}

jarray JNICALL Java_org_bridj_JNI_getLibrarySymbols(JNIEnv *env, jclass clazz, jlong libHandle, jlong symbolsHandle)
{
    jclass stringClass;
    jarray ret;
    DLSyms* pSyms = (DLSyms*)JLONG_TO_PTR(symbolsHandle);
	int count, i;
	if (!pSyms)
		return NULL;

	count = dlSymsCount(pSyms);
	stringClass = (*env)->FindClass(env, "java/lang/String");
	ret = (*env)->NewObjectArray(env, count, stringClass, 0);
    for (i = 0; i < count; i++) {
		const char* name = dlSymsName(pSyms, i);
		if (!name)
			continue;
		(*env)->SetObjectArrayElement(env, ret, i, (*env)->NewStringUTF(env, name));
    }
    return ret;
}


jstring JNICALL Java_org_bridj_JNI_findSymbolName(JNIEnv *env, jclass clazz, jlong libHandle, jlong symbolsHandle, jlong address)
{
	const char* name = dlSymsNameFromValue((DLSyms*)JLONG_TO_PTR(symbolsHandle), JLONG_TO_PTR(address));
	return name ? (*env)->NewStringUTF(env, name) : NULL;
}

jlong JNICALL Java_org_bridj_JNI_findSymbolInLibrary(JNIEnv *env, jclass clazz, jlong libHandle, jstring nameStr)
{
	const char* name;
	void* ptr;
	if (!nameStr)
		return 0;
	
	name = GET_CHARS(nameStr);
	
	ptr = dlFindSymbol((DLLib*)JLONG_TO_PTR(libHandle), name);
	RELEASE_CHARS(nameStr, name);
	return PTR_TO_JLONG(ptr);
}

jobject JNICALL Java_org_bridj_JNI_newDirectByteBuffer(JNIEnv *env, jobject jthis, jlong peer, jlong length) {
	jobject ret;
	ret = (*env)->NewDirectByteBuffer(env, JLONG_TO_PTR(peer), length);
	return ret;
}

JNIEXPORT jlong JNICALL Java_org_bridj_JNI_createCallTempStruct(JNIEnv* env, jclass clazz) {
	CallTempStruct* s = MALLOC_STRUCT(CallTempStruct);
	s->vm = dcNewCallVM(1024);
	return PTR_TO_JLONG(s);	
}
JNIEXPORT void JNICALL Java_org_bridj_JNI_deleteCallTempStruct(JNIEnv* env, jclass clazz, jlong handle) {
	CallTempStruct* s = (CallTempStruct*)JLONG_TO_PTR(handle);
	dcFree(s->vm);
	free(s);	
}

char getDCReturnType(JNIEnv* env, ValueType returnType) 
{
	switch (returnType) {
#define RET_TYPE_CASE(valueType, hiCase) \
		case valueType: \
			return DC_SIGCHAR_ ## hiCase;
		case eIntFlagSet:
		RET_TYPE_CASE(eIntValue, INT)
		RET_TYPE_CASE(eLongValue, LONGLONG)
		RET_TYPE_CASE(eShortValue, SHORT)
		RET_TYPE_CASE(eFloatValue, FLOAT)
		RET_TYPE_CASE(eDoubleValue, DOUBLE)
		case eBooleanValue:
		RET_TYPE_CASE(eByteValue, CHAR)
		case eCLongObjectValue:
		case eSizeTObjectValue:
		case eTimeTObjectValue:
			return DC_SIGCHAR_POINTER;
		case eCLongValue:
			return DC_SIGCHAR_LONGLONG;
		case eSizeTValue:
			return DC_SIGCHAR_LONGLONG;
		case eVoidValue:
			return DC_SIGCHAR_VOID;
		case ePointerValue:
			return DC_SIGCHAR_POINTER;
		case eWCharValue:
			switch (sizeof(wchar_t)) {
			case 1:
				return DC_SIGCHAR_CHAR;
			case 2:
				return DC_SIGCHAR_SHORT;
			case 4:
				return DC_SIGCHAR_INT;
			default:
				throwException(env, "wchar_t size not supported yet !");
				return DC_SIGCHAR_VOID;
			}
			// TODO
		case eNativeObjectValue:
			return DC_SIGCHAR_POINTER;
		default:
			throwException(env, "Return ValueType not supported yet !");
			return DC_SIGCHAR_VOID;
	}
}


void registerJavaFunction(JNIEnv* env, jclass declaringClass, const char* methName, const char* methSig, void (*callback)())
{
	JNINativeMethod meth;
	if (!callback) {
			throwException(env, "No callback !");
			return;
		}
	if (!methName) {
			throwException(env, "No methodName !");
			return;
		}
	if (!methSig) {
			throwException(env, "No methodSignature !");
			return;
		}
	if (!declaringClass) {
			throwException(env, "No declaringClass !");
			return;
		}

	meth.fnPtr = callback;
	meth.name = (char*)methName;
	meth.signature = (char*)methSig;
	(*env)->RegisterNatives(env, declaringClass, &meth, 1);
	
}

void initCommonCallInfo(
	struct CommonCallbackInfo* info,
	JNIEnv *env,
	jclass declaringClass, 
	jstring methodName, 
	jstring javaSignature,
	jint callMode,
	jint nParams,
	jint returnValueType, 
	jintArray paramsValueTypes,
	jobjectArray callIOs,
	jboolean registerJava,
	jobject method
) {
	const char* javaSig, *methName;
	javaSig = (char*)GET_CHARS(javaSignature);
	methName = (char*)GET_CHARS(methodName);
			
	info->fEnv = env;
	info->fDCMode = callMode;
	info->fReturnType = (ValueType)returnValueType;
	info->nParams = nParams;
	if (nParams) {
		info->fParamTypes = (ValueType*)malloc(nParams * sizeof(jint));	
		(*env)->GetIntArrayRegion(env, paramsValueTypes, 0, nParams, (jint*)info->fParamTypes);
	} else {
		info->fParamTypes = NULL;
	}
	info->fDCReturnType = getDCReturnType(env, info->fReturnType);
	
	if (callIOs) 
	{
		jsize n = (*env)->GetArrayLength(env, callIOs), i;
		if (n)
		{
			info->fCallIOs = (jobject*)malloc((n + 1) * sizeof(jobject));
			for (i = 0; i < n; i++) {
				jobject obj = (*env)->GetObjectArrayElement(env, callIOs, i);
				if (obj)
					obj = GLOBAL_REF(obj);
				info->fCallIOs[i] = obj;
			}
			info->fCallIOs[n] = NULL;
		}
	} else {
		info->fCallIOs = NULL;
	}
	
	if (registerJava)
		registerJavaFunction(env, declaringClass, methName, javaSig, info->fDCCallback);
		
	info->fMethodID = GetMethodIDOrFail(env, declaringClass, methName, javaSig);
	info->fMethod = GLOBAL_REF(method);
	
	
	RELEASE_CHARS(javaSignature, javaSig);
	RELEASE_CHARS(methodName, methName);
}

void* getJNICallFunction(JNIEnv* env, ValueType valueType) {
	switch (valueType) {
	case eIntValue:
		return (*env)->CallIntMethod;
	case eTimeTObjectValue:
	case eSizeTObjectValue:
	case eCLongObjectValue:
		return (*env)->CallObjectMethod;
	case eSizeTValue:
	case eCLongValue:
	case eLongValue:
		return (*env)->CallLongMethod;
	case eFloatValue:
		return (*env)->CallFloatMethod;
	case eDoubleValue:
		return (*env)->CallDoubleMethod;
	case eBooleanValue:
		return (*env)->CallBooleanMethod;
	case eByteValue:
		return (*env)->CallByteMethod;
	case eShortValue:
		return (*env)->CallShortMethod;
	case eWCharValue:
		return (*env)->CallCharMethod;
	case eVoidValue:
		return (*env)->CallVoidMethod;
	case eNativeObjectValue:
	case ePointerValue:
		return (*env)->CallObjectMethod;
	default:
		throwException(env, "Unhandled type in getJNICallFunction !");
		return NULL;
	}
}


void* getJNICallStaticFunction(JNIEnv* env, ValueType valueType) {
	switch (valueType) {
	case eIntValue:
		return (*env)->CallStaticIntMethod;
	case eTimeTObjectValue:
	case eSizeTObjectValue:
	case eCLongObjectValue:
		return (*env)->CallStaticObjectMethod;
	case eSizeTValue:
	case eCLongValue:
	case eLongValue:
		return (*env)->CallStaticLongMethod;
	case eFloatValue:
		return (*env)->CallStaticFloatMethod;
	case eDoubleValue:
		return (*env)->CallStaticDoubleMethod;
	case eBooleanValue:
		return (*env)->CallStaticBooleanMethod;
	case eByteValue:
		return (*env)->CallStaticByteMethod;
	case eShortValue:
		return (*env)->CallStaticShortMethod;
	case eWCharValue:
		return (*env)->CallStaticCharMethod;
	case eVoidValue:
		return (*env)->CallStaticVoidMethod;
	case eNativeObjectValue:
	case ePointerValue:
		return (*env)->CallStaticObjectMethod;
	default:
		throwException(env, "Unhandled type in getJNICallStaticFunction !");
		return NULL;
	}
}

#define NEW_STRUCTS(n, type, name) \
	struct type *name = NULL; \
	size_t sizeof ## name = n * sizeof(struct type); \
	name = (struct type*)malloc(sizeof ## name); \
	memset(name, 0, sizeof ## name);

	
void freeCommon(JNIEnv* env, CommonCallbackInfo* info)
{
	if (info->nParams && info->fParamTypes) {
		free(info->fParamTypes);
	}
	
	if (info->fCallIOs)
	{
		jobject* ptr = info->fCallIOs;
		while (*ptr) {
			DEL_GLOBAL_REF(*ptr);
			ptr++;
		}
		free(info->fCallIOs);
	}
	
	DEL_GLOBAL_REF(info->fMethod);
	
	if (info->fDCCallback) {
		dcbFreeCallback((DCCallback*)info->fDCCallback);
	}
}
	      
#define GetField_javaSignature()         jstring          javaSignature        = (*env)->GetObjectField(   env, methodCallInfo, gFieldId_javaSignature       )
#define GetField_dcSignature()           jstring          dcSignature          = (*env)->GetObjectField(   env, methodCallInfo, gFieldId_dcSignature         )
#define GetField_symbolName()            jstring          symbolName           = (*env)->GetObjectField(   env, methodCallInfo, gFieldId_symbolName          )
#define GetField_nativeClass()           jlong            nativeClass          = (*env)->GetLongField(     env, methodCallInfo, gFieldId_nativeClass         )
#define GetField_methodName()            jstring          methodName           = (*env)->GetObjectField(   env, methodCallInfo, gFieldId_methodName          )
#define GetField_method()                jobject          method               = (*env)->GetObjectField(   env, methodCallInfo, gFieldId_method              )
#define GetField_paramsValueTypes()      jintArray        paramsValueTypes     = (*env)->GetObjectField(   env, methodCallInfo, gFieldId_paramsValueTypes    )
#define GetField_javaCallback()          jobject          javaCallback         = (*env)->GetObjectField(   env, methodCallInfo, gFieldId_javaCallback        )
#define GetField_isGenericCallback()     jboolean         isGenericCallback    = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_isGenericCallback   )
#define GetField_isObjCBlock()           jboolean         isObjCBlock          = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_isObjCBlock         )
#define GetField_forwardedPointer()      jlong            forwardedPointer     = (*env)->GetLongField(     env, methodCallInfo, gFieldId_forwardedPointer    )
#define GetField_returnValueType()       jint             returnValueType      = (*env)->GetIntField(      env, methodCallInfo, gFieldId_returnValueType     )
#define GetField_virtualIndex()          jint             virtualIndex         = (*env)->GetIntField(      env, methodCallInfo, gFieldId_virtualIndex        )
#define GetField_virtualTableOffset()    jint             virtualTableOffset   = (*env)->GetIntField(      env, methodCallInfo, gFieldId_virtualTableOffset  )
#define GetField_dcCallingConvention()   jint             dcCallingConvention  = (*env)->GetIntField(      env, methodCallInfo, gFieldId_dcCallingConvention )
#define GetField_direct()                jboolean         direct               = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_direct              )
#define GetField_isCPlusPlus()           jboolean         isCPlusPlus          = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_isCPlusPlus         )
#define GetField_isStatic()              jboolean         isStatic             = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_isStatic            )
#define GetField_startsWithThis()        jboolean         startsWithThis       = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_startsWithThis      )
#define GetField_bNeedsThisPointer()     jboolean         bNeedsThisPointer    = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_bNeedsThisPointer   )
#define GetField_bThrowLastError()       jboolean         bThrowLastError      = (*env)->GetBooleanField(  env, methodCallInfo, gFieldId_bThrowLastError   )
#define GetField_declaringClass()        jstring          declaringClass       = (jclass)(*env)->GetObjectField(env, methodCallInfo, gFieldId_declaringClass )
#define GetField_nParams()               jsize            nParams              = (*env)->GetArrayLength(   env, paramsValueTypes                             )
#define GetField_callIOs()               jobjectArray     callIOs              = (*env)->CallObjectMethod( env, methodCallInfo, gGetCallIOsMethod            )


#define BEGIN_INFOS_LOOP(type)                                                                                           \
	jsize i, n = (*env)->GetArrayLength(env, methodCallInfos);															 \
	NEW_STRUCTS(n, type, infos);																						 \
	initMethods(env);                                                                                        	 		 \
	for (i = 0; i < n; i++)                                                                                          	 \
	{                  																								 	 \
		type* info = &infos[i];																						 	 \
		jobject methodCallInfo = (*env)->GetObjectArrayElement(env, methodCallInfos, i);
		
#define END_INFOS_LOOP() }

JNIEXPORT jlong JNICALL Java_org_bridj_JNI_createCToJavaCallback(
	JNIEnv *env, 
	jclass clazz,
	jobject methodCallInfo
) {
	initMethods(env);
	{
	struct NativeToJavaCallbackCallInfo* info = NULL;
	{
		const char* dcSig;
		
		GetField_javaSignature()        ;
		GetField_dcSignature()          ;
		GetField_symbolName()           ;
		GetField_nativeClass()          ;
		GetField_methodName()           ;
		GetField_method()               ;
		GetField_paramsValueTypes()     ;
		GetField_javaCallback()         ;
		GetField_isGenericCallback()    ;
		GetField_isObjCBlock()          ;
		//GetField_forwardedPointer()     ;
		GetField_returnValueType()      ;
		//GetField_virtualIndex()         ;
		//GetField_virtualTableOffset()   ;
		GetField_dcCallingConvention()  ;
		//GetField_direct()               ;
		//GetField_startsWithThis()       ;
		//GetField_bNeedsThisPointer()    ;
		GetField_isCPlusPlus()          ;
		GetField_declaringClass()       ;
		GetField_nParams()              ;
		GetField_callIOs()              ;
		
		{
			info = MALLOC_STRUCT(NativeToJavaCallbackCallInfo);
			memset(info, 0, sizeof(struct NativeToJavaCallbackCallInfo));
			
			// TODO DIRECT C++ virtual thunk
			dcSig = GET_CHARS(dcSignature);
			
			info->fInfo.fDCCallback = dcbNewCallback(dcSig, isCPlusPlus ? CPPToJavaCallHandler : CToJavaCallHandler, info);
			info->fCallbackInstance = WEAK_GLOBAL_REF(javaCallback);
			info->fIsGenericCallback = isGenericCallback;
			info->fIsObjCBlock = isObjCBlock;
			
			info->fJNICallFunction = getJNICallFunction(env, (ValueType)returnValueType);

			RELEASE_CHARS(dcSignature, dcSig);
			
			initCommonCallInfo(&info->fInfo, env, declaringClass, methodName, javaSignature, dcCallingConvention, nParams, returnValueType, paramsValueTypes, callIOs, JNI_FALSE, method);
		}
	}
	return PTR_TO_JLONG(info);
	}
}
JNIEXPORT jlong JNICALL Java_org_bridj_JNI_getActualCToJavaCallback(
	JNIEnv *env, 
	jclass clazz,
	jlong handle
) {
	struct NativeToJavaCallbackCallInfo* info = (struct NativeToJavaCallbackCallInfo*)JLONG_TO_PTR(handle);
	return PTR_TO_JLONG(info->fInfo.fDCCallback);
}
JNIEXPORT void JNICALL Java_org_bridj_JNI_freeCToJavaCallback(
	JNIEnv *env, 
	jclass clazz,
	jlong handle
) {
	struct NativeToJavaCallbackCallInfo* info = (struct NativeToJavaCallbackCallInfo*)JLONG_TO_PTR(handle);
	DEL_WEAK_GLOBAL_REF(info->fCallbackInstance);
	freeCommon(env, &info->fInfo);
	free(info);
}


JNIEXPORT jlong JNICALL Java_org_bridj_JNI_bindJavaToCCallbacks(
	JNIEnv *env, 
	jclass clazz,
	jobjectArray methodCallInfos
) {
	initMethods(env);
	{
	BEGIN_INFOS_LOOP(JavaToNativeCallbackCallInfo)
	
	GetField_javaSignature()        ;
	GetField_dcSignature()          ;
	//GetField_symbolName()           ;
	//GetField_nativeClass()          ;
	GetField_methodName()           ;
	GetField_method()               ;
	GetField_paramsValueTypes()     ;
	//GetField_javaCallback()         ;
	//GetField_forwardedPointer()     ;
	GetField_returnValueType()      ;
	//GetField_virtualIndex()         ;
	//GetField_virtualTableOffset()   ;
	GetField_dcCallingConvention()  ;
	//GetField_direct()               ;
	//GetField_startsWithThis()       ;
	//GetField_bNeedsThisPointer()    ;
	GetField_declaringClass()       ;
	GetField_nParams()              ;
	GetField_callIOs()              ;
	
	{
		//void* callback;
		const char* dcSig;
		
		// TODO DIRECT C++ virtual thunk
		dcSig = GET_CHARS(dcSignature);
		info->fInfo.fDCCallback = dcbNewCallback(dcSig, JavaToCCallHandler/* NativeToJavaCallHandler*/, info);
		RELEASE_CHARS(dcSignature, dcSig);
			
		initCommonCallInfo(&info->fInfo, env, declaringClass, methodName, javaSignature, dcCallingConvention, nParams, returnValueType, paramsValueTypes, callIOs, JNI_TRUE, method);
	}
	END_INFOS_LOOP()
	return PTR_TO_JLONG(infos);
	}
}
JNIEXPORT void JNICALL Java_org_bridj_JNI_freeJavaToCCallbacks(
	JNIEnv *env, 
	jclass clazz,
	jlong handle,
	jint size
) {
	JavaToNativeCallbackCallInfo* infos = (JavaToNativeCallbackCallInfo*)JLONG_TO_PTR(handle);
	jint i;
	if (!infos)
		return;
	for (i = 0; i < size; i++) {
		freeCommon(env, &infos[i].fInfo);
	}
	free(infos);
}

JNIEXPORT jlong JNICALL Java_org_bridj_JNI_bindJavaMethodsToCFunctions(
	JNIEnv *env, 
	jclass clazz,
	jobjectArray methodCallInfos
) {
	initMethods(env);
	{
	BEGIN_INFOS_LOOP(FunctionCallInfo)
	
	GetField_javaSignature()        ;
	GetField_dcSignature()          ;
	GetField_symbolName()           ;
	GetField_methodName()           ;
	GetField_method()               ;
	GetField_paramsValueTypes()     ;
	GetField_forwardedPointer()     ;
	GetField_returnValueType()      ;
	GetField_dcCallingConvention()  ;
	GetField_direct()               ;
	GetField_isCPlusPlus()          ;
	GetField_isStatic()             ;
	GetField_startsWithThis()       ;
	GetField_declaringClass()       ;
	GetField_bThrowLastError()      ;
	GetField_nParams()              ;
	GetField_callIOs()              ;
	
	{
		info->fForwardedSymbol = JLONG_TO_PTR(forwardedPointer);
		if (isCPlusPlus && !isStatic && declaringClass)
			info->fClass = GLOBAL_REF(declaringClass);
		
		info->fCheckLastError = bThrowLastError;
		
#ifndef NO_DIRECT_CALLS
		if (direct && !gProtected && forwardedPointer)
			info->fInfo.fDCCallback = (DCCallback*)dcRawCallAdapterSkipTwoArgs((void (*)())info->fForwardedSymbol, dcCallingConvention);
#endif
		if (!info->fInfo.fDCCallback) {
			const char* ds = GET_CHARS(dcSignature);
			//info->fInfo.fDCCallback = dcbNewCallback(ds, JavaToFunctionCallHandler, info);
			info->fInfo.fDCCallback = dcbNewCallback(ds, isCPlusPlus && !isStatic ? JavaToCPPMethodCallHandler : JavaToFunctionCallHandler, info);
			RELEASE_CHARS(dcSignature, ds);
		}
		initCommonCallInfo(&info->fInfo, env, declaringClass, methodName, javaSignature, dcCallingConvention, nParams, returnValueType, paramsValueTypes, callIOs, JNI_TRUE, method);
	}
	END_INFOS_LOOP()
	return PTR_TO_JLONG(infos);
	}
}
JNIEXPORT void JNICALL Java_org_bridj_JNI_freeCFunctionBindings(
	JNIEnv *env, 
	jclass clazz,
	jlong handle,
	jint size
) {
	FunctionCallInfo* infos = (FunctionCallInfo*)JLONG_TO_PTR(handle);
	jint i;
	if (!infos)
		return;
	for (i = 0; i < size; i++) {
		if (infos[i].fClass)
			DEL_GLOBAL_REF(infos[i].fClass);
		freeCommon(env, &infos[i].fInfo);
	}
	free(infos);
}
JNIEXPORT jlong JNICALL Java_org_bridj_JNI_bindJavaMethodsToObjCMethods(
	JNIEnv *env, 
	jclass clazz,
	jobjectArray methodCallInfos
) {
#ifdef BRIDJ_OBJC_SUPPORT
	initMethods(env);
	{
	BEGIN_INFOS_LOOP(JavaToObjCCallInfo)
	
	GetField_javaSignature()        ;
	GetField_dcSignature()          ;
	GetField_symbolName()           ;
	GetField_nativeClass()          ;
	GetField_methodName()           ;
	GetField_method()               ;
	GetField_paramsValueTypes()     ;
	//GetField_javaCallback()         ;
	//GetField_forwardedPointer()     ;
	GetField_returnValueType()      ;
	//GetField_virtualIndex()         ;
	//GetField_virtualTableOffset()   ;
	GetField_dcCallingConvention()  ;
	//GetField_direct()               ;
	//GetField_startsWithThis()       ;
	//GetField_bNeedsThisPointer()    ;
	GetField_declaringClass()       ;
	GetField_nParams()              ;
	GetField_callIOs()              ;
	
	{
		const char* ds, *methName;
	
		// TODO DIRECT ObjC thunk
		methName = (char*)GET_CHARS(symbolName);
		//ds = GET_CHARS(dcSignature);
		
		info->fInfo.fDCCallback = dcbNewCallback(ds, JavaToObjCCallHandler, info);
		info->fSelector = sel_registerName(methName);
		info->fNativeClass = nativeClass;
		
		//RELEASE_CHARS(dcSignature, ds);
		RELEASE_CHARS(symbolName, methName);
		
		
		initCommonCallInfo(&info->fInfo, env, declaringClass, methodName, javaSignature, dcCallingConvention, nParams, returnValueType, paramsValueTypes, callIOs, JNI_TRUE, method);
	}
	END_INFOS_LOOP()
	return PTR_TO_JLONG(infos);
	}
#else
	return 0;
#endif
}

JNIEXPORT void JNICALL Java_org_bridj_JNI_freeObjCMethodBindings(
	JNIEnv *env, 
	jclass clazz,
	jlong handle,
	jint size
) {
#ifdef BRIDJ_OBJC_SUPPORT
	JavaToObjCCallInfo* infos = (JavaToObjCCallInfo*)JLONG_TO_PTR(handle);
	jint i;
	if (!infos)
		return;
	for (i = 0; i < size; i++) {
		freeCommon(env, &infos[i].fInfo);
	}
	free(infos);
#endif
}


JNIEXPORT jlong JNICALL Java_org_bridj_JNI_bindJavaMethodsToVirtualMethods(
	JNIEnv *env, 
	jclass clazz,
	jobjectArray methodCallInfos
) {
	initMethods(env);
	{
	BEGIN_INFOS_LOOP(VirtualMethodCallInfo)
	
	GetField_javaSignature()        ;
	GetField_dcSignature()          ;
	GetField_symbolName()           ;
	GetField_methodName()           ;
	GetField_method()               ;
	GetField_paramsValueTypes()     ;
	GetField_returnValueType()      ;
	GetField_virtualIndex()         ;
	GetField_virtualTableOffset()   ;
	GetField_dcCallingConvention()  ;
	GetField_startsWithThis()       ;
	//GetField_bNeedsThisPointer()    ;
	GetField_declaringClass()       ;
	GetField_nParams()              ;
	GetField_callIOs()              ;
	
	{
		const char* ds;
	
		info->fClass = GLOBAL_REF(declaringClass);
		info->fHasThisPtrArg = startsWithThis;
		info->fVirtualIndex = virtualIndex;
		info->fVirtualTableOffset = virtualTableOffset;
		
		// TODO DIRECT C++ virtual thunk
		ds = GET_CHARS(dcSignature);
		info->fInfo.fDCCallback = dcbNewCallback(ds, JavaToVirtualMethodCallHandler, info);
		RELEASE_CHARS(dcSignature, ds);
		
		
		initCommonCallInfo(&info->fInfo, env, declaringClass, methodName, javaSignature, dcCallingConvention, nParams, returnValueType, paramsValueTypes, callIOs, JNI_TRUE, method);
	}
	END_INFOS_LOOP()
	return PTR_TO_JLONG(infos);
	}
}
JNIEXPORT void JNICALL Java_org_bridj_JNI_freeVirtualMethodBindings(
	JNIEnv *env, 
	jclass clazz,
	jlong handle,
	jint size
) {
	VirtualMethodCallInfo* infos = (VirtualMethodCallInfo*)JLONG_TO_PTR(handle);
	jint i;
	if (!infos)
		return;
	for (i = 0; i < size; i++) {
		DEL_GLOBAL_REF(infos[i].fClass);
		freeCommon(env, &infos[i].fInfo);
	}
	free(infos);
}

jlong JNICALL Java_org_bridj_JNI_mallocNulled(JNIEnv *env, jclass clazz, jlong size) 
{
	size_t len = (size_t)size;
	void* p = malloc(len);
	if (p)
		memset(p, 0, len);
	return PTR_TO_JLONG(p);
}
jlong JNICALL Java_org_bridj_JNI_mallocNulledAligned(JNIEnv *env, jclass clazz, jlong size, jint alignment) 
{
#if (_POSIX_C_SOURCE >= 200112L || _XOPEN_SOURCE >= 600)
	size_t len = (size_t)size;
	void* p;
	if (posix_memalign(&p, alignment, len))
		return 0;
	if (p)
		memset(p, 0, len);
	return PTR_TO_JLONG(p);
#else
	return 0;
#endif
}

jlong JNICALL Java_org_bridj_JNI_malloc(JNIEnv *env, jclass clazz, jlong size)
{
	jlong r = 0;
	BEGIN_TRY_CALL(env);
	r = PTR_TO_JLONG(malloc((size_t)size));
	END_TRY_CALL(env);
	return r;
}
jlong JNICALL Java_org_bridj_JNI_strlen(JNIEnv *env, jclass clazz, jlong ptr)
{
	jlong r = 0;
	BEGIN_TRY_CALL(env);
	r = strlen(JLONG_TO_PTR(ptr));
	END_TRY_CALL(env);
	return r;
}
jlong JNICALL Java_org_bridj_JNI_wcslen(JNIEnv *env, jclass clazz, jlong ptr)
{
	jlong r = 0;
	BEGIN_TRY_CALL(env);
	r = strlen(JLONG_TO_PTR(ptr));
	END_TRY_CALL(env);
	return r;
}
void JNICALL Java_org_bridj_JNI_free(JNIEnv *env, jclass clazz, jlong ptr)
{
	BEGIN_TRY_CALL(env);
	free(JLONG_TO_PTR(ptr));
	END_TRY_CALL(env);
}
void JNICALL Java_org_bridj_JNI_memcpy(JNIEnv *env, jclass clazz, jlong dest, jlong src, jlong size)
{
	BEGIN_TRY_CALL(env);
	memcpy(JLONG_TO_PTR(dest), JLONG_TO_PTR(src), (size_t)size);
	END_TRY_CALL(env);
}
void JNICALL Java_org_bridj_JNI_memmove(JNIEnv *env, jclass clazz, jlong dest, jlong src, jlong size)
{
	BEGIN_TRY_CALL(env);
	memmove(JLONG_TO_PTR(dest), JLONG_TO_PTR(src), (size_t)size);
	END_TRY_CALL(env);
}
jlong JNICALL Java_org_bridj_JNI_memchr(JNIEnv *env, jclass clazz, jlong ptr, jbyte value, jlong size)
{
	jlong r = 0;
	BEGIN_TRY_CALL(env);
	r = PTR_TO_JLONG(memchr(JLONG_TO_PTR(ptr), value, (size_t)size));
	END_TRY_CALL(env);
	return r;
}
jint JNICALL Java_org_bridj_JNI_memcmp(JNIEnv *env, jclass clazz, jlong ptr1, jlong ptr2, jlong size)
{
	jint r = 0;
	BEGIN_TRY_CALL(env);
	r = memcmp(JLONG_TO_PTR(ptr1), JLONG_TO_PTR(ptr2), (size_t)size);
	END_TRY_CALL(env);
	return r;
}
void JNICALL Java_org_bridj_JNI_memset(JNIEnv *env, jclass clazz, jlong ptr, jbyte value, jlong size)
{
	BEGIN_TRY_CALL(env);
	PTR_TO_JLONG(memset(JLONG_TO_PTR(ptr), value, (size_t)size));
	END_TRY_CALL(env);
}

jlong JNICALL Java_org_bridj_JNI_memmem(JNIEnv *env, jclass clazz, jlong haystack, jlong haystackLength, jlong needle, jlong needleLength) 
{
	const char* pHaystack = JLONG_TO_PTR(haystack);
	const char* pNeedle = JLONG_TO_PTR(needle);
	
	if (needleLength > haystackLength)
		return 0;
	if (!pHaystack || !pNeedle)
		return 0;
	
#ifndef memmem
	{
		jlong n = haystackLength - needleLength, i;
		char needleStart = *pNeedle;
		for (i = 0; i <= n; i++) {
			const char* position = pHaystack + i;
			if (*position == needleStart) {
				if (memcmp(position, pNeedle, (size_t)needleLength) == 0)
					return PTR_TO_JLONG(position);
			}
		}
		return 0;
	}
#else
	return memmem(pHaystack, (size_t)haystackLength, pNeedle, (size_t)needleLength);
#endif
}


jlong JNICALL Java_org_bridj_JNI_memmem_1last(JNIEnv *env, jclass clazz, jlong haystack, jlong haystackLength, jlong needle, jlong needleLength) 
{
	const char* pHaystack = JLONG_TO_PTR(haystack);
	const char* pNeedle = JLONG_TO_PTR(needle);
	
	if (needleLength > haystackLength)
		return 0;
	if (!pHaystack || !pNeedle)
		return 0;
	
	{
		jlong n = haystackLength - needleLength, i;
		char needleStart = *pNeedle;
		for (i = n; i >= n; i--) {
			const char* position = pHaystack + i;
			if (*position == needleStart) {
				if (memcmp(position, pNeedle, (size_t)needleLength) == 0)
					return PTR_TO_JLONG(position);
			}
		}
		return 0;
	}
}

#include "PrimDefs_int.h"
#include "JNI_prim.h"

#include "PrimDefs_long.h"
#include "JNI_prim.h"

#include "PrimDefs_short.h"
#include "JNI_prim.h"

#include "PrimDefs_byte.h"
#include "JNI_prim.h"

#include "PrimDefs_char.h"
#include "JNI_prim.h"

#include "PrimDefs_boolean.h"
#include "JNI_prim.h"

#include "PrimDefs_float.h"
#include "JNI_prim.h"

#include "PrimDefs_double.h"
#include "JNI_prim.h"
