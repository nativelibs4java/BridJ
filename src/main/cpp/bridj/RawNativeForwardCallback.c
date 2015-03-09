/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
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
