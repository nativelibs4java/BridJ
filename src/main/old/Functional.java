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
