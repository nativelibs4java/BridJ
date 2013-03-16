package org.bridj;
import org.bridj.FunctionTest.ETest;
import static org.bridj.util.DefaultParameterizedType.*;
import org.bridj.demangling.Demangler;
import org.bridj.demangling.Demangler.TypeRef;
import org.bridj.demangling.Demangler.Ident;
import org.bridj.demangling.Demangler.IdentLike;
import org.bridj.demangling.Demangler.DemanglingException;
import org.bridj.demangling.Demangler.MemberRef;
import org.bridj.demangling.GCC4Demangler;
import org.bridj.demangling.VC9Demangler;
import org.bridj.cpp.CPPType;
import org.bridj.demangling.Demangler.SpecialName;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import org.bridj.CPPTest3.Constructed;
import org.junit.Test;

import static org.junit.Assert.*;
public class DemanglingTest {

	@Test
	public void gcc() {
		demangle(
                        null,
			"__Z17testInPlaceSquarePdj", // REMARK (REMI): c++filt does not accept double underscore as a prefix, I don't know if the ones in this file are intended... in the end, it should not really hurt that our demangler is too permissive
			null, 
            ident("testInPlaceSquare"),
			void.class, Pointer.class, int.class
		);
	}
    static Type clongType = CLong.class;//CPPType.getCPPType(new Object[] { CLong.class });
    @Test
    public void testLongLongBackReference() {
        demangle(
            "?test_add9_long@@YA_J_J00000000@Z",
            "_Z14test_add9_longlllllllll",
            null, 
            ident("test_add9_long"),
            long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class
            //clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType
        );
    }
    public static class C {}
    @Test
    public void testStaticMethod() {
        demangle(
            "?m@C@@SAPAV1@XZ",
            null,
            C.class,
            ident("m"),
            paramType(Pointer.class, C.class)
        );
    }
    @Test
    public void testCLongsBackReference() {
        demangle(
            "?testAddCLongs@@YAJJJ@Z",
            null,
            null, 
            ident("testAddCLongs"),
            CLong.class, CLong.class, CLong.class
        );
    }
    @Test
    public void testJLongsBackReference() {
        demangle(
            "?testAddJLongs@@YA_J_J0@Z",
            null,
            null, 
            ident("testAddJLongs"),
            long.class, long.class, long.class
        );
    }
    


    static Type etestEnumType = paramType(ValuedEnum.class, ETest.class);
    @Test
    public void testEnumArgAndRet() {
        demangle(
            "?testEnum@@YA?AW4ETest@@W41@@Z",
            null,
            null, 
            ident("testEnum"),
            etestEnumType, etestEnumType
        );
    }
    @Test
    public void testEnumArg() {
        demangle(
            "?testEnumArgSecond@@YAHW4ETest@@@Z",
            null,
            null, 
            ident("testEnumArgSecond"),
            int.class, etestEnumType
        );
    }
    @Test
    public void testEnumRet() {
        demangle(
            "?testEnumRetSecond@@YA?AW4ETest@@XZ",
            null,
            null, 
            ident("testEnumRetSecond"),
            etestEnumType
        );
    }
    @Test
    public void testEnumArgs() {
        demangle(
            "?testEnumArgs@@YAHW4ETest@@0@Z",
            null,
            null, 
            ident("testEnumArgs"),
            int.class, etestEnumType, etestEnumType
        );
    }
    //
    //
	
    @Test
    public void testSimple() {
        demangle(
            null,
            "_Z17test_incr_int_outiPi",
            null, 
            ident("test_incr_int_out"),
            null, int.class, paramType(Pointer.class, int.class)
        );
    }
    
    
    @Test
    public void testPtrsBackRef() {
        demangle(
			"?f@@YAPADPADPAF1@Z",
			null,
			"byte* f(byte*, short*, short*)"
        );
    }

    @Test
    public void simpleCppFun() {
        demangle(
            null,
            "__Z11sizeOfCtestv",
            "null sizeOfCtest()"
        );
    }
    @Test
    public void testPrimsBackRef() {
        demangle(
			"?f@@YADDFF@Z",
			null,
			"byte f(byte, short, short)"
        );
    }
    @Test
    public void testPrimPtrs() {
        demangle(
			"?f@@YAPADPADPAFPAHPAJPA_JPAMPAN@Z",
			null,
			"byte* f(byte*, short*, int*, CLong*, long*, float*, double*)"
        );
    }
    @Test
    public void testPrims() {
        demangle(
			"?ff@@YAFDFHJ_JMN@Z",
			null,
			"short ff(byte, short, int, CLong, long, float, double)"
        );
    }
    @Test
	public void parameterlessFunction() {
		demangle(
			null, // TODO
			"_Z14test_no_paramsv",
			null, 
            ident("test_no_params"),
			null
		);
	}
    @Test
	public void simpleConstructor() {
		demangle(
			"??0Ctest@@QEAA@XZ",
			"_ZN5CtestC1Ev",
			CPPTest.Ctest.class,
            SpecialName.Constructor,
			null
		);
        demangle(
            null,
            "__ZN11ConstructedC2EPKcS1_PS1_",
            Constructed.class,
            SpecialName.SpecialConstructor,
            null,
            paramType(Pointer.class, Byte.class), 
            paramType(Pointer.class, Byte.class), 
            paramType(Pointer.class, paramType(Pointer.class, Byte.class))
        );
	}
	@Test
    public void methods() {
    	demangle(
			null, 
			"_ZN5Ctest7testAddEii", 
			CPPTest.Ctest.class, 
            ident("testAdd"),
			int.class, int.class, int.class
		);
    	
    }

