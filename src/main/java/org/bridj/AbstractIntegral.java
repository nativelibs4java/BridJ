/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

/**
 * Base class for custom integral numbers
 * @author Olivier
 */
class AbstractIntegral extends Number {
    
	protected final long value;
    public AbstractIntegral(long value) {
        this.value = value;
    }

	private static final long HIGH_NEG = 0xffffffff00000000L;
	public static int safeIntCast(long value) {
		long high = value & HIGH_NEG;
		if (high != 0 && high != HIGH_NEG) 
		//if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE)
            throw new RuntimeException("Value " + value + " = 0x" + Long.toHexString(value) + " is not within the int range");
		
        return (int)(value & 0xffffffff);
	}

    @Override
    public int intValue() {
        return safeIntCast(value);
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
    		if (o == null || !(o instanceof AbstractIntegral))
    			return false;
    		if (!o.getClass().equals(getClass()))
    			return false;
        return value == ((AbstractIntegral)o).value;
    }
    
    @Override
    public int hashCode() {
    		return ((Long)value).hashCode();
    }
    
    @Override
    public String toString() {
    		return getClass().getSimpleName() + "(" + value + ")";
    }
}
