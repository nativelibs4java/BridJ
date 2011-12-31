
#include "RawNativeForwardCallback.h"

#ifdef _WIN64
#include "dyncallback/dyncall_callback_x64.h"
#include "dyncallback/dyncall_args_x64.h"
#else
#ifdef _WIN32
#include "dyncallback/dyncall_callback_x86.h"
#include "dyncallback/dyncall_args_x86.h"
#endif
#endif

#include "dyncallback/dyncall_alloc_wx.h"
#include "dyncallback/dyncall_thunk.h"
#include "dyncall/dyncall_signature.h"

#pragma warning(disable: 4152) // casting a function pointer as a data pointer

//extern "C" {
extern void dcRawCallAdapterSkipTwoArgs64();
extern void dcRawCallAdapterSkipTwoArgs32_cdecl();
//}

#if (defined(DC__OS_Linux) || defined(DC__OS_Darwin)) && defined(DC__Arch_AMD64) || defined(_WIN64)
#define DIRECT_SKIP_TWO_ARGS dcRawCallAdapterSkipTwoArgs64
#elif defined(_WIN32)
#define DIRECT_SKIP_TWO_ARGS dcRawCallAdapterSkipTwoArgs32_cdecl
#endif

#ifndef DIRECT_SKIP_TWO_ARGS
struct DCAdapterCallback
{
};
#else
struct DCAdapterCallback
{
	DCThunk  	         thunk;    // offset 0,  size 24
	void (*handler)();
};
#endif

DCAdapterCallback* dcRawCallAdapterSkipTwoArgs(void (*handler)(), int callMode)
{
#ifndef DIRECT_SKIP_TWO_ARGS
	return NULL;
#else
	int err;
	DCAdapterCallback* pcb;
	if (callMode != DC_CALL_C_DEFAULT)
		return NULL;
	
	err = dcAllocWX(sizeof(DCAdapterCallback), (void**) &pcb);
	if (err != 0) 
		return 0;
	
	dcbInitThunk(&pcb->thunk, DIRECT_SKIP_TWO_ARGS);
	pcb->handler = handler;
	return pcb;
#endif
}
