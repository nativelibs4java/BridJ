#include "bridj.hpp"
#include <string.h>
#include "Exceptions.h"
#include <stdlib.h>

extern jclass gBridJClass;
//extern jmethodID gGetTempCallStruct;
//extern jmethodID gReleaseTempCallStruct;
void freeCurrentThreadLocalData();

typedef struct CallTempStructNode {
	struct CallTempStruct fCallTempStruct;
	struct CallTempStructNode* fPrevious;
	struct CallTempStructNode* fNext;
	jboolean fUsed;
} CallTempStructNode;

void InitCallTempStruct(CallTempStruct* s) {
	s->vm = dcNewCallVM(1024);
}
CallTempStructNode* NewNode(CallTempStructNode* pPrevious) {
	//printf("### Creating new temp node...\n");
	CallTempStructNode* pNode = MALLOC_STRUCT(CallTempStructNode);
	memset(pNode, 0, sizeof(CallTempStructNode));
	InitCallTempStruct(&pNode->fCallTempStruct);
	//pNode->fCallTempStruct.vm = dcNewCallVM(1024);
	if (pPrevious) {
		pPrevious->fNext = pNode;
		pNode->fPrevious = pPrevious;
	}
	return pNode;
}

void FreeCallTempStruct(CallTempStruct* s) {
	dcFree(s->vm);
}


void FreeNodes(CallTempStructNode* pNode) {
	while (pNode) {
		CallTempStructNode* pNext = pNode->fNext;
		FreeCallTempStruct(&pNode->fCallTempStruct);
		free(pNode);
		pNode = pNext;
	}
}

#if defined(DC__OS_Win64) || defined(DC__OS_Win32)

#include <windows.h>

DWORD gTlsIndex = TLS_OUT_OF_INDEXES;
#define GET_THREAD_LOCAL_DATA() ((CallTempStructNode*)TlsGetValue(gTlsIndex))
#define SET_THREAD_LOCAL_DATA(data) TlsSetValue(gTlsIndex, data);

void initThreadLocal(JNIEnv* env) {
	gTlsIndex = TlsAlloc();
	if (gTlsIndex == TLS_OUT_OF_INDEXES) {
		throwException(env, "Failed to initialize the thread-local mechanism !");
		return;
	}
}

BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved)                
{ 
    switch (fdwReason) 
    {
	case DLL_PROCESS_ATTACH:
		break;
	case DLL_THREAD_ATTACH:
		break;
	case DLL_THREAD_DETACH:
		freeCurrentThreadLocalData();
		break;
	case DLL_PROCESS_DETACH:
		if (gTlsIndex != TLS_OUT_OF_INDEXES) {
			freeCurrentThreadLocalData();
			TlsFree(gTlsIndex);
		}
		break;
	default:
		break;
    }
 
    return TRUE; 
    UNREFERENCED_PARAMETER(hinstDLL); 
    UNREFERENCED_PARAMETER(lpvReserved); 
}

#else

#include <pthread.h>

pthread_key_t gTlsKey;
#define GET_THREAD_LOCAL_DATA() ((CallTempStructNode*)pthread_getspecific(gTlsKey))
#define SET_THREAD_LOCAL_DATA(data) pthread_setspecific(gTlsKey, data);

void destroyThreadLocal(void* data) {
	FreeNodes((CallTempStructNode*)data);
}
void initThreadLocal(JNIEnv* env) {
	pthread_key_create(&gTlsKey, destroyThreadLocal);
}

/*
CallTempStruct* getTempCallStruct(JNIEnv* env) {
	jlong handle = (*env)->CallStaticLongMethod(env, gBridJClass, gGetTempCallStruct);
	return (CallTempStruct*)JLONG_TO_PTR(handle);
}
void releaseTempCallStruct(JNIEnv* env, CallTempStruct* s) {
	//s->env = NULL;
	jlong h = PTR_TO_JLONG(s);
	(*env)->CallStaticVoidMethod(env, gBridJClass, gReleaseTempCallStruct, h);
}*/

#endif

#if 1
CallTempStruct* getTempCallStruct(JNIEnv* env) {
	CallTempStructNode* pNode = (CallTempStructNode*)GET_THREAD_LOCAL_DATA();
	if (!pNode) {
		pNode = NewNode(NULL);
		SET_THREAD_LOCAL_DATA(pNode);
	}

	if (pNode->fUsed) {
		if (!pNode->fNext)
			pNode->fNext = NewNode(pNode);
		
		pNode = pNode->fNext;
		SET_THREAD_LOCAL_DATA(pNode);
	}
	pNode->fUsed = JNI_TRUE;
	return &pNode->fCallTempStruct;
}

CallTempStruct* getCurrentTempCallStruct(JNIEnv* env) {
	CallTempStructNode* pNode = (CallTempStructNode*)GET_THREAD_LOCAL_DATA();
	if (!pNode || !pNode->fUsed)
		return NULL;
	
	return &pNode->fCallTempStruct;
}

void releaseTempCallStruct(JNIEnv* env, CallTempStruct* s) {
	CallTempStructNode* pNode = (CallTempStructNode*)GET_THREAD_LOCAL_DATA();
	if (!pNode || &pNode->fCallTempStruct != s) {
		throwException(env, "Invalid thread-local status : critical bug !");
		return;
	}
	pNode->fUsed = JNI_FALSE;
	if (pNode->fPrevious)
		SET_THREAD_LOCAL_DATA(pNode->fPrevious);
}

void freeCurrentThreadLocalData() {
	CallTempStructNode* pNode = (CallTempStructNode*)GET_THREAD_LOCAL_DATA();
	FreeNodes(pNode);
	SET_THREAD_LOCAL_DATA(NULL);
}
#else

CallTempStruct* getTempCallStruct(JNIEnv* env) {
	CallTempStruct* s = MALLOC_STRUCT(CallTempStruct);
	InitCallTempStruct(s);
	return s;
}

void releaseTempCallStruct(JNIEnv* env, CallTempStruct* s) {
	FreeCallTempStruct(s);
	free(s);
}

void freeCurrentThreadLocalData() {
}
#endif
