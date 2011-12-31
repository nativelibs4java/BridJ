package org.bridj.examples;
import org.bridj.ann.*;
import org.bridj.*;
import java.nio.*;
import java.util.*;

/**
 *
 * @author ochafik
 */ 
public class MyStruct extends StructObject {
    @Field(0)
    public native int toto();

    @Field(1) @Bits(1)
    public native int isOk();

    @Field(2) @Array(10)
    public native Pointer<Integer> values();
    public native void values(Pointer<Integer> buf);

    public native MyStruct toto(int toto);

    public static void main(String[] args) throws CloneNotSupportedException {
        MyStruct s = new MyStruct();
        s.toto(10);
        s.values(Pointer.pointerToInts(new int[] { 1, 2, 3}));
        int[] out = s.values().getIntsAtOffset(0, 3);
        System.out.println(Arrays.toString(out));

        MyStruct ns = (MyStruct) s.clone();
        out = s.values().getIntsAtOffset(0, 3);
        System.out.println(Arrays.toString(out));

        System.out.println(s.toto());
        System.out.println(s);
    }
}