    @Test
	public void template1() {
		demangle(
			null,
			"__ZN5Temp1IdE4tempEd",
			CPPType.getCPPType(new Object[] { CPPTemplateTest.Temp1.class, Double.class }),
			ident("temp"),
			void.class,
			double.class
		);
	}
    
    @Test
	public void template2() {
		demangle(
			null,
			"__ZN5Temp2IisE4tempEis",
			CPPType.getCPPType(new Object[] { CPPTemplateTest.Temp2.class, int.class, short.class }),
			ident("temp"),
			void.class,
			int.class,
            short.class
		);
	}

    @Test
	public void templateHardConstructor1() {
		demangle(
			null,
			"_ZN24InvisibleSourcesTemplateILi10ESsEC1Ei",
            CPPType.getCPPType(new Object[] { CPPTemplateTest.InvisibleSourcesTemplate.class, 10, int.class }),
            SpecialName.Constructor,
			void.class,
            int.class
		);
	}
    @Test
	public void templateHardConstructor2() {
		demangle(
			null,
			"_ZN24InvisibleSourcesTemplateILi10EiEC2Ei",
            CPPType.getCPPType(new Object[] { CPPTemplateTest.InvisibleSourcesTemplate.class, 10, int.class }),
            SpecialName.SpecialConstructor,
			void.class,
            int.class
		);
	}

    @Test
	public void simpleFunctions() {
		demangle("?sinInt@@YANH@Z", "_Z6sinInti", null,
				ident("sinInt"),
				double.class, int.class);
		demangle("?forwardCall@@YAHP6AHHH@ZHH@Z", "_Z11forwardCallPvii", null, ident("forwardCall"), int.class, Pointer.class, int.class, int.class);
                // NB: the forwardCall test for gcc is written with a "void*" as first parameter (I could not get the pointer type from the VC6 mangled name
	}

    @Test
    public void complexPointerTypeParameters() {
        // TODO VC versions
        // NB: with gcc, we have no info about the return type (don't know about VC6)
        demangle(null, "_Z14pointerAliasesPPvS_PS0_PPi", null, ident("pointerAliases"), null, Pointer.class, Pointer.class, Pointer.class, Pointer.class);
        demangle(null, "_Z14pointerAliasesPPvS_PS0_PPi", null, ident("pointerAliases"), null, "**Void", "*Void", "***Void", "**Integer");
    }

    @Test
    public void gccMemoryShortcuts() {
        // does VC do something similar?
        demangle(null, "_Z15shortcutsSimplePPPPccS_S0_S1_S2_PS2_", "null shortcutsSimple(byte****, byte, byte*, byte**, byte***, byte****, byte*****)");
    }

    @Test
    public void gccBuiltinShortcuts() {
        // does VC do something similar?
        //demangle(null, "_ZN1a1bI1cI1d1eEE1f", "");
        demangle(null, "_Z25shortcutsBuiltinStdPrefixSt9exception", "null shortcutsBuiltinStdPrefix(std.exception)");
        String str = "std.basic_string<byte, std.char_traits<byte>, std.allocator<byte>>"; //std::basic_string<char, std::char_traits<char>, std::allocator<char> > (from c++filt)
        demangle(null, "_Z22shortcutsBuiltinStringSs", "null shortcutsBuiltinString(" + str + ")");
        String iostream = "std.basic_iostream<byte, std.char_traits<byte>>"; //std::basic_iostream<char, std::char_traits<char> >
        String istream = iostream.replaceAll("iostream", "istream");
        String ostream = iostream.replaceAll("iostream", "ostream");
        demangle(null, "_Z7streamsSiSoSd", "null streams(" + istream + ", " + ostream + ", " + iostream + ")");
    }

    @Test
    public void gccTrickyMemoryAndBuiltinShortcutsWithTemplates() {
        demangle(null, "_Z3blaPN7Helping4HandE", "null bla(Helping.Hand*)"); // validate the output syntax for pointers to namespaced types
        String vectorOfXYZ = "std.vector<XYZ, std.allocator<XYZ>>";
        String str = "std.basic_string<byte, std.char_traits<byte>, std.allocator<byte>>";
        demangle(null, "_ZN3bla5inputEPSt6vectorISsSaISsEEPS0_IPN7Helping4HandESaIS6_EE",
                // bla::input(std::vector<std::basic_string<char, std::char_traits<char>, std::allocator<char> >, std::allocator<std::basic_string<char, std::char_traits<char>, std::allocator<char> > > >*, std::vector<Helping::Hand*, std::allocator<Helping::Hand*> >*)
                // bla::input(vectorof(string)*, vectorof(Helping::Hand*)*)
                "null bla.input(" + vectorOfXYZ.replaceAll("XYZ", str) + "*, " + vectorOfXYZ.replaceAll("XYZ", "Helping.Hand*") + "*)");
    }

