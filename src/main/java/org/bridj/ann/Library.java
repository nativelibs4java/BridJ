package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the name of the native library that should be bound.<br>
 * This name can then be changed at runtime to match the platform-specific name of the library with @see org.bridj.BridJ#setNativeLibraryActualName(String, String).<br>
 * Alternative aliases can also be added at runtime with @see org.bridj.BridJ#addNativeLibraryAlias(String, String).
 * @author Olivier Chafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Library {
    String value();

    public String versionPattern() default "";
}
