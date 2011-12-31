package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Olivier Chafik
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PlatformCondition {
    public enum Endianness {
        Any, LittleEndian, BigEndian
    }
    Endianness endianness() default Endianness.Any;

    public enum OperatingSystem {
        Any,
        Windows,
        Linux,
        Solaris,
        MacOSX,
        FreeBSD,
        Unix
    }
    OperatingSystem[] os() default OperatingSystem.Any;

    public enum AddressWidth {
        Any,
        Is64Bits,
        Is32Bits
    }
    AddressWidth addressWidth() default AddressWidth.Any;
}
