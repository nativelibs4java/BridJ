/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

/**
 * Wraps a value which size is the same as the 'long' C type (32 bits on a 32 bits platform, 64 bits on a 64 bits platform with GCC and still 32 bits with MSVC++ on 64 bits platforms)
 * @author Olivier
 */
public final class CLong extends AbstractIntegral {
    
	public static final int SIZE = Platform.CLONG_SIZE;
	
	private static final long serialVersionUID = 1542942327767932396L;
    public CLong(long value) {
        super(value);
    }
    
    public static CLong valueOf(long value) {
    		return new CLong(value);
    }
}
