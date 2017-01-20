/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bridj;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bridj.ann.Alignment;
import org.bridj.ann.Array;
import org.bridj.ann.Bits;
import org.bridj.ann.Field;
import org.bridj.ann.Union;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.AnnotationInclusion;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;

class StructFieldDeclaration {

    final StructFieldDescription desc = new StructFieldDescription();
    Method setter;
    long index = -1, unionWith = -1;//, byteOffset = -1;
    Class<?> valueClass;
    Class<?> declaringClass;

    @Override
    public String toString() {
        return desc.name + " (index = " + index + (unionWith < 0 ? "" : ", unionWith = " + unionWith) + ", desc = " + desc + ")";
    }
    
    protected static boolean acceptFieldGetter(ResolvedMember<?> member, boolean getter) {
      if ((member instanceof ResolvedMethod) && ((ResolvedMethod) member).getRawMember().getParameterTypes().length != (getter ? 0 : 1)) {
          return false;
      }

      if (member.get(Field.class) == null) {
          return false;
      }

      return !member.isStatic();
  }
    
    protected static List<StructFieldDeclaration> listFields2(Class<?> structClass) {
      List<StructFieldDeclaration> list = new ArrayList<StructFieldDeclaration>();
      ResolvedTypeWithMembers resolvedStruct = resolveType(structClass);
      for (ResolvedMethod method : resolvedStruct.getMemberMethods()) {
        if (acceptFieldGetter(method, true)) {
            StructFieldDeclaration io = fromGetter(method);
            try {
                // this only works when the names are equal, does not support setXXX methods.
                ResolvedMethod setter = getMethod( resolvedStruct.getMemberMethods(), method.getName(), io.valueClass);
                if (acceptFieldGetter(setter, false)) {
                    io.setter = setter.getRawMember();
                }
            } catch (Exception ex) {
                //assert BridJ.info("No setter for getter " + method);
            }
            if (io != null) {
                list.add(io);
            }
        }
    }

    int nFieldFields = 0;
    for ( ResolvedField field : resolvedStruct.getMemberFields()) {
        if (acceptFieldGetter(field, true)) {
            StructFieldDeclaration io = StructFieldDeclaration.fromField(field);
            if (io != null) {
                list.add(io);
                nFieldFields++;
            }
        }
    }
    if (nFieldFields > 0 && BridJ.warnStructFields) {
        BridJ.warning("Struct " + structClass.getName() + " has " + nFieldFields + " struct fields implemented as Java fields, which won't give the best performance and might require counter-intuitive calls to BridJ.readFromNative / .writeToNative. Please consider using JNAerator to generate your struct instead, or use BRIDJ_WARN_STRUCT_FIELDS=0 or -Dbridj.warnStructFields=false to mute this warning.");
    }

    return list;
    }
    
    public static ResolvedMethod getMethod( ResolvedMethod[] methods, String name, Class<?>... params ) {
      METHODS: for( ResolvedMethod method : methods ) {
        if( !name.equals(method.getName()) ) continue METHODS;
        if( params.length != method.getArgumentCount()) continue METHODS;
        for( int i = 0; i < params.length; i++ ) {
          if( !method.getArgumentType(i).isInstanceOf(params[i])) continue METHODS;
        }
        return method;
      }
      return null;
    }
    
    protected static String nameForMember( ResolvedMember<?> member ) {
      String name = member.getName();
      if (name.matches("get[A-Z].*")) {
          return Character.toLowerCase(name.charAt(3)) + name.substring(4);
      } else if ( name.matches("set[A-Z].*")) {
          return Character.toLowerCase(name.charAt(3)) + name.substring(4);
      } else {
          return name;
      }
    }
    
    protected static ResolvedTypeWithMembers resolveType( Class<?> structClass ) {
      TypeResolver resolver = new TypeResolver();
      ResolvedType classType = resolver.resolve(structClass);
      MemberResolver mr = new MemberResolver(resolver);
      mr.setMethodFilter(method->
            method.getRawMember().getParameterTypes().length < 2 &&
            !method.isStatic());
      mr.setFieldFilter(field->!field.isStatic());
      AnnotationConfiguration annConfig = new AnnotationConfiguration.StdConfiguration(AnnotationInclusion.INCLUDE_BUT_DONT_INHERIT);
      return mr.resolve(classType, annConfig, null);
    }

    protected static StructFieldDeclaration fromField(ResolvedField getter) {
        StructFieldDeclaration field = fromMember((ResolvedField) getter);
        field.desc.field = getter.getRawMember();
        field.desc.valueType = getter.getType();
        field.valueClass = getter.getType().getErasedType();
        return field;
    }

    protected static StructFieldDeclaration fromGetter(ResolvedMethod getter) {
      StructFieldDeclaration field = fromMember((ResolvedMember) getter);
      field.desc.getter = getter.getRawMember();
      field.desc.valueType = getter.getReturnType();
      field.valueClass = getter.getReturnType().getErasedType();
      return field;
  }

    private static StructFieldDeclaration fromMember(ResolvedMember<?> member) {
        StructFieldDeclaration field = new StructFieldDeclaration();
        field.declaringClass = member.getRawMember().getDeclaringClass();

        String name = member.getName();
        if (name.matches("get[A-Z].*")) {
            name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
        }

        field.desc.name = name;

        Field fil = member.get(Field.class);
        Bits bits = member.get(Bits.class);
        Alignment alignment = member.get(Alignment.class);
        Array arr = member.get(Array.class);
        if (fil != null) {
            field.index = fil.value();
            //field.byteOffset = fil.offset();
            field.unionWith = fil.unionWith();
        }
        if (field.unionWith < 0 && field.declaringClass.getAnnotation(Union.class) != null) {
            field.unionWith = 0;
        }

        if (bits != null) {
            field.desc.bitLength = bits.value();
        }
        if (alignment != null) {
            field.desc.alignment = alignment.value();
        }
        if (arr != null) {
            long length = 1;
            for (long dim : arr.value()) {
                length *= dim;
            }
            field.desc.arrayLength = length;
            field.desc.isArray = true;
        }
        field.desc.isCLong = member.get(org.bridj.ann.CLong.class) != null;
        field.desc.isSizeT = member.get(org.bridj.ann.Ptr.class) != null;
        return field;
    }
}
