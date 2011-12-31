package org.bridj;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

class StringList {
	public final ByteBuffer stringsBuffer;
	public final IntBuffer offsetsBuffer;
	public StringList(String[] strings) {
		int n = strings.length;
		byte[][] bytes = new byte[n][];
		offsetsBuffer = ByteBuffer.allocateDirect(n * 4).asIntBuffer();
		int offset = 0;
		for (int i = 0; i < n; i++) {
			offsetsBuffer.put(i, offset);
			offset += (bytes[i] = strings[i].getBytes()).length + 1;
		}
		stringsBuffer = ByteBuffer.allocateDirect(offset);
		for (int i = 0; i < n; i++) {
			byte[] str = bytes[i];
			stringsBuffer.put(str);
			stringsBuffer.put((byte)0);
		}
	}
}