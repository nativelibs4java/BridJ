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
package org.bridj;

/**
 * Pointer LRU cache, not synchronized.
 * Uses a cyclic array, LRU element is at index |index|, which is continually decreased.
 */
abstract class PointerLRUCache {
    private final Pointer<?>[] values;
    private int index;
    private final int tolerance;
    
    PointerLRUCache(int length, int tolerance) {
        assert tolerance >= 0;
        this.tolerance = tolerance;
        values = new Pointer[length];
    }
    
    protected abstract <T> Pointer<T> pointerToAddress(long peer, PointerIO<T> pointerIO);

    Pointer<?> get(long peer, PointerIO<?> pointerIO) {
        int idx = this.index;
        for (int i = 0, length = values.length; i < length; i++) {
            Pointer<?> ptr = values[idx];
            if (ptr == null) {
                // Found an empty slot, cool!
                values[idx] = ptr = pointerToAddress(peer, pointerIO);
                return ptr;
            } else if (ptr.getPeer() == peer && ptr.getIO() == pointerIO) {
                // Found a matching pointer: promote it if it's not the head yet.
            	  if (i > tolerance) {
                    int idx2 = decrementedIndex();
                    if (idx != idx2) {
                        Pointer<?> temp = values[idx];
                        values[idx] = values[idx2];
                        values[idx2] = temp;
                    }
                }
                return ptr;
            }
            idx++;
            if (idx == length) {
                idx = 0;
            }
        }
        // No match: overwrite least recently used, at position index - 1
        Pointer<?> ptr = pointerToAddress(peer, pointerIO);
        values[decrementedIndex()] = ptr;
        return ptr;
    }

    private int decrementedIndex() {
        int i = this.index;
        i--;
        if (i < 0) {
            i = values.length - 1;
        }
        this.index = i;
        return i;
    }
    
}
