package org.bridj.demangling;

import java.io.EOFException;
import org.bridj.ann.Convention.Style;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.Arrays;
import org.bridj.AbstractBridJRuntime;
import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.FlagSet;
import org.bridj.JNI;
import org.bridj.NativeLibrary;
import org.bridj.NativeObject;
import org.bridj.Platform;

import org.bridj.Pointer;
import org.bridj.TimeT;
import org.bridj.SizeT;
import org.bridj.CLong;
import org.bridj.ValuedEnum;
import org.bridj.ann.Constructor;
import org.bridj.ann.Ptr;
import org.bridj.ann.Convention;
import org.bridj.ann.Name;
import org.bridj.ann.Template;
import org.bridj.util.AnnotationUtils;
import static org.bridj.util.AnnotationUtils.*;
import org.bridj.util.DefaultParameterizedType;
import org.bridj.util.Utils;

/*
mvn compile exec:java -Dexec.mainClass=org.bridj.demangling.Demangler -e "-Dexec.args=?method_name@class_name@@QAEPAPADPAD0@Z"

??4Class1@TestLib@@QAEAAV01@ABV01@@Z
?f@Class1@TestLib@@QAEPADPAD0@Z

class TestLib::Class1 & TestLib::Class1::operator=(class TestLib::Class1 const &)
char * TestLib::Class1::f(char *,char *)
*/
/**
 * Base class and core structures for symbol demanglers (typically, for C++ symbols).
 */
public abstract class Demangler {
	
