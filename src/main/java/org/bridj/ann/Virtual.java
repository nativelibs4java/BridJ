package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a C++ method as virtual and specify its position in the virtual table.<br>
 * The virtual table offset is optional but strongly recommended (will fail in many cases without it).<br>
 * This position is relative to the struct's declared class, not to the parent structures/classes (unlike {@link Field}, which index is absolute).
 * @author ochafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Virtual {
	/**
	 * Optional relative virtual table offset for the C++ method (starts at 0 for each C++ class, even if it has ancestors with virtual methods)
	 */
    int value() default -1;
}
