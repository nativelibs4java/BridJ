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
package org.bridj;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

/**
 * Set of int-valued enum values that is itself int-valued (bitwise OR of all
 * the values).<br>
 * This helps use Java enums (that implement {@link ValuedEnum}) as combinable C
 * flags (see {@link FlagSet#fromValues(Enum[]) fromValues(E...) }).
 *
 * @author ochafik
 */
public class FlagSet<E extends Enum<E>> implements ValuedEnum<E> {

    private final long value;
    private final Class<E> enumClass;
    private E[] enumClassValues;

    protected FlagSet(long value, Class<E> enumClass, E[] enumClassValues) {
        this.enumClass = enumClass;
        this.value = value;
        this.enumClassValues = enumClassValues;
    }
    private static Map<Class<?>, Object[]> enumsCache = new WeakHashMap<Class<?>, Object[]>();

    @SuppressWarnings("unchecked")
    private static synchronized <EE extends Enum<EE>> EE[] getValues(Class<EE> enumClass) {
        EE[] values = (EE[]) enumsCache.get(enumClass);
        if (values == null) {
            try {
                Method valuesMethod = enumClass.getMethod("values");
                Class<?> valuesType = valuesMethod.getReturnType();
                if (!valuesType.isArray() || !ValuedEnum.class.isAssignableFrom(valuesType.getComponentType())) {
                    throw new RuntimeException();
                }
                enumsCache.put(enumClass, values = (EE[]) valuesMethod.invoke(null));
            } catch (Exception ex) {
                throw new IllegalArgumentException("Class " + enumClass + " does not have a public static " + ValuedEnum.class.getName() + "[] values() method.", ex);
            }
        }
        return (EE[]) values;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValuedEnum)) {
            return false;
        }
        return value() == ((ValuedEnum) o).value();
    }

    @Override
    public int hashCode() {
        return ((Long) value()).hashCode();
    }

    //@Override
    public Iterator<E> iterator() {
        return getMatchingEnums().iterator();
    }

    public E toEnum() {
        E nullMatch = null;
        E match = null;
        for (E e : getMatchingEnums()) {
            if (((ValuedEnum) e).value() == 0) {
                nullMatch = e;
            } else if (match == null) {
                match = e;
            } else {
                throw new NoSuchElementException("More than one enum value corresponding to " + this + " : " + e + " and " + match + "...");
            }
        }
        if (match != null) {
            return match;
        }

        if (value() == 0) {
            return nullMatch;
        }

        throw new NoSuchElementException("No enum value corresponding to " + this);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(enumClass.getSimpleName()).append("(").append(value()).append(" = ");
        try {
            boolean first = true;
            for (E e : this.getMatchingEnums()) {
                if (first) {
                    first = false;
                } else {
                    b.append(" | ");
                }
                b.append(e);
            }
        } catch (Throwable th) {
            b.append("?");
        }
        b.append(")");
        return b.toString();
    }

    public static <EE extends Enum<EE>> FlagSet<EE> createFlagSet(long value, Class<EE> enumClass) {
        return new FlagSet<EE>(value, enumClass, null);
    }

    public static class IntFlagSet<E extends Enum<E>> extends FlagSet<E> implements IntValuedEnum<E> {

        protected IntFlagSet(long value, Class<E> enumClass, E[] enumClassValues) {
            super(value, enumClass, enumClassValues);
        }
    }

    public static <EE extends Enum<EE>> IntFlagSet<EE> createFlagSet(int value, Class<EE> enumClass) {
        return new IntFlagSet<EE>(value, enumClass, null);
    }

    public static <EE extends Enum<EE>> FlagSet<EE> fromValue(ValuedEnum<EE> value) {
        if (value instanceof Enum) {
            return FlagSet.createFlagSet(value.value(), (EE) value);
        } else {
            return (FlagSet<EE>) value;
        }
    }

    public static <EE extends Enum<EE>> FlagSet<EE> createFlagSet(long value, EE... enumValue) {
        if (enumValue == null) {
            throw new IllegalArgumentException("Expected at least one enum value");
        }
        Class<EE> enumClass = (Class) enumValue[0].getClass();
        if (IntValuedEnum.class.isAssignableFrom(enumClass)) {
            return new IntFlagSet<EE>(value, enumClass, enumValue);
        } else {
            return new FlagSet<EE>(value, enumClass, enumValue);
        }
    }
