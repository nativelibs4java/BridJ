package org.bridj.util;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.AnnotationConfiguration.StdConfiguration;
import com.fasterxml.classmate.AnnotationInclusion;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;

/**
 * Static utilities for resolving types.
 * 
 * @author Christian Trimble
 *
 */
public class Types {
  static final TypeResolver resolver = new TypeResolver();
  
  public static TypeResolver getResolver() {
    return resolver;
  }
  
  public static ResolvedTypeWithMembers resolveTypeWithInstanceMethods( Class<?> unresolved ) {
    return resolveTypeWithInstanceMethods(resolver.resolve(unresolved));
  }

  public static ResolvedTypeWithMembers resolveTypeWithInstanceMethods( ResolvedType resolvedType ) {
    MemberResolver mr = new MemberResolver(resolver);
    mr.setMethodFilter(method->!method.isStatic());
    mr.setFieldFilter(field->!field.isStatic());
    AnnotationConfiguration annConfig = new AnnotationConfiguration.StdConfiguration(AnnotationInclusion.INCLUDE_BUT_DONT_INHERIT);
    return mr.resolve(resolvedType, annConfig, null);
  }

  public static ResolvedTypeWithMembers resolveTypeWithNativeMethods( Class<?> unresolved ) {
    return resolveTypeWithNativeMethods(resolver.resolve(unresolved));
  }
    public static ResolvedTypeWithMembers resolveTypeWithNativeMethods( ResolvedType resolvedType ) {
    MemberResolver mr = new MemberResolver(resolver);
    mr.setMethodFilter(method->method.isNative());
    mr.setFieldFilter(field->false);
    AnnotationConfiguration annConfig = new AnnotationConfiguration.StdConfiguration(AnnotationInclusion.INCLUDE_BUT_DONT_INHERIT);
    return mr.resolve(resolvedType, annConfig, null);
  }
}
