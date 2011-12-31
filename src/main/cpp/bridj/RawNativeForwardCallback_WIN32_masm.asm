;//////////////////////////////////////////////////////////////////////////////
;/// 
;/// Copyright (c) 2009-2010 Olivier Chafik
;/// 
;//////////////////////////////////////////////////////////////////////////////

.386
.MODEL FLAT
.CODE

CTX_phandler      =  16

_dcRawCallAdapterSkipTwoArgs32_cdecl PROC ; EXPORT

    OPTION PROLOGUE:NONE, EPILOGUE:NONE

    ; http://www.arl.wustl.edu/~lockwood/class/cs306/books/artofasm/toc.html
	; remove JNIEnv *env and jobject *this
    push dword ptr[esp + 4 * 10]
    push dword ptr[esp + 4 * 10]
    push dword ptr[esp + 4 * 10]
    push dword ptr[esp + 4 * 10]
    push dword ptr[esp + 4 * 10]
    push dword ptr[esp + 4 * 10]
    push dword ptr[esp + 4 * 10]
    push dword ptr[esp + 4 * 10]
    call dword ptr[eax + CTX_phandler] ; call function
    add  esp, 4 * 8
	
    ret
    
_dcRawCallAdapterSkipTwoArgs32_cdecl ENDP

END