/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

/**
 * Wraps a value which size is the same as the 'size_t' C type (32 bits on a 32 bits platform, 64 bits on a 64 bits platform)
 * @author Olivier
 */
public final class SizeT extends AbstractIntegral {
    
	public static final int SIZE = Platform.SIZE_T_SIZE;
	
	private static final long serialVersionUID = 1547942367767922396L;
	public SizeT(long value) {
		super(value);
    }
    
    public static SizeT valueOf(long value) {
    		return new SizeT(value);
    }
}
