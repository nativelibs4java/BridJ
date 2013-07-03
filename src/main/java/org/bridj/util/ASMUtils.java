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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import org.bridj.Pointer;
import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Utilities for <a href="http://asm.ow2.org/">ASM</a>.
 *
 * @author ochafik
 */
public class ASMUtils {

    public static String typeDesc(java.lang.reflect.Type t) {
        if (t instanceof Class) {
            Class c = (Class) t;
            if (c == Pointer.class) {
                return "Pointer";
            }
            if (c.isPrimitive()) {
                String s = c.getSimpleName();
                return Character.toUpperCase(s.charAt(0)) + s.substring(1);
            } else if (c.isArray()) {
                return typeDesc(c.getComponentType()) + "Array";
            }
            return c.getName().replace('.', '_');
        } else {
            ParameterizedType p = (ParameterizedType) t;
            StringBuilder b = new StringBuilder(typeDesc(p.getRawType()));
            for (java.lang.reflect.Type pp : p.getActualTypeArguments()) {
                b.append("_").append(typeDesc(pp));
            }
            return b.toString();
        }
    }

    public static void addSuperCall(ClassVisitor cv, String superClassInternalName) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClassInternalName, "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    public static <T> Class<? extends T> createSubclassWithSynchronizedNativeMethodsAndNoStaticFields(Class<T> original, ClassDefiner classDefiner) throws IOException {
        String suffix = "$SynchronizedNative";
        final String originalInternalName = JNIUtils.getNativeName(original);
        final String synchronizedName = original.getName() + suffix;
        final String synchronizedInternalName = originalInternalName + suffix;

        ClassWriter classWriter = new ClassWriter(0);
        //TraceClassVisitor traceVisitor = new TraceClassVisitor(classWriter, new PrintWriter(System.out));
        ClassVisitor cv = new ClassVisitor(ASM4, classWriter) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, synchronizedInternalName, null, originalInternalName, new String[0]);
                addSuperCall(cv, originalInternalName);
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                // Do nothing.
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (!Modifier.isNative(access)) {
                    return null;
                }

                return super.visitMethod(access | Modifier.SYNCHRONIZED, name, desc, signature, exceptions);
            }
        };

        ClassReader classReader = new ClassReader(original.getName());
        classReader.accept(cv, 0);
        return (Class<? extends T>) classDefiner.defineClass(synchronizedName, classWriter.toByteArray());
    }
}
