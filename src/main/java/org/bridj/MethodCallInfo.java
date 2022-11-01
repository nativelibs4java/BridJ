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

import static org.bridj.dyncall.DyncallLibrary.DC_CALL_C_DEFAULT;
import static org.bridj.dyncall.DyncallLibrary.DC_CALL_C_X86_WIN32_FAST_GNU;
import static org.bridj.dyncall.DyncallLibrary.DC_CALL_C_X86_WIN32_FAST_MS;
import static org.bridj.dyncall.DyncallLibrary.DC_CALL_C_X86_WIN32_STD;
import static org.bridj.dyncall.DyncallLibrary.DC_CALL_C_X86_WIN32_THIS_MS;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_BOOL;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_CC_FASTCALL_GNU;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_CC_FASTCALL_MS;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_CC_PREFIX;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_CC_STDCALL;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_CC_THISCALL_MS;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_CHAR;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_DOUBLE;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_FLOAT;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_INT;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_LONG;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_LONGLONG;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_POINTER;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_SHORT;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_STRUCT;
import static org.bridj.dyncall.DyncallLibrary.DC_SIGCHAR_VOID;
import static org.bridj.util.AnnotationUtils.getAnnotation;
import static org.bridj.util.AnnotationUtils.getInheritableAnnotation;
import static org.bridj.util.AnnotationUtils.isAnnotationPresent;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.bridj.NativeConstants.ValueType;
import org.bridj.ann.Constructor;
//import org.bridj.cpp.CPPObject;
import org.bridj.ann.Convention;
import org.bridj.ann.DisableDirect;
import org.bridj.ann.Ptr;
import org.bridj.ann.SetsLastError;
import org.bridj.ann.Static;
import org.bridj.ann.Virtual;
import org.bridj.dyncall.DyncallLibrary.DCstruct;
import org.bridj.util.Utils;

/**
 * Internal class that encapsulate all the knowledge about a native method call
 * : signatures (ASM, dyncall and Java), calling convention, context...
 *
 * @author Olivier
 */
public class MethodCallInfo {

    /*public static class GenericMethodInfo {
     Type returnType, paramsTypes[];
     }
     GenericMethodInfo genericInfo = new GenericMethodInfo();*/
    List<CallIO> callIOs;
    private Class<?> declaringClass;
    long nativeClass;
    int returnValueType, paramsValueTypes[];
    Method method;//, definition;
    String methodName, symbolName;
    private long forwardedPointer;
    String dcSignature;
    String javaSignature;
    String asmSignature;
    Object javaCallback;
    boolean isGenericCallback;
    boolean isObjCBlock;
    int virtualIndex = -1;
    int virtualTableOffset = 0;
    private int dcCallingConvention = DC_CALL_C_DEFAULT;
    boolean isVarArgs;
    boolean isStatic;
    boolean isCPlusPlus;
    boolean direct;
    boolean startsWithThis;
    boolean bNeedsThisPointer;
    boolean throwsLastError;
    boolean setsLastError;

    public MethodCallInfo(Method method) {
        this(method, method);
    }

    static boolean derivesFrom(Class<?> c, String className) {
        while (c != null) {
            if (c.getName().equals(className)) {
                return true;
            }
            c = c.getSuperclass();
        }
        return false;
    }

    public MethodCallInfo(Type genericReturnType, Type[] parameterTypes, boolean prependJNIPointers) {
        this(genericReturnType, new Annotation[0], parameterTypes, new Annotation[parameterTypes.length][], prependJNIPointers);
    }

    public MethodCallInfo(Type genericReturnType, Annotation[] returnAnnotations, Type[] parameterTypes, Annotation[][] paramsAnnotations, boolean prependJNIPointers) {
        init(null, Utils.getClass(genericReturnType), genericReturnType, returnAnnotations, Utils.getClasses(parameterTypes), parameterTypes, paramsAnnotations, prependJNIPointers, false, true);
    }

