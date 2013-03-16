package org.bridj.util;

/**
 *
 * @author ochafik
 */
public interface ClassDefiner {
    Class<?> defineClass(String className, byte[] data) throws ClassFormatError;
}
