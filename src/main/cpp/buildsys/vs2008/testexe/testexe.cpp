#include "stdafx.h"

#include "dynload/dynload.h"
#include "dyncall/dyncall.h"
#include "../../../bridj/RawNativeForwardCallback.h"

#include <Objbase.h>
#include <shobjidl.h>
#include <exdisp.h>
#include <shobjidl.h>



int f() {
	return 10;
}
int fVarArgs(int i, ...) {
	printf("i = %d\n", i);
	return i * 2;
}

__declspec(dllimport) long test_incr_int(long value);

void fOneInt(int a) {
	printf("i = %d\n", a);
}
void fTwoInts(int a, int b) {
	printf("i = %d, %d\n", a, b);
}
void fOneDouble(double a) {
	printf("i = %f\n", a);
}

float forwardFloatCall(void* a, void* b, float (*adder)(void*, void*, void*, int, int, float, float), float value) {
	return adder(NULL, NULL, NULL, 1, 1, -3, value);
}
float floatIncr(float value) {
	return value * 8;
}

char floatCbHandler(DCCallback* pcb, DCArgs* args, DCValue* result, void* userdata) {
	dcbArgPointer(args);
	dcbArgPointer(args);
	dcbArgPointer(args);
	dcbArgInt(args);
	dcbArgInt(args);
	dcbArgFloat(args);
	float value = dcbArgFloat(args);


	{
		DCCallVM* vm = dcNewCallVM(1024);
		dcReset(vm);
		dcArgFloat(vm, value);
		float res = dcCallFloat(vm, floatIncr);
		result->f = res;
	}
	return DC_SIGCHAR_FLOAT;
}
float forwardCaller(void* cb, float value) {
	DCCallVM* vm = dcNewCallVM(1024);
	dcReset(vm);

	dcArgPointer(vm, NULL);
	dcArgPointer(vm, NULL);
	dcArgPointer(vm, cb);
	dcArgFloat(vm, value);
	float res = dcCallFloat(vm, forwardFloatCall);
	dcFree(vm);
	return res;
}

void f4int_impl(int a, int b, int c, int d) {
	printf("a = %d, b = %d, c = %d, d = %d\n", a, b, c, d);
}

void f3char_impl(char a, char b, char c) {
	printf("a = %d, b = %d, c = %d\n", (int)a, (int)b, (int)c);
}
int _tmain(int argc, _TCHAR* argv[])
{
	/*
	CK_INFO info;
	ptrdiff_t offset = ((char*)&info.flags)-(char*)&info;
	printf("sizeof(CK_INFO) = %d\n", sizeof(CK_INFO));
    printf("offset(CK_INFO.flags) = %d\n", offset);
	DCCallback* cb = dcbNewCallback("pppiif)f", floatCbHandler, NULL);
	float value = 1;
	float incr = forwardCaller(cb, value);
	//float incr = forwardFloatCall((float (*)(void*, void*, void*, int, int, float, float))cb, value);
	printf("incr = %d\n", incr);
	*/
	typedef void (*pf4)(void*, void*, int, int, int, int);
	pf4 f4 = (pf4)dcRawCallAdapterSkipTwoArgs((void (*)())f4int_impl, DC_CALL_C_DEFAULT);
	f4((void*)16, (void*)32, 1, 2, 3, 4);

	
	typedef void (*pf3)(void*, void*, char, char, char);
	pf3 f3 = (pf3)dcRawCallAdapterSkipTwoArgs((void (*)())f3char_impl, DC_CALL_C_DEFAULT);
	f3((void*)16, (void*)32, (char)1, (char)2, (char)3);

	/*
	int (*fSkipped)(void*, void*, int);
	DCAdapterCallback* cb = dcRawCallAdapterSkipTwoArgs((void (*)())test_incr_int, DC_CALL_C_DEFAULT);
	fSkipped = (int (*)(void*, void*, int))cb;

	int rrr = fSkipped((void*)1, (void*)2, 3);
	int a = f();
	if (a != 0) {
		printf("ok");
	}*/
	/*
	int ret = CoInitialize(NULL);
	void* instance;

	int s = sizeof(GUID);
	char *cls = (char*)&CLSID_ShellWindows, *uid = (char*)&IID_IShellWindows;
	//ret = CoCreateInstance(CLSID_ShellFSFolder, NULL, 0, IID_IShellFolder, &instance);
	ret = CoCreateInstance(CLSID_ShellWindows, NULL, CLSCTX_ALL, IID_IShellWindows, &instance);

	int ok = S_OK;

	DLLib* lib = dlLoadLibrary("Ole32.dll");
	void* fCoInitialize = dlFindSymbol(lib, "CoInitialize");
	DCCallVM* vm = dcNewCallVM(1024);
	dcReset(vm);

	//dcMode(vm, DC_CALL_C_DEFAULT);
	dcMode(vm, DC_CALL_C_X86_WIN32_STD);
	dcArgInt(vm, 10);
	ret = dcCallInt(vm, fVarArgs);

	dcMode(vm, DC_CALL_C_X86_WIN32_STD);
	dcArgPointer(vm, NULL);
	ret = dcCallInt(vm, fCoInitialize);
	*/
	return 0;
}

