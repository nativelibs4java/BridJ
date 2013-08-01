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

import org.bridj.util.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.bridj.Pointer.*;
import org.bridj.ann.Alignment;
import org.bridj.ann.Struct;

class StructUtils {

    static long alignSize(long size, long alignment) {
        if (alignment > 1) {
            long r = size % alignment;
            if (r != 0) {
                size += alignment - r;
            }
        }
        return size;
    }

    /**
     * Orders the fields to match the actual structure layout
     */
    static void orderFields(List<StructFieldDeclaration> fields) {
        Collections.sort(fields, new Comparator<StructFieldDeclaration>() {
            //@Override
            public int compare(StructFieldDeclaration o1, StructFieldDeclaration o2) {
                long d = o1.index - o2.index;
                if (d != 0) {
                    return d < 0 ? -1 : d == 0 ? 0 : 1;
                }

                if (o1.declaringClass.isAssignableFrom(o2.declaringClass)) {
                    return -1;
                }
                if (o2.declaringClass.isAssignableFrom(o1.declaringClass)) {
                    return 1;
                }

                throw new RuntimeException("Failed to order fields " + o2.desc.name + " and " + o2.desc.name);
            }
        });
    }

    static long primTypeAlignment(Class<?> primType, long length) {
        if (isDouble(primType)
                && !BridJ.alignDoubles
                && Platform.isLinux()
                && !Platform.is64Bits()) {
            return 4;
        }
        return length;
    }

    static boolean isDouble(Class<?> primType) {
        return primType == Double.class || primType == double.class;
    }

    static int primTypeLength(Class<?> primType) {
        if (primType == Integer.class || primType == int.class) {
            return 4;
        } else if (primType == Long.class || primType == long.class) {
            return 8;
        } else if (primType == Short.class || primType == short.class) {
            return 2;
        } else if (primType == Byte.class || primType == byte.class) {
            return 1;
        } else if (primType == Character.class || primType == char.class) {
            return 2;
        } else if (primType == Boolean.class || primType == boolean.class) {
            // BOOL is int, not C++'s bool !
            //   if (Platform.isWindows())
            //     return 4;
            //   else
            return 1;
        } else if (primType == Float.class || primType == float.class) {
            return 4;
        } else if (isDouble(primType)) {
            return 8;
        } else if (Pointer.class.isAssignableFrom(primType)) {
            return Pointer.SIZE;
        } else {
            throw new UnsupportedOperationException("Field type " + primType.getName() + " not supported yet");
        }

    }

    static long alignmentOf(Type tpe) {
        Class c = PointerIO.getClass(tpe);
        if (StructObject.class.isAssignableFrom(c)) {
            return StructIO.getInstance(c).desc.getStructAlignment();
        }
        return BridJ.sizeOf(tpe);
    }

