/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cpp;

import java.util.Stack;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

import org.bridj.NativeObject;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Runtime;
import org.bridj.ann.Template;
import org.bridj.cpp.mfc.MFCRuntime;
import org.bridj.BridJ;


/**
 * Representation of a C++ type (including template parameters, which can be types or constants).
 * @author Olivier
 */
public class CPPType implements ParameterizedType {
	private final Type[] actualTypeArguments;
    private final Type ownerType;
    private final Type rawType;
    private final Object[] templateParameters;
	
    public CPPType(Type ownerType, Type rawType, Object... templateParameters) {
		this.ownerType = ownerType;
		this.actualTypeArguments = getTypes(templateParameters);
		this.rawType = rawType;
		this.templateParameters = templateParameters;
    }
    public CPPType(Type rawType, Object... templateParameters) {
        this(null, rawType, templateParameters);
    }
    
	private static Type[] getTypes(Object[] objects) {
		List<Type> ret = new ArrayList<Type>(objects.length);
		for (int i = 0, n = objects.length; i < n; i++) {
			Object o = objects[i];
			if (o instanceof Type)
				ret.add((Type)o);
		}
		return ret.toArray(new Type[ret.size()]);
	}
    
	static Object[] cons(Class firstClass, Object... flattenedClassesAndParams) {
		Object[] a = new Object[flattenedClassesAndParams.length + 1];
		a[0] = firstClass;
		System.arraycopy(flattenedClassesAndParams, 0, a, 1, flattenedClassesAndParams.length);
		return a;
	}
    public static Type getCPPType(Object... flattenedClassesAndParams) {
    		int[] position = new int[] { 0 };
    		Type t = parseCPPType(flattenedClassesAndParams, position);
    		if (position[0] < flattenedClassesAndParams.length)
    			parseError("Unexpected trailing parameters", flattenedClassesAndParams, position);
    		
    		return t;
    }
    static void parseError(String message, Object[] flattenedClassesAndParams, int[] position) {
    		throw new IllegalArgumentException("Error while parsing C++ type in " + Arrays.asList(flattenedClassesAndParams) + " at offset " + position[0] + " : " + message);
    }
    static void notEOF(String message, Object[] flattenedClassesAndParams, int[] position) {
    		if (position[0] >= flattenedClassesAndParams.length)
    			throw new IllegalArgumentException("EOF while parsing C++ type in " + Arrays.asList(flattenedClassesAndParams) + " at offset " + position[0] + " : " + message);
    }
    
    static Type parseCPPType(Object[] flattenedClassesAndParams, int[] position) {
    		notEOF("expecting class", flattenedClassesAndParams, position);
    		Object oc = flattenedClassesAndParams[position[0]];
    		if (!(oc instanceof Class))
    			parseError("expected class", flattenedClassesAndParams, position);
    		Class<?> c = (Class)oc;
    		position[0]++;
    		Template t = c.getAnnotation(Template.class);
    		
    		Class<?>[] paramTypes = t == null ? null : t.value();
    		int nParams = paramTypes == null ? 0 : paramTypes.length;
    		Object[] params = new Object[nParams];
    		for (int iParam = 0; iParam < nParams; iParam++) {
    			notEOF("expecting param " + iParam + " for template " + c.getName(), flattenedClassesAndParams, position);
    			Object param = flattenedClassesAndParams[position[0]];
    			Type paramType = paramTypes[iParam];
    			if (paramType.equals(Class.class) && param.getClass().equals(Class.class))
    				param = parseCPPType(flattenedClassesAndParams, position);
    			else {
    				if (!((Class)paramType).isInstance(param))
					parseError("bad type for template param " + iParam + " : expected a " + paramType + ", got " + param, flattenedClassesAndParams, position);
    				position[0]++;
    			}
			params[iParam] = param;
    		}
    		return nParams == 0 ? c : new CPPType(c, params);
    }
    
    //@Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments.clone();
    }

    //@Override
    public java.lang.reflect.Type getOwnerType() {
        return ownerType;
    }

    //@Override
    public java.lang.reflect.Type getRawType() {
        return rawType;
    }
    
	public Object[] getTemplateParameters() {
		return templateParameters.clone();
	}
	
	@Override
	public int hashCode() {
		int h = getRawType().hashCode();
		if (getOwnerType() != null)
			h ^= getOwnerType().hashCode();
		for (int i = 0, n = templateParameters.length; i < n; i++)
			h ^= templateParameters[i].hashCode();
		return h;
	}
	
	static boolean eq(Object a, Object b) {
		if ((a == null) != (b == null))
			return false;
		if (a != null && !a.equals(b))
			return false;
		return true;
	}
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof CPPType))
			return false;
		
		CPPType t = (CPPType)o;
		if (!eq(getRawType(), t.getRawType()))
			return false;
		if (!eq(getOwnerType(), t.getOwnerType()))
			return false;
		
		Object[] tp = t.templateParameters;
		if (templateParameters.length != tp.length)
			return false;
		
		for (int i = 0, n = templateParameters.length; i < n; i++)
			if (!eq(templateParameters[i], tp[i]))
				return false;
			
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (getOwnerType() != null)
			b.append(getOwnerType()).append('.');
		
		b.append(getRawType());
		int n = templateParameters.length;
		if (n != 0) {
			b.append('<');
			for (int i = 0; i < n; i++) {
				if (i > 0)
					b.append(", ");
				b.append(templateParameters[i]);
			}
			b.append('>');
		}
		return b.toString();
	}
}
