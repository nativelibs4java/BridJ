/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;


/**
 * Interface for Java enumerations that have an int value associated.<br>
 * Beware: while this is the default, in C++ not all enums are ints (any integral types may be used for enums).
 * @param <E> type of the enum
 */
public interface IntValuedEnum<E extends Enum<E>> extends ValuedEnum<E> {
}