	public static void main(String[] args) {
//        try {
////            String s = "?f@@YA?AW4E@@W41@@Z";
//            String s = "?f@@YAPADPADPAF1@Z"; // "byte* f(byte*, short*, short*)"
//            //String s = "?m@C@@SAPAV1@XZ";
//            MemberRef mr = new VC9Demangler(null, s).parseSymbol();
//            System.out.println(mr);
//        } catch (DemanglingException ex) {
//            Logger.getLogger(Demangler.class.getName()).error(null, ex);
//        }
        for (String arg : args) {
			try {
				System.out.println("VC9: " + new VC9Demangler(null, arg).parseSymbol());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				System.out.println("GCC4: " + new GCC4Demangler(null, arg).parseSymbol());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public interface Annotations {
		<A extends Annotation> A getAnnotation(Class<A> c);	
        boolean isAnnotationPresent(Class<? extends Annotation> c);
	}
	public static Annotations annotations(final Annotation[] aa) {
		return new Annotations() {
			//@Override
			public <A extends Annotation> A getAnnotation(Class<A> c) {
                if (aa == null)
                    return null;
                
				for (Annotation a : aa)
					if (c.isInstance(a))
						return (A)a;
				return null;
			}

            public boolean isAnnotationPresent(Class<? extends Annotation> c) {
                return AnnotationUtils.isAnnotationPresent(c, aa);
            }
            
		};
	}

	public static Annotations annotations(final Type e) {
        return annotations((AnnotatedElement)Utils.getClass(e));
	}
	public static Annotations annotations(final AnnotatedElement e) {
		return new Annotations() {
			//@Override
			public <A extends Annotation> A getAnnotation(Class<A> c) {
				return e.getAnnotation(c);
			}

            public boolean isAnnotationPresent(Class<? extends Annotation> c) {
                return AnnotationUtils.isAnnotationPresent(c, e);
            }
            
		};
	}
	
	public class DemanglingException extends Exception {
		public DemanglingException(String mess) {
			this(mess, null);
		}
        public DemanglingException(String mess, Throwable cause) {
			super(mess + " (in symbol '" + str + "')", cause);
		}
	}
	public abstract MemberRef parseSymbol() throws DemanglingException;

	protected final String str;
	protected final int length;
	protected int position = 0;
	protected final NativeLibrary library;
	public Demangler(NativeLibrary library, String str) {
		this.str = str;
		this.length = str.length();
		this.library = library;
	}
	
	public String getString() {
		return str;
	}
	
	protected void expectChars(char... cs) throws DemanglingException {
		for (char c : cs) {
			char cc = consumeChar();
			if (cc != c)
				throw error("Expected char '" + c + "', found '" + cc + "'");
		}
	}
	protected void expectAnyChar(char... cs) throws DemanglingException {
		char cc = consumeChar();
		for (char c : cs) {
			if (cc == c)
				return;
		}
		throw error("Expected any of " + Arrays.toString(cs) + ", found '" + cc + "'");
	}
	public static StringBuilder implode(StringBuilder b, Object[] items, String sep) {
		return implode(b, Arrays.asList(items), sep);
	}
	public static StringBuilder implode(StringBuilder b, Iterable<?> items, String sep) {
		boolean first = true;
		for (Object item : items) {
			if (first)
				first = false;
			else
				b.append(sep);
			b.append(item);
		}
		return b;
	}
	protected char peekChar() {
		return peekChar(1);
	}
	protected char peekChar(int dist) {
		int p = position + dist - 1;
		return p >= length ? 0 : str.charAt(p);
	}
	protected char lastChar() {
		return position == 0 ? 0 : str.charAt(position - 1);
	}
	protected char consumeChar() throws DemanglingException {
		char c = peekChar();
		if (c != 0)
			position++;
        else
            throw new DemanglingException("Reached end of string '" + str + "'");
		return c;
	}
    protected boolean consumeCharsIf(char... nextChars) {
        int initialPosition = position;
        try {
            for (char c : nextChars) {
                char cc = consumeChar();
                if (cc != c) {
                    position = initialPosition;
                    return false;
                }
            }
            return true;
        } catch (DemanglingException ex) {
            position = initialPosition;
            return false;
        }
    }
	protected boolean consumeCharIf(char... allowedChars) {
		char c = peekChar();
		for (char allowedChar : allowedChars)
			if (allowedChar == c) {
				position++;
				return true;
			}
		return false;
	}
	protected DemanglingException error(int deltaPosition) {
		return error(null, deltaPosition);
	}
    protected DemanglingException error(String mess) {
        return error(mess, -1);
    }
	protected DemanglingException error(String mess, int deltaPosition) {
		StringBuilder err = new StringBuilder(position + 1);
		int position = this.position + deltaPosition;
		for (int i = 0; i < position; i++)
			err.append(' ');
		err.append('^');
		return new DemanglingException("Parsing error at position " + position + (mess == null ? "" : ": " + mess) + " \n\t" + str + "\n\t" + err);
	}
	
	public interface TemplateArg {
		public boolean matchesParam(Object param, Annotations annotations);
	}

    public static String getMethodName(Method method) {
        Name nameAnn = method.getAnnotation(Name.class);
        if (nameAnn != null)
            return nameAnn.value();

        return method.getName();
    }

	public static class Symbol {
		final String symbol;
		long address;
		MemberRef ref;
		boolean refParsed;
		final NativeLibrary library;
		
		public Symbol(String symbol, NativeLibrary library) {
			this.symbol = symbol;
			this.library = library;
			
		}

        public NativeLibrary getLibrary() {
            return library;
        }

        public MemberRef getRef() {
            return ref;
        }

        public Style getStyle() {
            return style;
        }

        public String getSymbol() {
            return symbol;
        }

        @Override
        public String toString() {
            return symbol + (ref == null ? "" : " (" + ref + ")");
        }


        public long getAddress() {
            if (address == 0)
                address = library.getSymbolAddress(symbol);
            return address;
        }

        public void setAddress(long address) {
            this.address = address;
        }
		
		private Convention.Style style;
		public Convention.Style getInferredCallingConvention() {
			if (style == null) {
				//System.out.println("Symbol " + symbol + " stdcall = " + symbol.matches("_.*?@\\d+"));
				if (symbol.matches("_.*?@\\d+"))
					style = Convention.Style.StdCall;
				else if (symbol.matches("@.*?@\\d+"))
					style = Convention.Style.FastCall;
				else if (Platform.isWindows() && symbol.contains("@"))
					try {
						MemberRef mr = getParsedRef();
						if (mr != null)
							style = mr.callingConvention;
					} catch (Throwable th) {}
			}
			return style;
		}
        public boolean matches(Method method) {
			if (!symbol.contains(getMethodName(method)))
				return false;
		
			//if (!Modifier.isStatic(method.getModifiers()) && !symbol.contains(method.getDeclaringClass().getSimpleName()))
			//	return false;
		
			parse();

            try {
                if (ref != null) {
                	boolean res = ref.matches(method);
                	if (!res && BridJ.debug) {
                		System.err.println("Symbol " + symbol + " was a good candidate but expected demangled signature " + ref + " did not match the method " + method);
                	}
                    return res;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
		}
		public MemberRef getParsedRef() {
			parse();
			return ref;
		}
		synchronized void parse() { 
			if (!refParsed) {
				try {
					ref = library.parseSymbol(symbol);
				} catch (DemanglingException ex) {
                    if (BridJ.debug)
						ex.printStackTrace();
					if (BridJ.verbose)
                        BridJ.warning("Symbol parsing failed : " + ex.getMessage());
				}
				refParsed = true;
			}
		}

        public String getName() {
            return symbol;
        }

        public boolean matchesVirtualTable(Class<?> type) {
			if (!symbol.contains(type.getSimpleName()))
				return false;
		
			parse();

            try {
                if (ref != null) {
                	return ref.matchesVirtualTable(type);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
		}
		public boolean matchesConstructor(Type type, java.lang.reflect.Constructor<?> constr) {
			if (!symbol.contains(Utils.getClass(type).getSimpleName()))
				return false;
		
			parse();

            try {
                if (ref != null) {
                	return ref.matchesConstructor(type, constr);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
		}
		public boolean matchesDestructor(Class<?> type) {
			if (!symbol.contains(type.getSimpleName()))
				return false;
		
			parse();

            try {
                if (ref != null) {
                	return ref.matchesDestructor(type);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
		}

		public boolean isVirtualTable() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	
	public static abstract class TypeRef implements TemplateArg {
		public abstract StringBuilder getQualifiedName(StringBuilder b, boolean generic);
		public boolean matchesParam(Object param, Annotations annotations) {
			return (param instanceof Type) && matches((Type)param, annotations);
		}
		public boolean matches(Type type, Annotations annotations) {
            String thisName = getQualifiedName(new StringBuilder(), false).toString();
            Class typeClass = getTypeClass(type);
            String typeName = typeClass.getSimpleName();
			return thisName.equals(typeName) || thisName.equals(typeClass.getName());
		}

        @Override
        public boolean equals(Object obj) {
            return toString().equals(obj.toString());
        }
		
	}

	public static class Constant implements TemplateArg {
		Object value;

        public Constant(Object value) {
            this.value = value;
        }
        public boolean matchesParam(Object param, Annotations annotations) {
			return value.equals(param);
		}

        @Override
        public String toString() {
            return value.toString();
        }
	}

	public static class NamespaceRef extends TypeRef {
		Object[] namespace;
        public NamespaceRef(Object... namespace) {
            this.namespace = namespace;
        }
		public StringBuilder getQualifiedName(StringBuilder b, boolean generic) {
			return implode(b, namespace, ".");
		}

        @Override
        public String toString() {
            return getQualifiedName(new StringBuilder(), true).toString();
        }

	}

    public static class PointerTypeRef extends TypeRef {
        
        public TypeRef pointedType;

        public PointerTypeRef(TypeRef pointedType) {
            this.pointedType = pointedType;
        }

        @Override
        public StringBuilder getQualifiedName(StringBuilder b, boolean generic) {
            return b.append("org.bridj.Pointer");
        }
        @Override
        public String toString() {
        		return pointedType + "*";
        }
    }

    protected static TypeRef pointerType(TypeRef tr) {
        return new PointerTypeRef(tr);
    }
    protected static TypeRef classType(final Class<?> c, Class<? extends Annotation>... annotations) {
        return classType(c, null, annotations);
    }
    protected static TypeRef classType(final Class<?> c, final java.lang.reflect.Type[] genericTypes, Class<? extends Annotation>... annotations) {
		JavaTypeRef tr = new JavaTypeRef();
        if (genericTypes == null)
            tr.type = c;
        else
            tr.type = new DefaultParameterizedType(c, genericTypes);
            
		tr.annotations = annotations;
		return tr;
	}
    protected static TypeRef simpleType(String name, TemplateArg... args) {
		return new ClassRef(new Ident(name, args));
	}
    protected static TypeRef simpleType(Ident ident) {
		return new ClassRef(ident);
	}
    static Class<?> getTypeClass(Type type) {
		
		if (type instanceof Class<?>)
			return (Class<?>)type;
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)type;
			Class<?> c = (Class<?>)pt.getRawType();
			if (ValuedEnum.class.isAssignableFrom(c)) {
				Type[] types = pt.getActualTypeArguments();
				if (types == null || types.length != 1)
					c = int.class;
				else
					c = getTypeClass(pt.getActualTypeArguments()[0]);
			}
			return c;
		}
        if (type instanceof GenericArrayType) {
            if (Object.class.isAssignableFrom(getTypeClass(((GenericArrayType)type).getGenericComponentType())))
                return Object[].class;
        }
        throw new UnsupportedOperationException("Unknown type type : " + type.getClass().getName());
	}
	static boolean equivalentTypes(Type a, Class ac, Annotations aAnnotations, Type b, Class bc, Annotations bAnnotations) {
		if (a.equals(b))
			return true;
		
		int as = getIntegralSize(a, ac, aAnnotations);
		int bs = getIntegralSize(b, bc, bAnnotations);
		
		//System.out.println("a = " + a + ", b = " + b + ", as = " + as + ", bs = " + bs + ", equiv = " + (as != -1 && as == bs));
		return as != -1 && as == bs;
	}
	static int getIntegralSize(Type type, Class typec, Annotations annotations) {
		if (type == int.class || type == Integer.class)
			return 4;
		if (type == long.class || type == Long.class) {
			if (annotations != null) {
				if (annotations.isAnnotationPresent(Ptr.class))
					return SizeT.SIZE;
				if (annotations.isAnnotationPresent(org.bridj.ann.CLong.class))
					return CLong.SIZE;
			}
			return 8;
		}
		if (type == CLong.class)
			return CLong.SIZE;
		if (type == SizeT.class)
			return SizeT.SIZE;
		if (type == TimeT.class)
			return TimeT.SIZE;
		if (type == byte.class || type == Byte.class)
			return 1;
		if (type == char.class || type == Character.class || type == short.class || type == Short.class)
			return 2;
		if (ValuedEnum.class.isAssignableFrom(typec))
			return 4;
		if (Pointer.class.isAssignableFrom(typec))
			return SizeT.SIZE;
		return -1;
	}
	public static boolean equivalentTypes(Type a, Type b) {
		return equivalentTypes(a, getTypeClass(a), null, b, getTypeClass(b), null);
	}
	
	
	public static class JavaTypeRef extends TypeRef {

		java.lang.reflect.Type type;
        Class<? extends Annotation>[] annotations;
		
		@Override
		public StringBuilder getQualifiedName(StringBuilder b, boolean generic) {
			return b.append(getTypeClass(this.type).getName());
		}
		@Override
		public boolean matches(Type type, Annotations annotations) {
            Class<?> tc = getTypeClass(this.type);
            Class<?> typec = getTypeClass(type);
            if (tc == typec || tc.equals(typec))
                return true;
            
            if ((type == Long.TYPE && Pointer.class.isAssignableFrom(tc)) ||
                    (Pointer.class.isAssignableFrom(typec) && tc == Long.TYPE))
                return true;
                
            return equivalentTypes(type, typec, annotations, this.type, tc, null); // TODO isAssignableFrom or the opposite, depending on context
		}

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (Class<?> ann : annotations)
                b.append(ann.getSimpleName()).append(' ');
            b.append((type instanceof Class<?>) ? ((Class<?>)type).getSimpleName() : type.toString());
            return b.toString();
        }
		
	}
    public interface IdentLike {

    }
    public static class Ident implements IdentLike {
		Object simpleName;
		TemplateArg[] templateArguments;

		public Ident(String simpleName, TemplateArg... templateArguments) {
            this.simpleName = simpleName;
            this.templateArguments = templateArguments;
		}

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Ident))
                return false;

            Ident ident = (Ident)o;
            if (!simpleName.equals(ident.simpleName))
                return false;

            int n = templateArguments.length;
            if (ident.templateArguments.length != n)
                return false;

            for (int i = 0; i < n; i++)
                if (!templateArguments[i].equals(ident.templateArguments[i]))
                    return false;

            return true;
        }


        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();

            b.append(simpleName);
            appendTemplateArgs(b, templateArguments);
            return b.toString();
        }
    }
	public static class ClassRef extends TypeRef {
		private TypeRef enclosingType;
		//private Object simpleName;
		//TemplateArg[] templateArguments;
        final Ident ident;

		public ClassRef(Ident ident) {
            this.ident = ident;
		}
		public StringBuilder getQualifiedName(StringBuilder b, boolean generic) {
			if (getEnclosingType() instanceof ClassRef) {
				getEnclosingType().getQualifiedName(b, generic).append('$');
			} else if (getEnclosingType() instanceof NamespaceRef) {
				getEnclosingType().getQualifiedName(b, generic).append('.');
			}
			b.append(ident.simpleName);
			if (generic && ident.templateArguments != null) {
				int args = 0;
				for (int i = 0, n = ident.templateArguments.length; i < n; i++) {
					TemplateArg arg = ident.templateArguments[i];
					if (!(arg instanceof TypeRef))
						continue;
					
					if (args == 0)
						b.append('<');
					else
						b.append(", ");
					((TypeRef)arg).getQualifiedName(b, generic);
					args++;
				}
				if (args > 0)
					b.append('>');
			}
			return b;
		}

        public Ident getIdent() {
            return ident;
        }


		public void setEnclosingType(TypeRef enclosingType) {
			this.enclosingType = enclosingType;
		}

		public TypeRef getEnclosingType() {
			return enclosingType;
		}

        @Override
        public boolean matches(Type type, Annotations annotations) {
        	if (getTypeClass(type).getSimpleName().equals(ident.simpleName))
                return true;
            
            return false;
        }



        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();

            if (enclosingType != null)
                b.append(enclosingType).append('.');

            b.append(ident);
            return b.toString();
        }


	}
    static void appendTemplateArgs(StringBuilder b, Object[] params) {
        if (params == null || params.length == 0)
            return;
        appendArgs(b, '<', '>', params);
    }
    static void appendArgs(StringBuilder b, char pre, char post, Object[] params) {
        b.append(pre);
        if (params != null)
			for (int i = 0; i < params.length; i++) {
				if (i != 0)
					b.append(", ");
				b.append(params[i]);
			}
        b.append(post);
    }
	public static class FunctionTypeRef extends TypeRef {
		MemberRef function;

        public FunctionTypeRef(MemberRef function) {
            this.function = function;
        }
		@Override
		public StringBuilder getQualifiedName(StringBuilder b, boolean generic) {
			// TODO Auto-generated method stub
			return null;
		}


        @Override
        public String toString() {
            return function.toString();
        }
	}
    public enum SpecialName implements IdentLike {
        Constructor("", true, true),
        SpecialConstructor("", true, true),
        Destructor("", true, true),
        SelfishDestructor("", true, true),
        DeletingDestructor("", true, true),
        New("new", true, true),
        Delete("delete", true, true),
        NewArray("new[]", true, true),
        DeleteArray("delete[]", true, true),
        VFTable("vftable", false, true),
        VBTable("vbtable", false, true),
        VCall("vcall", false, false), // What is that ???
        TypeOf("typeof", false, false),
        ScalarDeletingDestructor("'scalar deleting destructor'", true, true),
        VectorDeletingDestructor("'vector deleting destructor'", true, true),
        
        OperatorAssign("operator=", true, true),
        OperatorRShift("operator>>", true, true),
        OperatorDivideAssign("operator/=", true, true),
        OperatorModuloAssign("operator%=", true, true),
        OperatorRShiftAssign("operator>>=", true, true),
        OperatorLShiftAssign("operator<<=", true, true),
        OperatorBitAndAssign("operator&=", true, true),
        OperatorBitOrAssign("operator|=", true, true),
        OperatorXORAssign("operator^=", true, true),
        OperatorLShift("operator<<", true, true),
        OperatorLogicNot("operator!", true, true),
        OperatorEquals("operator==", true, true),
        OperatorDifferent("operator!=", true, true),
        OperatorSquareBrackets("operator[]", true, true),
        OperatorCast("'some cast operator'", true, true),
        OperatorArrow("operator->", true, true),
        OperatorMultiply("operator*", true, true),
        OperatorIncrement("operator++", true, true),
        OperatorDecrement("operator--", true, true),
        OperatorSubstract("operator-", true, true),
        OperatorAdd("operator+", true, true),
        OperatorBitAnd("operator&=", true, true),
        /// Member pointer selector
        OperatorArrowStar("operator->*", true, true),
        OperatorDivide("operator/", true, true),
        OperatorModulo("operator%", true, true),
        OperatorLower("operator<", true, true),
        OperatorLowerEquals("operator<=", true, true),
        OperatorGreater("operator>", true, true),
        OperatorGreaterEquals("operator>=", true, true),
        OperatorComma("operator,", true, true),
        OperatorParenthesis("operator()", true, true),
        OperatorBitNot("operator~", true, true),
        OperatorXOR("operator^", true, true),
        OperatorBitOr("operator|", true, true),
        OperatorLogicAnd("operator&&", true, true),
        OperatorLogicOr("operator||", true, true),
        OperatorMultiplyAssign("operator*=", true, true),
        OperatorAddAssign("operator+=", true, true),
        OperatorSubstractAssign("operator-=", true, true);

        private SpecialName(String name, boolean isFunction, boolean isMember) {
            this.name = name;
            this.isFunction = isFunction;
            this.isMember = isMember;
        }
        final String name;
        final boolean isFunction;
        final boolean isMember;

        @Override
        public String toString() {
            return name;
        }

        public boolean isFunction() {
            return isFunction;
        }

        public boolean isMember() {
            return isMember;
        }




    }
	public static class MemberRef {

        private Integer argumentsStackSize;
		private TypeRef enclosingType;
		private TypeRef valueType;
		private IdentLike memberName;
		Boolean isStatic, isProtected, isPrivate;
		public int modifiers;
		public TypeRef[] paramTypes, throwTypes;
		TemplateArg[] templateArguments;
        public Style callingConvention;

        public void setTemplateArguments(TemplateArg[] templateArguments) {
            this.templateArguments = templateArguments;
        }

        public Integer getArgumentsStackSize() {
            return argumentsStackSize;
        }

        public void setArgumentsStackSize(Integer argumentsStackSize) {
            this.argumentsStackSize = argumentsStackSize;
        }
       
        protected boolean matchesEnclosingType(Type type) {
			return getEnclosingType() != null && getEnclosingType().matches(type, annotations(type));
		}

		protected boolean matchesVirtualTable(Type type) {
			return memberName == SpecialName.VFTable && matchesEnclosingType(type);
		}
        protected boolean matchesConstructor(Type type, java.lang.reflect.Constructor<?> constr) {
            if (memberName != SpecialName.Constructor)
                return false;

            if (!matchesEnclosingType(type))
                return false;

            Template temp = Utils.getClass(type).getAnnotation(Template.class);
            Annotation[][] anns = constr.getParameterAnnotations();
            Type[] parameterTypes = constr.getGenericParameterTypes();

            int overrideOffset = Utils.getEnclosedConstructorParametersOffset(constr);
            if (!matchesArgs(parameterTypes, anns, overrideOffset + (temp == null ? 0 : temp.value().length)))
            	return false;

            return true;
		}
        protected boolean matchesDestructor(Type type) {
        		return memberName == SpecialName.Destructor && matchesEnclosingType(type);
		}
        static boolean hasInstance(Object[] array, Class<? extends Annotation>... cs) {
            for (Object o : array)
                for (Class<?> c : cs)
                    if (c.isInstance(o))
                        return true;
            return false;
        }
        static int getArgumentsStackSize(Method method) {
            int total = 0;
            Type[] paramTypes = method.getGenericParameterTypes();
            Annotation[][] anns = method.getParameterAnnotations();
            for (int iArg = 0, nArgs = paramTypes.length; iArg < nArgs; iArg++) {
                Class<?> paramType = getTypeClass(paramTypes[iArg]);
                if (paramType == int.class)
                    total += 4;
                else if (paramType == long.class) {
                    Annotation[] as = anns[iArg];
                    if (isAnnotationPresent(Ptr.class, as) || isAnnotationPresent(org.bridj.ann.CLong.class, as))
                    //if (hasInstance(anns[iArg], Ptr.class, CLong.class))
                        total += Pointer.SIZE;
                    else
                        total += 8;
                } else if (paramType == float.class)
                    total += 4;
                else if (paramType == double.class)
                    total += 8;
                else if (paramType == byte.class)
                    total += 1;
                else if (paramType == char.class)
                    total += Platform.WCHAR_T_SIZE;
                else if (paramType == CLong.class)
                    total += Platform.CLONG_SIZE;
                else if (paramType == SizeT.class)
                    total += Platform.SIZE_T_SIZE;
                else if (paramType == TimeT.class)
                    total += Platform.TIME_T_SIZE;
                else if (paramType == short.class)
                    total += 2;
                else if (paramType == boolean.class)
                    total += 1;
                else if (Pointer.class.isAssignableFrom(paramType))
                    total += Pointer.SIZE;
                else if (NativeObject.class.isAssignableFrom(paramType))
                    total += ((CRuntime)BridJ.getRuntime(paramType)).sizeOf(paramTypes[iArg], null);
                else if (FlagSet.class.isAssignableFrom(paramType))
                    total += 4; // TODO
                else
                    throw new RuntimeException("Type not handled : " + paramType.getName());
            }
            return total;
        }
		protected boolean matches(Method method) {

			if (memberName instanceof SpecialName)
            	return false; // use matchesConstructor... 

            if (!matchesEnclosingType(method))
                return false;
		
            return matchesSignature(method);
        }
        public boolean matchesEnclosingType(Method method) {
            TypeRef et = getEnclosingType();
            if (et == null)
                return true;
            
            Annotations annotations = annotations(method);
            Class dc = method.getDeclaringClass();
            do {
                if (et.matches(dc, annotations))
                    return true;

                dc = dc.getSuperclass();
            } while (dc != null && dc != Object.class);
            
            return false;
        }
        public boolean matchesSignature(Method method) {
        	
            if (getArgumentsStackSize() != null && getArgumentsStackSize().intValue() != getArgumentsStackSize(method))
                return false;
            
			if (getMemberName() != null && !getMemberName().toString().equals(getMethodName(method)))
				return false;
			
			if (getValueType() != null && !getValueType().matches(method.getReturnType(), annotations(method)))
				return false;
			
			Template temp = method.getAnnotation(Template.class);
            Annotation[][] anns = method.getParameterAnnotations();
//            Class<?>[] methodArgTypes = method.getParameterTypes();
            Type[] parameterTypes = method.getGenericParameterTypes();
            //boolean hasThisAsFirstArgument = BridJ.hasThisAsFirstArgument(method);//methodArgTypes, anns, true);
            
            if (paramTypes != null && !matchesArgs(parameterTypes, anns, temp == null ? 0 : temp.value().length))///*, hasThisAsFirstArgument*/))
            	return false;
            
            //int thisDirac = hasThisAsFirstArgument ? 1 : 0;
            /*
            switch (type) {
            case Constructor:
            case Destructor:
            	Annotation ann = method.getAnnotation(type == SpecialName.Constructor ? Constructor.class : Destructor.class);
            	if (ann == null)
            		return false;
            	if (!hasThisAsFirstArgument)
            		return false;
            	if (methodArgTypes.length - thisDirac != 0 )
            		return false;
            	break;
            case InstanceMethod:
            	if (!hasThisAsFirstArgument)
            		return false;
            	break;
            case StaticMethod:
            	if (hasThisAsFirstArgument)
            		return false;
            	break;
            }*/
            
            return true;
		}
		private boolean matchesArgs(Type[] parameterTypes, Annotation[][] anns, int offset) {
			int totalArgs = offset;
            for (int i = 0, n = templateArguments == null ? 0 : templateArguments.length; i < n; i++) {
                if (totalArgs >= parameterTypes.length)
                    return false;

                Type paramType = parameterTypes[offset + i];

                TemplateArg arg = templateArguments[i];
                if (arg instanceof TypeRef) {
                    if (!paramType.equals(Class.class))
                        return false;
                } else if (arg instanceof Constant) {
                    try {
                        getTypeClass(paramType).cast(((Constant)arg).value);
                    } catch (ClassCastException ex) {
                        return false;
                    }
                }
                totalArgs++;
            }
            
            for (int i = 0, n = paramTypes == null ? 0 : paramTypes.length; i < n; i++) {
                if (totalArgs >= parameterTypes.length)
                    return false;

                TypeRef paramType = paramTypes[i];
                Type parameterType = parameterTypes[totalArgs];
                if (!paramType.matches(parameterType, annotations(anns == null ? null : anns[i])))
                    return false;

                totalArgs++;
            }
            
            if (totalArgs != parameterTypes.length) {
            		System.err.println("Not enough args for " + this);
                return false;
            }

            return true;
		}

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();

            b.append(valueType).append(' ');
            boolean nameWritten = false;
            if (enclosingType != null) {
                b.append(enclosingType);
                b.append('.');
                if (memberName instanceof SpecialName) {
                    switch ((SpecialName)memberName) {
                        case Destructor:
                            b.append('~');
                        case Constructor:
                            b.append(((ClassRef)enclosingType).ident.simpleName);
                            nameWritten = true;
                            break;
                    }
                }
            }
            if (!nameWritten)
                b.append(memberName);
            
            appendTemplateArgs(b, templateArguments);
            appendArgs(b, '(', ')', paramTypes);
            return b.toString();
        }
        
		public void setMemberName(IdentLike memberName) {
			this.memberName = memberName;
		}
		public IdentLike getMemberName() {
			return memberName;
		}
		public void setValueType(TypeRef valueType) {
			this.valueType = valueType;
		}
		public TypeRef getValueType() {
			return valueType;
		}
		public void setEnclosingType(TypeRef enclosingType) {
			this.enclosingType = enclosingType;
		}
		public TypeRef getEnclosingType() {
			return enclosingType;
		}
	}
}
