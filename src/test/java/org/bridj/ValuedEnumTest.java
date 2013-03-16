package org.bridj;

import java.util.Collections;
import java.util.Iterator;
import org.bridj.ann.*;
import org.junit.Test;
import static org.junit.Assert.*;

@Library("test")
public class ValuedEnumTest {
    static {
		BridJ.register();
	}
	public enum MyEnum implements IntValuedEnum<MyEnum > {
		One(0),
		Two(1),
		Three(2);
		MyEnum(long value) {
			this.value = value;
		}
		public final long value;
		public long value() {
			return this.value;
		}
		public Iterator<MyEnum > iterator() {
			return Collections.singleton(this).iterator();
		}
		public static ValuedEnum<MyEnum > fromValue(long value) {
			return FlagSet.fromValue(value, values());
		}
	};
	public static native ValuedEnum<MyEnum > intToMyEnum(int value);
	public static native int MyEnumToInt(ValuedEnum<MyEnum > value);
    
    private static final int nTests = 100000;
    
    @Test
    public void testSingleIntToEnum() {
        MyEnum expected = MyEnum.Two;
        int expectedInt = (int)expected.value();
        ValuedEnum<MyEnum> ret = intToMyEnum(expectedInt);
        FlagSet<MyEnum> f = (FlagSet<MyEnum>)ret;
        assertEquals(MyEnum.class, f.getEnumClass());
        assertEquals(expectedInt, f.value());
    }
    
    @Test
    public void testIntToEnum() {
        MyEnum expected = MyEnum.Two;
        int expectedInt = (int)expected.value();
        for (int i = 0; i < nTests; i++) {
            ValuedEnum<MyEnum> ret = intToMyEnum(expectedInt);
            if (expectedInt != ret.value())
                assertEquals(expectedInt, ret.value());
        }
    }
    
    
    @Test
    public void testEnumToInt() {
        MyEnum expected = MyEnum.Two;
        int expectedInt = (int)expected.value();
        for (int i = 0; i < nTests; i++) {
            int ret = MyEnumToInt(expected);
            if (expectedInt != ret)
                assertEquals(expectedInt, ret);
        }
    }
}
