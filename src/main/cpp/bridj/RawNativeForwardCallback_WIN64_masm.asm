;//////////////////////////////////////////////////////////////////////////////
;/// 
;/// Copyright (c) 2009-2010 Olivier Chafik
;/// 
;//////////////////////////////////////////////////////////////////////////////

.CODE

; struct DCCallback
CTX_thunk       =    0
CTX_handler     =   24
CTX_userdata    =   32
DCCallback_size =   40
STACK_offset    =   48
;ZERO_IREG_ISMS	=	

dcRawCallAdapterSkipTwoArgs64 PROC ; EXPORT

  OPTION PROLOGUE:NONE, EPILOGUE:NONE
	
  	push     rbp
	mov      rbp, rsp		; aligns stack to 16
	push rbx
    push rsi
    
	; Copying the 4 first arguments
	movd rcx, xmm2
	movd xmm0, rcx
	movd rcx, xmm3
  	movd xmm1, rcx
  	movd xmm2, r8
  	movd xmm3, r9
  	
	mov rcx, r8
	mov rdx, r9
	mov r8, [rbp + STACK_offset]
	mov r9, [rbp + STACK_offset + 8]
  	
  	sub rsp, 4 * 8			; push spill space for register args
    
	call    qword ptr[rax+CTX_handler]
	
	; todo copyu original spill into spill!
	add rsp, 4 * 8			; pop spill space
	
	pop rsi
    pop rbx
	pop      rbp
	;add		rsp, 8

	ret
	
dcRawCallAdapterSkipTwoArgs64 ENDP

END
