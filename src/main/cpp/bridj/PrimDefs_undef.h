/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
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
#ifdef primName  
	#undef primName 	
#endif       

#ifdef halfJPrimName
	#undef halfJPrimName	
#endif       

#ifdef primCapName  
	#undef primCapName 
#endif              

#ifdef wrapperName  
	#undef wrapperName 
#endif              

#ifdef bufferName  
	#undef bufferName 	
#endif              

#ifdef primSize  
	#undef primSize	
#endif              

#ifdef jprimName  
	#undef jprimName	
#endif              

#ifdef jprimArray
	#undef jprimArray
#endif        

#ifdef primJNICapName
	#undef primJNICapName
#endif        

#ifdef alignmentMask
	#undef alignmentMask
#endif

#ifdef REORDER_VALUE_BYTES
	#undef REORDER_VALUE_BYTES
#endif

#ifdef REORDER_VALUE_BYTES_
	#undef REORDER_VALUE_BYTES_
#endif

#ifdef REORDER_VALUE_BYTES
	#undef REORDER_VALUE_BYTES
#endif

#ifdef TEMP_REORDER_VAR_TYPE
	#undef TEMP_REORDER_VAR_TYPE
#endif

#ifndef __GNUC__
#ifndef BIG_ENDIAN
#define BIG_ENDIAN
#endif
#endif

#ifndef REORDER_VALUE_BYTES_jshort
#define REORDER_VALUE_BYTES_jshort(peer, lowerIndex, upperIndex) \
	((((jshort)((unsigned char*)JLONG_TO_PTR(peer))[upperIndex]) << 8) | ((unsigned char*)JLONG_TO_PTR(peer))[lowerIndex])
#endif

#ifndef REORDER_VALUE_BYTES_jint
#define REORDER_VALUE_BYTES_jint(peer, idx0, idx1, idx2, idx3) \
	( \
		(((jint)((unsigned char*)JLONG_TO_PTR(peer))[idx3]) << 24) | \
		(((jint)((unsigned char*)JLONG_TO_PTR(peer))[idx2]) << 16) | \
		(((jint)((unsigned char*)JLONG_TO_PTR(peer))[idx1]) << 8) | \
				((unsigned char*)JLONG_TO_PTR(peer))[idx0] \
	)
#endif

#ifndef REORDER_VALUE_BYTES_jlong
#define REORDER_VALUE_BYTES_jlong(peer, idx0, idx1, idx2, idx3, idx4, idx5, idx6, idx7) \
	( \
		(((jlong)((unsigned char*)JLONG_TO_PTR(peer))[idx7]) << 56) | \
		(((jlong)((unsigned char*)JLONG_TO_PTR(peer))[idx6]) << 48) | \
		(((jlong)((unsigned char*)JLONG_TO_PTR(peer))[idx5]) << 40) | \
		(((jlong)((unsigned char*)JLONG_TO_PTR(peer))[idx4]) << 32) | \
		(((jlong)((unsigned char*)JLONG_TO_PTR(peer))[idx3]) << 24) | \
		(((jlong)((unsigned char*)JLONG_TO_PTR(peer))[idx2]) << 16) | \
		(((jlong)((unsigned char*)JLONG_TO_PTR(peer))[idx1]) << 8) | \
				 ((unsigned char*)JLONG_TO_PTR(peer))[idx0] \
	)
#endif