    static IdentLike ident(String name) {
        return new Ident(name);
    }
    private void demangle(String vc9, String gcc4, Type enclosingType, IdentLike memberName, Type returnType, Object... paramTypes) {
        try {
			if (vc9 != null)
				checkSymbol(vc9, new VC9Demangler(null, vc9).parseSymbol(), enclosingType, memberName, returnType, paramTypes, null, null);
			if (gcc4 != null)
				checkSymbol(gcc4, new GCC4Demangler(null, gcc4).parseSymbol(), enclosingType, memberName, returnType, paramTypes,null, null);
		} catch (DemanglingException ex) {
			Logger.getLogger(DemanglingTest.class.getName()).log(Level.SEVERE, null, ex);
			throw new AssertionError(ex.toString());
		}
    }
    
    private void demangle(String vc9, String gcc4, String toString) {
		try {
			if (vc9 != null)
				assertEquals(toString, new VC9Demangler(null, vc9).parseSymbol().toString());
			if (gcc4 != null)
				assertEquals(toString, new GCC4Demangler(null, gcc4).parseSymbol().toString());
		} catch (DemanglingException ex) {
			Logger.getLogger(DemanglingTest.class.getName()).log(Level.SEVERE, null, ex);
			throw new AssertionError(ex.toString());
		}
    }

    private void checkSymbol(String str, MemberRef symbol, Type enclosingType, IdentLike memberName, Type returnType, Object[] paramTypes, Annotation[][] paramAnns, AnnotatedElement element) {
        if (symbol == null)
        		assertTrue("Symbol not successfully parsed \"" + str + "\"", false);
    		if (memberName != null)
            assertEquals("Bad name", memberName, symbol.getMemberName());
        if (enclosingType != null) {
        	assertNotNull("Null enclosing type : " + symbol, symbol.getEnclosingType());
            assertTrue("Bad enclosing type (got " + symbol.getEnclosingType() + ", expected " + (enclosingType instanceof Class ? ((Class)enclosingType).getName() : enclosingType.toString()) + ")", symbol.getEnclosingType().matches(enclosingType, Demangler.annotations(enclosingType)));
        }
        if (returnType != null && symbol.getValueType() != null)
	    assertTrue("Bad return type : expected " + returnType + ", got " + symbol.getValueType() + " (got class " + symbol.getValueType().getClass().getName() + ")", symbol.getValueType().matches(returnType, Demangler.annotations(element)));

        int nArgs = symbol.paramTypes.length;
        assertEquals("Bad number of parameters (symbol = " + symbol + ")", paramTypes.length, nArgs);

        for (int iArg = 0; iArg < nArgs; iArg++) {
            if (paramTypes[iArg] instanceof Type) {
            	Type expecting = (Type)paramTypes[iArg];
            	TypeRef demangled = symbol.paramTypes[iArg];
            	 assertTrue("Bad type for " + (iArg + 1) + "th param : (symbol = " + symbol + ", expecting " + expecting + " and demangled " + demangled + " (" + demangled.getClass().getName() + ")", 
            	 	 demangled.matches(expecting, paramAnns == null ? null : Demangler.annotations(paramAnns[iArg])));
            	 
            } else if (paramTypes[iArg] instanceof String) {
                String targetType = (String) paramTypes[iArg];
                TypeRef currentType = symbol.paramTypes[iArg];
                int count = 0;
                while (targetType.startsWith("*")) {
                    assertEquals("For " + (iArg + 1) + "th param, after " + count + " dereferencing, wrong non-pointer type", Demangler.PointerTypeRef.class, currentType.getClass());
                    targetType = targetType.substring(1);
                    currentType = ((Demangler.PointerTypeRef) currentType).pointedType;
                    count++;
                }
                Class targetPointedType = null;
                try {
                    if (targetType.contains(".")) {
                        throw new RuntimeException("this code has never been used before, comment this exception and check it");
                        //targetPointedType = Class.forName(targetType);
                    } else {
                        targetPointedType = (Class) Class.forName("java.lang." + targetType).getDeclaredField("TYPE").get(null);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Check your test for target type " + targetType, e);
                }
                assertTrue("For " + (iArg + 1) + "th parameter, after " + count + " dereferencing, wrong final pointed type: expected " + targetType + " got " + currentType.getQualifiedName(new StringBuilder(), true), currentType.matches(targetPointedType, null));
            } else {
                assertTrue("Problem in the expression of the test code", false);
            }
        }
    }

}