    static int compare(StructObject a, StructObject b, SolidRanges solidRanges) {
        Pointer<StructObject> pA = getPointer(a), pB = getPointer(b);
        if (pA == null || pB == null) {
            return pA != null ? 1 : pB != null ? -1 : 0;
        }

        long[] offsets = solidRanges.offsets, lengths = solidRanges.lengths;
        for (int i = 0, n = offsets.length; i < n; i++) {
            long offset = offsets[i], length = lengths[i];
            int cmp = pA.compareBytesAtOffset(offset, pB, offset, length);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    static String describe(StructObject struct, Type structType, StructFieldDescription[] fields) {
        StringBuilder b = new StringBuilder();
        b.append(describe(structType)).append(" { ");
        for (StructFieldDescription fd : fields) {
            b.append("\n\t").append(fd.name).append(" = ");
            try {
                Object value;
                if (fd.getter != null) {
                    value = fd.getter.invoke(struct);
                } else {
                    value = fd.field.get(struct);
                }

                if (value instanceof String) {
                    b.append('"').append(value.toString().replaceAll("\"", "\\\"")).append('"');
                } else if (value instanceof Character) {
                    b.append('\'').append(value).append('\'');
                } else if (value instanceof NativeObject) {
                    String d = BridJ.describe((NativeObject) value);
                    b.append(d.replaceAll("\n", "\n\t"));
                } else {
                    b.append(value);
                }
            } catch (Throwable th) {
                if (BridJ.debug) {
                    th.printStackTrace();
                }
                b.append("?");
            }
            b.append("; ");
        }
        b.append("\n}");
        return b.toString();
    }

    static String describe(Type t) {
        if (t == null) {
            return "?";
        }
        if (t instanceof Class) {
            return ((Class) t).getSimpleName();
        }
        return t.toString().
                replaceAll("\\bjava\\.lang\\.", "").
                replaceAll("\\borg\\.bridj\\.cpp\\.com\\.", "").
                replaceAll("\\borg\\.bridj\\.Pointer\\b", "Pointer");

    }

    static Pointer fixIntegralTypeIOToMatchLength(Pointer ptr, long byteLength, long arrayLength) {
        long targetSize = ptr.getTargetSize();
        if (targetSize * arrayLength == byteLength) {
            return ptr;
        }

        Type targetType = ptr.getTargetType();
        if (!Utils.isSignedIntegral(targetType)) {
            return ptr;
        }
        //throw new UnsupportedOperationException("Cannot change byte length of non-signed-integral fields (field type = " + Utils.toString(targetType) + ", target size = " + ptr.getTargetSize() + ", byteLength = " + byteLength + ")");

        switch ((int) byteLength) {
            case 1:
                return ptr.as(byte.class);
            case 2:
                return ptr.as(short.class);
            case 4:
                return ptr.as(int.class);
            case 8:
                return ptr.as(long.class);
            default:
                return ptr; // this case happens... TODO check // throw new RuntimeException("Invalid integral type byte length : " + byteLength);
        }
    }

    protected static void computeStructLayout(StructDescription desc, StructCustomizer customizer) {
        List<StructFieldDeclaration> fieldDecls = StructFieldDeclaration.listFields(desc.structClass);
        orderFields(fieldDecls);

        customizer.beforeAggregation(desc, fieldDecls);

        Map<Pair<Class<?>, Long>, List<StructFieldDeclaration>> fieldsMap = new LinkedHashMap<Pair<Class<?>, Long>, List<StructFieldDeclaration>>();
        for (StructFieldDeclaration field : fieldDecls) {
            if (field.index < 0) {
                throw new RuntimeException("Negative field index not allowed for field " + field.desc.name);
            }

            long index = field.unionWith >= 0 ? field.unionWith : field.index;
            Pair<Class<?>, Long> key = new Pair<Class<?>, Long>(field.declaringClass, index);
            List<StructFieldDeclaration> siblings = fieldsMap.get(key);
            if (siblings == null) {
                fieldsMap.put(key, siblings = new ArrayList<StructFieldDeclaration>());
            }
            siblings.add(field);
        }

        Alignment alignment = desc.structClass.getAnnotation(Alignment.class);
        desc.structAlignment = alignment != null ? alignment.value() : 1; //TODO get platform default alignment

        List<StructFieldDescription> aggregatedFields = new ArrayList<StructFieldDescription>();
        for (List<StructFieldDeclaration> fieldGroup : fieldsMap.values()) {
            StructFieldDescription aggregatedField = StructFieldDescription.aggregateDeclarations(desc.structType, fieldGroup);
            if (aggregatedField != null) {
                aggregatedFields.add(aggregatedField);
            }
        }
        desc.setAggregatedFields(aggregatedFields);

        // Last chance to remove fields :
        customizer.beforeLayout(desc, aggregatedFields);

        performLayout(desc, aggregatedFields);
        customizer.afterLayout(desc, aggregatedFields);

        List<StructFieldDescription> fieldDescs = new ArrayList<StructFieldDescription>();
        SolidRanges.Builder rangesBuilder = new SolidRanges.Builder();
        for (StructFieldDescription aggregatedField : aggregatedFields) {
            for (StructFieldDeclaration field : aggregatedField.aggregatedFields) {//fieldGroup) {
                // Propagate offsets of aggregated field descs to field decls's descs
                StructFieldDescription fieldDesc = field.desc;
                fieldDesc.byteOffset = aggregatedField.byteOffset;
                fieldDesc.byteLength = aggregatedField.byteLength;
                fieldDesc.bitOffset = aggregatedField.bitOffset;

                fieldDescs.add(fieldDesc);
                rangesBuilder.add(fieldDesc);

                desc.hasFieldFields = desc.hasFieldFields || fieldDesc.field != null;
            }
        }
        desc.solidRanges = rangesBuilder.toSolidRanges();
        desc.fields = fieldDescs.toArray(new StructFieldDescription[fieldDescs.size()]);
    }

    static void performLayout(StructDescription desc, Iterable<StructFieldDescription> aggregatedFields) {
        long structSize = 0;
        long structAlignment = -1;

        Struct s = desc.structClass.getAnnotation(Struct.class);
        int pack = s != null ? s.pack() : -1;

        if (desc.isVirtual()) {
            structSize += Pointer.SIZE;
            if (Pointer.SIZE >= structAlignment) {
                structAlignment = Pointer.SIZE;
            }
        }

        int cumulativeBitOffset = 0;

        for (StructFieldDescription aggregatedField : aggregatedFields) {
            structAlignment = Math.max(structAlignment, aggregatedField.alignment);

            if (aggregatedField.bitLength < 0) {
                // Align fields as appropriate
                if (cumulativeBitOffset != 0) {
                    cumulativeBitOffset = 0;
                    structSize++;
                }
                //structAlignment = Math.max(structAlignment, aggregatedField.alignment);
                structSize = alignSize(structSize, pack > 0 ? pack : aggregatedField.alignment);
            }
            long fieldByteOffset = structSize,
                    fieldBitOffset = cumulativeBitOffset;

            if (aggregatedField.bitLength >= 0) {
                //fieldByteLength = (aggregatedField.bitLength >>> 3) + ((aggregatedField.bitLength & 7) != 0 ? 1 : 0);
                cumulativeBitOffset += aggregatedField.bitLength;
                structSize += cumulativeBitOffset >>> 3;
                cumulativeBitOffset &= 7;
            } else {
                structSize += aggregatedField.byteLength;
            }

            aggregatedField.byteOffset = fieldByteOffset;
            aggregatedField.bitOffset = fieldBitOffset;
        }

        if (cumulativeBitOffset > 0) {
            structSize = alignSize(structSize + 1, structAlignment);
        } else if (structSize > 0) {
            structSize = alignSize(structSize, pack > 0 ? pack : structAlignment);
        }

        desc.structSize = structSize;
        desc.structAlignment = structAlignment;
    }
}
