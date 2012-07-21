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
	sub rsp, 4 * 8			; push spill space for register args
    
	; Copying the 4 first arguments
	mov rcx, r8
	mov rdx, r9
	mov r8, [rbp + STACK_offset]
	mov r9, [rbp + STACK_offset + 8]
  	
  	;mov rcx, rbp
	;add rcx, STACK_offset
	;mov r8, [rcx]
	;mov r9, [rcx + 4]
	
  	movd xmm0, rcx
  	movd xmm1, rdx
  	movd xmm2, r8
  	movd xmm3, r9
  	
	call    qword ptr[rax+CTX_handler]
	
	add rsp, 4 * 8			; pop spill space
	
	pop rsi
    pop rbx
	pop      rbp
	;add		rsp, 8

	ret
	
dcRawCallAdapterSkipTwoArgs64 ENDP

END
