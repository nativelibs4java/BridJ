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
package org.bridj;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridj.CPPTest3.Constructed;
import org.bridj.FunctionTest.ETest;
import org.bridj.ann.Ptr;
import org.bridj.cpp.CPPType;
import org.bridj.demangling.Demangler;
import org.bridj.demangling.Demangler.DemanglingException;
import org.bridj.demangling.Demangler.Ident;
import org.bridj.demangling.Demangler.IdentLike;
import org.bridj.demangling.Demangler.MemberRef;
import org.bridj.demangling.Demangler.SpecialName;
import org.bridj.demangling.Demangler.TypeRef;
import org.bridj.demangling.GCC4Demangler;
import org.bridj.demangling.VC9Demangler;
import static org.bridj.util.DefaultParameterizedType.*;
import static org.bridj.util.PlatformTestUtils.force32Bits;
import static org.bridj.util.PlatformTestUtils.force64Bits;
import static org.bridj.util.ReflectionUtils.makeFieldWritable;

import static org.junit.Assert.*;
import org.junit.Test;
public class DemanglingTest {

    Demangler.Annotations PTR_ANNOTATION = new Demangler.Annotations() {
        public <A extends Annotation> A getAnnotation(Class<A> c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        public boolean isAnnotationPresent(Class<? extends Annotation> c) {
            return c == Ptr.class;
        }
    };

    Demangler.Annotations NO_ANNOTATION = new Demangler.Annotations() {
        public <A extends Annotation> A getAnnotation(Class<A> c) {
            return null;
        }
        public boolean isAnnotationPresent(Class<? extends Annotation> c) {
            return false;
        }
    };
    
    static Type pointerType(Type target) {
        return paramType(Pointer.class, target);
    }
    
    @Test
    public void cFunctions() throws DemanglingException {
        for (String sym : new String[] { "__cxa_finalize", "__bss_start" }) {
            assertEquals(null, new GCC4Demangler(null, sym).parseSymbol());
            assertEquals(null, new VC9Demangler(null, sym).parseSymbol());
        }
    }
    
	@Test
	public void gcc() {
		demangle(
                        null,
			"__Z17testInPlaceSquarePdj", // REMARK (REMI): c++filt does not accept double underscore as a prefix, I don't know if the ones in this file are intended... in the end, it should not really hurt that our demangler is too permissive
			null, 
            ident("testInPlaceSquare"),
			void.class, pointerType(double.class), int.class
		);
	}

    @Test
	public void gccSimpleCallback() {
        abstract class CbFloatIntLong extends Callback {
            public abstract float apply(int i, long l);
        }
        demangle(
            null,
			"__Z12callCallbackPFfixEsc",
			null, 
            ident("callCallback"),
			double.class, pointerType(CbFloatIntLong.class), short.class, byte.class);
	}

    static Type clongType = CLong.class;//CPPType.getCPPType(new Object[] { CLong.class });
    @Test
    public void testLongLongBackReference() {
        demangle(
            "?test_add9_long@@YA_J_J00000000@Z",
            "_Z14test_add9_longlllllllll",
            null, 
            ident("test_add9_long"),
            //long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class
            //long.class, clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType
            clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType, clongType
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
            pointerType(C.class)
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
    public void testFloatPointer() {
        demangle(
            null,
			"__Z19test_incr_float_outfPf",
			null, 
            ident("test_incr_float_out"),
			void.class, float.class, pointerType(float.class)
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
            null, int.class, pointerType(int.class)
        );
    }
    
    @Test
    public void testSimple2() {
        demangle(
            null,
            "_Z11DeleteClassPv",
            null,
            ident("DeleteClass"),
            void.class, Pointer.class
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
            pointerType(Byte.class), 
            pointerType(Byte.class), 
            pointerType(pointerType(Byte.class))
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
        demangle(null, "_Z14pointerAliasesPPvS_PS0_PPi", null, ident("pointerAliases"), null, pointerType(Pointer.class), Pointer.class, pointerType(pointerType(Pointer.class)), pointerType(pointerType(int.class)));
        demangle(null, "_Z14pointerAliasesPPvS_PS0_PPi", null, ident("pointerAliases"), null, "**Void", "*Void", "***Void", "**Integer");
    }

    @Test
    public void gccMemoryShortcuts() {
        // does VC do something similar?
        demangle(null, "_Z15shortcutsSimplePPPPccS_S0_S1_S2_PS2_", "null shortcutsSimple(byte****, byte, byte*, byte**, byte***, byte****, byte*****)");
        
        demangle(null, "__Z1fPsS_", "null f(short*, short*)");
        demangle(null, "__Z1fPKsS_", "null f(const short*, short)");
        demangle(null, "__Z1fPKsS0_", "null f(const short*, const short*)");
        demangle(null, "__Z1fPKcS0_S0_PKsS2_PKdS4_", "null f(const byte*, const byte*, const byte*, const short*, const short*, const double*, const double*)");
        demangle(null, "__ZN1AC2EPS_PS0_S1_", "null A.(A*, A**, A**)");
        /*
         * 
         * 
0000000000001700 T __Z15simpleCallback1PFvPKcE
0000000000001710 T __Z15simpleCallback2PFvPcE
TEST_API void simpleCallback1(void (*cb)(const char*)) {}
TEST_API void simpleCallback2(void (*cb)(char*)) {}

0000000000001650 T __Z18repeatedCallbacks1PFvPKcS0_S0_PKsS2_PKdS4_E
0000000000001660 T __Z18repeatedCallbacks2PFvPcS_S_PsS0_PdS1_E
0000000000001670 T __Z18repeatedCallbacks3PFvPcS_PsS0_S_S_E
0000000000001680 T __Z18repeatedCallbacks4PFvPsS_PcS0_S_S_E
0000000000001690 T __Z18repeatedCallbacks5PFvcccssddE
TEST_API void repeatedCallbacks1(void (*cb)(const char*, const char*, const char*, const short*, const short*, const double*, const double*)) {}
TEST_API void repeatedCallbacks2(void (*cb)(char*, char*, char*, short*, short*, double*, double*)) {}
TEST_API void repeatedCallbacks3(void (*cb)(char*, char*, short*, short*, char*, char*)) {}
TEST_API void repeatedCallbacks4(void (*cb)(short*, short*, char*, char*, short*, short*)) {}
TEST_API void repeatedCallbacks5(void (*cb)(char, char, char, short, short, double, double)) {}

00000000000024d0 T __Z13repeatedCall1PKcS0_S0_PKsS2_PKdS4_
00000000000024e0 T __Z13repeatedCall2PcS_S_PsS0_PdS1_
00000000000024f0 T __Z13repeatedCall3PcS_PsS0_S_S_
0000000000002500 T __Z13repeatedCall4PsS_PcS0_S_S_
0000000000002510 T __Z13repeatedCall5cccssdd
TEST_API void repeatedCall1(const char*, const char*, const char*, const short*, const short*, const double*, const double*) {}
TEST_API void repeatedCall2(char*, char*, char*, short*, short*, double*, double*) {}
TEST_API void repeatedCall3(char*, char*, short*, short*, char*, char*) {}
TEST_API void repeatedCall4(short*, short*, char*, char*, short*, short*) {}
TEST_API void repeatedCall5(char, char, char, short, short, double, double) {}

* 
0000000000002480 T __Z13repeatedCall6PcS_
0000000000002410 T __Z14repeatedCall6_PsS_
0000000000002490 T __Z13repeatedCall7PKcS0_
0000000000002450 T __Z13repeatedCall8PKsS0_PKcS2_
TEST_API void repeatedCall6(char*, char*) {}
TEST_API void repeatedCall6_(short*, short*) {}
TEST_API void repeatedCall7(const char*, const char*) {}
TEST_API void repeatedCall8(const short*, const short*, const char*, const char*) {}

         */
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
                "null bla.input(std.vector<std.basic_string<byte, std.char_traits<byte>, std.allocator<byte>>, std.allocator<std.basic_string<byte, std.char_traits<byte>, std.allocator<byte>>>>*, std.allocator<std.basic_string<byte, std.char_traits<byte>, std.allocator<byte>>><Helping.Hand*, std.allocator<Helping.Hand*>>*)");
                //"null bla.input(" + vectorOfXYZ.replaceAll("XYZ", str) + "*, " + vectorOfXYZ.replaceAll("XYZ", "Helping.Hand*") + "*)");
    }

    static IdentLike ident(String name) {
        return new Ident(name);
    }
    static void demangle(String vc9, String gcc4, Type enclosingType, IdentLike memberName, Type returnType, Object... paramTypes) {
        try {
			if (vc9 != null)
				checkSymbol("msvc", vc9, new VC9Demangler(null, vc9).parseSymbol(), enclosingType, memberName, returnType, paramTypes, null, null);
			if (gcc4 != null)
				checkSymbol("gcc", gcc4, new GCC4Demangler(null, gcc4).parseSymbol(), enclosingType, memberName, returnType, paramTypes,null, null);
		} catch (DemanglingException ex) {
			Logger.getLogger(DemanglingTest.class.getName()).log(Level.SEVERE, null, ex);
			throw new AssertionError(ex.toString());
		}
    }
    
    static void demangle(String vc9, String gcc4, String toString) {
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

    static void checkSymbol(String demanglerName, String str, MemberRef symbol, Type enclosingType, IdentLike memberName, Type returnType, Object[] paramTypes, Annotation[][] paramAnns, AnnotatedElement element) {
	String demanglerSuffix = " for " + demanglerName;
        if (symbol == null)
        		assertTrue("Symbol not successfully parsed" + demanglerSuffix + ": \"" + str + "\"", false);
    		if (memberName != null)
            assertEquals("Bad name" + demanglerSuffix, memberName, symbol.getMemberName());
        if (enclosingType != null) {
        	assertNotNull("Null enclosing type" + demanglerSuffix + " : " + symbol, symbol.getEnclosingType());
            assertTrue("Bad enclosing type"+ demanglerSuffix + " (got " + symbol.getEnclosingType() + ", expected " + (enclosingType instanceof Class ? ((Class)enclosingType).getName() : enclosingType.toString()) + ")", symbol.getEnclosingType().matches(enclosingType, Demangler.annotations(enclosingType)));
        }
        if (returnType != null && symbol.getValueType() != null)
	    assertTrue("Bad return type"+ demanglerSuffix + " : expected " + returnType + ", got " + symbol.getValueType() + " (got class " + symbol.getValueType().getClass().getName() + ")", symbol.getValueType().matches(returnType, Demangler.annotations(element)));

        int nArgs = symbol.paramTypes.length;
        assertEquals("Bad number of parameters"+ demanglerSuffix + " (symbol = " + symbol + ")", paramTypes.length, nArgs);

        for (int iArg = 0; iArg < nArgs; iArg++) {
            if (paramTypes[iArg] instanceof Type) {
            	Type expecting = (Type)paramTypes[iArg];
            	TypeRef demangled = symbol.paramTypes[iArg];
            	 assertTrue("Bad type for " + (iArg + 1) + "th param"+ demanglerSuffix + " : (symbol = " + symbol + ", expecting " + expecting + " and demangled " + demangled + " (" + demangled.getClass().getName() + ")", 
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

    @Test
    public void testIntVsPointer_32bits() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        force32Bits();
        
        assertFalse(Demangler.equivalentTypes(int.class, NO_ANNOTATION, Pointer.class, NO_ANNOTATION));;
        // This should not even be allowed: int tagged as @Ptr.
        assertFalse(Demangler.equivalentTypes(int.class, PTR_ANNOTATION, Pointer.class, NO_ANNOTATION));
        
        assertFalse(Demangler.equivalentTypes(long.class, null, Pointer.class, NO_ANNOTATION));
        assertFalse(Demangler.equivalentTypes(long.class, NO_ANNOTATION, Pointer.class, NO_ANNOTATION));
        assertTrue(Demangler.equivalentTypes(long.class, PTR_ANNOTATION, Pointer.class, NO_ANNOTATION));
    }

    @Test
    public void testIntVsPointer_64bits() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        force64Bits();
        
        assertFalse(Demangler.equivalentTypes(int.class, NO_ANNOTATION, Pointer.class, NO_ANNOTATION));
        assertFalse(Demangler.equivalentTypes(int.class, PTR_ANNOTATION, Pointer.class, NO_ANNOTATION));
        
        assertTrue(Demangler.equivalentTypes(long.class, null, Pointer.class, NO_ANNOTATION)); 
        assertFalse(Demangler.equivalentTypes(long.class, NO_ANNOTATION, Pointer.class, NO_ANNOTATION)); 
        assertTrue(Demangler.equivalentTypes(long.class, PTR_ANNOTATION, Pointer.class, NO_ANNOTATION));
    }

}
