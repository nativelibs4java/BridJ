/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;

/**
 *
 * @author ochafik
 */
public interface ClassDefiner {
    Class<?> defineClass(String className, byte[] data) throws ClassFormatError;
}
