#ifndef _BRIDJ_JNI_H
#define _BRIDJ_JNI_H

#include <jni.h>

#define GLOBAL_REF(v) (*env)->NewGlobalRef(env, v)
#define DEL_GLOBAL_REF(v) (*env)->DeleteGlobalRef(env, v)
#define WEAK_GLOBAL_REF(v) (*env)->NewWeakGlobalRef(env, v)
#define DEL_WEAK_GLOBAL_REF(v) (*env)->DeleteWeakGlobalRef(env, v)
#define FIND_GLOBAL_CLASS(name) GLOBAL_REF((*env)->FindClass(env, name))
#define OBJECT_SIG 	"Ljava/lang/Object;"
#define STRING_SIG 	"Ljava/lang/String;"
#define CLASS_SIG 	"Ljava/lang/Class;"
#define TYPE_SIG 	"Ljava/lang/reflect/Type;"
#define METHOD_SIG 	"Ljava/lang/reflect/Method;"

#define POINTER_SIG 	"Lorg/bridj/Pointer;"
#define CALLIO_SIG 	"Lorg/bridj/CallIO;"


void initMethods(JNIEnv* env);

#endif // _BRIDJ_JNI_H
