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
    push rdi
    
    ; Copy 16 args from stack (including spill for 3rd and 4th args)
	sub rsp, 14 * 8	+ 8			; allocate stack space for 14 args (& align stack)
	mov rsi, rbp
	add rsi, 48					; source = extra args on the stack
	mov rcx, 14 * 8				; size of 14 arguments
	mov rdi, rsp				; destination = stack
	rep movsb					; copy

	; Allocate spill for 1st and 2nd args
	sub rsp, 2 * 8				
	
	; Shift / copy the 4 first integral arguments in registers
	mov rcx, r8
	mov rdx, r9
	mov r8, [rbp + STACK_offset]
	mov r9, [rbp + STACK_offset + 8]
  	
	; Shift / copy the 4 first float arguments in registers
	movapd xmm0, xmm2
  	movapd xmm1, xmm3
  	movd xmm2, r8
  	movd xmm3, r9
  	
	call    qword ptr[rax+CTX_handler]
	
	add rsp, 16 * 8 + 8			; pop spills + args + dealign
	
	pop rdi
	pop rsi
    pop rbx
	pop      rbp
	;add		rsp, 8

	ret
	
dcRawCallAdapterSkipTwoArgs64 ENDP

END
