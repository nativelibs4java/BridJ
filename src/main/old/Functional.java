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

import java.util.*;

public class Functional {
	
	/*static void checkLengths(int len, List... lists) {
		for (List list : lists)
			if (list.size() != len)
				throw new IllegalArgumentException("Expected list of length " + len + ", got length " + list.size() + " instead (" + list + ")");
	}*/
	
#foreach ($n in [0..7])
	
	/**
	 * Generic interface for functions that take #if ($n == 0)no#else$n#end arguments.
	 * Use generic return type java.lang.Void and return null for functions with void return type.
	 */
	public interface Func$n<R#if($n > 0)#foreach ($i in [1..$n]), A$i#end#end> {
		R apply(#if($n > 0)#foreach ($i in [1..$n])#if($i > 1), #end A$i a$i#end#end);
	}
	
#if ($n > 0)
	public static <R#foreach ($i in [1..$n]), A$i#end> List<R> apply(Func$n<R#foreach ($i in [1..$n]), A$i#end> f #foreach ($i in [1..$n]), A$i#if($n == 1)...#else[]#end array$i#end) {
		return apply(f#foreach ($i in [1..$n]), Arrays.asList(array$i)#end);
	}
	public static <R#foreach ($i in [1..$n]), A$i#end> List<R> apply(Func$n<R#foreach ($i in [1..$n]), A$i#end> f #foreach ($i in [1..$n]), Iterable<A$i> iterable$i#end) {
		List<R> ret;
		if (iterable1 instanceof List)
			ret = new ArrayList<R>(((List)iterable1).size());
		else
			ret = new LinkedList<R>();
#if ($n > 1)
#foreach ($i in [2..$n])
		Iterator<A$i> it$i = iterable${i}.iterator();
#end
#end
		for (A1 a1 : iterable1) {
			R r = f.apply(a1#if ($n > 1)#foreach ($i in [2..$n]), it${i}.next()#end#end);
			ret.add(r);
		}
		return ret;
	}
#end

#if ($n > 0)
	public static <#foreach ($i in [1..$n])#if($i > 1),#end A$i#end> boolean foreach(Func$n<Boolean#foreach ($i in [1..$n]), A$i#end> f #foreach ($i in [1..$n]), A$i#if($n == 1)...#else[]#end array$i#end) {
		
		return foreach(f#foreach ($i in [1..$n]), Arrays.asList(array$i)#end);
	}
	public static <#foreach ($i in [1..$n])#if($i > 1),#end A$i#end> boolean foreach(Func$n<Boolean#foreach ($i in [1..$n]), A$i#end> f #foreach ($i in [1..$n]), Iterable<A$i> iterable$i#end) {
#if ($n > 1)
#foreach ($i in [2..$n])
		Iterator<A$i> it$i = iterable${i}.iterator();
#end
#end
		for (A1 a1 : iterable1) {
			Boolean r = f.apply(a1#if ($n > 1)#foreach ($i in [2..$n]), it${i}.next()#end#end);
			if (r != Boolean.TRUE)
				return false;
		}
		return true;
	}
#end

#end
}
