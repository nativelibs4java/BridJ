/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj;

import java.util.RandomAccess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.bridj.cpp.std.vector;
import static org.bridj.Pointer.*;

/**
 * Interface for lists that use a native storage.<br>
 * The only method added to this interface {@link NativeList#getPointer()} returns a pointer to this list, which is does not necessarily point to the first element of the
list (it depends on the list's implementation : for instance {@link vector}.getPointer() will return a pointer to the vector structure's pointer, while a list created out of a pointer through {@link Pointer#asList() } will return their storage pointer)
 * @author ochafik
 */
public interface NativeList<T> extends List<T> {
    /**
     * Returns a pointer to this list, which is does not necessarily point to the first element of the
list.<br>
     * The semantics of the returned pointer depends on the list's implementation : 
     * for instance {@link vector}.getPointer() will return a pointer to the vector structure's pointer, 
     * while a list created out of a pointer through {@link Pointer#asList() } will return their storage pointer)
     */
    public Pointer<?> getPointer();
}
