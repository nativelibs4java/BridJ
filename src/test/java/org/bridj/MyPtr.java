package org.bridj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bridj.ann.Ptr;

/**
 * For annotations that can be forwarded to other annotations.
 * E.g. @Ptr can be forwarded to @MyPtr if the MyPtr annotation class is annotated with @Ptr
 * @author ochafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Ptr // forwarded to this !
public @interface MyPtr {
    
}