/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For annotations that can be forwarded to other annotations.
 * E.g. @Ptr can be forwarded to @MyPtr if the MyPtr annotation class is annotated with @Ptr
 * @author ochafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Forwardable {
    
}