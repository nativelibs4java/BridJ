#ifndef BRIDJ_H
#define BRIDJ_H

#ifndef BRIDJ_API
#define BRIDJ_API
#endif

#pragma warning(error: 4061)
#pragma warning(disable: 4127)
#pragma warning(disable: 4100) // unreferenced formal parameter
#pragma warning(disable: 4706) // assignment inside a conditional expression
#pragma warning(disable: 4054) // casting a function pointer to a data pointer
#pragma warning(disable: 4996)

#ifndef _WIN32
#define __cdecl
#endif

#ifdef _WIN32
#define SUPPORTS_UNALIGNED_ACCESS
#endif

#if defined(_WIN64) || (!defined (DC__OS_Darwin) && !defined(DC__OS_Linux) && !defined(_WIN32))
//#define NO_DIRECT_CALLS // TODO REMOVE ME !!! (issues with stack alignment on COM calls ?)
#endif

#include "dyncallback/dyncall_callback.h"
#include <jni.h>
#include <time.h>

#if defined(__GNUC__)
#include <setjmp.h>
#endif


#ifdef _MSC_VER
#	define THREAD_STATIC __declspec(thread) static
#else 
#	define THREAD_STATIC static 
//TODO http://www.opengroup.org/onlinepubs/009695399/functions/pthread_key_create.html
//static __thread
#endif

#define MAX(x, y) (x < y ? y : x)
#define PTR_TO_JLONG(ptr) ((jlong)(size_t)(ptr))
#define JLONG_TO_PTR(jl) ((void*)(size_t)(jl))
#define MALLOC_STRUCT(type) ((struct type*)malloc(sizeof(struct type)))
#define MALLOC_STRUCT_ARRAY(type, size) ((struct type*)malloc(sizeof(struct type) * size))

#define STRING_SIG "Ljava/lang/String;"
		
#define Modifier_ABSTRACT	1024
#define Modifier_FINAL	16
#define Modifier_INTERFACE	512
#define Modifier_NATIVE	256
#define Modifier_PRIVATE	2
#define Modifier_PROTECTED	4
#define Modifier_PUBLIC	1
#define Modifier_STATIC	8
#define Modifier_STRICT	2048
#define Modifier_SYNCHRONIZED	32
#define Modifier_TRANSIENT	128
#define Modifier_VOLATILE	64

extern jboolean gLog;

#include "Protected.h"

typedef enum ValueType {
	eVoidValue = 0,
	eWCharValue,
	eCLongValue,
	eCLongObjectValue,
	eSizeTValue,
	eSizeTObjectValue,
	eIntValue,
	eShortValue,
	eByteValue,
	eBooleanValue,
	eLongValue,
	eDoubleValue,
	eFloatValue,
	ePointerValue,
	eEllipsis,
	eIntFlagSet,
	eNativeObjectValue,
	eTimeTObjectValue
} ValueType;

typedef struct CallTempStruct {
	DCCallVM* vm;
	JNIEnv *env;
	jobject* pCallIOs;
#if defined(__GNUC__)
	jmp_buf exceptionContext;
	Signals signals;
	int signal, signalCode;
	jlong signalAddress;
#endif
} CallTempStruct;

typedef struct CommonCallbackInfo {
	int nParams;
	char fDCReturnType;
	enum ValueType fReturnType;
	enum ValueType* fParamTypes;
	int fDCMode;
	jobject* fCallIOs;
	void* fDCCallback;
	JNIEnv* fEnv;
	jmethodID fMethodID;
	jobject fMethod;
} CommonCallbackInfo;

typedef struct VirtualMethodCallInfo {
	struct CommonCallbackInfo fInfo;
	jclass fClass;
	jboolean fHasThisPtrArg;
	int fVirtualIndex;
	int fVirtualTableOffset;
} VirtualMethodCallInfo;

typedef struct FunctionCallInfo {
	struct CommonCallbackInfo fInfo;
	jclass fClass;
	void* fForwardedSymbol;
	jboolean fCheckLastError;
} FunctionCallInfo, CPPMethodCallInfo;

#ifdef BRIDJ_OBJC_SUPPORT
#include <objc/objc.h>

typedef struct JavaToObjCCallInfo {
	struct CommonCallbackInfo fInfo;
	SEL fSelector;
	jlong fNativeClass;
} JavaToObjCCallInfo;

char __cdecl JavaToObjCCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);
#endif

