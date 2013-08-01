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
package org.bridj.util;

public class Tuple {

    protected final Object[] data;

    public Tuple(Object[] data) {
        this.data = data;
    }

    public Tuple(int n) {
        this.data = new Object[n];
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0, n = size(); i < n; i++) {
            Object o = get(i);
            if (o == null) {
                continue;
            }
            h ^= o.hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Tuple)) {
            return false;
        }
        Tuple t = (Tuple) obj;
        int s = size();
        if (t.size() != s) {
            return false;
        }
        for (int i = 0; i < s; i++) {
            Object o1 = get(i), o2 = t.get(i);
            if (o1 == null) {
                if (o2 != null) {
                    return false;
                }
            } else if (!o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }

    public Object get(int index) {
        return data[index];
    }

    public void set(int index, Object value) {
        data[index] = value;
    }

    public int size() {
        return data.length;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("{");
        for (int i = 0, n = size(); i < n; i++) {
            if (i != 0) {
                b.append(',');
            }
            b.append('\n');
            b.append('\t').append(get(i));
        }
        b.append("\n}");
        return b.toString();
    }
}
