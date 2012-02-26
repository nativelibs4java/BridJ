package org.bridj.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class StringUtils {
	public static String implode(double[] array, String separator) {
		StringBuffer out = new StringBuffer();
		boolean first = true;
		for (double v : array) {
			if (first) first = false;
			else out.append(separator);
			out.append(v);
		}
		return out.toString();
	}
	
	public static String implode(Object[] values) {
		return implode(values, ", ");
	}
	public static String implode(Object[] values, Object separator) {
		return implode(Arrays.asList(values), separator);
	}
	public static final <T> String implode(Iterable<T> elements, Object separator) {
		String sepStr = separator.toString();
		StringBuilder out = new StringBuilder();
		boolean first = true;
		for (Object s : elements) {
			if (s == null)
				continue;
			
			if (first) 
				first = false;
			else 
				out.append(sepStr);
			out.append(s);
		}
		return out.toString();
	}
	public static final String implode(Iterable<?> strings) {
		return implode(strings, ", ");
	}
}