    public MethodCallInfo(Method method, Method definition) {
        this.setMethod(method);
        //this.setDefinition(definition);
        this.setDeclaringClass(method.getDeclaringClass());
        symbolName = methodName;

        int modifiers = method.getModifiers();
        isStatic = method.getAnnotation(Static.class) != null 
            || Modifier.isStatic(modifiers);
        isVarArgs = method.isVarArgs();
        boolean isNative = Modifier.isNative(modifiers);
        boolean isVirtual = isAnnotationPresent(Virtual.class, definition);
        boolean isDirectModeAllowed =
                getInheritableAnnotation(DisableDirect.class, definition) == null
                && BridJ.isDirectModeEnabled();

        isCPlusPlus = !isStatic && derivesFrom(method.getDeclaringClass(), "org.bridj.cpp.CPPObject");
        isObjCBlock = !isStatic && derivesFrom(method.getDeclaringClass(), "org.bridj.objc.ObjCBlock");

        init(
                method,
                method.getReturnType(), method.getGenericReturnType(), method.getAnnotations(),
                method.getParameterTypes(), method.getGenericParameterTypes(), method.getParameterAnnotations(),
                isNative,
                isVirtual,
                isDirectModeAllowed);

        Convention cc = getInheritableAnnotation(Convention.class, definition);
        if (cc != null) {
            setCallingConvention(cc.value());
        }
        List<Class<?>> exceptionTypes = Arrays.asList(definition.getExceptionTypes());
        if (!exceptionTypes.isEmpty()) {
            this.direct = false; // there is no crash / exception protection for direct raw calls
            if (exceptionTypes.contains(LastError.class)) {
                this.throwsLastError = true;
                this.setsLastError = true;
            }
        }
        if (getInheritableAnnotation(SetsLastError.class, definition) != null) {
            this.direct = false; // there is no crash / exception protection for direct raw calls
            this.setsLastError = true;
        }

    }

    protected void init(AnnotatedElement annotatedElement, Class<?> returnType, Type genericReturnType, Annotation[] returnAnnotations, Class<?>[] parameterTypes, Type[] genericParameterTypes, Annotation[][] paramsAnnotations, boolean prependJNIPointers, boolean isVirtual, boolean isDirectModeAllowed) {
        assert returnType != null;
        assert genericReturnType != null;
        assert parameterTypes != null;
        assert genericParameterTypes != null;
        assert returnAnnotations != null;
        assert parameterTypes.length == genericParameterTypes.length;
        assert paramsAnnotations.length == genericParameterTypes.length;

        int nParams = genericParameterTypes.length;
        paramsValueTypes = new int[nParams];

        direct = isDirectModeAllowed; // TODO on native side : test number of parameters (on 64 bits win : must be <= 4)

        StringBuilder javaSig = new StringBuilder(64),
                asmSig = new StringBuilder(64),
                dcSig = new StringBuilder(16);
        javaSig.append('(');
        asmSig.append('(');
        if (prependJNIPointers)//!isCPlusPlus)
        {
            dcSig.append(DC_SIGCHAR_POINTER).append(DC_SIGCHAR_POINTER); // JNIEnv*, jobject: always present in native-bound functions
        }
        if (BridJ.debug) {
            BridJ.info("Analyzing " + (declaringClass == null ? "anonymous method" : declaringClass.getName() + "." + methodName));
        }

        if (isObjCBlock) {
            appendToSignature(0, ValueType.ePointerValue, Pointer.class, Pointer.class, null, dcSig, null);
        }

        for (int iParam = 0; iParam < nParams; iParam++) {
//            Options paramOptions = paramsOptions[iParam] = new Options();
            Type genericParameterType = genericParameterTypes[iParam];
            Class<?> parameterType = parameterTypes[iParam];

            ValueType paramValueType = getValueType(iParam, nParams, parameterType, genericParameterType, null, paramsAnnotations[iParam]);
            if (BridJ.veryVerbose) {
                BridJ.info("\tparam " + paramValueType);
            }
            paramsValueTypes[iParam] = paramValueType.ordinal();

            appendToSignature(iParam, paramValueType, parameterType, genericParameterType, javaSig, dcSig, asmSig);
        }
        javaSig.append(')');
        asmSig.append(')');
        dcSig.append(')');

        ValueType retType = getValueType(-1, nParams, returnType, genericReturnType, annotatedElement, returnAnnotations);
        if (BridJ.veryVerbose) {
            BridJ.info("\treturns " + retType);
        }
        appendToSignature(-1, retType, returnType, genericReturnType, javaSig, dcSig, asmSig);
        returnValueType = retType.ordinal();

        javaSignature = javaSig.toString();
        asmSignature = asmSig.toString();
        dcSignature = dcSig.toString();

        isCPlusPlus = isCPlusPlus || isVirtual;

        if (isCPlusPlus && !isStatic) {
            if (!startsWithThis) {
                direct = false;
            }
            bNeedsThisPointer = true;
            if (Platform.isWindows()) {
                if (!Platform.is64Bits()) {
                    setDcCallingConvention(DC_CALL_C_X86_WIN32_THIS_MS);
                }
            } else {
                //if (!Platform.is64Bits())
                //	setDcCallingConvention(DC_CALL_C_X86_WIN32_THIS_GNU);
            }
        }

        if (nParams > Platform.getMaxDirectMappingArgCount()) {
            this.direct = false;
        }

        if (BridJ.veryVerbose) {
            BridJ.info("\t-> direct " + direct);
            BridJ.info("\t-> javaSignature " + javaSignature);
            BridJ.info("\t-> callIOs " + callIOs);
            BridJ.info("\t-> asmSignature " + asmSignature);
            BridJ.info("\t-> dcSignature " + dcSignature);
        }

        if (BridJ.veryVerbose) {
            BridJ.info((direct ? "[mappable as direct] " : "[not mappable as direct] ") + method);
        }
    }
    boolean hasCC;