typedef struct StructFieldInfo {
	struct CommonCallbackInfo fInfo;
	void* fJNICallFunction;
	jint fFieldIndex;
} StructFieldInfo;


typedef struct NativeToJavaCallbackCallInfo {
	struct CommonCallbackInfo fInfo;
	void* fJNICallFunction;
	jobject fCallbackInstance;
	jboolean fIsGenericCallback;
	jboolean fIsObjCBlock;
} NativeToJavaCallbackCallInfo;

typedef struct JavaToNativeCallbackCallInfo {
	struct CommonCallbackInfo fInfo;
} JavaToNativeCallbackCallInfo;

char __cdecl JavaToFunctionCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);
char __cdecl JavaToCPPMethodCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);
char __cdecl JavaToVirtualMethodCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);
char __cdecl JavaToCCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);
char __cdecl CToJavaCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);
char __cdecl CPPToJavaCallHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);
char __cdecl StructHandler(DCCallback* callback, DCArgs* args, DCValue* result, void* userdata);

extern jclass gPointerClass;

#define BOX_METHOD_DECL(prim, shortName, methShort, type, letter) \
extern jclass g ## shortName ## Class; \
jobject Box ## shortName(JNIEnv* env, type v); \
type Unbox ## shortName(JNIEnv* env, jobject v);
			
BOX_METHOD_DECL("org/bridj/TimeT", TimeT, Long, time_t, "J");
BOX_METHOD_DECL("org/bridj/SizeT", SizeT, Long, jlong, "J");
BOX_METHOD_DECL("org/bridj/CLong", CLong, Long, long, "J");
BOX_METHOD_DECL("java/lang/Integer", Int, Int, jint, "I");
BOX_METHOD_DECL("java/lang/Long", Long, Long, jlong, "J");
BOX_METHOD_DECL("java/lang/Short", Short, Short, jshort, "S");
BOX_METHOD_DECL("java/lang/Byte", Byte, Byte, jbyte, "B");
BOX_METHOD_DECL("java/lang/Boolean", Boolean, Boolean, jboolean, "Z");
BOX_METHOD_DECL("java/lang/Character", Char, Char, jchar, "C");
BOX_METHOD_DECL("java/lang/Float", Float, Float, jfloat, "F");
BOX_METHOD_DECL("java/lang/Double", Double, Double, jdouble, "D");
	
void* getNativeObjectPointer(JNIEnv* env, jobject instance, jclass targetClass);
void* getPointerPeer(JNIEnv *env, jobject pointer);
jobject getJavaObjectForNativePointer(JNIEnv *env, void* nativeObject);
jobject createPointerFromIO(JNIEnv *env, void* ptr, jobject callIO);
DCstruct* getStructFromIO(JNIEnv *env, jobject callIO);
DCstruct* getNativeObjectPointerWithIO(JNIEnv *env, jobject instance, jobject callIO);

void callSinglePointerArgVoidFunction(JNIEnv *env, void* constructor, void* thisPtr, int callMode);
jlong getFlagValue(JNIEnv *env, jobject flagSet);

JNIEnv* GetEnv();

#define THROW_EXCEPTION(env, message, ...) \
{ \
	char err[256]; \
	sprintf(err, message, ##__VA_ARGS__); \
	throwException(env, err); \
}

void throwException(JNIEnv* env, const char* message);
jboolean assertThrow(JNIEnv* env, jboolean value, const char* message);
void printStackTrace(JNIEnv* env, jthrowable ex);
void logCall(JNIEnv *env, jobject method);

void initThreadLocal(JNIEnv* env);
CallTempStruct* getTempCallStruct(JNIEnv* env);
CallTempStruct* getCurrentTempCallStruct(JNIEnv* env);
void releaseTempCallStruct(JNIEnv* env, CallTempStruct* s);
void cleanupCallHandler(CallTempStruct* call);

#define GET_CHARS(javaStr) (javaStr ? (*env)->GetStringUTFChars(env, javaStr, NULL) : NULL)
#define RELEASE_CHARS(javaStr, cStr) if (javaStr) (*env)->ReleaseStringUTFChars(env, javaStr, cStr) 

#define FORCE_CRASH() \
	{\
		char* p = (char*)NULL;\
		for (int i = 0; i < 1000; i++)\
			*p = *p + 1;\
	}

#if defined(_WIN32) && !defined(_WIN64)
#define HACK_REFETCH_ENV() env = GetEnv(); 
#else
#define HACK_REFETCH_ENV()
#endif


#endif

