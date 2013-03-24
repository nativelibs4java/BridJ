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
import org.bridj.ann.*;
import java.util.List;
import java.util.Date;

/**
 * Wraps a value which size is the same as the 'time_t' C type (defined in time.h)
 * @author Olivier
 */
public final class TimeT extends AbstractIntegral {
    
	public static final int SIZE = Platform.TIME_T_SIZE;
	static {
		BridJ.register();
	}
	
    public TimeT(long value) {
        super(value);
    }
    
    public Date toDate() {
    		return new Date(value);
    }
    
    public static TimeT valueOf(long value) {
    		return new TimeT(value);
    }
	
	public static TimeT valueOf(Date value) {
    		return valueOf(value.getTime());
    }
    
    @Override
    public String toString() {
    		return "TimeT(value = " + value + ", time = " + toDate() + ")";
    }
	
	@Struct(customizer = timeval_customizer.class)
	public static class timeval extends StructObject {
		 
		public long getTime() {
			return seconds() * 1000 + milliseconds();
		}
		
		@Field(0) 
		public long seconds() {
			return this.io.getCLongField(this, 0);
		}
		@Field(0) 
		public timeval seconds(long seconds) {
			this.io.setCLongField(this, 0, seconds);
			return this;
		}
		public final long seconds_$eq(long seconds) {
			seconds(seconds);
			return seconds;
		}
		@Field(1) 
		public int milliseconds() {
			return this.io.getIntField(this, 1);
		}
		@Field(1) 
		public timeval milliseconds(int milliseconds) {
			this.io.setIntField(this, 1, milliseconds);
			return this;
		}
		public final int milliseconds_$eq(int milliseconds) {
			milliseconds(milliseconds);
			return milliseconds;
		}
	}

	public static class timeval_customizer extends StructCustomizer {
		@Override
		public void beforeLayout(StructDescription desc, List<StructFieldDescription> aggregatedFields) {
			StructFieldDescription secondsField = aggregatedFields.get(0);
			if (Platform.isWindows() || !Platform.is64Bits())
				secondsField.byteLength = 4;
			else
				secondsField.byteLength = 8;
			
			secondsField.alignment = secondsField.byteLength;
		}
	}
}
