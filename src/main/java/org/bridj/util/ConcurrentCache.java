package org.bridj.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache that creates its missing values automatically, using the value class' default constructor (override {@link ConcurrentCache#newInstance(Object)} to call another constructor)
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
                if (valueConstructor != null && valueConstructor.isAccessible())
                    valueConstructor.setAccessible(true);
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
            if (oldV != null)
                v = oldV;
            else
                v = newV;
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