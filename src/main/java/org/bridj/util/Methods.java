package org.bridj.util;

import com.fasterxml.classmate.members.ResolvedMethod;

public class Methods {

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

}
