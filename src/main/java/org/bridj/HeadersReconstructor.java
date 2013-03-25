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
import java.util.*;
import java.io.*;
import static org.bridj.demangling.Demangler.*;

/**
 * Unfinished experiment to (partially) reconstruct a header out of parsed shared libraries symbols.<br>
 * Beware: GCC does not add return types to mangled C++ signatures, so this will be left as '?' in the resulting sources.
 * @author ochafik
 */
public class HeadersReconstructor {
	
	public static void reconstructHeaders(Iterable<NativeLibrary> libraries, PrintWriter out) {
		List<MemberRef> orphanMembers = new ArrayList<MemberRef>();
		Map<TypeRef, List<MemberRef>> membersByClass = new HashMap<TypeRef, List<MemberRef>>();
		for (NativeLibrary library : libraries) {
			for (Symbol symbol : library.getSymbols()) {
				MemberRef mr = symbol.getParsedRef();
				if (mr == null)
					continue;
				
				TypeRef et = mr.getEnclosingType();
				if (et == null)
					orphanMembers.add(mr);
				else {
					List<MemberRef> mrs = membersByClass.get(et);
					if (mrs == null)
						membersByClass.put(et, mrs = new ArrayList<MemberRef>());
					mrs.add(mr);
				}
			}
		}
		for (TypeRef tr : membersByClass.keySet())
			out.println("class " + tr + ";");
		
		for (MemberRef mr : orphanMembers)
			out.println(mr + ";");
		
		for (Map.Entry<TypeRef, List<MemberRef>> e : membersByClass.entrySet()) {
			TypeRef tr = e.getKey();
			List<MemberRef> mrs = e.getValue();
			out.println("class " + tr + " \n{");
			for (MemberRef mr : mrs) {
				out.println("\t" + mr + ";");
			}
			out.println("}");
		}
		 
	}
	
}
