package org.bridj.util;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.objectweb.asm.*;
import static org.objectweb.asm.ClassReader.*;
/**
 * Util class that scavenges through a class' bytecode to retrieve the original order of fields and methods, as defined in the sources (unlike what the reflection APIs return : they don't guarantee the order).
 * @author ochafik
 */
public class BytecodeAnalyzer {
    final List<String> fieldNames, methodNames;
    final BytecodeAnalyzer parentAnalyzer;
    public BytecodeAnalyzer(Class c, Class recurseTo) throws IOException {
        this(c, recurseTo, EnumSet.allOf(Features.class));
    }
    public BytecodeAnalyzer(Class c, Class recurseTo, EnumSet<Features> features) throws IOException {
        this(c, readBytes(getClassResource(c).openStream(), true), recurseTo, features);
    }
    public enum Features {
        FieldNames, MethodNames
    }
    public BytecodeAnalyzer(Class c, byte[] bytecode, Class recurseTo, final EnumSet<Features> features) throws IOException {
        fieldNames = new ArrayList<String>();
        methodNames = new ArrayList<String>();
        ClassReader r = new ClassReader(bytecode);
        ClassVisitor v = new EmptyVisitor() {
            final boolean getFieldNames = features.contains(Features.FieldNames);
            final boolean getMethodNames = features.contains(Features.MethodNames);
            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                if (getFieldNames)
                    fieldNames.add(name);
                return null;
            }
            @Override
            public MethodVisitor visitMethod(int i, String name, String string1, String string2, String[] strings) {
                if (getMethodNames)
                    methodNames.add(name);
                return null;
            }
        };
        r.accept(v, SKIP_DEBUG | SKIP_FRAMES | SKIP_CODE);
        
        Class p;
        if (recurseTo != c && (p = c.getSuperclass()) != null && p != recurseTo)
            parentAnalyzer = new BytecodeAnalyzer(p, recurseTo, features);
        else
            parentAnalyzer = null;
    }

    static List<String> add(List<String> a, List<String> b) {
        List<String> ret = new ArrayList<String>(a.size() + b.size());
        ret.addAll(a);
        ret.addAll(b);
        return ret;
    }
    public List<String> getFieldNames() {
        if (parentAnalyzer != null)
            return add(parentAnalyzer.getFieldNames(), fieldNames);
        
        return fieldNames;
    }

    public List<String> getMethodNames() {
        if (parentAnalyzer != null)
            return add(parentAnalyzer.getMethodNames(), methodNames);
        
        return methodNames;
    }
    
    static URL getClassResource(Class c) throws FileNotFoundException {
        String n = c.getName();
        String p = n.replace('.', '/') + ".class";
        URL u = c.getClassLoader().getResource(p);
        if (u == null)
            throw new FileNotFoundException("Resource '" + p + "'");
        return u;
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
        return new BytecodeAnalyzer(c, recurseTo, EnumSet.of(Features.FieldNames)).getFieldNames();
    }
    public static List<String> getMethodNames(Class c, Class recurseTo) throws IOException {
        return new BytecodeAnalyzer(c, recurseTo, EnumSet.of(Features.MethodNames)).getMethodNames();
    }
    static class EmptyVisitor implements ClassVisitor {

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
