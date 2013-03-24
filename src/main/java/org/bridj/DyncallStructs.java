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


import org.bridj.StructFieldDeclaration;
import java.lang.reflect.Type;
import java.util.List;
import org.bridj.StructFieldDescription;
import org.bridj.util.Utils;
import static org.bridj.dyncall.DyncallLibrary.*;

class DyncallStructs {
    Pointer<DCstruct> struct;
    
    static int toDCAlignment(long structIOAlignment) {
        return structIOAlignment <= 0 ? DEFAULT_ALIGNMENT : (int)structIOAlignment;
    }
    public static Pointer<DCstruct> buildDCstruct(StructDescription desc) {
        if (!BridJ.Switch.StructsByValue.enabled)
            return null;
        
        List<StructFieldDescription> aggregatedFields = desc.getAggregatedFields();
        Pointer<DCstruct> struct = dcNewStruct(aggregatedFields.size(), toDCAlignment(desc.getStructAlignment())).withReleaser(new Pointer.Releaser() {
            public void release(Pointer<?> p) {
                dcFreeStruct(p.as(DCstruct.class));
            }
        });
        fillDCstruct(desc.structType, struct, aggregatedFields);
        dcCloseStruct(struct);
        
        long expectedSize = desc.getStructSize();
        long size = dcStructSize(struct);
        
        if (expectedSize != size) {
            BridJ.error("Struct size computed for " + Utils.toString(desc.structType) + " by BridJ (" + expectedSize + " bytes) and dyncall (" + size + " bytes) differ !");
            return null;
        }
        return struct;
    }
    protected static void fillDCstruct(Type structType, Pointer<DCstruct> struct, List<StructFieldDescription> aggregatedFields) {
        for (StructFieldDescription aggregatedField : aggregatedFields) {
            StructFieldDeclaration field = aggregatedField.aggregatedFields.get(0);
            Type fieldType = field.desc.nativeTypeOrPointerTargetType;
            if (fieldType == null)
                fieldType = field.desc.valueType;
            Class fieldClass = Utils.getClass(fieldType);

            int alignment = toDCAlignment(aggregatedField.alignment);
            long arrayLength = field.desc.arrayLength;
            
            if (StructObject.class.isAssignableFrom(fieldClass)) {
                StructIO subIO = StructIO.getInstance(fieldClass, fieldType);
                List<StructFieldDescription> subAggregatedFields = subIO.desc.getAggregatedFields();
        
                dcSubStruct(struct, subAggregatedFields.size(), alignment, arrayLength);
                try {
                    fillDCstruct(subIO.desc.structType, struct, subAggregatedFields);
                } finally {
                    dcCloseStruct(struct);
                }
            } else {
                int dctype;
                if (fieldClass == int.class)
                    dctype = DC_SIGCHAR_INT;
                else if (fieldClass == long.class || fieldClass == Long.class) {
                    // TODO handle weird cases
                    dctype = DC_SIGCHAR_LONGLONG;
                } else if (fieldClass == short.class || fieldClass == char.class || fieldClass == Short.class || fieldClass == Character.class)
                    dctype = DC_SIGCHAR_SHORT;
                else if (fieldClass == byte.class || fieldClass == boolean.class || fieldClass == Byte.class || fieldClass == Boolean.class)
                    dctype = DC_SIGCHAR_CHAR; // handle @IntBool annotation ?
                else if (fieldClass == float.class || fieldClass == Float.class)
                    dctype = DC_SIGCHAR_FLOAT;
                else if (fieldClass == double.class || fieldClass == Double.class)
                    dctype = DC_SIGCHAR_DOUBLE;
                else if (Pointer.class.isAssignableFrom(fieldClass))
                    dctype = DC_SIGCHAR_POINTER;
                else
                    throw new IllegalArgumentException("Unable to create dyncall struct field for type " + Utils.toString(fieldType) + " in struct " + Utils.toString(structType));

                dcStructField(struct, dctype, alignment, arrayLength);
            }
        }
    }
}
