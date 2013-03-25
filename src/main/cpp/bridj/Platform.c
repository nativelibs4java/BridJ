/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
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

