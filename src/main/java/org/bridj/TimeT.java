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

	public static class timeval_customizer extends StructIO.DefaultCustomizer {
		@Override
		public void beforeLayout(StructIO io, List<StructIO.AggregatedFieldDesc> aggregatedFields) {
			StructIO.AggregatedFieldDesc secondsField = aggregatedFields.get(0);
			if (Platform.isWindows() || !Platform.is64Bits())
				secondsField.byteLength = 4;
			else
				secondsField.byteLength = 8;
			
			secondsField.alignment = secondsField.byteLength;
		}
	}
}
