package org.bridj;

import org.bridj.ann.Constructor;

/**
 * Base class for C structs.
 * @author Olivier
 */
@org.bridj.ann.Runtime(CRuntime.class)
public abstract class StructObject extends NativeObject {
	protected StructIO io;

    protected StructObject() {
		super();
	}
    /**
     * Identified constructor with an arbirary number of arguments
     * @param voidArg always null, here to disambiguate some sub-constructors
     * @param constructorId identifier of the constructor, has to match a {@link Constructor} annotation or be -1.
     * @param args
     */
    protected StructObject(Void voidArg, int constructorId, Object... args) {
    	super(constructorId, args);
    }
    protected StructObject(Pointer<? extends StructObject> peer) {
    	super(peer);
    }
    
    /**
     * Creates a String out of this struct using BridJ.describe(this) (see {@link BridJ#describe(NativeObject) }).
     */
    @Override
    public String toString() {
    		return BridJ.describe(this);
    }
}