    public boolean hasCallingConvention() {
        return hasCC;
    }

    public void setCallingConvention(Convention.Style style) {
        if (style == null) {
            return;
        }

        if (!Platform.isWindows() || Platform.is64Bits()) {
            return;
        }

        switch (style) {
            case FastCall:
                this.direct = false;
                setDcCallingConvention(Platform.isWindows() ? DC_CALL_C_X86_WIN32_FAST_MS : DC_CALL_C_DEFAULT); // TODO allow GCC-compiled C++ libs on windows
                break;
            case Pascal:
            case StdCall:
                this.direct = false;
                setDcCallingConvention(DC_CALL_C_X86_WIN32_STD);
                break;
            case ThisCall:
                this.direct = false;
                setDcCallingConvention(Platform.isWindows() ? DC_CALL_C_X86_WIN32_THIS_MS : DC_CALL_C_DEFAULT);
            default:
        }
        if (BridJ.veryVerbose) {
            BridJ.info("Setting CC " + style + " (-> " + dcCallingConvention + ") for " + methodName);
        }

    }

    void addCallIO(CallIO handler) {
        if (callIOs == null) {
            callIOs = new ArrayList<CallIO>();
        }
        assert handler != null;
        callIOs.add(handler);
    }

    public CallIO[] getCallIOs() {
        if (callIOs == null) {
            return new CallIO[0];
        }
        return callIOs.toArray(new CallIO[callIOs.size()]);
    }

    public void prependCallbackCC() {
        char cc = getDcCallbackConvention(getDcCallingConvention());
        if (cc == 0) {
            return;
        }

        dcSignature = String.valueOf(DC_SIGCHAR_CC_PREFIX) + String.valueOf(cc) + dcSignature;
    }

    public String getDcSignature() {
        return dcSignature;
    }

    public String getJavaSignature() {
        return javaSignature;
    }

    public String getASMSignature() {
        return asmSignature;
    }

    boolean getBoolAnnotation(Class<? extends Annotation> ac, AnnotatedElement element, Annotation... directAnnotations) {
        Annotation ann = getAnnotation(ac, element, directAnnotations);
        return ann != null;
    }

