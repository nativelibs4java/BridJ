package org.bridj;

/**
 * Class used by JNAerator to represent pointers to unknown structs that were typedef-ed in the following frequent pattern :
 * <pre>{@code
 *  typedef struct _A *A;
 * }</pre>
 * @author ochafik
 */
public class TypedPointer extends Pointer.OrderedPointer {
	Pointer<?> copy;
	
	private TypedPointer(PointerIO<?> io, long peer) {
		super(io, peer, UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, null, 0, null);
	}
	public TypedPointer(long address) {
        this(PointerIO.getPointerInstance(), address);
	}
	public TypedPointer(Pointer<?> ptr) {
        this(PointerIO.getPointerInstance(), ptr.getPeer());
		copy = ptr;
	}
}
