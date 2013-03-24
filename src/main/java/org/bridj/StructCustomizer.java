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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bridj.ann.Struct;

/**
* Interface for type customizers that can be used to perform platform-specific type adjustments or other hacks.<br>
* A type customizer can be specified with {@link Struct#customizer() }.<br>
* Each implementation must have a default constructor, and an unique instance of each implementation class will be cached by {@link StructIO#getCustomizer(java.lang.Class) }.
* @deprecated The StructIO API is subject to future changes. Use this with care and be prepared to migrate your code...
*/
@Deprecated
public class StructCustomizer {
	/**
	 * Last chance to remove field declarations
	 */
	public void beforeAggregation(StructDescription desc, List<StructFieldDeclaration> fieldDecls) {}
	/**
	 * Last chance to remove aggregated fields
	 */
	public void beforeLayout(StructDescription desc, List<StructFieldDescription> aggregatedFields) {}
	/**
	 * This method can alter the aggregated fields and may even call again the performLayout(aggregatedFields) method.
	 * This is before field offsets and sizes are propagated to field declarations.
	 */
	public void afterLayout(StructDescription desc, List<StructFieldDescription> aggregatedFields) {}
	/**
	 * Called after everything is setup in the StructIO.<br>
	 * It is the most dangerous callback, here it's advised to only call the prependBytes, appendBytes and setFieldOffset methods.
	 */
	public void afterBuild(StructDescription desc) {}
	
	private static StructCustomizer dummyCustomizer = new StructCustomizer();
    
	private static ConcurrentHashMap<Class, StructCustomizer> customizers = new ConcurrentHashMap<Class, StructCustomizer>();
    static StructCustomizer getInstance(Class<?> structClass) {
		StructCustomizer c = customizers.get(structClass);
		if (c == null) {
			Struct s = structClass.getAnnotation(Struct.class);
			if (s != null) {
				Class<? extends StructCustomizer> customizerClass = s.customizer();
				if (customizerClass != null && customizerClass != StructCustomizer.class) {
					try {
						c = customizerClass.newInstance();
					} catch (Throwable th) {
						throw new RuntimeException("Failed to create customizer of class " + customizerClass.getName() + " for struct class " + structClass.getName() + " : " + th, th);
					}
				}
			}
			if (c == null)
				c = dummyCustomizer;
			StructCustomizer existingConcurrentCustomizer =
			  customizers.putIfAbsent(structClass, c);
			if (existingConcurrentCustomizer != null)
			  return existingConcurrentCustomizer;
		}
		return c;
    }
}
