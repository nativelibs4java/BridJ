package org.bridj;
import org.bridj.util.AnnotationUtils;
import org.bridj.ann.Ptr;
import org.bridj.ann.Field;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author ochafik
 */
public class AnnotationsTest {
    public static class MyPtrStruct extends StructObject {
        @Field(0)
        @MyPtr 
        public long value;
    }
    
    @Test
    public void testMyPtr() throws NoSuchFieldException {
        assertTrue(AnnotationUtils.isAnnotationPresent(Ptr.class, MyPtrStruct.class.getField("value")));
        assertEquals(Pointer.SIZE, BridJ.sizeOf(MyPtrStruct.class));
    }
}
