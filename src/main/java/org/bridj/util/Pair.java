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

import java.util.Map;

public class Pair<U, V> implements Comparable<Pair<U, V>>, Map.Entry<U, V> {
	private U first;
	private V second;

	public Pair(U first, V second) {
		this.first = first;
		this.second = second;
	}

	public Pair() {}

	public U getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

	public void setFirst(U first) {
		this.first = first;
	}

	public void setSecond(V second) {
		this.second = second;
	}

	@SuppressWarnings("unchecked")
	public int compareTo(Pair<U, V> o) {
		Comparable<U> cu = (Comparable<U>)getFirst();
		if (cu == null) {
			if (first != null)
				return 1;
		} else {
			int d = cu.compareTo(o.getFirst());
			if (d != 0)
				return d;
		}

		Comparable<V> cv = (Comparable<V>)getSecond();
		if (cv == null)
			return second != null ? 1 : -1;
		return cv.compareTo(o.getSecond());
	}

	@Override
	public String toString() {
		return "Pair("+first+", "+second+")";
	}

	public U getKey() {
		return first;
	}

	public V getValue() {
		return second;
	}

	public V setValue(V value) {
		V oldValue = second;
		second = value;
		return oldValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	public boolean isFull() {
		return getFirst() != null && getSecond() != null;
	}

	public boolean isEmpty() {
		return getFirst() == null && getSecond() == null;
	}
}