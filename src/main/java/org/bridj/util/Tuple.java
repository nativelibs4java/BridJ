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
			if (o == null)
				continue;
			h ^= o.hashCode();
		}
		return h;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Tuple))
			return false;
		Tuple t = (Tuple)obj;
		int s = size();
		if (t.size() != s)
			return false;
		for (int i = 0; i < s; i++) {
			Object o1 = get(i), o2 = t.get(i);
			if (o1 == null) {
				if (o2 != null)
					return false;
			} else if (!o1.equals(o2))
				return false;
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
			if (i != 0)
				b.append(',');
			b.append('\n');
			b.append('\t').append(get(i));
		}
		b.append("\n}");
		return b.toString();
	}
}
