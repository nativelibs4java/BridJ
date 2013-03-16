/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2013, Olivier Chafik (http://ochafik.com/)
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
package org.bridj.util;
import org.bridj.Platform;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.objectweb.asm.*;
import static org.objectweb.asm.ClassReader.*;
/**
 * Util class that scavenges through a class' bytecode to retrieve the original order of fields and methods, as defined in the sources (unlike what the reflection APIs return : they don't guarantee the order).
 * @author ochafik
 */
public final class BytecodeAnalyzer {
    private BytecodeAnalyzer() {}
    
    /**
     * Returns array of String[] { declaringClassInternalName, methodName, signature }
     */
    public static List<String[]> getNativeMethodSignatures(Class c) throws IOException {
        return getNativeMethodSignatures(getInternalName(c), Platform.getClassLoader(c));
    }
    /**
     * Returns array of String[] { declaringClassInternalName, methodName, signature }
     */
    public static List<String[]> getNativeMethodSignatures(String internalName, ClassLoader classLoader) throws IOException {
        return getNativeMethodSignatures(internalName, classLoader, new ArrayList<String[]>());
    }
    private static List<String[]> getNativeMethodSignatures(final String internalName, ClassLoader classLoader, final List<String[]> ret) throws IOException {
        ClassReader r = new ClassReader(readByteCode(internalName, classLoader));
        String p = r.getSuperName();
        if (p != null && !p.equals("java/lang/Object"))
            getNativeMethodSignatures(p, classLoader, ret);
        
        r.accept(new EmptyVisitor() {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (Modifier.isNative(access)) {
                    ret.add(new String[] { internalName, name, desc });
                }
                return null;
            }
        }, SKIP_DEBUG | SKIP_FRAMES | SKIP_CODE);
        
        return ret;
    }
    
    private static List<String> getFieldNames(final String internalName, String recurseToInternalName, ClassLoader classLoader, final List<String> ret) throws IOException {
        ClassReader r = new ClassReader(readByteCode(internalName, classLoader));
        String p = r.getSuperName();
        if (p != null && !p.equals("java/lang/Object") && !recurseToInternalName.equals(internalName))
            getFieldNames(p, recurseToInternalName, classLoader, ret);
        
        r.accept(new EmptyVisitor() {
            @Override
            public FieldVisitor visitField(int i, String name, String string1, String string2, Object o) {
                ret.add(name);
                return null;
            }
        }, SKIP_DEBUG | SKIP_FRAMES | SKIP_CODE);
        
        return ret;
    }
    
    private static List<String> getMethodNames(final String internalName, String recurseToInternalName, ClassLoader classLoader, final List<String> ret) throws IOException {
        ClassReader r = new ClassReader(readByteCode(internalName, classLoader));
        String p = r.getSuperName();
        if (p != null && !p.equals("java/lang/Object") && !recurseToInternalName.equals(internalName))
            getMethodNames(p, recurseToInternalName, classLoader, ret);
        
        r.accept(new EmptyVisitor() {
            @Override
            public MethodVisitor visitMethod(int i, String name, String string1, String string2, String[] strings) {
                ret.add(name);
                return null;
            }
        }, SKIP_DEBUG | SKIP_FRAMES | SKIP_CODE);
        
        return ret;
    }
    
    static String getInternalName(Class c) {
        return c.getName().replace('.', '/');
    }
    static URL getClassResource(Class c) throws FileNotFoundException {
        return getClassResource(getInternalName(c), Platform.getClassLoader(c));
    }
    static URL getClassResource(String internalClassName, ClassLoader classLoader) throws FileNotFoundException {
        String p = internalClassName + ".class";
        URL u = classLoader.getResource(p);
        if (u == null)
            throw new FileNotFoundException("Resource '" + p + "'");
        return u;
    }
    static byte[] readByteCode(String classInternalName, ClassLoader classLoader) throws FileNotFoundException, IOException {
        return readBytes(getClassResource(classInternalName, classLoader).openStream(), true);
    }
    static byte[] readBytes(InputStream in, boolean close) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = in.read(b)) > 0) {
            out.write(b, 0, len);
        }
        if (close)
            in.close();
        return out.toByteArray();
    }
    
    public static List<String> getFieldNames(Class c, Class recurseTo) throws IOException {
        return getFieldNames(getInternalName(c), getInternalName(recurseTo), Platform.getClassLoader(c), new ArrayList<String>());
    }
    public static List<String> getMethodNames(Class c, Class recurseTo) throws IOException {
        return getMethodNames(getInternalName(c), getInternalName(recurseTo), Platform.getClassLoader(c), new ArrayList<String>());
    }
    static class EmptyVisitor extends ClassVisitor {

        public EmptyVisitor() {
            super(Opcodes.ASM4);
        }
        public void visit(int i, int i1, String string, String string1, String string2, String[] strings) {
            
        }

        public void visitSource(String string, String string1) {
            
        }

        public void visitOuterClass(String string, String string1, String string2) {
            
        }

        public AnnotationVisitor visitAnnotation(String string, boolean bln) {
            return null;
        }

        public void visitAttribute(Attribute atrbt) {
            
        }

        public void visitInnerClass(String string, String string1, String string2, int i) {
            
        }

        public FieldVisitor visitField(int i, String string, String string1, String string2, Object o) {
            return null;
        }

        public MethodVisitor visitMethod(int i, String string, String string1, String string2, String[] strings) {
            return null;
        }

        public void visitEnd() {
            
        }
        
    }
}
