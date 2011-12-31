package org.bridj;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.bridj.NativeConstants.ValueType;

class EllipsisHelper {
	static ThreadLocal<IntBuffer[]> holders = new ThreadLocal<IntBuffer[]>() {
		protected IntBuffer[] initialValue() {
			return new IntBuffer[1];
		}
	};
	public static IntBuffer unrollEllipsis(Object[] args) {
		IntBuffer[] holder = holders.get();
		int n = args.length;
		IntBuffer buf = holder[0];
		if (buf == null || buf.capacity() < n) {
			buf = ByteBuffer.allocateDirect(n * 4).asIntBuffer();
		}
		for (int i = 0; i < n; i++) {
			Object arg = args[i];
			ValueType type;
			if (arg == null || arg instanceof Pointer<?>)
				type = ValueType.ePointerValue;
			else if (arg instanceof Integer)
				type = ValueType.eIntValue;
			else if (arg instanceof Long)
				type = ValueType.eLongValue;
			else if (arg instanceof Short)
				type = ValueType.eShortValue;
			else if (arg instanceof Double)
				type = ValueType.eDoubleValue;
			else if (arg instanceof Float)
				type = ValueType.eFloatValue;
			else if (arg instanceof Byte)
				type = ValueType.eByteValue;
			else if (arg instanceof Boolean)
				type = ValueType.eBooleanValue;
			else if (arg instanceof Character)
				type = ValueType.eWCharValue;
			else if (arg instanceof SizeT) {
				type = ValueType.eSizeTValue;
				args[i] = arg = ((SizeT)arg).longValue();
			} else if (arg instanceof CLong) {
				type = ValueType.eCLongValue;
				args[i] = arg = ((CLong)arg).longValue();
			} else if (arg instanceof NativeObject) {
				type = ValueType.eNativeObjectValue;
			} else
				throw new IllegalArgumentException("Argument type not handled in variable argument calls  : " + arg + " (" + arg.getClass().getName() + ")");
			
			buf.put(i, type.ordinal());
		}
		return buf;
	}
}
