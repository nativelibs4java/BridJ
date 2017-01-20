package org.bridj;

import static org.bridj.Pointer.getPointer;

import org.bridj.ann.Field;
import org.junit.Test;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

public class StructGenericsTest {
  public static abstract class AbstractStructField<S extends StructObject, T extends AbstractStructField<S, T>> extends StructObject {
    @Field(0)
    public S getStruct() {
      return io.getNativeObjectField(this, 0);
    }
    
    public T setStruct( S struct ) {
      io.setNativeObjectField(this, 0, struct);
      return (T)this;
    }
  }
  
  public static class StructOfInteger extends StructObject {
    @Field(0)
    public int getField() {
      return io.getIntField(this, 0);
    }
    
    public StructOfInteger setField( int field ) {
      io.setIntField(this, 0, field);
      return this;
    }
  }
  
  public static class NestedStructOfInteger
    extends AbstractStructField<StructOfInteger, NestedStructOfInteger> {}
  
  @Test
  public void shouldSupportNestedStructOfPrimative() {
    NestedStructOfInteger s = new NestedStructOfInteger();
    s.setStruct(new StructOfInteger().setField(2));
    BridJ.writeToNative(s);
    s = getPointer(s).get();
    MatcherAssert.assertThat(s.getStruct().getField(), CoreMatchers.equalTo(2));
  }
  
  @Test
  public void shouldSupportNestedStructs() {
    
  }
}
