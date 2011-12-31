/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

import java.util.Collections;
import java.util.Iterator;

/**
 * Interface for Java enumerations that have an integral value associated
 * @author ochafik
 * @param <E> type of the enum
 */
public interface ValuedEnum<E extends Enum<E>> extends Iterable<E> {
    long value();
//
//    public static class EnumWrapper<EE extends Enum<EE>> implements ValuedEnum<EE> {
//        EE enumValue;
//        public EnumWrapper(EE enumValue) {
//            if (enumValue == null)
//                throw new IllegalArgumentException("Null enum value !");
//            this.enumValue = enumValue;
//        }
//
//        @Override
//        public long value() {
//            return enumValue.ordinal();
//        }
//
//        @Override
//        public Iterator<EE> iterator() {
//            return Collections.singleton(enumValue).iterator();
//        }
//
//    }
//
//    public enum MyEnum implements ValuedEnum<MyEnum> {
//        A(1), B(2);
//
//        MyEnum(long value) { this.value = value; }
//        long value;
//        @Override
//        public long value() {
//            return ordinal();
//        }
//
//        @Override
//        public Iterator<MyEnum> iterator() {
//            return Collections.singleton(this).iterator();
//        }
//
//        public static ValuedEnum<MyEnum> fromValue(long value) {
//            return FlagSet.fromValue(value, values());
//        }
//    }
}