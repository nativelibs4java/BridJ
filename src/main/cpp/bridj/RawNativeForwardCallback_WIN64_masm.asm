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
;ZERO_IREG_ISMS	=	

dcRawCallAdapterSkipTwoArgs64 PROC ; EXPORT

  OPTION PROLOGUE:NONE, EPILOGUE:NONE
	
  	push     rbp
	mov      rbp, rsp		; aligns stack to 16
	push rbx
	;push 0
    push rsi
    
	; Copying the 4 first arguments
	mov rcx, r8
	mov rdx, r9
	mov r8, [rbp + 8 + 8]
	mov r9, [rbp + 16 + 8]
  	movd xmm0, rcx
  	movd xmm1, rdx
  	movd xmm2, r8
  	movd xmm3, r9
  	
	call    qword ptr[rax+CTX_handler]
		
	;mov      rsp, rbp
	pop rsi
    pop rbx
	pop      rbp
	;add		rsp, 8

	ret
	
dcRawCallAdapterSkipTwoArgs64 ENDP

END
