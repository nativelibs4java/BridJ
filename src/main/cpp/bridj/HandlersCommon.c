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
#include "HandlersCommon.h"
#include "JNI.h"

jboolean callFunction(CallTempStruct* call, CommonCallbackInfo* info, DCArgs* args, DCValue* result, void* callback, CallFlags flags)
{
  return 
    followArgs(call, args, info->nParams, info->fParamTypes, flags) 
    &&
    followCall(call, info->fReturnType, result, callback, flags);
}

jboolean callGenericFunction(CallTempStruct* call, CommonCallbackInfo* info, DCArgs* args, DCValue* result, void* callback)
{
  return 
    followArgsGenericJavaCallback(call, args, info->nParams, info->fParamTypes) 
    &&
    followCallGenericJavaCallback(call, info->fReturnType, result, callback);
}

jboolean followArgs(CallTempStruct* call, DCArgs* args, int nTypes, ValueType* pTypes, CallFlags flags) 
{	
	JNIEnv* env = call->env;
	int iParam;
	for (iParam = 0; iParam < nTypes; iParam++) {
		ValueType type = pTypes[iParam];
		switch (type) {
			case eIntFlagSet:
				{
					jobject callIO = call && call->pCallIOs ? *(call->pCallIOs++) : NULL;
					if (flags & CALLING_JAVA) {
						int flags = dcbArgInt(args);
						jobject obj = createPointerFromIO(env, JLONG_TO_PTR ((jlong)flags), callIO);
						dcArgPointer(call->vm, obj);
						(*env)->DeleteLocalRef(env, obj);
					} else {
						jobject obj = (jobject)dcbArgPointer(args);
						int arg = (jint)getFlagValue(env, obj);
						(*env)->DeleteLocalRef(env, obj);
						if (flags & IS_VAR_ARGS)
							dcArgPointer(call->vm, (void*)(ptrdiff_t)arg);
						else
							dcArgInt(call->vm, arg);
					}
				}
				break;
			case eIntValue:
				{
					int arg = dcbArgInt(args);
					if (flags & IS_VAR_ARGS)
						dcArgPointer(call->vm, (void*)(ptrdiff_t)arg);
					else
						dcArgInt(call->vm, arg);
				}
				break;
			#define ARG_BOXED_INTEGRAL(type, capitalized) \
				{ \
					if (flags & CALLING_JAVA) { \
						type arg = (sizeof(type) == 4) ? (type)dcbArgInt(args) : (type)dcbArgLongLong(args); \
						jobject boxed = Box ## capitalized(env, arg); \
						dcArgPointer(call->vm, boxed); \
						/*addTempCallLocalRef(call, boxed);*/ \
					} else { \
						jobject parg = dcbArgPointer(args); \
						jlong arg = Unbox ## capitalized(env, parg); \
						if (flags & IS_VAR_ARGS) \
							dcArgPointer(call->vm, (void*)(ptrdiff_t)arg); \
						else if (sizeof(type) == 4) \
							dcArgInt(call->vm, (jint)arg); \
						else \
							dcArgLongLong(call->vm, (jlong)arg); \
						(*env)->DeleteLocalRef(env, parg); \
					} \
				}
			#define ARG_UNBOXED_INTEGRAL(type, capitalized) \
				{ \
					if (flags & CALLING_JAVA) { \
						type arg = (sizeof(type) == 4) ? (type)dcbArgInt(args) : (type)dcbArgLongLong(args); \
						dcArgLongLong(call->vm, (jlong)arg); \
					} else { \
						jlong arg = dcbArgLongLong(args); \
						if (flags & IS_VAR_ARGS) \
							dcArgPointer(call->vm, (void*)(ptrdiff_t)arg); \
						else if (sizeof(type) == 4) \
							dcArgInt(call->vm, (jint)arg); \
						else \
							dcArgLongLong(call->vm, (jlong)arg); \
					} \
				}
			case eCLongValue:
				ARG_UNBOXED_INTEGRAL(long, CLong);
				break;
			case eSizeTValue:
				ARG_UNBOXED_INTEGRAL(size_t, SizeT);
				break;
			case eCLongObjectValue:
				ARG_BOXED_INTEGRAL(long, CLong);
				break;
			case eSizeTObjectValue:
				ARG_BOXED_INTEGRAL(size_t, SizeT);
				break;
			case eTimeTObjectValue:
				ARG_BOXED_INTEGRAL(time_t, TimeT);
				break;
			case eLongValue:
				dcArgLongLong(call->vm, dcbArgLongLong(args));
				break;
			case eShortValue:
				{
					short arg = dcbArgShort(args);
					if (flags & IS_VAR_ARGS)
						dcArgPointer(call->vm, (void*)(ptrdiff_t)arg);
					else
						dcArgShort(call->vm, arg);
				}
				break;
			case eBooleanValue:
			case eByteValue: 
				{
					char arg = dcbArgChar(args);
					if (flags & IS_VAR_ARGS)
						dcArgPointer(call->vm, (void*)(ptrdiff_t)arg);
					else
						dcArgChar(call->vm, arg);
				}
				break;
			case eFloatValue: 
				{
					float arg = dcbArgFloat(args);
					if (flags & IS_VAR_ARGS)
						dcArgDouble(call->vm, arg);
					else
						dcArgFloat(call->vm, arg);
				}
				break;
			case eDoubleValue:
				dcArgDouble(call->vm, dcbArgDouble(args));
				break;
			case ePointerValue:
				{
					void* ptr = dcbArgPointer(args);
					jobject callIO = call && call->pCallIOs ? *(call->pCallIOs++) : NULL;
					if (flags & CALLING_JAVA)
					{
						ptr = createPointerFromIO(env, ptr, callIO);
						addTempCallLocalRef(call, ptr);
					} else {
						ptr = ptr ? getPointerPeer(env, ptr) : NULL;
					}
					dcArgPointer(call->vm, ptr);
				}
				break;
			case eWCharValue:
				switch (sizeof(wchar_t)) {
				case 1:
					dcArgChar(call->vm, dcbArgChar(args));
					break;
				case 2:
					dcArgShort(call->vm, dcbArgShort(args));
					break;
				case 4:
					dcArgInt(call->vm, dcbArgInt(args));
					break;
				default:
					throwException(env, "Invalid wchar_t size for argument !");
					return JNI_FALSE;
				}
				break;
			case eNativeObjectValue: {
				jobject callIO = call && call->pCallIOs ? *(call->pCallIOs++) : NULL;
				DCstruct* s = getStructFromIO(env, callIO);
				void* pStruct = getNativeObjectPointerWithIO(env, dcbArgPointer(args), callIO);
				if (!s) {
					throwException(env, "Failed to get low-level struct representation !");
					return JNI_FALSE;
				}
				if (!pStruct) {
					throwException(env, "Struct by value cannot be null !");
					return JNI_FALSE;
				}
				dcArgStruct(call->vm, s, pStruct);
				break;
			}	
			case eEllipsis: {
				if (flags & CALLING_JAVA) {
					throwException(env, "Calling Java ellipsis is not supported yet !");
					return JNI_FALSE;
				} else {
					jobjectArray arr = (jobjectArray)dcbArgPointer(args);
					jsize n = (*env)->GetArrayLength(env, arr), i;

					for (i = 0; i < n; i++) {
						jobject arg = (*env)->GetObjectArrayElement(env, arr, i);
						#define TEST_INSTANCEOF(cl, st) \
							if ((*env)->IsInstanceOf(env, arg, cl)) st;

						if (arg == NULL)
							dcArgPointer(call->vm, NULL);//getPointerPeer(env, (void*)NULL));
						else
						// As per the C standard for varargs, all ints are promoted to ptrdiff_t and float is promoted to double : 
						TEST_INSTANCEOF(gIntClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)UnboxInt(env, arg)))
						else
						TEST_INSTANCEOF(gLongClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)UnboxLong(env, arg)))
						else
						TEST_INSTANCEOF(gShortClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)UnboxShort(env, arg)))
						else
						TEST_INSTANCEOF(gByteClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)UnboxByte(env, arg)))
						else
						TEST_INSTANCEOF(gBooleanClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)(char)UnboxBoolean(env, arg)))
						else
						TEST_INSTANCEOF(gCharClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)(short)UnboxChar(env, arg)))
						else
						TEST_INSTANCEOF(gDoubleClass, dcArgDouble(call->vm, UnboxDouble(env, arg)))
						else
						TEST_INSTANCEOF(gFloatClass, dcArgDouble(call->vm, UnboxFloat(env, arg)))
						else
						TEST_INSTANCEOF(gCLongClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)(long)UnboxCLong(env, arg)))
						else
						TEST_INSTANCEOF(gSizeTClass, dcArgPointer(call->vm, (void*)(ptrdiff_t)UnboxSizeT(env, arg)))
						else
						TEST_INSTANCEOF(gPointerClass, dcArgPointer(call->vm, getPointerPeer(env, (void*)arg)))
						else {
							throwException(env, "Invalid value type in ellipsis");
							(*env)->DeleteLocalRef(env, arg);
							(*env)->DeleteLocalRef(env, arr);
							return JNI_FALSE;
						}
						(*env)->DeleteLocalRef(env, arg);
					}
					(*env)->DeleteLocalRef(env, arr);
				}
				break;
			}
			default:
				throwException(env, "Invalid argument value type !");
				return JNI_FALSE;
		}
	}
	if ((*env)->ExceptionCheck(env))
		return JNI_FALSE;
	return JNI_TRUE;
}

