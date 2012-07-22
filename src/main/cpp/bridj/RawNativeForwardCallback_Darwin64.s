/*
 Package: dyncall
 Library: dyncallback
 File: dyncallback/dyncall_callback_x64_apple.s
 Description: Callback Thunk - Implementation for x64 (Apple as assembly)
 License:

 Copyright (c) 2007-2009 Daniel Adler <dadler@uni-goettingen.de>,
                         Tassilo Philipp <tphilipp@potion-studios.com>

 Permission to use, copy, modify, and distribute this software for any
 purpose with or without fee is hereby granted, provided that the above
 copyright notice and this permission notice appear in all copies.

 THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

*/

.intel_syntax
.text

/* sizes */

.set DCThunk_size	,  24
.set DCArgs_size	, 128
.set DCValue_size	,   8

/* frame local variable offsets relative to %rbp*/

.set FRAME_arg0		,  16
.set FRAME_return	,   8
.set FRAME_parent	,   0
.set FRAME_DCArgs	,-128
.set FRAME_DCValue	,-136

/* struct DCCallback */

.set CTX_thunk		,   0
.set CTX_handler	,  24
.set CTX_userdata	,  32
.set DCCallback_size	,  40
.set STACK_offset	,  48//80 // TODO FIXME

//.globl _dcCallbackThunkEntry
//_dcCallbackThunkEntry:

.globl _dcRawCallAdapterSkipTwoArgs64
_dcRawCallAdapterSkipTwoArgs64:

	pushq %rbp
	movq  %rbp, %rsp

	// Up to 6 ints and/or 8 floats are passed  through registers
	// To support a max of 16 args (& 2 skipped ones), we hence need to copy a worst case of 10 values from the stack
	sub %rsp, 10 * 8
	
	push rcx		// shadow-push rcx
	add %rsp, 8
	
	// TODO FIXME
	mov %rsi, %rbp
	add %rsi, STACK_offset      // source of stack args
	mov %rdi, %rsp    // destination
	mov %rcx, 10  // size to copy
	rep movsq         // copy!
	
	sub %rsp, 8
	pop rcx
	
	// integer parameters
	mov	 %rdi	, %rdx	# parameter 0
	mov	 %rsi	, %rcx	# parameter 1
	mov	 %rdx	, %r8	# parameter 2
	mov	 %rcx	, %r9	# parameter 3
	mov  %r8, [%rbp + STACK_offset]
	mov  %r9, [%rbp + STACK_offset + 8]

	// float parameters
	//movapd %xmm0  , %xmm2  # float parameter 0
	//movapd %xmm1  , %xmm3  # float parameter 1
	//movapd %xmm2  , %xmm4  # float parameter 2
	//movapd %xmm3  , %xmm5  # float parameter 3
	//movapd %xmm4  , %xmm6  # float parameter 4
	//movapd %xmm5  , %xmm7  # float parameter 5
	//movd   %xmm6  , %r8    # float parameter 6 	
	//movd   %xmm7  , %r9    # float parameter 7
	
	call [%rax+CTX_handler]

	//movd %xmm0, %rax

.return:
	mov  %rsp, %rbp
	pop  %rbp
	ret

