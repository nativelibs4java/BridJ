package org.bridj;

import java.util.List;

import org.bridj.ann.Field;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class StructFieldDeclarationTest {
  
  public static class BasicFieldStruct extends StructObject {
    @Field(0)
    public int intField;
  }

  public static class StaticNestedStruct extends StructObject {
    @Field(0)
    public BasicFieldStruct structField;
  }

  public static class GenericNestedStruct<S extends StructObject> extends StructObject {
    @Field(0)
    public S structField;
  }
  
  public static class NestedStructOfBasicFieldStruct
  extends GenericNestedStruct<BasicFieldStruct> {}

}
