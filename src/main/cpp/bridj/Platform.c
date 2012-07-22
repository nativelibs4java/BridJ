#include "org_bridj_Platform.h"
#include "JNI.h"

#include "bridj.hpp"
#include <string.h>

#define STRING_SIG "Ljava/lang/String;"
#define NEW_STRING(x) (*env)->NewStringUTF(env, x)

//jclass gPlatform_utsnameClass = NULL;
//jmethodID gPlatform_utsnameConstr = NULL;

void initPlatformMethods(JNIEnv *env) 
{
	/*gPlatform_utsnameClass = FIND_GLOBAL_CLASS("org/bridj/Platform$utsname");
	gPlatform_utsnameConstr = (*env)->GetMethodID(env, gPlatform_utsnameClass, "<init>",
		"(" STRING_SIG STRING_SIG STRING_SIG STRING_SIG STRING_SIG ")V"
	);*/
}		
	

#define JNI_SIZEOF(type, escType) \
jint JNICALL Java_org_bridj_Platform_sizeOf_1 ## escType(JNIEnv *env, jclass clazz) { return sizeof(type); }

#define JNI_SIZEOF_t(type) JNI_SIZEOF(type ## _t, type ## _1t)

JNI_SIZEOF_t(size)
JNI_SIZEOF_t(time)
JNI_SIZEOF_t(wchar)
JNI_SIZEOF_t(ptrdiff)
JNI_SIZEOF(long, long)


JNIEXPORT jint JNICALL Java_org_bridj_Platform_getMaxDirectMappingArgCount(JNIEnv *env, jclass clazz) {
#if defined(_WIN64)
	return 16;
#elif defined(DC__OS_Darwin) && defined(DC__Arch_AMD64)
	return 4;//16;
#elif defined(DC__OS_Linux) && defined(DC__Arch_AMD64)
	return 4;
#elif defined(_WIN32)
	return 8;
#else
	return -1;
#endif
}

/*
#ifdef _WIN32
jobject JNICALL Java_org_bridj_Platform_uname(JNIEnv *env, jclass clazz) {
	return NULL;
}
#else

#include <sys/utsname.h>
jobject JNICALL Java_org_bridj_Platform_uname(JNIEnv *env, jclass clazz) 
{
	initMethods(env);
	
	struct utsname name;
	uname(&name);
	return (*env)->NewObject(
		env, 
		gPlatform_utsnameClass, 
		gPlatform_utsnameConstr, 
		NEW_STRING(name.sysname),
		NEW_STRING(name.nodename),
		NEW_STRING(name.release),
		NEW_STRING(name.version),
		NEW_STRING(name.machine)
	);
}
#endif
*/

