package org.bridj.objc;
import org.bridj.*;
import static org.bridj.Pointer.*;
import static org.bridj.objc.ObjectiveCRuntime.*;

public class IMP extends TypedPointer {
		public IMP(long peer) { super(peer); }
		public IMP(Pointer<?> ptr) { super(ptr); }
}