    @SuppressWarnings("unchecked")
    public ValueType getValueType(int iParam, int nParams, Class<?> c, Type t, AnnotatedElement element, Annotation... directAnnotations) {
        boolean isPtr = isAnnotationPresent(Ptr.class, element, directAnnotations);
        boolean isCLong = isAnnotationPresent(org.bridj.ann.CLong.class, element, directAnnotations);
        Constructor cons = getAnnotation(Constructor.class, element, directAnnotations);

        if (isPtr || cons != null || isCLong) {
            if (!(c == Long.class || c == Long.TYPE)) {
                throw new RuntimeException("Annotation should only be used on a long parameter, not on a " + c.getName());
            }

            if (isPtr) {
                if (!Platform.is64Bits()) {
                    direct = false;
                }
            } else if (isCLong) {
                if (Platform.CLONG_SIZE != 8) {
                    direct = false;
                }
            } else if (cons != null) {
                isCPlusPlus = true;
                startsWithThis = true;
                if (iParam != 0) {
                    throw new RuntimeException("Annotation " + Constructor.class.getName() + " cannot have more than one (long) argument");
                }
            }
            return isCLong ? ValueType.eCLongValue : ValueType.eSizeTValue;
        }
        if (c == null || c.equals(Void.TYPE)) {
            return ValueType.eVoidValue;
        }
        if (c == Integer.class || c == Integer.TYPE) {
            return ValueType.eIntValue;
        }
        if (c == Long.class || c == Long.TYPE) {
            return !isPtr || Platform.is64Bits() ? ValueType.eLongValue : ValueType.eIntValue;
        }
        if (c == Short.class || c == Short.TYPE) {
            return ValueType.eShortValue;
        }
        if (c == Byte.class || c == Byte.TYPE) {
            return ValueType.eByteValue;
        }
        if (c == Boolean.class || c == Boolean.TYPE) {
            return ValueType.eBooleanValue;
        }
        if (c == Float.class || c == Float.TYPE) {
            usesFloats();
            return ValueType.eFloatValue;
        }
        if (c == char.class || c == Character.TYPE) {
            if (Platform.WCHAR_T_SIZE != 2) {
                direct = false;
            }
            return ValueType.eWCharValue;
        }
        if (c == Double.class || c == Double.TYPE) {
            usesFloats();
            return ValueType.eDoubleValue;
        }
        if (c == CLong.class) {
            direct = false;
            return ValueType.eCLongObjectValue;
        }
        if (c == SizeT.class) {
            direct = false;
            return ValueType.eSizeTObjectValue;
        }
        if (c == TimeT.class) {
            direct = false;
            return ValueType.eTimeTObjectValue;
        }
        if (Pointer.class.isAssignableFrom(c)) {
            direct = false;
            CallIO cio = CallIO.Utils.createPointerCallIO(c, t);
            if (BridJ.veryVerbose) {
                BridJ.info("CallIO : " + cio);
            }
            addCallIO(cio);
            return ValueType.ePointerValue;
        }
        if (c.isArray() && iParam == nParams - 1) {
            direct = false;
            return ValueType.eEllipsis;
        }
        if (ValuedEnum.class.isAssignableFrom(c)) {
            direct = false;
            @SuppressWarnings({ "rawtypes" })
            CallIO cio = CallIO.Utils.createValuedEnumCallIO((Class)Utils.getClass(Utils.getUniqueParameterizedTypeParameter(t)));
            if (BridJ.veryVerbose) {
                BridJ.info("CallIO : " + cio);
            }
            addCallIO(cio);

            return ValueType.eIntFlagSet;
        }
        if (NativeObject.class.isAssignableFrom(c)) {
            Pointer<DCstruct> pStruct = null;
            if (StructObject.class.isAssignableFrom(c)) {
                StructIO io = StructIO.getInstance(c, t);
                try {
                    pStruct = DyncallStructs.buildDCstruct(io.desc);
                } catch (Throwable th) {
                    BridJ.error("Unable to create low-level struct metadata for " + Utils.toString(t) + " : won't be able to use it as a by-value function argument.", th);
                }
            }
            addCallIO(new CallIO.NativeObjectHandler((Class<? extends NativeObject>) c, t, pStruct));
            direct = false;
            return ValueType.eNativeObjectValue;
        }

        throw new NoSuchElementException("No " + ValueType.class.getSimpleName() + " for class " + c.getName());
    }

