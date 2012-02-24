/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

/**
 * Set of int-valued enum values that is itself int-valued (bitwise OR of all the values).<br>
 * This helps use Java enums (that implement {@link ValuedEnum}) as combinable C flags (see {@link FlagSet#fromValues(Enum[]) fromValues(E...) }).
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
        EE[] values = (EE[])enumsCache.get(enumClass);
        if (values == null) {
            try {
                Method valuesMethod = enumClass.getMethod("values");
                Class<?> valuesType = valuesMethod.getReturnType();
                if (!valuesType.isArray() || !ValuedEnum.class.isAssignableFrom(valuesType.getComponentType()))
                    throw new RuntimeException();
                enumsCache.put(enumClass, values = (EE[])valuesMethod.invoke(null));
            } catch (Exception ex) {
                throw new IllegalArgumentException("Class " + enumClass + " does not have a public static " + ValuedEnum.class.getName() + "[] values() method.", ex);
            }
        }
        return (EE[])values;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValuedEnum))
            return false;
        return value() == ((ValuedEnum)o).value();
    }

    @Override
    public int hashCode() {
        return ((Long)value()).hashCode();
    }

    //@Override
    public Iterator<E> iterator() {
        return getMatchingEnums().iterator();
    }
    public E toEnum() {
        E nullMatch = null;
        E match = null;
        for (E e : getMatchingEnums()) {
            if (((ValuedEnum)e).value() == 0)
                nullMatch = e;
            else if (match == null)
                match = e;
            else
                throw new NoSuchElementException("More than one enum value corresponding to " + this + " : " + e + " and " + match + "...");
        }
        if (match != null)
            return match;

        if (value() == 0)
            return nullMatch;

        throw new NoSuchElementException("No enum value corresponding to " + this);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(enumClass.getSimpleName()).append("(").append(value()).append(" = ");
        try {
            boolean first = true;
            for (E e : this.getMatchingEnums()) {
                if (first)
                    first = false;
                else
                    b.append(" | ");
                b.append(e);
            }
        } catch (Throwable th) {
            b.append("?");
        }
        b.append(")");
        return b.toString();
    }

    public static <EE extends Enum<EE>> FlagSet<EE> fromValue(long value, Class<EE> enumClass) {
        return new FlagSet<EE>(value, enumClass, null);
    }
    public static class IntFlagSet<E extends Enum<E>> extends FlagSet<E> implements IntValuedEnum<E> {
    	protected IntFlagSet(long value, Class<E> enumClass, E[] enumClassValues) {
    		super(value, enumClass, enumClassValues);
    	}
    }
    public static <EE extends Enum<EE>> IntFlagSet<EE> fromValue(int value, Class<EE> enumClass) {
        return new IntFlagSet<EE>(value, enumClass, null);
    }
    public static <EE extends Enum<EE>> FlagSet<EE> fromValue(ValuedEnum<EE> value) {
        if (value instanceof Enum)
            return FlagSet.fromValue(value.value(), (EE)value);
        else 
            return (FlagSet<EE>)value;
    }
    public static <EE extends Enum<EE>> FlagSet<EE> fromValue(long value, EE... enumValue) {
        return new FlagSet<EE>(value, null, enumValue);
    }
    public static <EE extends Enum<EE>> IntFlagSet<EE> fromValue(int value, EE... enumValue) {
        return new IntFlagSet<EE>(value, null, enumValue);
    }
    /**
     * Isolate bits that are set in the value.<br>
     * For instance, {@code getBits(0xf)} yields {@literal 0x1, 0x2, 0x4, 0x8}
     * @param value
     * @return split bits, which give the value back if OR-ed all together.
     */
    public static List<Long> getBits(final long value) {
        List<Long> list = new ArrayList<Long>();
        for (int i = 0; i < 64; i++) {
            long bit = 1L << i;
            if ((value & bit) != 0)
                list.add(bit);
        }
        return list;
    }

    /**
     * Get the integral value of this FlagSet.
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
     * Tests if the flagset value is equal to the OR combination of all the given values combined with bitwise OR operations.<br>
     * The following C code :
     * <pre>{@code
     * E v = ...; // E is an enum type
     * if (v == (E_V1 | E_V2)) { ... }
     * }</pre>
     * Can be translated to the following Java + BridJ code :
     * <pre>{@code
     * FlagSet<E> v = ...;
     * if (v.is(E_V1, E_V2)) { ... }
     * }</pre>
     */
    public boolean is(E... valuesToBeCombinedWithOR) {
    	return value() == orValue(valuesToBeCombinedWithOR);
    }
    
    /**
     * Tests if the flagset value is contains the OR combination of all the given values combined with bitwise OR operations.<br>
     * The following C code :
     * <pre>{@code
     * E v = ...; // E is an enum type
     * if (v & (E_V1 | E_V2)) { ... }
     * }</pre>
     * Can be translated to the following Java + BridJ code :
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
    	for (E v : valuesToBeCombinedWithOR)
    		value |= ((ValuedEnum)v).value();
    	return value;
    }
    public FlagSet<E> without(E... valuesToBeCombinedWithOR) {
    	return new FlagSet(value() & ~orValue(valuesToBeCombinedWithOR), enumClass, null);
    }
    public FlagSet<E> and(E... valuesToBeCombinedWithOR) {
    	return new FlagSet(value() & orValue(valuesToBeCombinedWithOR), enumClass, null);
    }

    protected List<E> getMatchingEnums() {
        List<E> ret = new ArrayList<E>();
        if (enumClass != null) {
            for (E e : getEnumClassValues()) {
                long eMask = ((ValuedEnum<?>)e).value();
                if ((value & eMask) == eMask)
                    ret.add((E)e);
            }
        }

        return ret;
    }

	public static <E extends Enum<E>> FlagSet<E> fromValues(E... enumValues) {
		long value = 0;
		Class cl = null;
		for (E enumValue : enumValues) {
            if (enumValue == null)
                continue;
			if (cl == null)
				cl = enumValue.getClass();
			value |= ((ValuedEnum)enumValue).value();
		}
		return new FlagSet<E>(value, cl, enumValues);
	}

}
