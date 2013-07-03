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
package org.bridj.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;
import org.bridj.ann.Forwardable;

/**
 * Util methods for annotations (inheritable annotations, forwarded annotations,
 * annotations from AnnotatedElements and/or direct annotation arrays...)
 *
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
            if (ac.isInstance(ann)) {
                return true;
            }

            if (isForwardable) {
                if (ann.annotationType().isAnnotationPresent(ac)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAnnotationPresent(Class<? extends Annotation> ac, AnnotatedElement m, Annotation... directAnnotations) {
        boolean isForwardable = isForwardable(ac);
        if (m != null) {
            if (isForwardable) {
                if (isAnnotationPresent(ac, true, m.getAnnotations())) {
                    return true;
                }
            } else {
                if (m.isAnnotationPresent(ac)) {
                    return true;
                }
            }
        }
        if (directAnnotations != null) {
            return isAnnotationPresent(ac, isForwardable, directAnnotations);
        }

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
                    if (a != null) {
                        return a;
                    }
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
