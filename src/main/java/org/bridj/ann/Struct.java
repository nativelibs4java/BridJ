package org.bridj.ann;

import org.bridj.StructIO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Struct details such as explicit fields packing and padding.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Struct {
	int pack() default -1;
	//int padding() default -1;
	int fieldCount() default -1;
	int size() default -1;
	Class<? extends StructIO.Customizer> customizer() default StructIO.Customizer.class;
}
