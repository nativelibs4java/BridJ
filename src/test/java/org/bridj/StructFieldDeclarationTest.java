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

  @Test
  public void testDifferences() {
    List<StructFieldDeclaration> legacy = StructFieldDeclaration.listFields(BasicFieldStruct.class);
    List<StructFieldDeclaration> resolved = StructFieldDeclaration.listFields2(BasicFieldStruct.class);
    
    assertThat(resolved.size(), equalTo(legacy.size()));
  }

  @Test
  public void testStaticNestedStruct() {
    List<StructFieldDeclaration> legacy = StructFieldDeclaration.listFields(StaticNestedStruct.class);
    List<StructFieldDeclaration> resolved = StructFieldDeclaration.listFields2(StaticNestedStruct.class);
    
    assertThat(resolved.size(), equalTo(legacy.size()));
    
    for( int i = 0; i < legacy.size(); i++ ) {
      assertThat(resolved.get(i).declaringClass, equalTo(legacy.get(i).declaringClass));
      assertThat(resolved.get(i).valueClass, equalTo(legacy.get(i).valueClass));
    }
  }

  @Test
  public void testGenericNestedStruct() {
    List<StructFieldDeclaration> legacy = StructFieldDeclaration.listFields(NestedStructOfBasicFieldStruct.class);
    List<StructFieldDeclaration> resolved = StructFieldDeclaration.listFields2(NestedStructOfBasicFieldStruct.class);
    
    assertThat(resolved.size(), equalTo(legacy.size()));
    
    for( int i = 0; i < legacy.size(); i++ ) {
      assertThat("declaringClass", resolved.get(i).declaringClass, equalTo(legacy.get(i).declaringClass));
      assertThat("legacyValueClass", legacy.get(i).valueClass, equalTo(StructObject.class));
      assertThat("resolvedValueClass", resolved.get(i).valueClass, equalTo(BasicFieldStruct.class));
      assertThat("index", resolved.get(i).index, equalTo(legacy.get(i).index));
      assertThat("setter", resolved.get(i).setter, equalTo(legacy.get(i).setter));
      assertThat("index", resolved.get(i).unionWith, equalTo(legacy.get(i).unionWith));
      
    }
  }
}
