package org.bridj;

import java.util.Stack;

/**
 * Base class for native objects.
 * @author Olivier
 */
public abstract class NativeObject implements NativeObjectInterface {

    protected Pointer<? extends NativeObject> peer;
    protected BridJRuntime.TypeInfo typeInfo;

    protected NativeObject(Pointer<? extends NativeObject> peer) {
        BridJ.initialize(this, peer);
    }

    protected NativeObject() {
        BridJ.initialize(this);
    }

    protected NativeObject(int constructorId, Object... args) {
        BridJ.initialize(this, constructorId, args);
    }
    /*
    @Override
    protected void finalize() throws Throwable {
    BridJ.deallocate(this);
    }*/

    public NativeObject clone() throws CloneNotSupportedException {
        return BridJ.clone(this);
    }
    
    @Override
    public boolean equals(Object o) {
    		if (!(o instanceof NativeObject))
    			return false;
    		
    		return typeInfo.equal(this, (NativeObject)o);
    }
}