    void usesFloats() {
        /*
         if (direct && Platform.isMacOSX()) {
         direct = false;
         assert BridJ.warning("[unstable direct] FIXME Disable direct call due to float/double usage in " + method);
         }
         */
    }

    public void appendToSignature(int iParam, ValueType type, Class<?> parameterType, Type genericParameterType, StringBuilder javaSig, StringBuilder dcSig, StringBuilder asmSig) {
        char dcChar;
        String javaChar, asmChar = null;
        switch (type) {
            case eVoidValue:
                dcChar = DC_SIGCHAR_VOID;
                javaChar = "V";
                break;
            case eIntValue:
                dcChar = DC_SIGCHAR_INT;
                javaChar = "I";
                break;
            case eLongValue:
                dcChar = DC_SIGCHAR_LONGLONG;
                javaChar = "J";
                break;
            case eCLongValue:
                javaChar = "J";
				dcChar = DC_SIGCHAR_LONG;
                if (Platform.CLONG_SIZE != 8) {
                    direct = false;
                }
                break;
            case eSizeTValue:
                javaChar = "J";
                if (Platform.SIZE_T_SIZE == 8) {
                    dcChar = DC_SIGCHAR_LONGLONG;
                } else {
                    dcChar = DC_SIGCHAR_INT;
                    direct = false;
                }
                break;
            case eShortValue:
                dcChar = DC_SIGCHAR_SHORT;
                javaChar = "S";
                break;
            case eDoubleValue:
                dcChar = DC_SIGCHAR_DOUBLE;
                javaChar = "D";
                break;
            case eFloatValue:
                dcChar = DC_SIGCHAR_FLOAT;
                javaChar = "F";
                break;
            case eByteValue:
                dcChar = DC_SIGCHAR_CHAR;
                javaChar = "B";
                break;
            case eBooleanValue:
                dcChar = DC_SIGCHAR_BOOL;
                javaChar = "Z";
                break;
            case eWCharValue:
                switch (Platform.WCHAR_T_SIZE) {
                    case 1:
                        dcChar = DC_SIGCHAR_CHAR;
                        direct = false;
                        break;
                    case 2:
                        dcChar = DC_SIGCHAR_SHORT;
                        break;
                    case 4:
                        dcChar = DC_SIGCHAR_INT;
                        direct = false;
                        break;
                    default:
                        throw new RuntimeException("Unhandled sizeof(wchar_t) in GetJavaTypeSignature: " + Platform.WCHAR_T_SIZE);
                }
                javaChar = "C";
                break;
            case eIntFlagSet:
                dcChar = DC_SIGCHAR_INT;
                javaChar = "L" + parameterType.getName().replace('.', '/') + ";";//"Lorg/bridj/ValuedEnum;";
                direct = false;
                break;
            case eCLongObjectValue:
                dcChar = DC_SIGCHAR_POINTER;
                javaChar = "Lorg/bridj/CLong;";
                direct = false;
                break;
            case eSizeTObjectValue:
                dcChar = DC_SIGCHAR_POINTER;
                javaChar = "Lorg/bridj/SizeT;";
                direct = false;
                break;
            case eTimeTObjectValue:
                dcChar = DC_SIGCHAR_POINTER;
                javaChar = "Lorg/bridj/TimeT;";
                direct = false;
                break;
            case ePointerValue:
                dcChar = DC_SIGCHAR_POINTER;
                javaChar = "L" + parameterType.getName().replace('.', '/') + ";";
//                javaChar = "Lorg/bridj/Pointer;";
                direct = false;
                break;
            case eNativeObjectValue:
                dcChar = DC_SIGCHAR_STRUCT; // TODO : unroll struct signature ?
                javaChar = "L" + parameterType.getName().replace('.', '/') + ";";
                direct = false;
//              if (parameterType.equals(declaringClass)) {
//                    // special case of self-returning pointers
//                    dcChar = DC_SIGCHAR_POINTER;
                break;
            case eEllipsis:
                javaChar = "[Ljava/lang/Object;";
                dcChar = '?';
                break;
            default:
                direct = false;
                throw new RuntimeException("Unhandled " + ValueType.class.getSimpleName() + ": " + type);
        }
        if (genericParameterType instanceof ParameterizedType && iParam < 0) {
            ParameterizedType pt = (ParameterizedType) genericParameterType;
            // TODO handle all cases !!!
            Type[] ts = pt.getActualTypeArguments();
            if (ts != null && ts.length == 1) {
                Type t = ts[0];
                if (t instanceof ParameterizedType) {
                    t = ((ParameterizedType) t).getRawType();
                }
                if (t instanceof Class) {
                    Class<?> c = (Class<?>) t;
                    if (javaChar.endsWith(";")) {
                        asmChar = javaChar.substring(0, javaChar.length() - 1) + "<*L" + c.getName().replace('.', '/') + ";>";
                        //asmChar += ";";
                    }
                }
            }
        }
        if (javaSig != null) {
            javaSig.append(javaChar);
        }
        if (asmChar == null) {
            asmChar = javaChar;
        }
        if (asmSig != null) {
            asmSig.append(asmChar);
        }
        if (dcSig != null) {
            dcSig.append(dcChar);
        }
    }
    /*
     public void setDefinition(Method definition) {
     this.definition = definition;
     }

     public Method getDefinition() {
     return definition;
     }
     */

