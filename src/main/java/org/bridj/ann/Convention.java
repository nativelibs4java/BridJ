package org.bridj.ann;

import java.lang.annotation.ElementType;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Force the method call convention to some value.<br>
 * Without this annotation, BridJ will do its best to infer the call convention from the context (C++ method, symbol decoration...)
 * @author Olivier Chafik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Inherited
public @interface Convention {
    /**
     * Calling convention enums
     */
    public enum Style {
        /**
         * __stdcall convention (specific to Windows x86, won't have any effect on other platforms)
         */
        StdCall,
        /**
         * __fastcall convention
         */
        FastCall,
        /**
         * __cdecl convention (default for regular C functions)
         */
        CDecl,
        Pascal,
        /**
         * __clrcall convention (not supported, specific to Windows .NET mixed-mode assemblies)
         */
        CLRCall,
        /**
         * __thiscall convention (default for regular C++ methods)
         */
        ThisCall
    }
    Style value();
}
