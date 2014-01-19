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
// This is done to get the posix version of strerror_r, that returns an int error code.
#define _POSIX_C_SOURCE 200112L

#include "bridj.hpp"
#include "jni.h"
#include "JNI.h"

#include "Errors.h"

#include <string.h>
#include <errno.h>

#ifdef _WIN32
#include "windows.h"
#define strerror_r(errno,buf,len) strerror_s(buf,len,errno)
#endif

#define STRERROR_BUFLEN 1024

extern jclass gLastErrorClass;
extern jmethodID gSetLastErrorMethod;

// http://msdn.microsoft.com/en-us/library/ms679356(VS.85).aspx

void clearLastError(JNIEnv* env) {
#ifdef _WIN32
	SetLastError(0);
#endif
	errno = 0;
}

#ifdef _WIN32
jstring formatWin32ErrorMessage(JNIEnv* env, int errorCode)
{
	jstring message = NULL;
	// http://msdn.microsoft.com/en-us/library/ms680582(v=vs.85).aspx
	LPVOID lpMsgBuf;
	int res;
	res = FormatMessageA(
		FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL,
		errorCode,
		MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
		(LPSTR) &lpMsgBuf,
		0, 
		NULL 
	);
	if (res) {
		message = (*env)->NewStringUTF(env, (LPCSTR)lpMsgBuf);
		LocalFree(lpMsgBuf);
	} else {
#define MESSAGE_BUF_SIZE 2048
		char lpMsgBuf[MESSAGE_BUF_SIZE + 1];
		//sprintf(lpMsgBuf, "Last Error Code = %d", errorCode);
		message = (*env)->NewStringUTF(env, lpMsgBuf);
	}
	return message;
}
#endif

void setLastError(JNIEnv* env, LastError lastError, jboolean throwsLastError) {
	if (lastError.value) {
		jobject err = (*env)->CallStaticObjectMethod(env, gLastErrorClass, gSetLastErrorMethod, 
			lastError.value,
			lastError.kind);
		if (err && throwsLastError) {
			(*env)->Throw(env, err);
		}
	}
}

LastError getLastError() {
	int errnoCopy = errno;
	LastError ret;
#ifdef _WIN32
	int errorCode = GetLastError();
	if (errorCode) {
	  ret.value = errorCode;
	  ret.kind = eLastErrorKindWindows;
	}
	else
#endif
	{
		ret.value = errnoCopy;
		ret.kind = eLastErrorKindCLibrary;
	}
	return ret;
}

JNIEXPORT jstring JNICALL Java_org_bridj_LastError_getDescription(JNIEnv* env, jclass clazz, jint code, jint kind) {
  if (!code) {
    return NULL;
  }
  switch ((LastErrorKind)kind) {
#ifdef _WIN32
  case eLastErrorKindWindows:
    return formatWin32ErrorMessage(env, code);
#endif
  case eLastErrorKindCLibrary:
    {
    	int err;
    	char msg[STRERROR_BUFLEN];
    	*msg = '\0';
      err = strerror_r(code, msg, STRERROR_BUFLEN);
      return err == 0 ? (*env)->NewStringUTF(env, msg) : NULL;
    }
  default:
    return NULL; // TODO throw something?
  }
}

