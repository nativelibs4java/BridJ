package org.bridj;

/**
 * Class used by JNAerator to represent pointers to unknown structs that were typedef-ed in the following frequent pattern :
 * <pre>{@code
 *  typedef struct _A *A;
 * }</pre>
 * @author ochafik
 */
public class TypedPointer extends Pointer {
	Pointer<?> copy;
	public TypedPointer(long address) {
        //TODO
        super(PointerIO.getPointerInstance(), address);
	}
	public TypedPointer(Pointer<?> ptr) {
		//TODO
        super(PointerIO.getPointerInstance(), ptr.getPeer());
		copy = ptr;
	}
}