//    public static <EE extends Enum<EE>> IntFlagSet<EE> createFlagSet(int value, EE... enumValue) {
//        return (IntFlagSet<EE>)createFlagSet((long)value, enumValue);
//    }

    public static <EE extends Enum<EE>> IntValuedEnum<EE> fromValue(int value, Class<EE> enumClass) {
        return (IntValuedEnum<EE>) fromValue((long) value, enumClass, enumClass.getEnumConstants());
    }

    public static <EE extends Enum<EE>> IntValuedEnum<EE> fromValue(int value, EE... enumValues) {
        return (IntValuedEnum<EE>) fromValue((long) value, enumValues);
    }

    public static <EE extends Enum<EE>> ValuedEnum<EE> fromValue(long value, EE... enumValues) {
        if (enumValues == null || enumValues.length == 0) {
            throw new IllegalArgumentException("Expected at least one enum value");
        }
        Class<EE> enumClass = (Class) enumValues[0].getClass();
        return fromValue(value, enumClass, enumValues);
    }

    protected static <EE extends Enum<EE>> ValuedEnum<EE> fromValue(long value, Class<EE> enumClass, EE... enumValue) {
        List<EE> enums = getMatchingEnums(value, enumClass.getEnumConstants());
        if (enums.size() == 1) {
            return (ValuedEnum<EE>) enums.get(0);
        }
        if (IntValuedEnum.class.isAssignableFrom(enumClass)) {
            return new IntFlagSet<EE>(value, enumClass, enums.toArray((EE[]) Array.newInstance(enumClass, enums.size())));
        } else {
            return new FlagSet<EE>(value, enumClass, enums.toArray((EE[]) Array.newInstance(enumClass, enums.size())));
        }
    }

    /**
     * Isolate bits that are set in the value.<br>
     * For instance, {@code getBits(0xf)} yields {@literal 0x1, 0x2, 0x4, 0x8}
     *
     * @param value
     * @return split bits, which give the value back if OR-ed all together.
     */
    public static List<Long> getBits(final long value) {
        List<Long> list = new ArrayList<Long>();
        for (int i = 0; i < 64; i++) {
            long bit = 1L << i;
            if ((value & bit) != 0) {
                list.add(bit);
            }
        }
        return list;
    }

    /**
     * Get the integral value of this FlagSet.
     *
     * @return value of the flag set
     */
    //@Override
    public long value() {
        return value;
    }

    public Class<E> getEnumClass() {
        return enumClass;
    }

    protected E[] getEnumClassValues() {
        return enumClassValues == null ? enumClassValues = getValues(enumClass) : enumClassValues;
    }

    /**
     * Tests if the flagset value is equal to the OR combination of all the
     * given values combined with bitwise OR operations.<br>
     * The following C code :
     * <pre>{@code
     * E v = ...; // E is an enum type
     * if (v == (E_V1 | E_V2)) { ... }
     * }</pre> Can be translated to the following Java + BridJ code :
     * <pre>{@code
     * FlagSet<E> v = ...;
     * if (v.is(E_V1, E_V2)) { ... }
     * }</pre>
     */
    public boolean is(E... valuesToBeCombinedWithOR) {
        return value() == orValue(valuesToBeCombinedWithOR);
    }

    /**
     * Tests if the flagset value is contains the OR combination of all the
     * given values combined with bitwise OR operations.<br>
     * The following C code :
     * <pre>{@code
     * E v = ...; // E is an enum type
     * if (v & (E_V1 | E_V2)) { ... }
     * }</pre> Can be translated to the following Java + BridJ code :
     * <pre>{@code
     * FlagSet<E> v = ...;
     * if (v.has(E_V1, E_V2)) { ... }
     * }</pre>
     */
    public boolean has(E... valuesToBeCombinedWithOR) {
        return (value() & orValue(valuesToBeCombinedWithOR)) != 0;
    }

    public FlagSet<E> or(E... valuesToBeCombinedWithOR) {
        return new FlagSet(value() | orValue(valuesToBeCombinedWithOR), enumClass, null);
    }

    static <E extends Enum<E>> long orValue(E... valuesToBeCombinedWithOR) {
        long value = 0;
        for (E v : valuesToBeCombinedWithOR) {
            value |= ((ValuedEnum) v).value();
        }
        return value;
    }

    public FlagSet<E> without(E... valuesToBeCombinedWithOR) {
        return new FlagSet(value() & ~orValue(valuesToBeCombinedWithOR), enumClass, null);
    }

    public FlagSet<E> and(E... valuesToBeCombinedWithOR) {
        return new FlagSet(value() & orValue(valuesToBeCombinedWithOR), enumClass, null);
    }

    protected List<E> getMatchingEnums() {
        return enumClass == null ? Collections.EMPTY_LIST : getMatchingEnums(value, getEnumClassValues());
    }

    protected static <EE extends Enum<EE>> List<EE> getMatchingEnums(long value, EE[] enums) {
        List<EE> ret = new ArrayList<EE>();
        for (EE e : enums) {
            long eMask = ((ValuedEnum<?>) e).value();
            if ((eMask == 0 && value == 0) || (eMask != 0 && (value & eMask) == eMask)) {
                ret.add((EE) e);
            }
        }
        return ret;
    }

    public static <E extends Enum<E>> FlagSet<E> fromValues(E... enumValues) {
        long value = 0;
        Class cl = null;
        for (E enumValue : enumValues) {
            if (enumValue == null) {
                continue;
            }
            if (cl == null) {
                cl = enumValue.getClass();
            }
            value |= ((ValuedEnum) enumValue).value();
        }
        return new FlagSet<E>(value, cl, enumValues);
    }
}
