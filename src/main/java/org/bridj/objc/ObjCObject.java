package org.bridj.objc;

import org.bridj.NativeObject;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import org.bridj.BridJ;

@org.bridj.ann.Runtime(ObjectiveCRuntime.class)
public class ObjCObject extends NativeObject {
	static {
		BridJ.register();
	}
	
    ObjCObject type;

    //public native <T extends ObjCObject> Pointer<T> create();
    public native <T extends ObjCObject> Pointer<T> init();
    public native Pointer<NSString> stringValue(); 
    public native Pointer<NSString> description(); 
    
    public native int hash();
    public native boolean isEqual(Pointer<? extends ObjCObject> anObject);

	public native boolean isKindOf(Pointer<? extends ObjCObject> aClassObject);
	public native boolean isMemberOf(Pointer<? extends ObjCObject> aClassObject);
	public native boolean isKindOfClassNamed(Pointer<Byte> aClassName);
	public native boolean isMemberOfClassNamed(Pointer<Byte> aClassName);

	public native boolean respondsTo(SEL aSelector);
	public native IMP methodFor(SEL aSelector);
	
	public native Pointer<?> perform(SEL aSelector);
	public native Pointer<?> perform$with(SEL aSelector, Pointer<?> anObject);
	public native Pointer<?> perform$with$with(SEL aSelector, Pointer<?> object1, Pointer<?> object2);

    public ObjCObject(Pointer<? extends NativeObject> peer) {
        super(peer);
    }

    public ObjCObject() {
        super();
    }

    public ObjCObject(int constructorId, Object... args) {
        super(constructorId, args);
    }
    
    @Override
    public String toString() {
    		Pointer<NSString> p = description();
    		if (p == null)
    			p = stringValue();
    		
    		return p.get().toString();
    }
    @Override
    public boolean equals(Object o) {
    		if (!(o instanceof ObjCObject))
    			return false;
    		
    		Pointer<ObjCObject> p = pointerTo((ObjCObject)o);
    		return isEqual(p);
    }
    @Override
    public int hashCode() {
    		return hash();
    }
}
