/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
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
