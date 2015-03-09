/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache that creates its missing values automatically, using the value class'
 * default constructor (override {@link ConcurrentCache#newInstance(Object)} to
 * call another constructor)
 */
public class ConcurrentCache<K, V> {

    protected final ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
    protected final Class<V> valueClass;

    public ConcurrentCache(Class<V> valueClass) {
        this.valueClass = valueClass;
    }
    private volatile Constructor<V> valueConstructor;

    private Constructor<V> getValueConstructor() {
        if (valueConstructor == null) {
            try {
                valueConstructor = valueClass.getConstructor();
                if (valueConstructor != null && valueConstructor.isAccessible()) {
                    valueConstructor.setAccessible(true);
                }
            } catch (Exception ex) {
                throw new RuntimeException("No accessible default constructor in class " + (valueClass == null ? "null" : valueClass.getName()), ex);
            }
        }
        return valueConstructor;
    }

    protected V newInstance(K key) {
        try {
            return getValueConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to call constructor " + valueConstructor, ex);
        }
    }

    public V get(K key) {
        V v = map.get(key);
        if (v == null) {
            V newV = newInstance(key);
            V oldV = map.putIfAbsent(key, newV);
            if (oldV != null) {
                v = oldV;
            } else {
                v = newV;
            }
        }
        return v;
    }

    public void clear() {
        map.clear();
    }

    public Iterable<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
