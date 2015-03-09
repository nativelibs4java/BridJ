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

import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import org.bridj.CallIO.NativeObjectHandler;
import org.bridj.util.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Member;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.nio.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.concurrent.*;
import org.bridj.ann.Virtual;
import org.bridj.ann.Array;
import org.bridj.ann.Union;
import org.bridj.ann.Bits;
import org.bridj.ann.Field;
import org.bridj.ann.Struct;
import org.bridj.ann.Alignment;
import static org.bridj.Pointer.*;
import static org.bridj.StructUtils.*;

/**
 * Representation of a C struct's memory layout, built thanks to the annotations
 * found in the Java bindings.<br>
 * End-users should not use this class, it's used by runtimes.<br>
 * Annotations currently used are
 * {@link org.bridj.ann.Virtual}, {@link org.bridj.ann.Array}, {@link org.bridj.ann.Bits}, {@link org.bridj.ann.Field}, {@link org.bridj.ann.Alignment}
 * and {@link org.bridj.ann.Struct}
 *
 * @author ochafik
 */
public class StructDescription {

    protected volatile StructFieldDescription[] fields;
    protected long structSize = -1;
    protected long structAlignment = -1;
    protected final Class<?> structClass;
    protected final Type structType;
    protected boolean hasFieldFields;

    public void prependBytes(long bytes) {
        build();
        for (StructFieldDescription field : fields) {
            field.offset(bytes);
        }
        structSize += bytes;
    }

    public void appendBytes(long bytes) {
        build();
        structSize += bytes;
    }

    public void setFieldOffset(String fieldName, long fieldOffset, boolean propagateChanges) {
        build();

        long propagatedOffsetDelta = 0;
        long originalOffset = 0;
        for (StructFieldDescription field : fields) {
            if (field.name.equals(fieldName)) {
                originalOffset = field.byteOffset;
                propagatedOffsetDelta = fieldOffset - field.byteOffset;
                field.offset(propagatedOffsetDelta);
                if (!propagateChanges) {
                    long minSize = fieldOffset + field.byteLength;
                    structSize = structSize < minSize ? minSize : structSize;
                    return;
                }
            }
        }
        structSize += propagatedOffsetDelta;
        for (StructFieldDescription field : fields) {
            if (!field.name.equals(fieldName) && field.byteOffset > originalOffset) {
                field.offset(propagatedOffsetDelta);
            }
        }
    }
    StructCustomizer customizer;

    public StructDescription(Class<?> structClass, Type structType, StructCustomizer customizer) {
        this.structClass = structClass;
        this.structType = structType;
        this.customizer = customizer;
        if (Utils.containsTypeVariables(structType)) {
            throw new RuntimeException("Type " + structType + " contains unresolved type variables!");
        }
        // Don't call build here, for recursive initialization cases.
    }

    boolean isVirtual() {
        for (Method m : structClass.getMethods()) {
            if (m.getAnnotation(Virtual.class) != null) {
                return true;
            }
        }
        return false;
    }

    public Class<?> getStructClass() {
        return structClass;
    }

    public Type getStructType() {
        return structType;
    }

    @Override
    public String toString() {
        return "StructDescription(" + Utils.toString(structType) + ")";
    }

    /// Call whenever an instanceof a struct that depends on that StructIO is created
    void build() {
        if (fields == null) {
            synchronized (this) {
                if (fields == null) {
                    computeStructLayout(this, customizer);
                    customizer.afterBuild(this);
                    if (BridJ.debug) {
                        BridJ.info(describe());
                    }
                }
            }
        }
    }

    public final long getStructSize() {
        build();
        return structSize;
    }

    public final long getStructAlignment() {
        build();
        return structAlignment;
    }
    private List<StructFieldDescription> aggregatedFields;

    public void setAggregatedFields(List<StructFieldDescription> aggregatedFields) {
        this.aggregatedFields = aggregatedFields;
    }

    public List<StructFieldDescription> getAggregatedFields() {
        build();
        return aggregatedFields;
    }
    SolidRanges solidRanges;

    public SolidRanges getSolidRanges() {
        build();
        return solidRanges;
    }

    public final String describe(StructObject struct) {
        return StructUtils.describe(struct, structType, fields);
    }

    public final String describe() {
        StringBuilder b = new StringBuilder();
        b.append("// ");
        b.append("size = ").append(structSize).append(", ");
        b.append("alignment = ").append(structAlignment);
        b.append("\nstruct ");
        b.append(StructUtils.describe(structType)).append(" { ");
        for (int iField = 0, nFields = fields.length; iField < nFields; iField++) {
            StructFieldDescription fd = fields[iField];
            b.append("\n\t");
            b.append("@Field(").append(iField).append(") ");
            if (fd.isCLong) {
                b.append("@CLong ");
            } else if (fd.isSizeT) {
                b.append("@Ptr ");
            }
            b.append(StructUtils.describe(fd.valueType)).append(" ").append(fd.name).append("; ");

            b.append("// ");
            b.append("offset = ").append(fd.byteOffset).append(", ");
            b.append("length = ").append(fd.byteLength).append(", ");
            if (fd.bitOffset != 0) {
                b.append("bitOffset = ").append(fd.bitOffset).append(", ");
            }
            if (fd.bitLength != -1) {
                b.append("bitLength = ").append(fd.bitLength).append(", ");
            }
            if (fd.arrayLength != 1) {
                b.append("arrayLength = ").append(fd.arrayLength).append(", ");
            }
            if (fd.alignment != 1) {
                b.append("alignment = ").append(fd.alignment);//.append(", ");
            }
        }
        b.append("\n}");
        return b.toString();
    }
}
