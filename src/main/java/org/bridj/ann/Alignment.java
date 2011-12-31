package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Alignment of a C struct / struct field, in bytes.<br>
 * If this annotation is not present, BridJ will infer the alignment using the C/C++ rules.
 * @author ochafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Alignment {
    int value();
}
