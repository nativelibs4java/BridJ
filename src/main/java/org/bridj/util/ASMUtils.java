/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;
import java.io.IOException;
import java.lang.reflect.Modifier;
import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;

/**
 *
 * @author ochafik
 */
public class ASMUtils {
    public static class Sub extends ASMUtils {
        public Sub() {
            super();
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
                if (!Modifier.isNative(access))
                    return null;
                
                return super.visitMethod(access | Modifier.SYNCHRONIZED, name, desc, signature, exceptions);
            }
        };
                
        ClassReader classReader = new ClassReader(original.getName());
        classReader.accept(cv, 0);
        return (Class<? extends T>) classDefiner.defineClass(synchronizedName, classWriter.toByteArray());
    }
}
