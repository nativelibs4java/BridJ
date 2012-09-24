package org.bridj;


import org.bridj.StructIO.FieldDecl;
import java.lang.reflect.Type;
import java.util.List;
import org.bridj.StructIO.AggregatedFieldDesc;
import org.bridj.util.Utils;
import static org.bridj.dyncall.DyncallLibrary.*;

class DyncallStructs {
    Pointer<DCstruct> struct;
    
    static int toDCAlignment(long structIOAlignment) {
        return structIOAlignment <= 0 ? DEFAULT_ALIGNMENT : (int)structIOAlignment;
    }
    public static Pointer<DCstruct> buildDCstruct(StructIO io) {
        if (!BridJ.Switch.StructsByValue.enabled)
            return null;
        
        List<AggregatedFieldDesc> aggregatedFields = io.getAggregatedFields();
        Pointer<DCstruct> struct = dcNewStruct(aggregatedFields.size(), toDCAlignment(io.getStructAlignment())).withReleaser(new Pointer.Releaser() {
            public void release(Pointer<?> p) {
                dcFreeStruct(p.as(DCstruct.class));
            }
        });
        fillDCstruct(io.structType, struct, aggregatedFields);
        dcCloseStruct(struct);
        
        long expectedSize = io.getStructSize();
        long size = dcStructSize(struct);
        
        if (expectedSize != size) {
            BridJ.error("Struct size computed for " + Utils.toString(io.structType) + " by BridJ (" + expectedSize + " bytes) and dyncall (" + size + " bytes) differ !");
            return null;
        }
        return struct;
    }
    protected static void fillDCstruct(Type structType, Pointer<DCstruct> struct, List<AggregatedFieldDesc> aggregatedFields) {
        for (AggregatedFieldDesc aggregatedField : aggregatedFields) {
            FieldDecl field = aggregatedField.fields.get(0);
            Type fieldType = field.desc.nativeTypeOrPointerTargetType;
            if (fieldType == null)
                fieldType = field.desc.valueType;
            Class fieldClass = Utils.getClass(fieldType);

            int alignment = toDCAlignment(aggregatedField.alignment);
            long arrayLength = field.desc.arrayLength;
            
            if (StructObject.class.isAssignableFrom(fieldClass)) {
                StructIO subIO = StructIO.getInstance(fieldClass, fieldType);
                List<AggregatedFieldDesc> subAggregatedFields = subIO.getAggregatedFields();
        
                dcSubStruct(struct, subAggregatedFields.size(), alignment, arrayLength);
                try {
                    fillDCstruct(subIO.structType, struct, subAggregatedFields);
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
