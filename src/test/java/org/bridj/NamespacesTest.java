/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
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
import org.bridj.FunctionTest.ETest;
import static org.bridj.util.DefaultParameterizedType.*;
import static org.bridj.DemanglingTest.*;
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
import java.util.Collections;
import java.util.Iterator;
import org.bridj.CPPTest3.Constructed;
import org.junit.Test;

import static org.junit.Assert.*;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Constructor;
import org.bridj.ann.Namespace;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Name;
import org.bridj.ann.Runtime;
import org.bridj.cpp.CPPObject;
import org.bridj.cpp.CPPRuntime;

@Library("test") 
@Runtime(CPPRuntime.class) 
public class NamespacesTest {
    static {
		BridJ.register();
	}
	@Namespace("com::nativelibs4java::bridj") 
	public static class FullyNamespacedClass extends CPPObject {
		static {
			BridJ.register();
		}
		@Constructor(0) 
		public FullyNamespacedClass(int value) {
			super((Void)null, 0, value);
		}
		@Field(0) 
		public int m_value() {
			return this.io.getIntField(this, 0);
		}
		@Field(0) 
		public FullyNamespacedClass m_value(int m_value) {
			this.io.setIntField(this, 0, m_value);
			return this;
		}
		public FullyNamespacedClass() {
			super();
		}
		public FullyNamespacedClass(Pointer pointer) {
			super(pointer);
		}
        
        @Name("getValue")
        public native int renamedGetValue();
        
        @Name("getEnumValuePlus1")
        public native int incrementedEnumValue(IntValuedEnum<RenamedEnum> e);
	};
    @Namespace("com::nativelibs4java::bridj") 
    @Name("FullyNamespacedEnum")
    public enum RenamedEnum implements IntValuedEnum<RenamedEnum > {
		A(0),
		B(1),
		C(2);
		RenamedEnum(long value) {
			this.value = value;
		}
		public final long value;
		public long value() {
			return this.value;
		}
		public Iterator<RenamedEnum > iterator() {
			return Collections.singleton(this).iterator();
		}
		public static IntValuedEnum<RenamedEnum > fromValue(int value) {
			return FlagSet.fromValue(value, values());
		}
	};
    
    
	@Namespace("bridj") 
    @Name("SimplyNamespacedClass")
	public static class RenamedSimplyNamespacedClass extends CPPObject {
		static {
			BridJ.register();
		}
		@Constructor(0) 
		public RenamedSimplyNamespacedClass(int value) {
			super((Void)null, 0, value);
		}
		@Field(0) 
		public int m_value() {
			return this.io.getIntField(this, 0);
		}
		@Field(0) 
		public RenamedSimplyNamespacedClass m_value(int m_value) {
			this.io.setIntField(this, 0, m_value);
			return this;
		}
		public RenamedSimplyNamespacedClass() {
			super();
		}
		public RenamedSimplyNamespacedClass(Pointer pointer) {
			super(pointer);
		}
        
        @Name("getValue")
        public native int renamedGetValue();
	}
	
    @Name("SimplyNamespacedClass")
    public static class TopLevelClass extends CPPObject {
		static {
			BridJ.register();
		}
		@Constructor(0) 
		public TopLevelClass(int value) {
			super((Void)null, 0, value);
		}
		@Field(0) 
		public int m_value() {
			return this.io.getIntField(this, 0);
		}
		@Field(0) 
		public TopLevelClass m_value(int m_value) {
			this.io.setIntField(this, 0, m_value);
			return this;
		}
		public TopLevelClass() {
			super();
		}
		public TopLevelClass(Pointer pointer) {
			super(pointer);
		}
        
        @Name("getValue")
        public native int renamedGetValue();
	}
    
    @Test
    public void testSimplyNamespaced() {
        RenamedSimplyNamespacedClass c = new RenamedSimplyNamespacedClass(10);
        assertEquals(10, c.renamedGetValue());
    }
    
    @Test
    public void testTopLevelClass() {
        TopLevelClass c = new TopLevelClass(10);
        assertEquals(10 + 100, c.renamedGetValue());
    }
    
    @Test
    public void testFullyNamespaced() {
        FullyNamespacedClass c = new FullyNamespacedClass(10);
        assertEquals(10, c.renamedGetValue());
        
        for (RenamedEnum v : RenamedEnum.values())
            assertEquals(v.value + 1, c.incrementedEnumValue(v));
    }
}