jboolean followCall(CallTempStruct* call, ValueType returnType, DCValue* result, void* callback, CallFlags flags) 
{
	JNIEnv* env = call->env;
	switch (returnType) {
#define GET_LAST_ERROR() \
		if (flags & SETS_LASTERROR) call->lastError = getLastError();
#define CALL_CASE(valueType, capCase, hiCase, uni) \
		case valueType: \
			result->uni = dcCall ## capCase(call->vm, callback); \
			GET_LAST_ERROR(); \
			break;
		CALL_CASE(eIntValue, Int, INT, i)
		CALL_CASE(eLongValue, LongLong, LONGLONG, l)
		CALL_CASE(eShortValue, Short, SHORT, s)
		CALL_CASE(eFloatValue, Float, FLOAT, f)
		CALL_CASE(eDoubleValue, Double, DOUBLE, d)
		case eBooleanValue:
		CALL_CASE(eByteValue, Char, CHAR, c)
		case eCLongValue:
			result->L = (jlong)dcCallLong(call->vm, callback);
			GET_LAST_ERROR();
			break;
		case eSizeTValue:
			result->L = (size_t)dcCallPointer(call->vm, callback);
			GET_LAST_ERROR();
			break;
		    
		#define CALL_BOXED_INTEGRAL(type, capitalized) \
			if (flags & CALLING_JAVA) { \
				void* pt = dcCallPointer(call->vm, callback); \
				type tt; \
				GET_LAST_ERROR(); \
				tt = (type)Unbox ## capitalized(env, pt); \
				if (sizeof(type) == 4) \
					result->i = (jint)tt; \
				else \
					result->l = (jlong)tt; \
			} else { \
				type tt = (sizeof(type) == 4) ? (type)dcCallInt(call->vm, callback) : (type)dcCallLongLong(call->vm, callback); \
				GET_LAST_ERROR(); \
				result->p = Box ## capitalized(env, tt); \
			}
			
		case eCLongObjectValue:
			CALL_BOXED_INTEGRAL(long, CLong);
			break;
		case eSizeTObjectValue:
			CALL_BOXED_INTEGRAL(size_t, SizeT);
		    break;
		case eTimeTObjectValue: 
			CALL_BOXED_INTEGRAL(time_t, TimeT);
		    break;
		case eVoidValue:
			dcCallVoid(call->vm, callback);
			GET_LAST_ERROR();
			break;
		case eIntFlagSet:
			{
				int flags = dcCallInt(call->vm, callback);
				jobject callIO, obj;
				GET_LAST_ERROR();
				callIO = call && call->pCallIOs ? *(call->pCallIOs++) : NULL;
				obj = createPointerFromIO(env, JLONG_TO_PTR ((jlong)flags), callIO);
					
				result->p = obj;
			}
			break;
		case ePointerValue:
			{
				void* ptr = dcCallPointer(call->vm, callback);
				GET_LAST_ERROR();
				if (flags & CALLING_JAVA)
					result->p = ptr ? getPointerPeer(env, ptr) : NULL;
					//result->p = ptr;
				else
				{
					jobject callIO = call && call->pCallIOs ? *(call->pCallIOs++) : NULL;
					result->p = createPointerFromIO(env, ptr, callIO);
				}
			}
			break;
		case eWCharValue:
			switch (sizeof(wchar_t)) {
			case 1:
				result->c = dcCallChar(call->vm, callback);
				break;
			case 2:
				result->s = dcCallShort(call->vm, callback);
				break;
			case 4:
				result->i = dcCallInt(call->vm, callback);
				break;
			default:
				throwException(env, "Invalid wchar_t size !");
				return JNI_FALSE;
			}
			GET_LAST_ERROR();
			break;
		default:
			if (flags & FORCE_VOID_RETURN) 
			{
				dcCallVoid(call->vm, callback);
				GET_LAST_ERROR();
				break;
			}
			throwException(env, "Invalid return value type !");
			return JNI_FALSE;
	}
	HACK_REFETCH_ENV(); 
	if ((flags & CALLING_JAVA) && (*env)->ExceptionCheck(env))
		return JNI_FALSE;
	return JNI_TRUE;
}

jobject initCallHandler(DCArgs* args, CallTempStruct** callOut, JNIEnv* env, CommonCallbackInfo* info)
{
	jobject instance = NULL;
	CallTempStruct* call = NULL;
	
	if (args) {
		env = (JNIEnv*)dcbArgPointer(args); // first arg = Java env
		instance = dcbArgPointer(args); // skip second arg = jclass or jobject
	}
	if (env) {
		//initMethods(env);
		*callOut = call = getTempCallStruct(env);
		call->env = env;
	} else
		*callOut = NULL;

	if (gLog && call && info)
		logCall(call->env, info->fMethod);

	return instance;
}

void addTempCallLocalRef(CallTempStruct* call, jobject obj) {
	vectorAppend(&call->localRefsToCleanup, obj);
}

void cleanupCallHandler(CallTempStruct* call)
{
	JNIEnv *env = call->env;
	size_t i, n;
	for (i = 0, n = call->localRefsToCleanup.length; i < n; i++) {
		(*env)->DeleteLocalRef(env, call->localRefsToCleanup.buffer[i]);
	}
	call->localRefsToCleanup.length = 0;
	dcReset(call->vm);
	releaseTempCallStruct(call->env, call);
}
