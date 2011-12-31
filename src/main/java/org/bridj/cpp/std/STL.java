package org.bridj.cpp.std;

import org.bridj.*;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html

/**
 * Util methods for STL bindings in BridJ, <i>intended for internal use only</i>.
 * @author ochafik
 */
public final class STL extends StructIO.DefaultCustomizer {
    /**
     * Perform platform-dependent structure bindings adjustments
     */
    @Override
	public void afterBuild(StructIO io) {
        Class c = io.getStructClass();
        if (c == vector.class) {
            if (Platform.isWindows()) {
                // On Windows, vector begins by 3 pointers, before the start+finish+end pointers :
                io.prependBytes(3 * Pointer.SIZE);
            }
        }
	}
}

