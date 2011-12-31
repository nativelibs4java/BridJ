package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forbid direct assembly wiring of a native method.<br>
 * On select platforms and when some conditions are met, BridJ connects the native code to Java using optimized assembly glues, which might not be as stable as using <a href="http://dyncall.org/">dyncall</a> (BridJ's ffi library).<br>
 * In case of unexplained crash / bug, one should first try to set the BRIDJ_DIRECT=0 environment variable or set the bridj.direct=false Java property.<br>
 * If this solves the issue, <a href="http://code.google.com/p/nativelibs4java/issues/entry">a bug should be filed</a> and this annotation can be used as a workaround to selectively disable raw calls for some methods. 
 * @author ochafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DisableDirect { }
