/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;
import org.bridj.ann.Forwardable;

/**
 * Util methods for annotations (inheritable annotations, forwarded annotations, annotations from AnnotatedElements and/or direct annotation arrays...)
 * @author ochafik
 */
public class AnnotationUtils {
    
    public static <A extends Annotation> A getInheritableAnnotation(Class<A> ac, AnnotatedElement m, Annotation... directAnnotations) {
        return getAnnotation(ac, true, m, directAnnotations);
    }
    public static <A extends Annotation> A getAnnotation(Class<A> ac, AnnotatedElement m, Annotation... directAnnotations) {
        return getAnnotation(ac, false, m, directAnnotations);
    }
    private static boolean isForwardable(Class<? extends Annotation> ac) {
        return ac.isAnnotationPresent(Forwardable.class);
    }
    public static boolean isAnnotationPresent(Class<? extends Annotation> ac, Annotation... annotations) {
        return isAnnotationPresent(ac, isForwardable(ac), annotations);
    }
    private static boolean isAnnotationPresent(Class<? extends Annotation> ac, boolean isForwardable, Annotation... annotations) {
        for (Annotation ann : annotations) {
            if (ac.isInstance(ann))
                return true;

            if (isForwardable) {
                if (ann.annotationType().isAnnotationPresent(ac))
                    return true;
            }
        }
        return false;
    }
    public static boolean isAnnotationPresent(Class<? extends Annotation> ac, AnnotatedElement m, Annotation... directAnnotations) {
        boolean isForwardable = isForwardable(ac);
        if (m != null) {
            if (isForwardable) {
                if (isAnnotationPresent(ac, true, m.getAnnotations()))
                    return true;
            } else {
                if (m.isAnnotationPresent(ac))
                    return true;
            }
        }
        if (directAnnotations != null)
            return isAnnotationPresent(ac, isForwardable, directAnnotations);
        
        return false;
    }

    private static <A extends Annotation> A getAnnotation(Class<A> ac, boolean inherit, AnnotatedElement m, Annotation... directAnnotations) {
        if (directAnnotations != null) {
            for (Annotation ann : directAnnotations) {
                if (ac.isInstance(ann)) {
                    return ac.cast(ann);
                }
            }
        }

        if (m == null) {
            return null;
        }
        A a = m.getAnnotation(ac);
        if (a != null) {
            return a;
        }

        if (inherit) {
            if (m instanceof Member) {
                return getAnnotation(ac, inherit, ((Member) m).getDeclaringClass());
            }
            
            if (m instanceof Class<?>) {
            	Class<?> c = (Class<?>) m, dc = c.getDeclaringClass();
                Class p = c.getSuperclass();
				while (p != null) {
					a = getAnnotation(ac, true, p);
					if (a != null)
						return a;
					p = p.getSuperclass();
				}

                if (dc != null) {
	                return getAnnotation(ac, inherit, dc);
	        }
        }
        }
        return null;
    }
}
