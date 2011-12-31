package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the raw native shared symbol for a function / method, including the mangling (C++, __stdcall...).<br>
 * If you just need to change the name but don't know the exact mangled symbol, use {@link Name} instead.
 * @author Olivier Chafik
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Symbol {
    String[] value();
}
