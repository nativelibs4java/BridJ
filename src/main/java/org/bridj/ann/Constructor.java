package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate the index of a constructor.<br>
 * It is important that all constructors have an unique index that helps identify them, and that they call NativeObject's parent constructor with the same index. 
 * @author ochafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Constructor {
    int value() default -1;
}