    public void setMethod(Method method) {
        this.method = method;
        if (method != null) {
            this.methodName = method.getName();
        }
        if (declaringClass == null) {
            setDeclaringClass(method.getDeclaringClass());
        }

    }

    public void setJavaSignature(String javaSignature) {
        this.javaSignature = javaSignature;
    }

    public Method getMethod() {
        return method;
    }

    public void setDeclaringClass(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public void setForwardedPointer(long forwardedPointer) {
        this.forwardedPointer = forwardedPointer;
    }

    public long getForwardedPointer() {
        return forwardedPointer;
    }

    /**
     * Used for C++ virtual indexes and for struct fields ids
     *
     * @param virtualIndex
     */
    public void setVirtualIndex(int virtualIndex) {
        //new RuntimeException("Setting virtualIndex of " + getMethod().getName() + " = " + virtualIndex).printStackTrace();
        this.virtualIndex = virtualIndex;

        if (BridJ.veryVerbose) {
            BridJ.info("\t-> virtualIndex " + virtualIndex);
        }
    }

    public int getVirtualIndex() {
        return virtualIndex;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    static char getDcCallbackConvention(int dcCallingConvention) {
        switch (dcCallingConvention) {
            case DC_CALL_C_X86_WIN32_STD:
                return DC_SIGCHAR_CC_STDCALL;
            case DC_CALL_C_X86_WIN32_FAST_MS:
                return DC_SIGCHAR_CC_FASTCALL_MS;
            case DC_CALL_C_X86_WIN32_FAST_GNU:
                return DC_SIGCHAR_CC_FASTCALL_GNU;
            case DC_CALL_C_X86_WIN32_THIS_MS:
                return DC_SIGCHAR_CC_THISCALL_MS;
            default:
                return 0;
        }
    }

    public void setDcCallingConvention(int dcCallingConvention) {
        hasCC = true;
        this.dcCallingConvention = dcCallingConvention;
    }

    public int getDcCallingConvention() {
        return dcCallingConvention;
    }

    public Object getJavaCallback() {
        return javaCallback;
    }

    public void setJavaCallback(Object javaCallback) {
        this.javaCallback = javaCallback;
    }

    public void setGenericCallback(boolean genericCallback) {
        this.isGenericCallback = genericCallback;
    }

    public boolean isGenericCallback() {
        return isGenericCallback;
    }

    public void setNativeClass(long nativeClass) {
        this.nativeClass = nativeClass;
    }
}
