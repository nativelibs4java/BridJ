/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj;

/**
 *
 * @author ochafik
 */
interface ClassDefiner {
    Class<?> defineClass(String className, byte[] data) throws ClassFormatError;
}
