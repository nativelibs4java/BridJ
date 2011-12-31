package org.bridj;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import org.bridj.util.JNIUtils;
import static org.bridj.demangling.Demangler.*;

/**
 * mvn compile exec:java -o -Dexec.mainClass=org.bridj.ProxyGen
 * @author ochafik
 */
public class ProxyGen {
	
	public static class Toto {
		public native CLong testCLong(CLong a);
		public native SizeT testSizeT(SizeT a);
		public native Pointer<Integer> testIntPtr(Pointer<Integer> p, int a, double b);
	}
	public static void main(String[] args) throws Exception {
		String p = generateProxy(Toto.class);
		System.out.println(p);
	}
	/*
	
	*/
	public static String generateProxy(Class interfaceClass) throws Exception {
		StringBuilder b = new StringBuilder();
		b.append("#include <jni.h>\n");
		b.append("#ifdef _WIN32\n");
		b.append("#define PROXY_EXPORT __declspec(dllexport)\n");
		b.append("#else\n");
		b.append("#define PROXY_EXPORT\n");
		b.append("#endif\n");
		b.append("#define FIND_GLOBAL_CLASS(name) (*env)->NewGlobalRef(env, (*env)->FindClass(env, name))\n");

		
		List<ProxiedMethod> methods = new ArrayList<ProxiedMethod>();
		int iClassName = 0, iMethodName = 0;
		Map<Class, String> classVarNames = new HashMap<Class, String>();
		
		for (Method m : interfaceClass.getDeclaredMethods()) {
			try {
				ProxiedMethod pm = getProxiedMethod(m);
				String classVarName = classVarNames.get(pm.owner);
				if (classVarName == null)
					classVarNames.put(pm.owner, classVarName = "gClass" + (++iClassName));
				pm.classVarName = classVarName;
				pm.methodVarName = "gMethod" + (++iMethodName);
				methods.add(pm);
			} catch (Throwable th) {
				// th.printStackTrace();
			}
		}
		
		b.append("jboolean inited = JNI_FALSE;\n");
		String instanceVarName = "gProxiedInstance";
		b.append("JNIEnv* env = NULL;\n");
		b.append("JavaVM* jvm = NULL;\n");
		b.append("jobject ").append(instanceVarName).append(" = NULL;\n");
		for (String n : classVarNames.values())
			b.append("jclass ").append(n).append(" = NULL;\n");
		
		for (ProxiedMethod pm : methods)
			b.append("jmethodID ").append(pm.methodVarName).append(" = NULL;\n");
		
		String jniInit = "jni_init";
		b.append("void ").append(jniInit).append("(JNIEnv* env) {\n");
		b.append("\tif (inited) return; else inited = JNI_TRUE;\n");
		
		for (Map.Entry<Class, String> e : classVarNames.entrySet()) {
			String n = e.getValue();
			Class c = e.getKey();
			b.append("\t").append(n).append(" = ").append("FIND_GLOBAL_CLASS(\"").append(JNIUtils.getNativeName(c)).append("\");\n");
		}
		for (ProxiedMethod pm : methods) {
			int mods = pm.method.getModifiers();
			b.append("\t").append(pm.methodVarName).append(" = ").
			append("(*env)->").append(Modifier.isStatic(mods) ? "GetStaticMethodID" : "GetMethodID").
			append("(env, ").append(pm.classVarName).append(", \"").append(pm.name).append("\", \"").append(pm.jni_signature).append("\");\n");
		}
		b.append("}\n");
		
		for (ProxiedMethod pm : methods) {
			b.append("PROXY_EXPORT ").append(pm.c_signature).append(" {\n");
			int mods = pm.method.getModifiers();
			
			b.append("\t").append(jniInit).append("();\n");
			b.append("\t");
			if (pm.method.getReturnType() != null && !pm.method.getReturnType().equals(void.class))
				b.append("return ");
			
			StringBuilder r = new StringBuilder();
			
			boolean stat = Modifier.isStatic(mods);
			r.append("(*env)->").append("Call" + (stat ? "Static" : "") + pm.retCapitalized + "Method").append("(env, ");
			if (stat)
				r.append(pm.classVarName);
			else
				r.append(instanceVarName);
			for (String argValue : pm.argValues) {
				r.append(", \n\t\t");
				r.append(argValue);
			}
				
			// TODO...
			r.append("\n\t)");
			b.append(c_signature(pm.method.getReturnType(), r.toString())[2]);
			
			b.append(";\n");
			
			b.append("}\n");
		}
		
		
		return b.toString();
	}
	static ProxiedMethod getProxiedMethod(Method method) {
		ProxiedMethod p = new ProxiedMethod(method);
		
		return p;
	}
	static String jni_capitalized(Class c) {
		if (c == int.class)
			return "Int";
		if (c == long.class)
			return "Long";
		if (c == short.class)
			return "Short";
		if (c == byte.class)
			return "Byte";
		if (c == boolean.class)
			return "Bool";
		if (c == double.class)
			return "Double";
		if (c == float.class)
			return "Float";
		if (c == char.class)
			return "Char";
		if (c == void.class)
			return "Void";
		return "Object";
	}
	static String[] strs(String... vals) {
		return vals;
	}
	static String[] c_signature(Class c, String expr) {
		if (c.isPrimitive())
			return strs("j" + c.toString(), expr, expr);
		if (c == Pointer.class)
			return strs("void*", "createPointerFromIO(env, " + expr + ", NULL)", "getPointerPeer(env, " + expr + ")"); // TODO callIO
		if (c == CLong.class)
			return strs("long", "BoxCLong(env, " + expr + ")", "UnBoxCLong(env, " + expr + ")");
		if (c == SizeT.class)
			return strs("size_t", "BoxSizeT(env, " + expr + ")", "UnBoxSizeT(env, " + expr + ")");
		if (c == TimeT.class)
			return strs("time_t", "BoxTimeT(env, " + expr + ")", "UnBoxTimeT(env, " + expr + ")");
		
		throw new UnsupportedOperationException("Cannot compute C signature for " + c.getName());
	}
	static class ProxiedMethod {
		String methodVarName, classVarName;
		Method method;
		String name;
		String jni_signature, c_signature, c_args;
		Class owner;
		
		List<String> argTypes = new ArrayList<String>(), argNames = new ArrayList<String>(), argValues = new ArrayList<String>();
		String retType, retCapitalized;
		
		public ProxiedMethod(Method method) {
			this.method = method;
			this.name = //method instanceof Constructor ? "<init>" : 
				method.getName();
			this.owner = method.getDeclaringClass();
			
			StringBuffer 
				jni_sig = new StringBuffer("("),
				c_sig = new StringBuffer();
			
			retCapitalized = jni_capitalized(method.getReturnType());
			String[] sigArg = c_signature(method.getReturnType(), "?");
			c_sig.append(retType = sigArg[0]).append(" ").append(name).append("(");
			int i = 0;
			for (Class c : method.getParameterTypes()) {
				jni_sig.append(JNIUtils.getNativeSignature(c));
				if (i > 0)
					c_sig.append(", ");
				
				String argName = "arg" + (i + 1);
				sigArg = c_signature(c, argName);
				String argType = sigArg[0];
				
				argTypes.add(argType);
				argNames.add(argName);
				argValues.add(sigArg[1]);
				
				c_sig.append(argType).append(" ").append(argName);
				i++;
			}
			c_sig.append(")");
			jni_sig.append(")").append(JNIUtils.getNativeSignature(method.getReturnType()));
			this.jni_signature = jni_sig.toString();
			this.c_signature = c_sig.toString();
		}
	}
	
}
