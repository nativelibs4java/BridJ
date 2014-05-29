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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import static org.bridj.SizeT.safeIntCast;

/**
 * Pointer to a native memory location.<br>
 * Pointer is the entry point of any pointer-related operation in BridJ.
 * <p>
 * <u><b>Manipulating memory</b></u>
 * <p>
 * <ul>
 *	<li>Wrapping a memory address as a pointer : {@link Pointer#pointerToAddress(long)}
 *  </li>
 *	<li>Reading / writing a primitive from / to the pointed memory location :<br>
#foreach ($prim in $primitives)
 *		{@link Pointer#get${prim.CapName}()} / {@link Pointer#set${prim.CapName}(${prim.Name})} <br>
#end
#foreach ($sizePrim in ["SizeT", "CLong"])
 *		{@link Pointer#get${sizePrim}()} / {@link Pointer#set${sizePrim}(long)} <br>
#end
 *</li>
 *	<li>Reading / writing the nth contiguous primitive value from / to the pointed memory location :<br>
#foreach ($prim in $primitives)
 *		{@link Pointer#get${prim.CapName}AtIndex(long)} / {@link Pointer#set${prim.CapName}AtIndex(long, ${prim.Name})} <br>
#end
#foreach ($sizePrim in ["SizeT", "CLong"])
 *	  {@link Pointer#get${sizePrim}AtIndex(long)} / {@link Pointer#set${sizePrim}AtIndex(long, long)} <br>
#end
 *</li>
 *	<li>Reading / writing a primitive from / to the pointed memory location with a byte offset:<br>
#foreach ($prim in $primitives)
 *		{@link Pointer#get${prim.CapName}AtOffset(long)} / {@link Pointer#set${prim.CapName}AtOffset(long, ${prim.Name})} <br>
#end
#foreach ($sizePrim in ["SizeT", "CLong"])
 *		{@link Pointer#get${sizePrim}AtOffset(long)} / {@link Pointer#set${sizePrim}AtOffset(long, long)} <br>
#end
 *</li>
 *	<li>Reading / writing an array of primitives from / to the pointed memory location :<br>
#foreach ($prim in $primitives)
 *		{@link Pointer#get${prim.CapName}s(int)} / {@link Pointer#set${prim.CapName}s(${prim.Name}[])} ; With an offset : {@link Pointer#get${prim.CapName}sAtOffset(long, int)} / {@link Pointer#set${prim.CapName}sAtOffset(long, ${prim.Name}[])}<br>
#end
#foreach ($sizePrim in ["SizeT", "CLong"])
 *		{@link Pointer#get${sizePrim}s(int)} / {@link Pointer#set${sizePrim}s(long[])} ; With an offset : {@link Pointer#get${sizePrim}sAtOffset(long, int)} / {@link Pointer#set${sizePrim}sAtOffset(long, long[])}<br>
#end
 *  </li>
 *	<li>Reading / writing an NIO buffer of primitives from / to the pointed memory location :<br>
#foreach ($prim in $primitivesNoBool)
#if ($prim.Name != "char")*		{@link Pointer#get${prim.BufferName}(long)} (can be used for writing as well) / {@link Pointer#set${prim.CapName}s(${prim.BufferName})}<br>
#end
#end
 *  </li>
 *  <li>Reading / writing a String from / to the pointed memory location using the default charset :<br>
#foreach ($string in ["C", "WideC"])
*		{@link Pointer#get${string}String()} / {@link Pointer#set${string}String(String)} ; With an offset : {@link Pointer#get${string}StringAtOffset(long)} / {@link Pointer#set${string}StringAtOffset(long, String)}<br>
#end
 *  </li>
 *  <li>Reading / writing a String with control on the charset :<br>
 *		{@link Pointer#getStringAtOffset(long, StringType, Charset)} / {@link Pointer#setStringAtOffset(long, String, StringType, Charset)}<br>
 * </ul>
 * <p>
 * <u><b>Allocating memory</b></u>
 * <p>
 * <ul>
 *	<li>Getting the pointer to a struct / a C++ class / a COM object :
 *		{@link Pointer#getPointer(NativeObject)}
 *  </li>
 *  <li>Allocating a dynamic callback (without a static {@link Callback} definition, which would be the preferred way) :<br>
 *      {@link Pointer#allocateDynamicCallback(DynamicCallback, org.bridj.ann.Convention.Style, Type, Type[])}
 *  </li>
 *	<li>Allocating a primitive with / without an initial value (zero-initialized) :<br>
#foreach ($prim in $primitives)
 *		{@link Pointer#pointerTo${prim.CapName}(${prim.Name})} / {@link Pointer#allocate${prim.CapName}()}<br>
#end
#foreach ($sizePrim in ["SizeT", "CLong"])
 *		{@link Pointer#pointerTo${sizePrim}(long)} / {@link Pointer#allocate${sizePrim}()}<br>
#end
 *  </li>
 *	<li>Allocating an array of primitives with / without initial values (zero-initialized) :<br>
#foreach ($prim in $primitivesNoBool)
 *		{@link Pointer#pointerTo${prim.CapName}s(${prim.Name}[])} or {@link Pointer#pointerTo${prim.CapName}s(${prim.BufferName})} / {@link Pointer#allocate${prim.CapName}s(long)}<br>
#end
#foreach ($sizePrim in ["SizeT", "CLong"])
 *		{@link Pointer#pointerTo${sizePrim}s(long[])} / {@link Pointer#allocate${sizePrim}s(long)}<br>
#end
 *		{@link Pointer#pointerToBuffer(Buffer)} / n/a<br>
 *  </li>
 *  <li>Allocating a native String :<br>
#foreach ($string in ["C", "WideC"])
*		{@link Pointer#pointerTo${string}String(String) } (default charset)<br>
#end
 *		{@link Pointer#pointerToString(String, StringType, Charset) }<br>
 *  </li>
 *  <li>Allocating a {@link ListType#Dynamic} Java {@link java.util.List} that uses native memory storage  (think of getting back the pointer with {@link NativeList#getPointer()} when you're done mutating the list):<br>
 *		{@link Pointer#allocateList(Class, long) }
 *  </li>
 *  <li>Transforming a pointer to a Java {@link java.util.List} that uses the pointer as storage (think of getting back the pointer with {@link NativeList#getPointer()} when you're done mutating the list, if it's {@link ListType#Dynamic}) :<br>
 *		{@link Pointer#asList(ListType) }<br>
 *		{@link Pointer#asList() }<br>
 *  </li>
 * </ul>
 * <p>
 * <u><b>Casting pointers</b></u>
 * <p>
 * <ul>
 *	<li>Cast a pointer to a {@link DynamicFunction} :<br>
 *		{@link Pointer#asDynamicFunction(org.bridj.ann.Convention.Style, java.lang.reflect.Type, java.lang.reflect.Type[]) }
 *  </li>
 *	<li>Cast a pointer to a {@link StructObject} or a {@link Callback} (as the ones generated by <a href="http://code.google.com/p/jnaerator/">JNAerator</a>) <br>:
 *		{@link Pointer#as(Class) }
 *  </li>
 *	<li>Cast a pointer to a complex type pointer (use {@link org.bridj.cpp.CPPType#getCPPType(Object[])} to create a C++ template type, for instance) :<br>
 *		{@link Pointer#as(Type) }
 *  </li>
 *	<li>Get an untyped pointer :<br>
 *		{@link Pointer#asUntyped() }
 *  </li>
 * </ul>
 * <p>
 * <u><b>Dealing with pointer bounds</b></u>
 * <p>
 * <ul>
 *	<li>Pointers to memory allocated through Pointer.pointerTo*, Pointer.allocate* have validity bounds that help prevent buffer overflows, at least when the Pointer API is used
 *  </li>
 *	<li>{@link Pointer#offset(long)}, {@link Pointer#next(long)} and other similar methods retain pointer bounds
 *  </li>
 *	<li>{@link Pointer#getValidBytes()} and {@link Pointer#getValidElements()} return the amount of valid memory readable from the pointer 
 *  </li>
 *	<li>Bounds can be declared manually with {@link Pointer#validBytes(long)} (useful for memory allocated by native code) 
 *  </li>
 * </ul>
 */
public abstract class Pointer<T> implements Comparable<Pointer<?>>, Iterable<T>
{

#macro (declareCheckedPeerAtOffset $byteOffset $validityCheckLength)
	long checkedPeer = getPeer() + $byteOffset;
	if (validStart != UNKNOWN_VALIDITY && (
			checkedPeer < validStart || 
			(checkedPeer + $validityCheckLength) > validEnd
	   )) {
		invalidPeer(checkedPeer, $validityCheckLength);
	}
#end

#macro (declareCheckedPeer $validityCheckLength)
	#declareCheckedPeerAtOffset("0", $validityCheckLength)
#end
	
#macro (docAllocateCopy $cPrimName $primWrapper)
	/**
     * Allocate enough memory for a single $cPrimName value, copy the value provided in argument into it and return a pointer to that memory.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * @param value initial value for the created memory location
     * @return pointer to a new memory location that initially contains the $cPrimName value given in argument
     */
#end
#macro (docAllocateArrayCopy $cPrimName $primWrapper)
	/**
     * Allocate enough memory for values.length $cPrimName values, copy the values provided as argument into it and return a pointer to that memory.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * The returned pointer is also an {@code Iterable<$primWrapper>} instance that can be safely iterated upon :
     <pre>{@code
     for (float f : pointerToFloats(1f, 2f, 3.3f))
     	System.out.println(f); }</pre>
     * @param values initial values for the created memory location
     * @return pointer to a new memory location that initially contains the $cPrimName consecutive values provided in argument
     */
#end
#macro (docAllocateArray2DCopy $cPrimName $primWrapper)
    /**
     * Allocate enough memory for all the values in the 2D $cPrimName array, copy the values provided as argument into it as packed multi-dimensional C array and return a pointer to that memory.<br>
     * Assumes that all of the subarrays of the provided array are non null and have the same size.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * @param values initial values for the created memory location
     * @return pointer to a new memory location that initially contains the $cPrimName values provided in argument packed as a 2D C array would be
     */
#end
#macro (docAllocateArray3DCopy $cPrimName $primWrapper)
    /**
     * Allocate enough memory for all the values in the 3D $cPrimName array, copy the values provided as argument into it as packed multi-dimensional C array and return a pointer to that memory.<br>
     * Assumes that all of the subarrays of the provided array are non null and have the same size.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * @param values initial values for the created memory location
     * @return pointer to a new memory location that initially contains the $cPrimName values provided in argument packed as a 3D C array would be
     */
#end
#macro (docAllocate $cPrimName $primWrapper)
	/**
     * Allocate enough memory for a $cPrimName value and return a pointer to it.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * @return pointer to a single zero-initialized $cPrimName value
     */
#end
#macro (docAllocateArray $cPrimName $primWrapper)
	/**
     * Allocate enough memory for arrayLength $cPrimName values and return a pointer to that memory.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * The returned pointer is also an {@code Iterable<$primWrapper>} instance that can be safely iterated upon.
     * @return pointer to arrayLength zero-initialized $cPrimName consecutive values
     */
#end
#macro (docAllocateArray2D $cPrimName $primWrapper)
	/**
     * Allocate enough memory for dim1 * dim2 $cPrimName values in a packed multi-dimensional C array and return a pointer to that memory.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * @return pointer to dim1 * dim2 zero-initialized $cPrimName consecutive values
     */
#end
#macro (docAllocateArray3D $cPrimName $primWrapper)
	/**
     * Allocate enough memory for dim1 * dim2 * dim3 $cPrimName values in a packed multi-dimensional C array and return a pointer to that memory.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * @return pointer to dim1 * dim2 * dim3 zero-initialized $cPrimName consecutive values
     */
#end
#macro (docGet $cPrimName $primWrapper)
	/**
     * Read a $cPrimName value from the pointed memory location
     */
#end
#macro (docGetOffset $cPrimName $primWrapper $signatureWithoutOffset)
	/**
     * Read a $cPrimName value from the pointed memory location shifted by a byte offset
     * @deprecated Avoid using the byte offset methods variants unless you know what you're doing (may cause alignment issues). Please favour {@link $signatureWithoutOffset} over this method. 
	 */
#end
#macro (docGetIndex $typeName $equivalentOffsetCall)
	/**
     * Read the nth contiguous $typeName value from the pointed memory location.<br>
	   * Equivalent to <code>${equivalentOffsetCall}</code>.
     * @param valueIndex index of the value to read
	 */
#end
#macro (docGetArray $cPrimName $primWrapper)
	/**
     * Read an array of $cPrimName values of the specified length from the pointed memory location
     */
#end
#macro (docGetRemainingArray $cPrimName $primWrapper)
	/**
     * Read the array of remaining $cPrimName values from the pointed memory location
     */
#end
#macro (docGetArrayOffset $cPrimName $primWrapper $signatureWithoutOffset)
	/**
     * Read an array of $cPrimName values of the specified length from the pointed memory location shifted by a byte offset
     * @deprecated Avoid using the byte offset methods variants unless you know what you're doing (may cause alignment issues). Please favour {@link $signatureWithoutOffset} over this method. 
	 */
#end
#macro (docSet $cPrimName $primWrapper)
	/**
     * Write a $cPrimName value to the pointed memory location
     */
#end
#macro (docSetOffset $cPrimName $primWrapper $signatureWithoutOffset)
    /**
     * Write a $cPrimName value to the pointed memory location shifted by a byte offset
     * @deprecated Avoid using the byte offset methods variants unless you know what you're doing (may cause alignment issues). Please favour {@link $signatureWithoutOffset} over this method. 
	 */
#end
#macro (docSetIndex $typeName $equivalentOffsetCall)
	/**
     * Write the nth contiguous $typeName value to the pointed memory location.<br>
	   * Equivalent to <code>${equivalentOffsetCall}</code>.
     * @param valueIndex index of the value to write
     * @param value $typeName value to write
	 */
#end
#macro (docSetArray $cPrimName $primWrapper)
	/**
     * Write an array of $cPrimName values to the pointed memory location
     */
#end
#macro (docSetArrayOffset $cPrimName $primWrapper $signatureWithoutOffset)
	/**
     * Write an array of $cPrimName values to the pointed memory location shifted by a byte offset
     * @deprecated Avoid using the byte offset methods variants unless you know what you're doing (may cause alignment issues). Please favour {@link $signatureWithoutOffset} over this method. 
	 */
#end
	
	/** The NULL pointer is <b>always</b> Java's null value */
    public static final Pointer<?> NULL = null;
	
    /** 
     * Size of a pointer in bytes. <br>
     * This is 4 bytes in a 32 bits environment and 8 bytes in a 64 bits environment.<br>
     * Note that some 64 bits environments allow for 32 bits JVM execution (using the -d32 command line argument for Sun's JVM, for instance). In that case, Java programs will believe they're executed in a 32 bits environment. 
     */
    public static final int SIZE = Platform.POINTER_SIZE;
    
	static {
        Platform.initLibrary();
    }
    
    
	protected static final long UNKNOWN_VALIDITY = -1;
	protected static final long NO_PARENT = 0/*-1*/;
  private static final long POINTER_MASK = Platform.is64Bits() ? -1 : 0xFFFFFFFFL;
	
	/**
	 * Default alignment used to allocate memory from the static factory methods in Pointer class (any value lower or equal to 1 means no alignment)
	 */
	public static final int defaultAlignment = Integer.parseInt(Platform.getenvOrProperty("BRIDJ_DEFAULT_ALIGNMENT", "bridj.defaultAlignment", "-1"));
	
	protected final PointerIO<T> io;
	private final long peer_;
     protected final long offsetInParent;
	protected final Pointer<?> parent;
	protected volatile Object sibling;
	protected final long validStart;
     protected final long validEnd;

	/**
	 * Object responsible for reclamation of some pointed memory when it's not used anymore.
	 */
	public interface Releaser {
		void release(Pointer<?> p);
	}
	
	Pointer(PointerIO<T> io, long peer, long validStart, long validEnd, Pointer<?> parent, long offsetInParent, Object sibling) {
		this.io = io;
		this.peer_ = peer;
		this.validStart = validStart;
		this.validEnd = validEnd;
		this.parent = parent;
		this.offsetInParent = offsetInParent;
		this.sibling = sibling;
		if (peer == 0)
			throw new IllegalArgumentException("Pointer instance cannot have NULL peer ! (use null Pointer instead)");
		if (BridJ.debugPointers) {
			creationTrace = new RuntimeException().fillInStackTrace();
          }
	}
	Throwable creationTrace;
     Throwable deletionTrace;
     Throwable releaseTrace;
     
#foreach ($data in [ ["Ordered", true, ""], ["Disordered", false, "_disordered"] ])
#set ($orderingPrefix = $data.get(0))
#set ($ordered = $data.get(1))
#set ($nativeSuffix = $data.get(2))

	static class ${orderingPrefix}Pointer<T> extends Pointer<T> {
		${orderingPrefix}Pointer(PointerIO<T> io, long peer, long validStart, long validEnd, Pointer<?> parent, long offsetInParent, Object sibling) {
			super(io, peer, validStart, validEnd, parent, offsetInParent, sibling);
		}
	
		@Override
		public boolean isOrdered() {
			return $ordered;
		}
		
#foreach ($prim in $primitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end

		@Override
		public Pointer<T> set${prim.CapName}(${prim.Name} value) {
			#if ($prim.Name == "char")
			if (Platform.WCHAR_T_SIZE == 4)
				return setInt((int)value);
			#end
			
			#declareCheckedPeer(${primSize})
			#if ($prim.Name != "byte" && $prim.Name != "boolean")
			JNI.set_${prim.Name}${nativeSuffix}(checkedPeer, value);
			#else
			JNI.set_${prim.Name}(checkedPeer, value);
			#end
			return this;
		}
		
		@Override
		public Pointer<T> set${prim.CapName}AtOffset(long byteOffset, ${prim.Name} value) {
			#if ($prim.Name == "char")
			if (Platform.WCHAR_T_SIZE == 4)
				return setIntAtOffset(byteOffset, (int)value);
			#end
			#declareCheckedPeerAtOffset("byteOffset" "${primSize}")
			#if ($prim.Name != "byte" && $prim.Name != "boolean")
			JNI.set_${prim.Name}${nativeSuffix}(checkedPeer, value);
			#else
			JNI.set_${prim.Name}(checkedPeer, value);
			#end
			return this;
		}
	
		@Override
		public ${prim.Name} get${prim.CapName}() {
			#if ($prim.Name == "char")
			if (Platform.WCHAR_T_SIZE == 4)
				return (char)getInt();
			#end
			#declareCheckedPeer(${primSize})
			#if ($prim.Name != "byte" && $prim.Name != "boolean")
			return JNI.get_${prim.Name}${nativeSuffix}(checkedPeer);
			#else
			return JNI.get_${prim.Name}(checkedPeer);
			#end
		}
    
		@Override
		public ${prim.Name} get${prim.CapName}AtOffset(long byteOffset) {
			#if ($prim.Name == "char")
			if (Platform.WCHAR_T_SIZE == 4)
				return (char)getIntAtOffset(byteOffset);
			#end
			#declareCheckedPeerAtOffset("byteOffset" "${primSize}")
			#if ($prim.Name != "byte" && $prim.Name != "boolean")
			return JNI.get_${prim.Name}${nativeSuffix}(checkedPeer);
			#else
			return JNI.get_${prim.Name}(checkedPeer);
			#end
		}
#end


#foreach ($sizePrim in ["SizeT", "CLong"])

#macro (setPrimitiveValue $primName $peer $value)
	#if ($primName != "byte" && $primName != "boolean")
	JNI.set_${primName}${nativeSuffix}($peer, $value);
	#else
	JNI.set_${primName}($peer, value);
	#end
#end

	@Override
    public Pointer<T> set${sizePrim}sAtOffset(long byteOffset, long[] values, int valuesOffset, int length) {
		if (values == null)
			throw new IllegalArgumentException("Null values");
		if (${sizePrim}.SIZE == 8) {
			setLongsAtOffset(byteOffset, values, valuesOffset, length);
		} else {
			int n = length;
			#declareCheckedPeerAtOffset("byteOffset" "n * 4")
            
			long peer = checkedPeer;
			int valuesIndex = valuesOffset;
			for (int i = 0; i < n; i++) {
				int value = (int)values[valuesIndex];
				#setPrimitiveValue("int" "peer" "value")
				peer += 4;
				valuesIndex++;
			}
		}
		return this;
	}
#docSetArrayOffset($sizePrim $sizePrim "Pointer#set${sizePrim}s(int[])")
	public Pointer<T> set${sizePrim}sAtOffset(long byteOffset, int[] values) {
		if (${sizePrim}.SIZE == 4) {
			setIntsAtOffset(byteOffset, values);
		} else {
			int n = values.length;
			#declareCheckedPeerAtOffset("byteOffset" "n * 8")
            
			long peer = checkedPeer;
			for (int i = 0; i < n; i++) {
				int value = values[i];
				#setPrimitiveValue("long" "peer" "value")
				peer += 8;
			}
		}
		return this;
	}
#end
	}
#end

	/**
	 * Create a {@code Pointer<T>} type. <br>
	 * For Instance, {@code Pointer.pointerType(Integer.class) } returns a type that represents {@code Pointer<Integer> }  
	 */
	public static Type pointerType(Type targetType) {
		return org.bridj.util.DefaultParameterizedType.paramType(Pointer.class, targetType);	
	}
	/**
	 * Create a {@code IntValuedEnum<T>} type. <br>
	 * For Instance, {@code Pointer.intEnumType(SomeEnum.class) } returns a type that represents {@code IntValuedEnum<SomeEnum> }  
	 */
	public static <E extends Enum<E>> Type intEnumType(Class<? extends IntValuedEnum<E>> targetType) {
		return org.bridj.util.DefaultParameterizedType.paramType(IntValuedEnum.class, targetType);	
	}
	
	/**
	 * Manually release the memory pointed by this pointer if it was allocated on the Java side.<br>
	 * If the pointer is an offset version of another pointer (using {@link Pointer#offset(long)} or {@link Pointer#next(long)}, for instance), this method tries to release the original pointer.<br>
	 * If the memory was not allocated from the Java side, this method does nothing either.<br>
	 * If the memory was already successfully released, this throws a RuntimeException.
	 * @throws RuntimeException if the pointer was already released
	 */
	public synchronized void release() {
		Object sibling = this.sibling;
		this.sibling = null;
		if (sibling instanceof Pointer) {
			((Pointer)sibling).release();
          }
          //this.peer_ = 0;
          if (BridJ.debugPointerReleases) {
               releaseTrace = new RuntimeException().fillInStackTrace();
          }
	}

	/**
	 * Compare to another pointer based on pointed addresses.
	 * @param p other pointer
	 * @return 1 if this pointer's address is greater than p's (or if p is null), -1 if the opposite is true, 0 if this and p point to the same memory location.
	 */
	//@Override
    public int compareTo(Pointer<?> p) {
		if (p == null)
			return 1;
		
		long p1 = getPeer(), p2 = p.getPeer();
		return p1 == p2 ? 0 : p1 < p2 ? -1 : 1;
	}
	
	/**
	* Compare the byteCount bytes at the memory location pointed by this pointer to the byteCount bytes at the memory location pointer by other using the C <a href="http://www.cplusplus.com/reference/clibrary/cstring/memcmp/">memcmp</a> function.<br>
	 * @return 0 if the two memory blocks are equal, -1 if this pointer's memory is "less" than the other and 1 otherwise.
	 */
	public int compareBytes(Pointer<?> other, long byteCount) {
		return compareBytesAtOffset(0, other, 0, byteCount);	
	}
	
	/**
	 * Compare the byteCount bytes at the memory location pointed by this pointer shifted by byteOffset to the byteCount bytes at the memory location pointer by other shifted by otherByteOffset using the C <a href="http://www.cplusplus.com/reference/clibrary/cstring/memcmp/">memcmp</a> function.<br>
	 * @deprecated Avoid using the byte offset methods variants unless you know what you're doing (may cause alignment issues)
	 * @return 0 if the two memory blocks are equal, -1 if this pointer's memory is "less" than the other and 1 otherwise.
	 */
	public int compareBytesAtOffset(long byteOffset, Pointer<?> other, long otherByteOffset, long byteCount) {
		#declareCheckedPeerAtOffset("byteOffset" "byteCount")
		return JNI.memcmp(checkedPeer, other.getCheckedPeer(otherByteOffset, byteCount), byteCount);	
	}
	
    /**
	 * Compute a hash code based on pointed address.
	 */
	@Override
    public int hashCode() {
		int hc = new Long(getPeer()).hashCode();
		return hc;
    }
    
    @Override 
    public String toString() {
		return "Pointer(peer = 0x" + Long.toHexString(getPeer()) + ", targetType = " + Utils.toString(getTargetType()) + ", order = " + order() + ")";
    }
    
    protected final void invalidPeer(long peer, long validityCheckLength) {
		throw new IndexOutOfBoundsException("Cannot access to memory data of length " + validityCheckLength + " at offset " + (peer - getPeer()) + " : valid memory start is " + validStart + ", valid memory size is " + (validEnd - validStart));
	}
	
    private final long getCheckedPeer(long byteOffset, long validityCheckLength) {
		#declareCheckedPeerAtOffset("byteOffset" "validityCheckLength")
		return checkedPeer;
    }
    
    /**
	 * Returns a pointer which address value was obtained by this pointer's by adding a byte offset.<br>
	 * The returned pointer will prevent the memory associated to this pointer from being automatically reclaimed as long as it lives, unless Pointer.release() is called on the originally-allocated pointer.
	 * @param byteOffset offset in bytes of the new pointer vs. this pointer. The expression {@code p.offset(byteOffset).getPeer() - p.getPeer() == byteOffset} is always true.
	 */
    public Pointer<T> offset(long byteOffset) {
    	return offset(byteOffset, getIO());
    }

    <U> Pointer<U> offset(long byteOffset, PointerIO<U> pio) {
		if (byteOffset == 0)
			return pio == this.io ? (Pointer<U>)this : as(pio);
		
		long newPeer = getPeer() + byteOffset;
		
		Object newSibling = getSibling() != null ? getSibling() : this;
		if (validStart == UNKNOWN_VALIDITY)
			return newPointer(pio, newPeer, isOrdered(), UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, null, NO_PARENT, null, newSibling);	
		if (newPeer > validEnd || newPeer < validStart)
			throw new IndexOutOfBoundsException("Invalid pointer offset : " + byteOffset + " (validBytes = " + getValidBytes() + ") !");
		
		return newPointer(pio, newPeer, isOrdered(), validStart, validEnd, null, NO_PARENT, null, newSibling);	
	}
	
	/**
	 * Creates a pointer that has the given number of valid bytes ahead.<br>
	 * If the pointer was already bound, the valid bytes must be lower or equal to the current getValidBytes() value.
	 */
	public Pointer<T> validBytes(long byteCount) {
		long peer = getPeer();
		long newValidEnd = peer + byteCount;
		if (validStart == peer && validEnd == newValidEnd)
			return this;
		
		if (validEnd != UNKNOWN_VALIDITY && newValidEnd > validEnd)
			throw new IndexOutOfBoundsException("Cannot extend validity of pointed memory from " + validEnd + " to " + newValidEnd);
		
		Object newSibling = getSibling() != null ? getSibling() : this;
		return newPointer(getIO(), peer, isOrdered(), validStart, newValidEnd, parent, offsetInParent, null, newSibling);    	
	}
	
	/**
	 * Creates a pointer that forgot any memory validity information.<br>
	 * Such pointers are typically faster than validity-aware pointers, since they perform less checks at each operation, but they're more prone to crashes if misused.
	 * @deprecated Pointers obtained via this method are faster but unsafe and are likely to cause crashes hard to debug if your logic is wrong.
     */
	@Deprecated
	public Pointer<T> withoutValidityInformation() {
          long peer = getPeer();
		if (validStart == UNKNOWN_VALIDITY)
			return this;
		
		Object newSibling = getSibling() != null ? getSibling() : this;
		return newPointer(getIO(), peer, isOrdered(), UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, parent, offsetInParent, null, newSibling);    	
	}
	
	/**
	* Creates a copy of the pointed memory location (allocates a new area of memory) and returns a pointer to it.<br>
	* The pointer's bounds must be known (see {@link Pointer#getValidBytes()}, {@link Pointer#validBytes(long)} or {@link Pointer#validElements(long)}).
	 */
	public Pointer<T> clone() {
		long length = getValidElements();
		if (length < 0)
			throw new UnsupportedOperationException("Number of bytes unknown, unable to clone memory (use validBytes(long))");
		
		Pointer<T> c = allocateArray(getIO(), length);
		copyTo(c);
		return c;    	
	}
	
	/**
	 * Creates a pointer that has the given number of valid elements ahead.<br>
	 * If the pointer was already bound, elementCount must be lower or equal to the current getValidElements() value.
	 */
	public Pointer<T> validElements(long elementCount) {
		return validBytes(elementCount * getIO("Cannot define elements validity").getTargetSize());
    }   
	
	/**
	 * Returns a pointer to this pointer.<br>
	 * It will only succeed if this pointer was dereferenced from another pointer.<br>
	 * Let's take the following C++ code :
	 * <pre>{@code
	int** pp = ...;
	int* p = pp[10];
	int** ref = &p;
	ASSERT(pp == ref);
	 }</pre>
	 * Here is its equivalent Java code :
	 * <pre>{@code
	Pointer<Pointer<Integer>> pp = ...;
	Pointer<Integer> p = pp.get(10);
	Pointer<Pointer<Integer>> ref = p.getReference();
	assert pp.equals(ref);
	 }</pre>
	 */
    public Pointer<Pointer<T>> getReference() {
		if (parent == null)
			throw new UnsupportedOperationException("Cannot get reference to this pointer, it wasn't created from Pointer.getPointer(offset) or from a similar method.");
		
		PointerIO io = getIO();
		return parent.offset(offsetInParent).as(io == null ? null : io.getReferenceIO());
	}
	
	/**
	 * Get the address of the memory pointed to by this pointer ("cast this pointer to long", in C jargon).<br>
	 * This is equivalent to the C code {@code (size_t)&pointer}
	 * @return Address of the memory pointed to by this pointer
	 */
	public final long getPeer() {
          if (BridJ.debugPointerReleases) {
               if (releaseTrace != null) {
                    throw new RuntimeException("Pointer was released here:\n\t" + Utils.toString(releaseTrace).replaceAll("\n", "\n\t"));
               }
          }
		return peer_;
	}
    
	/**
	 * Create a native callback which signature corresponds to the provided calling convention, return type and parameter types, and which redirects calls to the provided Java {@link org.bridj.DynamicCallback} handler.<br/>
	 * For instance, a callback of C signature <code>double (*)(float, int)</code> that adds its two arguments can be created with :<br>
     * <code>{@code 
     * Pointer callback = Pointer.allocateDynamicCallback(
	 *	  new DynamicCallback<Integer>() {
	 *	      public Double apply(Object... args) {
	 *	          float a = (Float)args[0];
	 *	          int b = (Integer)args[1];
	 *	          return (double)(a + b);
	 *	      }
	 *	  }, 
	 *    null, // Use the platform's default calling convention
	 *    int.class, // return type
	 *    float.class, double.class // parameter types
	 * );
     * }</code><br>
     * For the <code>void</code> return type, you can use {@link java.lang.Void} :<br>
     * <code>{@code 
     * Pointer callback = Pointer.allocateDynamicCallback(
	 *	  new DynamicCallback<Void>() {
	 *	      public Void apply(Object... args) {
	 *	          ...
	 *	          return null; // Void cannot be instantiated anyway ;-)
	 *	      }
	 *	  }, 
	 *    null, // Use the platform's default calling convention
	 *    int.class, // return type
	 *    float.class, double.class // parameter types
	 * );
     * }</code><br>
	 * @return Pointer to a native callback that redirects calls to the provided Java callback instance, and that will be destroyed whenever the pointer is released (make sure you keep a reference to it !)
	 */
	public static <R> Pointer<DynamicFunction<R>> allocateDynamicCallback(DynamicCallback<R> callback, org.bridj.ann.Convention.Style callingConvention, Type returnType, Type... parameterTypes) {
		if (callback == null)
			throw new IllegalArgumentException("Java callback handler cannot be null !");
		if (returnType == null)
			throw new IllegalArgumentException("Callback return type cannot be null !");
		if (parameterTypes == null)
			throw new IllegalArgumentException("Invalid (null) list of parameter types !");
		try {
			MethodCallInfo mci = new MethodCallInfo(returnType, parameterTypes, false);
			Method method = DynamicCallback.class.getMethod("apply", Object[].class);
			mci.setMethod(method);
			mci.setJavaSignature("([Ljava/lang/Object;)Ljava/lang/Object;");
			mci.setCallingConvention(callingConvention);
			mci.setGenericCallback(true);
			mci.setJavaCallback(callback);
			
			//System.out.println("Java sig
			
			return CRuntime.createCToJavaCallback(mci, DynamicCallback.class);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to allocate dynamic callback for convention " + callingConvention + ", return type " + Utils.toString(returnType) + " and parameter types " + Arrays.asList(parameterTypes) + " : " + ex, ex);
		}
	}
    
    /**
     * Cast this pointer to another pointer type
     * @param newIO
     */
    public <U> Pointer<U> as(PointerIO<U> newIO) {
    	return viewAs(isOrdered(), newIO);
    }
    /**
     * Create a view of this pointer that has the byte order provided in argument, or return this if this pointer already uses the requested byte order.
     * @param order byte order (endianness) of the returned pointer
     */
    public Pointer<T> order(ByteOrder order) {
		if (order.equals(ByteOrder.nativeOrder()) == isOrdered())
			return this;
		
		return viewAs(!isOrdered(), getIO());
	}
    
	/**
     * Get the byte order (endianness) of this pointer.
     */
    public ByteOrder order() {
    		ByteOrder order = isOrdered() ? ByteOrder.nativeOrder() : ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
		return order;
    }

    <U> Pointer<U> viewAs(boolean ordered, PointerIO<U> newIO) {
    	if (newIO == io && ordered == isOrdered())
    		return (Pointer<U>)this;
    	else
    		return newPointer(newIO, getPeer(), ordered, getValidStart(), getValidEnd(), getParent(), getOffsetInParent(), null, getSibling() != null ? getSibling() : this);
    }

    /**
     * Get the PointerIO instance used by this pointer to get and set pointed values.
     */
    public final PointerIO<T> getIO() {
		return io;
	}
    
	/**
     * Whether this pointer reads data in the system's native byte order or not.
     * See {@link Pointer#order()}, {@link Pointer#order(ByteOrder)}
     */
    public abstract boolean isOrdered();
    
    final long getOffsetInParent() {
		return offsetInParent;
	}
    final Pointer<?> getParent() {
		return parent;
	}
    final Object getSibling() {
		return sibling;
	}
    
    final long getValidEnd() {
		return validEnd;
	}
    final long getValidStart() {
		return validStart;
	}

    /**
     * Cast this pointer to another pointer type<br>
     * Synonym of {@link Pointer#as(Class)}<br>
     * The following C code :<br>
     * <code>{@code 
     * T* pointerT = ...;
     * U* pointerU = (U*)pointerT;
     * }</code><br>
     * Can be translated to the following Java code :<br>
     * <code>{@code 
     * Pointer<T> pointerT = ...;
     * Pointer<U> pointerU = pointerT.as(U.class);
     * }</code><br>
     * @param <U> type of the elements pointed by the returned pointer
     * @param type type of the elements pointed by the returned pointer
     * @return pointer to type U elements at the same address as this pointer
     */
    public <U> Pointer<U> as(Type type) {
    	PointerIO<U> pio = PointerIO.getInstance(type);
    	return as(pio);
    }

    /**
     * Cast this pointer to another pointer type.<br>
     * Synonym of {@link Pointer#as(Type)}<br>
     * The following C code :<br>
     * <code>{@code 
     * T* pointerT = ...;
     * U* pointerU = (U*)pointerT;
     * }</code><br>
     * Can be translated to the following Java code :<br>
     * <code>{@code 
     * Pointer<T> pointerT = ...;
     * Pointer<U> pointerU = pointerT.as(U.class); // or pointerT.as(U.class);
     * }</code><br>
     * @param <U> type of the elements pointed by the returned pointer
     * @param type type of the elements pointed by the returned pointer
     * @return pointer to type U elements at the same address as this pointer
     */
    public <U> Pointer<U> as(Class<U> type) {
    	return as((Type)type);
    }
    
    /**
     * Cast this pointer as a function pointer to a function that returns the specified return type and takes the specified parameter types.<br>
     * See for instance the following C code that uses a function pointer :
     * <pre>{@code
     *	  double (*ptr)(int, const char*) = someAddress;
     *    double result = ptr(10, "hello");
     * }</pre>
     * Its Java equivalent with BridJ is the following :
     * <pre>{@code
     *	  DynamicFunction ptr = someAddress.asDynamicFunction(null, double.class, int.class, Pointer.class);
     *    double result = (Double)ptr.apply(10, pointerToCString("hello"));
     * }</pre>
     * Also see {@link CRuntime#getDynamicFunctionFactory(org.bridj.NativeLibrary, org.bridj.ann.Convention.Style, java.lang.reflect.Type, java.lang.reflect.Type[])  } for more options.
     * @param callingConvention calling convention used by the function (if null, default is typically {@link org.bridj.ann.Convention.Style#CDecl})
     * @param returnType return type of the function
     * @param parameterTypes parameter types of the function
     */
    public <R> DynamicFunction<R> asDynamicFunction(org.bridj.ann.Convention.Style callingConvention, Type returnType, Type... parameterTypes) {
    		return CRuntime.getInstance().getDynamicFunctionFactory(null, callingConvention, returnType, parameterTypes).newInstance(this);
    }
    
    /**
     * Cast this pointer to an untyped pointer.<br>
     * Synonym of {@code ptr.as((Class<?>)null)}.<br>
     * See {@link Pointer#as(Class)}<br>
     * The following C code :<br>
     * <code>{@code 
     * T* pointerT = ...;
     * void* pointer = (void*)pointerT;
     * }</code><br>
     * Can be translated to the following Java code :<br>
     * <code>{@code 
     * Pointer<T> pointerT = ...;
     * Pointer<?> pointer = pointerT.asUntyped(); // or pointerT.as((Class<?>)null);
     * }</code><br>
     * @return untyped pointer pointing to the same address as this pointer
     */
    public Pointer<?> asUntyped() {
    	return as((Class<?>)null);
    }

    /**
     * Get the amount of memory known to be valid from this pointer, or -1 if it is unknown.<br>
     * Memory validity information is available when the pointer was allocated by BridJ (with {@link Pointer#allocateBytes(long)}, for instance), created out of another pointer which memory validity information is available (with {@link Pointer#offset(long)}, {@link Pointer#next()}, {@link Pointer#next(long)}) or created from a direct NIO buffer ({@link Pointer#pointerToBuffer(Buffer)}, {@link Pointer#pointerToInts(IntBuffer)}...)
     * @return amount of bytes that can be safely read or written from this pointer, or -1 if this amount is unknown
     */
    public long getValidBytes() {
    	long ve = getValidEnd();
    	return ve == UNKNOWN_VALIDITY ? -1 : ve - getPeer();
    }
    
    /**
    * Get the amount of memory known to be valid from this pointer (expressed in elements of the target type, see {@link Pointer#getTargetType()}) or -1 if it is unknown.<br>
     * Memory validity information is available when the pointer was allocated by BridJ (with {@link Pointer#allocateBytes(long)}, for instance), created out of another pointer which memory validity information is available (with {@link Pointer#offset(long)}, {@link Pointer#next()}, {@link Pointer#next(long)}) or created from a direct NIO buffer ({@link Pointer#pointerToBuffer(Buffer)}, {@link Pointer#pointerToInts(IntBuffer)}...)
     * @return amount of elements that can be safely read or written from this pointer, or -1 if this amount is unknown
     */
    public long getValidElements() {
    	long bytes = getValidBytes();
    	long elementSize = getTargetSize();
    	if (bytes < 0 || elementSize <= 0)
    		return -1;
    	return bytes / elementSize;
    }
    
    /**
     * Returns an iterator over the elements pointed by this pointer.<br>
     * If this pointer was allocated from Java with the allocateXXX, pointerToXXX methods (or is a view or a clone of such a pointer), the iteration is safely bounded.<br>
     * If this iterator is just a wrapper for a native-allocated pointer (or a view / clone of such a pointer), iteration will go forever (until illegal areas of memory are reached and cause a JVM crash).
     */
    public ListIterator<T> iterator() {
    	return new ListIterator<T>() {
    		Pointer<T> next = Pointer.this.getValidElements() != 0 ? Pointer.this : null;
    		Pointer<T> previous;
    		//@Override
			public T next() {
				if (next == null)
					throw new NoSuchElementException();
                T value = next.get();
                previous = next;
                long valid = next.getValidElements();
				next = valid < 0 || valid > 1 ? next.next(1) : null;
				return value;
			}
			//@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			//@Override
			public boolean hasNext() {
				long rem;
				return next != null && ((rem = next.getValidBytes()) < 0 || rem > 0);
			}
			//@Override
			public void add(T o) {
				throw new UnsupportedOperationException();
			}
			//@Override
			public boolean hasPrevious() {
				return previous != null;
			}
			//@Override
			public int nextIndex() {
				throw new UnsupportedOperationException();
			}
			//@Override
			public T previous() {
				//TODO return previous;
				throw new UnsupportedOperationException();
			}
			//@Override
			public int previousIndex() {
				throw new UnsupportedOperationException();
			}
			//@Override
			public void set(T o) {
				if (previous == null)
					throw new NoSuchElementException("You haven't called next() prior to calling ListIterator.set(E)");
				previous.set(o);
			} 
    	};
    }
    
    
    /**
     * Get a pointer to an enum. 
     */
    public static <E extends Enum<E>> Pointer<IntValuedEnum<E>> pointerToEnum(IntValuedEnum<E> instance) {
    	Class<E> enumClass;
    	if (instance instanceof FlagSet) {
    		enumClass = ((FlagSet)instance).getEnumClass();
        } else if (instance instanceof Enum) {
        	enumClass = (Class)instance.getClass();
        } else 
        	throw new RuntimeException("Expected a FlagSet or an Enum, got " + instance);

    	PointerIO<IntValuedEnum<E>> io = (PointerIO)PointerIO.getInstance(DefaultParameterizedType.paramType(IntValuedEnum.class, enumClass));
    	Pointer<IntValuedEnum<E>> p = allocate(io);
    	p.setInt((int)instance.value());
    	return p;
    }

    /**
      * @deprecated Will be removed in a future version, please use {@link Pointer#getPointer(NativeObject)} instead.
      */
    @Deprecated
    public static <N extends NativeObject> Pointer<N> pointerTo(N instance) {
         return getPointer(instance);
    }
    
    /**
     * Get a pointer to a native object (C++ or ObjectiveC class, struct, union, callback...) 
     */
    public static <N extends NativeObject> Pointer<N> getPointer(N instance) {
    		return getPointer(instance, null);
    }
    /**
     * Get a pointer to a native object (C++ or ObjectiveC class, struct, union, callback...) 
     */
    public static <N extends NativeObjectInterface> Pointer<N> getPointer(N instance) {
    		return (Pointer)getPointer((NativeObject)instance);
    }
    
    /**
     * Get a pointer to a native object, specifying the type of the pointer's target.<br>
     * In C++, the address of the pointer to an object as its canonical class is not always the same as the address of the pointer to the same object cast to one of its parent classes. 
     */
    public static <R extends NativeObject> Pointer<R> getPointer(NativeObject instance, Type targetType) {
		return instance == null ? null : (Pointer<R>)instance.peer;
    }
    /**
    * Get the address of a native object, specifying the type of the pointer's target (same as {@code getPointer(instance, targetType).getPeer()}, see {@link Pointer#getPointer(NativeObject, Type)}).<br>
     * In C++, the address of the pointer to an object as its canonical class is not always the same as the address of the pointer to the same object cast to one of its parent classes. 
     */
    public static long getAddress(NativeObject instance, Class targetType) {
		return getPeer(getPointer(instance, targetType));
    }
    
#docGetOffset("native object", "O extends NativeObject", "Pointer#getNativeObject(Type)")
	public <O extends NativeObject> O getNativeObjectAtOffset(long byteOffset, Type type) {
		return (O)BridJ.createNativeObjectFromPointer((Pointer<O>)(byteOffset == 0 ? this : offset(byteOffset)), type);
	}
#docSet("native object", "O extends NativeObject")
	public <O extends NativeObject> Pointer<T> setNativeObject(O value, Type type) {
		BridJ.copyNativeObjectToAddress(value, type, (Pointer)this);
		return this;
	}
#docGetOffset("native object", "O extends NativeObject", "Pointer#getNativeObject(Class)")
	 public <O extends NativeObject> O getNativeObjectAtOffset(long byteOffset, Class<O> type) {
		return (O)getNativeObjectAtOffset(byteOffset, (Type)type);
	}
#docGet("native object", "O extends NativeObject")
    public <O extends NativeObject> O getNativeObject(Class<O> type) {
		return (O)getNativeObject((Type)type);
	}
#docGet("native object", "O extends NativeObject")
    public <O extends NativeObject> O getNativeObject(Type type) {
		O o = (O)getNativeObjectAtOffset(0, type);
		return o;
	}
	
	/**
	 * Check that the pointer's peer is aligned to the target type alignment.
	 * @throws RuntimeException If the target type of this pointer is unknown
	 * @return getPeer() % alignment == 0
	 */
	public boolean isAligned() {
        return isAligned(getIO("Cannot check alignment").getTargetAlignment());
	}
	
	/**
	 * Check that the pointer's peer is aligned to the given alignment.
	 * If the pointer has no peer, this method returns true.
	 * @return getPeer() % alignment == 0
	 */
	public boolean isAligned(long alignment) {
		return isAligned(getPeer(), alignment);
	}
	
	/**
	 * Check that the provided address is aligned to the given alignment.
	 * @return address % alignment == 0
	 */
	protected static boolean isAligned(long address, long alignment) {
		return computeRemainder(address, alignment) == 0;
	}
	
	protected static int computeRemainder(long address, long alignment) {
		switch ((int)alignment) {
		case -1:
		case 0:
		case 1:
			return 0;
		case 2:
			return (int)(address & 1);
		case 4:
			return (int)(address & 3);
		case 8:
			return (int)(address & 7);
		case 16:
			return (int)(address & 15);
		case 32:
			return (int)(address & 31);
		case 64:
			return (int)(address & 63);
		default:
			if (alignment < 0)
				return 0;
			return (int)(address % alignment);
		}
	}
	
	/**
	 * Dereference this pointer (*ptr).<br>
     Take the following C++ code fragment :
     <pre>{@code
     int* array = new int[10];
     for (int index = 0; index < 10; index++, array++) 
     	printf("%i\n", *array);
     }</pre>
     Here is its equivalent in Java :
     <pre>{@code
     import static org.bridj.Pointer.*;
     ...
     Pointer<Integer> array = allocateInts(10);
     for (int index = 0; index < 10; index++) { 
     	System.out.println("%i\n".format(array.get()));
     	array = array.next();
	 }
     }</pre>
     Here is a simpler equivalent in Java :
     <pre>{@code
     import static org.bridj.Pointer.*;
     ...
     Pointer<Integer> array = allocateInts(10);
     for (int value : array) // array knows its size, so we can iterate on it
     	System.out.println("%i\n".format(value));
     }</pre>
     @throws RuntimeException if called on an untyped {@code Pointer<?>} instance (see {@link  Pointer#getTargetType()}) 
	 */
    public T get() {
        return get(0);
    }
    
    /**
     * Returns null if pointer is null, otherwise dereferences the pointer (calls pointer.get()).
     */
    public static <T> T get(Pointer<T> pointer) {
    		return pointer == null ? null : pointer.get();
    }
    
    /**
     Gets the n-th element from this pointer.<br>
     This is equivalent to the C/C++ square bracket syntax.<br>
     Take the following C++ code fragment :
     <pre>{@code
	int* array = new int[10];
	int index = 5;
	int value = array[index];
     }</pre>
     Here is its equivalent in Java :
     <pre>{@code
	import static org.bridj.Pointer.*;
	...
	Pointer<Integer> array = allocateInts(10);
	int index = 5;
	int value = array.get(index);
     }</pre>
     @param index offset in pointed elements at which the value should be copied. Can be negative if the pointer was offset and the memory before it is valid.
     @throws RuntimeException if called on an untyped {@code Pointer<?>} instance ({@link  Pointer#getTargetType()}) 
	 */
	public T get(long index) {
        return getIO("Cannot get pointed value").get(this, index);
    }
    
    /**
	 Assign a value to the pointed memory location, and return it (different behaviour from {@link List#set(int, Object)} which returns the old value of that element !!!).<br>
     Take the following C++ code fragment :
     <pre>{@code
	int* array = new int[10];
	for (int index = 0; index < 10; index++, array++) { 
		int value = index;
		*array = value;
	}
     }</pre>
     Here is its equivalent in Java :
     <pre>{@code
	import static org.bridj.Pointer.*;
	...
	Pointer<Integer> array = allocateInts(10);
	for (int index = 0; index < 10; index++) {
		int value = index;
		array.set(value);
		array = array.next();
	}
     }</pre>
     @throws RuntimeException if called on a raw and untyped {@code Pointer} instance (see {@link Pointer#asUntyped()} and {@link  Pointer#getTargetType()}) 
	 @return The value that was given (not the old value as in {@link List#set(int, Object)} !!!)
	 */
    public T set(T value) {
        return set(0, value);
    }
    
    private static long getTargetSizeToAllocateArrayOrThrow(PointerIO<?> io) {
    		long targetSize = -1;
    		if (io == null || (targetSize = io.getTargetSize()) < 0)
			throwBecauseUntyped("Cannot allocate array ");
		return targetSize;
	}
    	
    private static void throwBecauseUntyped(String message) {
    	throw new RuntimeException("Pointer is not typed (call Pointer.as(Type) to create a typed pointer) : " + message);
    }
    static void throwUnexpected(Throwable ex) {
    	throw new RuntimeException("Unexpected error", ex);
    }
	/**
     Sets the n-th element from this pointer, and return it (different behaviour from {@link List#set(int, Object)} which returns the old value of that element !!!).<br>
     This is equivalent to the C/C++ square bracket assignment syntax.<br>
     Take the following C++ code fragment :
     <pre>{@code
     float* array = new float[10];
     int index = 5;
     float value = 12;
     array[index] = value;
     }</pre>
     Here is its equivalent in Java :
     <pre>{@code
     import static org.bridj.Pointer.*;
     ...
     Pointer<Float> array = allocateFloats(10);
     int index = 5;
     float value = 12;
     array.set(index, value);
     }</pre>
     @param index offset in pointed elements at which the value should be copied. Can be negative if the pointer was offset and the memory before it is valid.
     @param value value to set at pointed memory location
     @throws RuntimeException if called on a raw and untyped {@code Pointer} instance (see {@link Pointer#asUntyped()} and {@link  Pointer#getTargetType()})
     @return The value that was given (not the old value as in {@link List#set(int, Object)} !!!)
	 */
	public T set(long index, T value) {
        getIO("Cannot set pointed value").set(this, index, value);
        return value;
    }
	
    /**
     * Get a pointer's peer (see {@link Pointer#getPeer}), or zero if the pointer is null.
     */
	public static long getPeer(Pointer<?> pointer) {
        return pointer == null ? 0 : pointer.getPeer();
    }
	
    /**
     * Get the unitary size of the pointed elements in bytes.
     * @throws RuntimeException if the target type is unknown (see {@link Pointer#getTargetType()})
     */
	public long getTargetSize() {
        return getIO("Cannot compute target size").getTargetSize();
	}
	
	/**
	 * Returns a pointer to the next target.
	 * Same as incrementing a C pointer of delta elements, but creates a new pointer instance.
	 * @return next(1)
	 */
	public Pointer<T> next() {
		return next(1);
	}
	
	/**
	 * Returns a pointer to the n-th next (or previous) target.
	 * Same as incrementing a C pointer of delta elements, but creates a new pointer instance.
	 * @return offset(getTargetSize() * delta)
	 */
	public Pointer<T> next(long delta) {
        return offset(getIO("Cannot get pointers to next or previous targets").getTargetSize() * delta);
	}
	
	/**
     * Release pointers, if they're not null (see {@link Pointer#release}).
     */
	public static void release(Pointer... pointers) {
    		for (Pointer pointer : pointers)
    			if (pointer != null)
    				pointer.release();
	}

    /**
	 * Test equality of the pointer using the address.<br>
	 * @return true if and only if obj is a Pointer instance and {@code obj.getPeer() == this.getPeer() }
	 */
	@Override
    public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Pointer))
			return false;
		
		Pointer p = (Pointer)obj;
		return getPeer() == p.getPeer();
	}
  
	/**
     * Create a pointer out of a native memory address
     * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == address }
     */
    @Deprecated
    public static Pointer<?> pointerToAddress(long peer) {
    	return pointerToAddress(peer, (PointerIO) null);
    }

    /**
     * Create a pointer out of a native memory address
     * @param size number of bytes known to be readable at the pointed address 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    @Deprecated
    public static Pointer<?> pointerToAddress(long peer, long size) {
        return newPointer(null, peer, true, peer, peer + size, null, NO_PARENT, null, null);
    }
    
    /**
     * Create a pointer out of a native memory address
     * @param targetClass type of the elements pointed by the resulting pointer 
	 * @param releaser object responsible for reclaiming the native memory once whenever the returned pointer is garbage-collected 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    public static <P> Pointer<P> pointerToAddress(long peer, Class<P> targetClass, final Releaser releaser) {
        return pointerToAddress(peer, (Type)targetClass, releaser);
    }
    /**
     * Create a pointer out of a native memory address
     * @param targetType type of the elements pointed by the resulting pointer 
	 * @param releaser object responsible for reclaiming the native memory once whenever the returned pointer is garbage-collected 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    public static <P> Pointer<P> pointerToAddress(long peer, Type targetType, final Releaser releaser) {
    		PointerIO<P> pio = PointerIO.getInstance(targetType);
        return newPointer(pio, peer, true, UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, null, -1, releaser, null);
    }
    /**
     * Create a pointer out of a native memory address
     * @param io PointerIO instance that knows how to read the elements pointed by the resulting pointer 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    public static <P> Pointer<P> pointerToAddress(long peer, PointerIO<P> io) {
    	if (BridJ.cachePointers)
    		return (Pointer<P>)localCachedPointers.get().get(peer, io);
    	else
    		return pointerToAddress_(peer, io);
	}

	private static <P> Pointer<P> pointerToAddress_(long peer, PointerIO<P> io) {
    	return newPointer(io, peer, true, UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, null, NO_PARENT, null, null);
	}

	private static final int LRU_POINTER_CACHE_SIZE = 8;
  private static final int LRU_POINTER_CACHE_TOLERANCE = 1;
  private static final ThreadLocal<PointerLRUCache> localCachedPointers = new ThreadLocal<PointerLRUCache>() {
      @Override
      protected PointerLRUCache initialValue() {
          return new PointerLRUCache(LRU_POINTER_CACHE_SIZE, LRU_POINTER_CACHE_TOLERANCE) {
          	@Override
          	protected <P> Pointer<P> pointerToAddress(long peer, PointerIO<P> io) {
          		return pointerToAddress_(peer, io);
          	}
          };
      }
  };

	/**
     * Create a pointer out of a native memory address
     * @param io PointerIO instance that knows how to read the elements pointed by the resulting pointer 
	 * @param releaser object responsible for reclaiming the native memory once whenever the returned pointer is garbage-collected 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    static <P> Pointer<P> pointerToAddress(long peer, PointerIO<P> io, Releaser releaser) {
    	return newPointer(io, peer, true, UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, null, NO_PARENT, releaser, null);
	}
	
	/**
     * Create a pointer out of a native memory address
     * @param releaser object responsible for reclaiming the native memory once whenever the returned pointer is garbage-collected 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    @Deprecated
    public static Pointer<?> pointerToAddress(long peer, Releaser releaser) {
		return newPointer(null, peer, true, UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, null, NO_PARENT, releaser, null);
	}
    
	/**
     * Create a pointer out of a native memory address
     * @param releaser object responsible for reclaiming the native memory once whenever the returned pointer is garbage-collected 
	 * @param size number of bytes known to be readable at the pointed address 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    public static Pointer<?> pointerToAddress(long peer, long size, Releaser releaser) {
        return newPointer(null, peer, true, peer, peer + size, null, NO_PARENT, releaser, null);
    }
    
	/**
     * Create a pointer out of a native memory address
     * @param io PointerIO instance that knows how to read the elements pointed by the resulting pointer 
	 * @param releaser object responsible for reclaiming the native memory once whenever the returned pointer is garbage-collected 
	 * @param size number of bytes known to be readable at the pointed address 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    public static <P> Pointer<P> pointerToAddress(long peer, long size, PointerIO<P> io, Releaser releaser) {
        return newPointer(io, peer, true, peer, peer + size, null, NO_PARENT, releaser, null);
    }
	
	/**
     * Create a pointer out of a native memory address
     * @param targetClass type of the elements pointed by the resulting pointer 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    @Deprecated
    public static <P> Pointer<P> pointerToAddress(long peer, Class<P> targetClass) {
    		return pointerToAddress(peer, (Type)targetClass);
    }
    
	/**
     * Create a pointer out of a native memory address
     * @param targetType type of the elements pointed by the resulting pointer 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    @Deprecated
    public static <P> Pointer<P> pointerToAddress(long peer, Type targetType) {
    	return newPointer((PointerIO<P>)PointerIO.getInstance(targetType), peer, true, UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, null, -1, null, null);
    }
    
	/**
     * Create a pointer out of a native memory address
     * @param size number of bytes known to be readable at the pointed address 
	 * @param io PointerIO instance that knows how to read the elements pointed by the resulting pointer 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    static <U> Pointer<U> pointerToAddress(long peer, long size, PointerIO<U> io) {
    	return newPointer(io, peer, true, peer, peer + size, null, NO_PARENT, null, null);
	}
	
	/**
     * Create a pointer out of a native memory address
     * @param releaser object responsible for reclaiming the native memory once whenever the returned pointer is garbage-collected 
	 * @param peer native memory address that is to be converted to a pointer
	 * @return a pointer with the provided address : {@code pointer.getPeer() == peer }
     */
    static <U> Pointer<U> newPointer(
		PointerIO<U> io, 
		long peer, 
		boolean ordered, 
		long validStart, 
		long validEnd, 
		Pointer<?> parent, 
		long offsetInParent, 
		final Releaser releaser,
		Object sibling)
	{
    peer = peer & POINTER_MASK;
		if (peer == 0)
			return null;
		
		if (validEnd != UNKNOWN_VALIDITY && validEnd <= validStart)
			return null;
		
		if (releaser == null) {
			if (ordered) {
				return new OrderedPointer<U>(io, peer, validStart, validEnd, parent, offsetInParent, sibling);
			} else {
				return new DisorderedPointer<U>(io, peer, validStart, validEnd, parent, offsetInParent, sibling);
			}
		} else {
			assert sibling == null;
#macro (bodyOfPointerWithReleaser)
				private volatile Releaser rel = releaser;
				//@Override
				public synchronized void release() {
					if (rel != null) {
						Releaser rel = this.rel;
						this.rel = null;
						rel.release(this);
					}
                         //this.peer_ = 0;
                         if (BridJ.debugPointerReleases)
                              releaseTrace = new RuntimeException().fillInStackTrace();
				}
				protected void finalize() {
					release();
				}
				
				@Deprecated
				public synchronized Pointer<U> withReleaser(final Releaser beforeDeallocation) {
					final Releaser thisReleaser = rel;
					rel = null;
					return newPointer(getIO(), getPeer(), isOrdered(), getValidStart(), getValidEnd(), null, NO_PARENT, beforeDeallocation == null ? thisReleaser : new Releaser() {
						//@Override
						public void release(Pointer<?> p) {
							beforeDeallocation.release(p);
							if (thisReleaser != null)
								thisReleaser.release(p);
						}
					}, null);
				}
#end
			if (ordered) {
				return new OrderedPointer<U>(io, peer, validStart, validEnd, parent, offsetInParent, sibling) {
					#bodyOfPointerWithReleaser()
				};
			} else {
				return new DisorderedPointer<U>(io, peer, validStart, validEnd, parent, offsetInParent, sibling) {
					#bodyOfPointerWithReleaser()
				};
			}
		}
    }
	
#docAllocate("typed pointer", "P extends TypedPointer")
    public static <P extends TypedPointer> Pointer<P> allocateTypedPointer(Class<P> type) {
    	return (Pointer<P>)(Pointer)allocate(PointerIO.getInstance(type));
    }
#docAllocateArray("typed pointer", "P extends TypedPointer")
    public static <P extends TypedPointer> Pointer<P> allocateTypedPointers(Class<P> type, long arrayLength) {
    	return (Pointer<P>)(Pointer)allocateArray(PointerIO.getInstance(type), arrayLength);
    }
    /**
     * Create a memory area large enough to hold a pointer.
     * @param targetType target type of the pointer values to be stored in the allocated memory 
     * @return a pointer to a new memory area large enough to hold a single typed pointer
     */
    public static <P> Pointer<Pointer<P>> allocatePointer(Class<P> targetType) {
    	return allocatePointer((Type)targetType); 
    }
    /**
     * Create a memory area large enough to hold a pointer.
     * @param targetType target type of the pointer values to be stored in the allocated memory 
     * @return a pointer to a new memory area large enough to hold a single typed pointer
     */
    public static <P> Pointer<Pointer<P>> allocatePointer(Type targetType) {
    	return (Pointer<Pointer<P>>)(Pointer)allocate(PointerIO.getPointerInstance(targetType)); 
    }
    /**
     * Create a memory area large enough to hold a pointer to a pointer
     * @param targetType target type of the values pointed by the pointer values to be stored in the allocated memory 
     * @return a pointer to a new memory area large enough to hold a single typed pointer
     */
    public static <P> Pointer<Pointer<Pointer<P>>> allocatePointerPointer(Type targetType) {
    	return allocatePointer(pointerType(targetType)); 
    }/**
     * Create a memory area large enough to hold a pointer to a pointer
     * @param targetType target type of the values pointed by the pointer values to be stored in the allocated memory 
     * @return a pointer to a new memory area large enough to hold a single typed pointer
     */
    public static <P> Pointer<Pointer<Pointer<P>>> allocatePointerPointer(Class<P> targetType) {
    	return allocatePointerPointer((Type)targetType); 
    }
#docAllocate("untyped pointer", "Pointer<?>")
    /**
     * Create a memory area large enough to hold an untyped pointer.
     * @return a pointer to a new memory area large enough to hold a single untyped pointer
     */
    public static <V> Pointer<Pointer<?>> allocatePointer() {
    	return (Pointer)allocate(PointerIO.getPointerInstance());
    }
#docAllocateArray("untyped pointer", "Pointer<?>")
    public static Pointer<Pointer<?>> allocatePointers(int arrayLength) {
		return (Pointer<Pointer<?>>)(Pointer)allocateArray(PointerIO.getPointerInstance(), arrayLength); 
	}
	
    /**
     * Create a memory area large enough to hold an array of arrayLength typed pointers.
     * @param targetType target type of element pointers in the resulting pointer array. 
     * @param arrayLength size of the allocated array, in elements
     * @return a pointer to a new memory area large enough to hold an array of arrayLength typed pointers
     */
    public static <P> Pointer<Pointer<P>> allocatePointers(Class<P> targetType, int arrayLength) {
		return allocatePointers((Type)targetType, arrayLength);
	}
	
    /**
     * Create a memory area large enough to hold an array of arrayLength typed pointers.
     * @param targetType target type of element pointers in the resulting pointer array. 
     * @param arrayLength size of the allocated array, in elements
     * @return a pointer to a new memory area large enough to hold an array of arrayLength typed pointers
     */
    public static <P> Pointer<Pointer<P>> allocatePointers(Type targetType, int arrayLength) {
		return (Pointer<Pointer<P>>)(Pointer)allocateArray(PointerIO.getPointerInstance(targetType), arrayLength); // TODO 
	}
	
    
    /**
     * Create a memory area large enough to a single items of type elementClass.
     * @param elementClass type of the array elements
     * @return a pointer to a new memory area large enough to hold a single item of type elementClass.
     */
    public static <V> Pointer<V> allocate(Class<V> elementClass) {
        return allocate((Type)elementClass);
    }

    /**
     * Create a memory area large enough to a single items of type elementClass.
     * @param elementClass type of the array elements
     * @return a pointer to a new memory area large enough to hold a single item of type elementClass.
     */
    public static <V> Pointer<V> allocate(Type elementClass) {
        return allocateArray(elementClass, 1);
    }

    /**
     * Create a memory area large enough to hold one item of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()})
     * @param io PointerIO instance able to store and retrieve the element
     * @return a pointer to a new memory area large enough to hold one item of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()})
     */
    public static <V> Pointer<V> allocate(PointerIO<V> io) {
    		return allocateBytes(io, getTargetSizeToAllocateArrayOrThrow(io), null);
    }
    /**
     * Create a memory area large enough to hold arrayLength items of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()})
     * @param io PointerIO instance able to store and retrieve elements of the array
     * @param arrayLength length of the array in elements
     * @return a pointer to a new memory area large enough to hold arrayLength items of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()})
     */
    public static <V> Pointer<V> allocateArray(PointerIO<V> io, long arrayLength) {
		return allocateBytes(io, getTargetSizeToAllocateArrayOrThrow(io) * arrayLength, null);
    }
    /**
     * Create a memory area large enough to hold arrayLength items of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()})
     * @param io PointerIO instance able to store and retrieve elements of the array
     * @param arrayLength length of the array in elements
     * @param beforeDeallocation fake releaser that should be run just before the memory is actually released, for instance in order to call some object destructor
     * @return a pointer to a new memory area large enough to hold arrayLength items of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()})
     */
    public static <V> Pointer<V> allocateArray(PointerIO<V> io, long arrayLength, final Releaser beforeDeallocation) {
		return allocateBytes(io, getTargetSizeToAllocateArrayOrThrow(io) * arrayLength, beforeDeallocation);
    }
    /**
     * Create a memory area large enough to hold byteSize consecutive bytes and return a pointer to elements of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()})
     * @param io PointerIO instance able to store and retrieve elements of the array
     * @param byteSize length of the array in bytes
     * @param beforeDeallocation fake releaser that should be run just before the memory is actually released, for instance in order to call some object destructor
     * @return a pointer to a new memory area large enough to hold byteSize consecutive bytes
     */
    public static <V> Pointer<V> allocateBytes(PointerIO<V> io, long byteSize, final Releaser beforeDeallocation) {
    		return allocateAlignedBytes(io, byteSize, defaultAlignment, beforeDeallocation);
    }
    	
    /**
     * Create a memory area large enough to hold byteSize consecutive bytes and return a pointer to elements of the type associated to the provided PointerIO instance (see {@link PointerIO#getTargetType()}), ensuring the pointer to the memory is aligned to the provided boundary.
     * @param io PointerIO instance able to store and retrieve elements of the array
     * @param byteSize length of the array in bytes
     * @param alignment boundary to which the returned pointer should be aligned
     * @param beforeDeallocation fake releaser that should be run just before the memory is actually released, for instance in order to call some object destructor
     * @return a pointer to a new memory area large enough to hold byteSize consecutive bytes
     */
    public static <V> Pointer<V> allocateAlignedBytes(PointerIO<V> io, long byteSize, int alignment, final Releaser beforeDeallocation) {
        if (byteSize == 0)
        	return null;
        if (byteSize < 0)
        	throw new IllegalArgumentException("Cannot allocate a negative amount of memory !");
        
        long address, offset = 0;
        if (alignment <= 1)
        		address = JNI.mallocNulled(byteSize);
        	else {
        		//address = JNI.mallocNulledAligned(byteSize, alignment);
        		//if (address == 0) 
        		{
        			// invalid alignment (< sizeof(void*) or not a power of 2
        			address = JNI.mallocNulled(byteSize + alignment - 1);
				long remainder = address % alignment;
				if (remainder > 0)
					offset = alignment - remainder;
        		}
        	}
        	
        if (address == 0)
        	throw new RuntimeException("Failed to allocate " + byteSize);

		Pointer<V> ptr = newPointer(io, address, true, address, address + byteSize + offset, null, NO_PARENT, beforeDeallocation == null ? freeReleaser : new Releaser() {
        	//@Override
        	public void release(Pointer<?> p) {
        		beforeDeallocation.release(p);
        		freeReleaser.release(p);
        	}
        }, null);
        
        if (offset > 0)
        		ptr = ptr.offset(offset);
        
        return ptr;
    }
    
    /**
     * Create a pointer that depends on this pointer and will call a releaser prior to release this pointer, when it is GC'd.<br>
     * This pointer MUST NOT be used anymore.
     * @deprecated This method can easily be misused and is reserved to advanced users.
     * @param beforeDeallocation releaser that should be run before this pointer's releaser (if any).
     * @return a new pointer to the same memory location as this pointer
     */
    @Deprecated
    public synchronized Pointer<T> withReleaser(final Releaser beforeDeallocation) {
    		return newPointer(getIO(), getPeer(), isOrdered(), getValidStart(), getValidEnd(), null, NO_PARENT, beforeDeallocation, null);
    }
    static Releaser freeReleaser = new FreeReleaser();
    static class FreeReleaser implements Releaser {
    	//@Override
		public void release(Pointer<?> p) {
			assert p.getSibling() == null;
			assert p.validStart == p.getPeer();
			
               if (BridJ.debugPointers) {
                    p.deletionTrace = new RuntimeException().fillInStackTrace();
               	BridJ.info("Freeing pointer " + p +
                         " (peer = " + p.getPeer() +
                         ", validStart = " + p.validStart +
                         ", validEnd = " + p.validEnd + 
                         ", validBytes = " + p.getValidBytes() + 
                         ").\nCreation trace:\n\t" + Utils.toString(p.creationTrace).replaceAll("\n", "\n\t") +
                         "\nDeletion trace:\n\t" + Utils.toString(p.deletionTrace).replaceAll("\n", "\n\t"));
               }
          	if (!BridJ.debugNeverFree)
          		JNI.free(p.getPeer());
          }
    }
    
    /**
     * Create a memory area large enough to hold arrayLength items of type elementClass.
     * @param elementClass type of the array elements
     * @param arrayLength length of the array in elements
     * @return a pointer to a new memory area large enough to hold arrayLength items of type elementClass.  
     */
    public static <V> Pointer<V> allocateArray(Class<V> elementClass, long arrayLength) {
        return allocateArray((Type)elementClass, arrayLength);
    }
    /**
     * Create a memory area large enough to hold arrayLength items of type elementClass.
     * @param elementClass type of the array elements
     * @param arrayLength length of the array in elements
     * @return a pointer to a new memory area large enough to hold arrayLength items of type elementClass.
     */
    public static <V> Pointer<V> allocateArray(Type elementClass, long arrayLength) {
		if (arrayLength == 0)
			return null;
		
		PointerIO pio = PointerIO.getInstance(elementClass);
		if (pio == null)
			throw new UnsupportedOperationException("Cannot allocate memory for type " + (elementClass instanceof Class ? ((Class)elementClass).getName() : elementClass.toString()));
		return (Pointer<V>)allocateArray(pio, arrayLength);
    }
    
    
    /**
     * Create a memory area large enough to hold arrayLength items of type elementClass, ensuring the pointer to the memory is aligned to the provided boundary.
     * @param elementClass type of the array elements
     * @param arrayLength length of the array in elements
     * @param alignment boundary to which the returned pointer should be aligned
     * @return a pointer to a new memory area large enough to hold arrayLength items of type elementClass.  
     */
    public static <V> Pointer<V> allocateAlignedArray(Class<V> elementClass, long arrayLength, int alignment) {
        return allocateAlignedArray((Type)elementClass, arrayLength, alignment);
    }
    
    /**
     * Create a memory area large enough to hold arrayLength items of type elementClass, ensuring the pointer to the memory is aligned to the provided boundary.
     * @param elementClass type of the array elements
     * @param arrayLength length of the array in elements
     * @param alignment boundary to which the returned pointer should be aligned
     * @return a pointer to a new memory area large enough to hold arrayLength items of type elementClass.
     */
    public static <V> Pointer<V> allocateAlignedArray(Type elementClass, long arrayLength, int alignment) {
		PointerIO io = PointerIO.getInstance(elementClass);
		if (io == null)
			throw new UnsupportedOperationException("Cannot allocate memory for type " + (elementClass instanceof Class ? ((Class)elementClass).getName() : elementClass.toString()));
		return allocateAlignedBytes(io, getTargetSizeToAllocateArrayOrThrow(io) * arrayLength, alignment, null);
    }

    /**
     * Create a pointer to the memory location used by a direct NIO buffer.<br>
     * If the NIO buffer is not direct, then its backing Java array is copied to some native memory and will never be updated by changes to the native memory (calls {@link Pointer#pointerToArray(Object)}), unless a call to {@link Pointer#updateBuffer(Buffer)} is made manually.<br>
     * The returned pointer (and its subsequent views returned by {@link Pointer#offset(long)} or {@link Pointer#next(long)}) can be used safely : it retains a reference to the original NIO buffer, so that this latter cannot be garbage collected before the pointer.
     */
    public static Pointer<?> pointerToBuffer(Buffer buffer) {
        if (buffer == null)
			return null;
		
		#foreach ($prim in $primitivesNoBool)
		if (buffer instanceof ${prim.BufferName})
			return (Pointer)pointerTo${prim.CapName}s((${prim.BufferName})buffer);
		#end
        throw new UnsupportedOperationException("Unhandled buffer type : " + buffer.getClass().getName());
	}
	
	/**
	 * When a pointer was created with {@link Pointer#pointerToBuffer(Buffer)} on a non-direct buffer, a native copy of the buffer data was made.
	 * This method updates the original buffer with the native memory, and does nothing if the buffer is direct <b>and</b> points to the same memory location as this pointer.<br>
	 * @throws IllegalArgumentException if buffer is direct and does not point to the exact same location as this Pointer instance
     */
    public void updateBuffer(Buffer buffer) {
        if (buffer == null)
			throw new IllegalArgumentException("Cannot update a null Buffer !");
		
		if (Utils.isDirect(buffer)) {
			long address = JNI.getDirectBufferAddress(buffer);
			if (address != getPeer()) {
				throw new IllegalArgumentException("Direct buffer does not point to the same location as this Pointer instance, updating it makes no sense !");
			}
		} else {
			#foreach ($prim in $primitivesNoBool)
			#if ($prim.Name != "char")
			if (buffer instanceof ${prim.BufferName}) {
				((${prim.BufferName})buffer).duplicate().put(get${prim.BufferName}());
				return;
			}
			#end
			#end
			throw new UnsupportedOperationException("Unhandled buffer type : " + buffer.getClass().getName());
		}
	}

#foreach ($prim in $primitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end

//-- primitive: $prim.Name --
	
    #docAllocateCopy($prim.Name $prim.WrapperName)
    public static Pointer<${prim.WrapperName}> pointerTo${prim.CapName}(${prim.Name} value) {
        Pointer<${prim.WrapperName}> mem = allocate(PointerIO.get${prim.CapName}Instance());
        mem.set${prim.CapName}(value);
        return mem;
    }
	
#docAllocateArrayCopy($prim.Name $prim.WrapperName)
    public static Pointer<${prim.WrapperName}> pointerTo${prim.CapName}s(${prim.Name}... values) {
        if (values == null)
			return null;
		Pointer<${prim.WrapperName}> mem = allocateArray(PointerIO.get${prim.CapName}Instance(), values.length);
        mem.set${prim.CapName}sAtOffset(0, values, 0, values.length);
        return mem;
    }
    
    #docAllocateArray2DCopy($prim.Name $prim.WrapperName)
    public static Pointer<Pointer<${prim.WrapperName}>> pointerTo${prim.CapName}s(${prim.Name}[][] values) {
        if (values == null)
			return null;
		int dim1 = values.length, dim2 = values[0].length;
		Pointer<Pointer<${prim.WrapperName}>> mem = allocate${prim.CapName}s(dim1, dim2);
		for (int i1 = 0; i1 < dim1; i1++)
        	mem.set${prim.CapName}sAtOffset(i1 * dim2 * ${primSize}, values[i1], 0, dim2);
		return mem;
    }
    
    #docAllocateArray3DCopy($prim.Name $prim.WrapperName)
    public static Pointer<Pointer<Pointer<${prim.WrapperName}>>> pointerTo${prim.CapName}s(${prim.Name}[][][] values) {
        if (values == null)
			return null;
		int dim1 = values.length, dim2 = values[0].length, dim3 = values[0][0].length;
		Pointer<Pointer<Pointer<${prim.WrapperName}>>> mem = allocate${prim.CapName}s(dim1, dim2, dim3);
		for (int i1 = 0; i1 < dim1; i1++) {
        	int offset1 = i1 * dim2;
        	for (int i2 = 0; i2 < dim2; i2++) {
        		int offset2 = (offset1 + i2) * dim3;
				mem.set${prim.CapName}sAtOffset(offset2 * ${primSize}, values[i1][i2], 0, dim3);
			}
		}
		return mem;
    }
	
    #docAllocate($prim.Name $prim.WrapperName)
    public static Pointer<${prim.WrapperName}> allocate${prim.CapName}() {
        return allocate(PointerIO.get${prim.CapName}Instance());
    }
    #docAllocateArray($prim.Name $prim.WrapperName)
    public static Pointer<${prim.WrapperName}> allocate${prim.CapName}s(long arrayLength) {
        return allocateArray(PointerIO.get${prim.CapName}Instance(), arrayLength);
    }
    
    #docAllocateArray2D($prim.Name $prim.WrapperName)
    public static Pointer<Pointer<${prim.WrapperName}>> allocate${prim.CapName}s(long dim1, long dim2) {
        return allocateArray(PointerIO.getArrayInstance(PointerIO.get${prim.CapName}Instance(), new long[] { dim1, dim2 }, 0), dim1);
        
    }
    #docAllocateArray3D($prim.Name $prim.WrapperName)
    public static Pointer<Pointer<Pointer<${prim.WrapperName}>>> allocate${prim.CapName}s(long dim1, long dim2, long dim3) {
        long[] dims = new long[] { dim1, dim2, dim3 };
		return
			allocateArray(
				PointerIO.getArrayInstance(
					//PointerIO.get${prim.CapName}Instance(),
					PointerIO.getArrayInstance(
						PointerIO.get${prim.CapName}Instance(), 
						dims,
						1
					),
					dims,
					0
				),
				dim1
			)
		;
    }

#end
#foreach ($prim in $primitivesNoBool)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end
//-- primitive (no bool): $prim.Name --

	/**
     * Create a pointer to the memory location used by a direct NIO ${prim.BufferName}.<br>
     * If the NIO ${prim.BufferName} is not direct, then its backing Java array is copied to some native memory and will never be updated by changes to the native memory (calls {@link Pointer#pointerTo${prim.CapName}s(${prim.Name}[])}), unless a call to {@link Pointer#updateBuffer(Buffer)} is made manually.<br>
     * The returned pointer (and its subsequent views returned by {@link Pointer#offset(long)} or {@link Pointer#next(long)}) can be used safely : it retains a reference to the original NIO buffer, so that this latter cannot be garbage collected before the pointer.</br>
     */
    public static Pointer<${prim.WrapperName}> pointerTo${prim.CapName}s(${prim.BufferName} buffer) {
        if (buffer == null)
			return null;
		
		if (!buffer.isDirect()) {
			return pointerTo${prim.CapName}s(buffer.array());
			//throw new UnsupportedOperationException("Cannot create pointers to indirect ${prim.BufferName} buffers");
		}
		
		long address = JNI.getDirectBufferAddress(buffer);
		long size = JNI.getDirectBufferCapacity(buffer);
		
		// HACK (TODO?) the JNI spec says size is in bytes, but in practice on mac os x it's in elements !!!
		size *= ${primSize};
		//System.out.println("Buffer capacity = " + size);
		
		if (address == 0 || size == 0)
			return null;
		
		PointerIO<${prim.WrapperName}> io = CommonPointerIOs.${prim.Name}IO;
		boolean ordered = buffer.order().equals(ByteOrder.nativeOrder());
		return newPointer(io, address, ordered, address, address + size, null, NO_PARENT, null, buffer);
    }
	
#end
    
    /**
     * Get the type of pointed elements.
     */
	public Type getTargetType() {
        PointerIO<T> io = getIO();
        return io == null ? null : io.getTargetType();
    }
    
    /**
	 * Read an untyped pointer value from the pointed memory location
	 * @deprecated Avoid using untyped pointers, if possible.
	 */
	@Deprecated
    public Pointer<?> getPointer() {
    	return getPointerAtOffset(0, (PointerIO)null);	
    }
    
    /**
	 * Read a pointer value from the pointed memory location shifted by a byte offset
	 */
	public Pointer<?> getPointerAtOffset(long byteOffset) {
        return getPointerAtOffset(byteOffset, (PointerIO)null);
    }
    
#docGetIndex("pointer" "getPointerAtOffset(valueIndex * Pointer.SIZE)")
	public Pointer<?> getPointerAtIndex(long valueIndex) {
	    return getPointerAtOffset(valueIndex * Pointer.SIZE);
	}
    
    /**
	 * Read a pointer value from the pointed memory location.<br>
	 * @param c class of the elements pointed by the resulting pointer 
	 */
    public <U> Pointer<U> getPointer(Class<U> c) {
    	return getPointerAtOffset(0, (PointerIO<U>)PointerIO.getInstance(c));	
    }
    
    /**
	 * Read a pointer value from the pointed memory location
	 * @param pio PointerIO instance that knows how to read the elements pointed by the resulting pointer 
	 */
    public <U> Pointer<U> getPointer(PointerIO<U> pio) {
    	return getPointerAtOffset(0, pio);	
    }
    
    /**
	 * Read a pointer value from the pointed memory location shifted by a byte offset
	 * @param c class of the elements pointed by the resulting pointer 
	 */
	public <U> Pointer<U> getPointerAtOffset(long byteOffset, Class<U> c) {
    	return getPointerAtOffset(byteOffset, (Type)c);	
    }
    
    /**
	 * Read a pointer value from the pointed memory location shifted by a byte offset
	 * @param t type of the elements pointed by the resulting pointer 
	 */
	public <U> Pointer<U> getPointerAtOffset(long byteOffset, Type t) {
        return getPointerAtOffset(byteOffset, t == null ? null : (PointerIO<U>)PointerIO.getInstance(t));
    }
    
    /**
	 * Read a pointer value from the pointed memory location shifted by a byte offset
	 * @param pio PointerIO instance that knows how to read the elements pointed by the resulting pointer 
	 */
	public <U> Pointer<U> getPointerAtOffset(long byteOffset, PointerIO<U> pio) {
    	long value = getSizeTAtOffset(byteOffset);
    	if (value == 0)
    		return null;
    	return newPointer(pio, value, isOrdered(), UNKNOWN_VALIDITY, UNKNOWN_VALIDITY, this, byteOffset, null, null);
    }

    /**
     * Write a pointer value to the pointed memory location
     */
    public Pointer<T> setPointer(Pointer<?> value) {
    	return setPointerAtOffset(0, value);
    }
    
    /**
     * Write a pointer value to the pointed memory location shifted by a byte offset
     */
	public Pointer<T> setPointerAtOffset(long byteOffset, Pointer<?> value) {
        setSizeTAtOffset(byteOffset, value == null ? 0 : value.getPeer());
        return this;
    }

#docSetIndex("pointer" "setPointerAtOffset(valueIndex * Pointer.SIZE, value)")
    public Pointer<T> setPointerAtIndex(long valueIndex, Pointer<?> value) {
        setPointerAtOffset(valueIndex * Pointer.SIZE, value);
        return this;
    }
    
    /**
	 * Read an array of untyped pointer values from the pointed memory location shifted by a byte offset
	 * @deprecated Use a typed version instead : {@link Pointer#getPointersAtOffset(long, int, Type)}, {@link Pointer#getPointersAtOffset(long, int, Class)} or {@link Pointer#getPointersAtOffset(long, int, PointerIO)}
	 */
	public Pointer<?>[] getPointersAtOffset(long byteOffset, int arrayLength) {
        return getPointersAtOffset(byteOffset, arrayLength, (PointerIO)null);
    }
    /**
	 * Read the array of remaining untyped pointer values from the pointed memory location
	 * @deprecated Use a typed version instead : {@link Pointer#getPointersAtOffset(long, int, Type)}, {@link Pointer#getPointersAtOffset(long, int, Class)} or {@link Pointer#getPointersAtOffset(long, int, PointerIO)}
	 */
    @Deprecated
	public Pointer<?>[] getPointers() {
        long rem = getValidElements("Cannot create array if remaining length is not known. Please use getPointers(int length) instead.");
		return getPointersAtOffset(0L, (int)rem);
    }
    /**
	 * Read an array of untyped pointer values from the pointed memory location
	 * @deprecated Use a typed version instead : {@link Pointer#getPointersAtOffset(long, int, Type)}, {@link Pointer#getPointersAtOffset(long, int, Class)} or {@link Pointer#getPointersAtOffset(long, int, PointerIO)}
	 */
    @Deprecated                     
	public Pointer<?>[] getPointers(int arrayLength) {
        return getPointersAtOffset(0, arrayLength);
    }
    /**
	 * Read an array of pointer values from the pointed memory location shifted by a byte offset
	 * @param t type of the elements pointed by the resulting pointer 
	 */
	public <U> Pointer<U>[] getPointersAtOffset(long byteOffset, int arrayLength, Type t) {
        return getPointersAtOffset(byteOffset, arrayLength, t == null ? null : (PointerIO<U>)PointerIO.getInstance(t));
    }
    /**
	 * Read an array of pointer values from the pointed memory location shifted by a byte offset
	 * @param t class of the elements pointed by the resulting pointer 
	 */
	public <U> Pointer<U>[] getPointersAtOffset(long byteOffset, int arrayLength, Class<U> t) {
        return getPointersAtOffset(byteOffset, arrayLength, (Type)t);
    }
    
    /**
	 * Read an array of pointer values from the pointed memory location shifted by a byte offset
	 * @param pio PointerIO instance that knows how to read the elements pointed by the resulting pointer 
	 */
	public <U> Pointer<U>[] getPointersAtOffset(long byteOffset, int arrayLength, PointerIO pio) {
    	Pointer<U>[] values = (Pointer<U>[])new Pointer[arrayLength];
		int s = Platform.POINTER_SIZE;
		for (int i = 0; i < arrayLength; i++)
			values[i] = getPointerAtOffset(byteOffset + i * s, pio);
		return values;
	}
	/**
	 * Write an array of pointer values to the pointed memory location shifted by a byte offset
	 */
	public Pointer<T> setPointersAtOffset(long byteOffset, Pointer<?>[] values) {
    		return setPointersAtOffset(byteOffset, values, 0, values.length);
	}
	
	/**
	 * Write length pointer values from the given array (starting at the given value offset) to the pointed memory location shifted by a byte offset
	 */
	public Pointer<T> setPointersAtOffset(long byteOffset, Pointer<?>[] values, int valuesOffset, int length) {
		if (values == null)
			throw new IllegalArgumentException("Null values");
		int n = length, s = Platform.POINTER_SIZE;
		for (int i = 0; i < n; i++)
			setPointerAtOffset(byteOffset + i * s, values[valuesOffset + i]);
		return this;
	}
	
	/**
	 * Write an array of pointer values to the pointed memory location
	 */
    public Pointer<T> setPointers(Pointer<?>[] values) {
    		return setPointersAtOffset(0, values);
	}
	
	/**
	 * Read an array of elements from the pointed memory location shifted by a byte offset.<br>
	 * For pointers to primitive types (e.g. {@code Pointer<Integer> }), this method returns primitive arrays (e.g. {@code int[] }), unlike {@link Pointer#toArray } (which returns arrays of objects so primitives end up being boxed, e.g. {@code Integer[] })
	 * @return an array of values of the requested length. The array is an array of primitives if the pointer's target type is a primitive or a boxed primitive type
	 */
	public Object getArrayAtOffset(long byteOffset, int length) {
        return getIO("Cannot create sublist").getArray(this, byteOffset, length);	
	}
	
	/**
	 * Read an array of elements from the pointed memory location.<br>
	 * For pointers to primitive types (e.g. {@code Pointer<Integer> }), this method returns primitive arrays (e.g. {@code int[] }), unlike {@link Pointer#toArray } (which returns arrays of objects so primitives end up being boxed, e.g. {@code Integer[] })
	 * @return an array of values of the requested length. The array is an array of primitives if the pointer's target type is a primitive or a boxed primitive type
	 */
	public Object getArray(int length) {
		return getArrayAtOffset(0L, length);	
	}
	
	/**
	 * Read the array of remaining elements from the pointed memory location.<br>
	 * For pointers to primitive types (e.g. {@code Pointer<Integer> }), this method returns primitive arrays (e.g. {@code int[] }), unlike {@link Pointer#toArray } (which returns arrays of objects so primitives end up being boxed, e.g. {@code Integer[] })
	 * @return an array of values of the requested length. The array is an array of primitives if the pointer's target type is a primitive or a boxed primitive type
	 */
	public Object getArray() {
		return getArray((int)getValidElements());	
	}
	
	/**
	 * Read an NIO {@link Buffer} of elements from the pointed memory location shifted by a byte offset.<br>
	 * @return an NIO {@link Buffer} of values of the requested length.
	 * @throws UnsupportedOperationException if this pointer's target type is not a Java primitive type with a corresponding NIO {@link Buffer} class.
	 */
	public <B extends Buffer> B getBufferAtOffset(long byteOffset, int length) {
        return (B)getIO("Cannot create Buffer").getBuffer(this, byteOffset, length);	
	}
	
	/**
	 * Read an NIO {@link Buffer} of elements from the pointed memory location.<br>
	 * @return an NIO {@link Buffer} of values of the requested length.
	 * @throws UnsupportedOperationException if this pointer's target type is not a Java primitive type with a corresponding NIO {@link Buffer} class.
	 */
	public <B extends Buffer> B getBuffer(int length) {
		return (B)getBufferAtOffset(0L, length);	
	}
	
	/**
	 * Read the NIO {@link Buffer} of remaining elements from the pointed memory location.<br>
	 * @return an array of values of the requested length.
	 * @throws UnsupportedOperationException if this pointer's target type is not a Java primitive type with a corresponding NIO {@link Buffer} class.
	 */
	public <B extends Buffer> B getBuffer() {
		return (B)getBuffer((int)getValidElements());	
	}
	
	/**
	 * Write an array of elements to the pointed memory location shifted by a byte offset.<br>
	 * For pointers to primitive types (e.g. {@code Pointer<Integer> }), this method accepts primitive arrays (e.g. {@code int[] }) instead of arrays of boxed primitives (e.g. {@code Integer[] })
	 */
	public Pointer<T> setArrayAtOffset(long byteOffset, Object array) {
        getIO("Cannot create sublist").setArray(this, byteOffset, array);
        return this;
	}
	
	/**
     * Allocate enough memory for array.length values, copy the values of the array provided as argument into it and return a pointer to that memory.<br>
     * The memory will be automatically be freed when the pointer is garbage-collected or upon manual calls to {@link Pointer#release()}.<br>
     * The pointer won't be garbage-collected until all its views are garbage-collected themselves ({@link Pointer#offset(long)}, {@link Pointer#next(long)}, {@link Pointer#next()}).<br>
     * For pointers to primitive types (e.g. {@code Pointer<Integer> }), this method accepts primitive arrays (e.g. {@code int[] }) instead of arrays of boxed primitives (e.g. {@code Integer[] })
	 * @param array primitive array containing the initial values for the created memory area
     * @return pointer to a new memory location that initially contains the consecutive values provided in argument
     */
	public static <T> Pointer<T> pointerToArray(Object array) {
		if (array == null)
			return null;
		
		PointerIO<T> io = PointerIO.getArrayIO(array);
		if (io == null)
            throwBecauseUntyped("Cannot create pointer to array");
        
        Pointer<T> ptr = allocateArray(io, java.lang.reflect.Array.getLength(array));
        io.setArray(ptr, 0, array);
        return ptr;
	}
	
	/**
	 * Write an array of elements to the pointed memory location.<br>
	 * For pointers to primitive types (e.g. {@code Pointer<Integer> }), this method accepts primitive arrays (e.g. {@code int[] }) instead of arrays of boxed primitives (e.g. {@code Integer[] })
	 */
	public Pointer<T> setArray(Object array) {
		return setArrayAtOffset(0L, array);
	}
	
	#foreach ($sizePrim in ["SizeT", "CLong"])
//-- size primitive: $sizePrim --

#docAllocateCopy($sizePrim $sizePrim)
    public static Pointer<${sizePrim}> pointerTo${sizePrim}(long value) {
		Pointer<${sizePrim}> p = allocate(PointerIO.get${sizePrim}Instance());
		p.set${sizePrim}(value);
		return p;
	}
#docAllocateCopy($sizePrim $sizePrim)
    public static Pointer<${sizePrim}> pointerTo${sizePrim}(${sizePrim} value) {
		Pointer<${sizePrim}> p = allocate(PointerIO.get${sizePrim}Instance());
		p.set${sizePrim}(value);
		return p;
	}
#docAllocateArrayCopy($sizePrim $sizePrim)
    public static Pointer<${sizePrim}> pointerTo${sizePrim}s(long... values) {
		if (values == null)
			return null;
		return allocateArray(PointerIO.get${sizePrim}Instance(), values.length).set${sizePrim}sAtOffset(0, values);
	}
#docAllocateArrayCopy($sizePrim $sizePrim)
    public static Pointer<${sizePrim}> pointerTo${sizePrim}s(${sizePrim}... values) {
		if (values == null)
			return null;
		return allocateArray(PointerIO.get${sizePrim}Instance(), values.length).set${sizePrim}sAtOffset(0, values);
	}
	
#docAllocateArrayCopy($sizePrim $sizePrim)
    public static Pointer<${sizePrim}> pointerTo${sizePrim}s(int[] values) {
		if (values == null)
			return null;
		return allocateArray(PointerIO.get${sizePrim}Instance(), values.length).set${sizePrim}sAtOffset(0, values);
	}
	
#docAllocateArray($sizePrim $sizePrim)
    public static Pointer<${sizePrim}> allocate${sizePrim}s(long arrayLength) {
		return allocateArray(PointerIO.get${sizePrim}Instance(), arrayLength);
	}
#docAllocate($sizePrim $sizePrim)
    public static Pointer<${sizePrim}> allocate${sizePrim}() {
		return allocate(PointerIO.get${sizePrim}Instance());
	}
	
#docGet($sizePrim $sizePrim)
    public long get${sizePrim}() {
		return ${sizePrim}.SIZE == 8 ? 
			getLong() : 
			getInt();// & 0xFFFFFFFFL;
	}
#docGetOffset($sizePrim $sizePrim "Pointer#get${sizePrim}()")
    public long get${sizePrim}AtOffset(long byteOffset) {
		return ${sizePrim}.SIZE == 8 ? 
			getLongAtOffset(byteOffset) : 
			getIntAtOffset(byteOffset);// & 0xFFFFFFFFL;
	}
#docGetIndex($sizePrim "get${sizePrim}AtOffset(valueIndex * ${sizePrim}.SIZE")
	public long get${sizePrim}AtIndex(long valueIndex) {
	  return get${sizePrim}AtOffset(valueIndex * ${sizePrim}.SIZE);
	}
#docGetRemainingArray($sizePrim $sizePrim)
    public long[] get${sizePrim}s() {
		long rem = getValidElements("Cannot create array if remaining length is not known. Please use get${sizePrim}s(int length) instead.");
		if (${sizePrim}.SIZE == 8)
    		return getLongs((int)rem);
		return get${sizePrim}s((int)rem);
	}
#docGetArray($sizePrim $sizePrim)
    public long[] get${sizePrim}s(int arrayLength) {
    	if (${sizePrim}.SIZE == 8)
    		return getLongs(arrayLength);
		return get${sizePrim}sAtOffset(0, arrayLength);
	}
#docGetArrayOffset($sizePrim $sizePrim "Pointer#get${sizePrim}s(int)")
	public long[] get${sizePrim}sAtOffset(long byteOffset, int arrayLength) {
		if (${sizePrim}.SIZE == 8)  
			return getLongsAtOffset(byteOffset, arrayLength);
		
		int[] values = getIntsAtOffset(byteOffset, arrayLength);
		long[] ret = new long[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			ret[i] = //0xffffffffL & 
				values[i];
		}
		return ret;
	}
	
#docSet($sizePrim $sizePrim)
    public Pointer<T> set${sizePrim}(long value) {
    	if (${sizePrim}.SIZE == 8)
			setLong(value);
		else {
			setInt(SizeT.safeIntCast(value));
		}
		return this;
	}
#docSet($sizePrim $sizePrim)
    public Pointer<T> set${sizePrim}(${sizePrim} value) {
		return set${sizePrim}(value.longValue());
	}
#docSetOffset($sizePrim $sizePrim "Pointer#set${sizePrim}(long)")
	public Pointer<T> set${sizePrim}AtOffset(long byteOffset, long value) {
		if (${sizePrim}.SIZE == 8)
			setLongAtOffset(byteOffset, value);
		else {
			setIntAtOffset(byteOffset, SizeT.safeIntCast(value));
		}
		return this;
	}
#docSetIndex($sizePrim "set${sizePrim}AtOffset(valueIndex * ${sizePrim}.SIZE, value)")
  public Pointer<T> set${sizePrim}AtIndex(long valueIndex, long value) {
	  return set${sizePrim}AtOffset(valueIndex * ${sizePrim}.SIZE, value);
	}
	
#docSetOffset($sizePrim $sizePrim "Pointer#set${sizePrim}(${sizePrim})")
	public Pointer<T> set${sizePrim}AtOffset(long byteOffset, ${sizePrim} value) {
		return set${sizePrim}AtOffset(byteOffset, value.longValue());
	}
#docSetArray($sizePrim $sizePrim)
    public Pointer<T> set${sizePrim}s(long[] values) {
		if (${sizePrim}.SIZE == 8)
    		return setLongs(values);
		return set${sizePrim}sAtOffset(0, values);
	}
#docSetArray($sizePrim $sizePrim)
    public Pointer<T> set${sizePrim}s(int[] values) {
    	if (${sizePrim}.SIZE == 4)
    		return setInts(values);
		return set${sizePrim}sAtOffset(0, values);
	}
#docSetArray($sizePrim $sizePrim)
    public Pointer<T> set${sizePrim}s(${sizePrim}[] values) {
		return set${sizePrim}sAtOffset(0, values);
	}
#docSetArrayOffset($sizePrim $sizePrim "Pointer#set${sizePrim}s(long[])")
	public Pointer<T> set${sizePrim}sAtOffset(long byteOffset, long[] values) {
    		return set${sizePrim}sAtOffset(byteOffset, values, 0, values.length);
	}
#docSetArrayOffset($sizePrim $sizePrim "Pointer#set${sizePrim}s(long[])")
    public abstract Pointer<T> set${sizePrim}sAtOffset(long byteOffset, long[] values, int valuesOffset, int length);
#docSetArrayOffset($sizePrim $sizePrim "Pointer#set${sizePrim}s(${sizePrim}...)")
	public Pointer<T> set${sizePrim}sAtOffset(long byteOffset, ${sizePrim}... values) {
		if (values == null)
			throw new IllegalArgumentException("Null values");
		int n = values.length, s = ${sizePrim}.SIZE;
		for (int i = 0; i < n; i++)
			set${sizePrim}AtOffset(byteOffset + i * s, values[i].longValue());
		return this;
	}
#docSetArrayOffset($sizePrim $sizePrim "Pointer#set${sizePrim}s(int[])")
	public abstract Pointer<T> set${sizePrim}sAtOffset(long byteOffset, int[] values);
	
	#end
	
	void setSignedIntegralAtOffset(long byteOffset, long value, long sizeOfIntegral) {
		switch ((int)sizeOfIntegral) {
		case 1:
			if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE)
				throw new RuntimeException("Value out of byte bounds : " + value);
			setByteAtOffset(byteOffset, (byte)value);
			break;
		case 2:
			if (value > Short.MAX_VALUE || value < Short.MIN_VALUE)
				throw new RuntimeException("Value out of short bounds : " + value);
			setShortAtOffset(byteOffset, (short)value);
			break;
		case 4:
			if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE)
				throw new RuntimeException("Value out of int bounds : " + value);
			setIntAtOffset(byteOffset, (int)value);
			break;
		case 8:
			setLongAtOffset(byteOffset, value);
			break;
		default:
			throw new IllegalArgumentException("Cannot write integral type of size " + sizeOfIntegral + " (value = " + value + ")");
		}
	}
	long getSignedIntegralAtOffset(long byteOffset, long sizeOfIntegral) {
		switch ((int)sizeOfIntegral) {
		case 1:
			return getByteAtOffset(byteOffset);
		case 2:
			return getShortAtOffset(byteOffset);
		case 4:
			return getIntAtOffset(byteOffset);
		case 8:
			return getLongAtOffset(byteOffset);
		default:
			throw new IllegalArgumentException("Cannot read integral type of size " + sizeOfIntegral);
		}
	}
	
#docAllocateCopy("pointer", "Pointer")
    public static <T> Pointer<Pointer<T>> pointerToPointer(Pointer<T> value) {
		Pointer<Pointer<T>> p = (Pointer<Pointer<T>>)(Pointer)allocate(PointerIO.getPointerInstance());
		p.setPointerAtOffset(0, value);
		return p;
	}
	
#docAllocateArrayCopy("pointer", "Pointer")
	public static <T> Pointer<Pointer<T>> pointerToPointers(Pointer<T>... values) {
		if (values == null)
			return null;
		int n = values.length, s = Pointer.SIZE;
		PointerIO<Pointer> pio = PointerIO.getPointerInstance(); // TODO get actual pointer instances PointerIO !!!
		Pointer<Pointer<T>> p = (Pointer<Pointer<T>>)(Pointer)allocateArray(pio, n);
		for (int i = 0; i < n; i++) {
			p.setPointerAtOffset(i * s, values[i]);
		}
		return p;
	}
	
    /**
     * Copy all values from an NIO buffer to the pointed memory location shifted by a byte offset
     */
	public Pointer<T> setValuesAtOffset(long byteOffset, Buffer values) {
        #foreach ($prim in $primitivesNoBool)
        if (values instanceof ${prim.BufferName}) {
            set${prim.CapName}sAtOffset(byteOffset, (${prim.BufferName})values);
            return this;
        }
        #end
        throw new UnsupportedOperationException("Unhandled buffer type : " + values.getClass().getName());
    }
    
    /**
     * Copy length values from an NIO buffer (beginning at element at valuesOffset index) to the pointed memory location shifted by a byte offset
     */
	public Pointer<T> setValuesAtOffset(long byteOffset, Buffer values, int valuesOffset, int length) {
        #foreach ($prim in $primitivesNoBool)
        if (values instanceof ${prim.BufferName}) {
            set${prim.CapName}sAtOffset(byteOffset, (${prim.BufferName})values, valuesOffset, length);
            return this;
        }
        #end
        throw new UnsupportedOperationException("Unhandled buffer type : " + values.getClass().getName());
    }
    
    /**
     * Copy values from an NIO buffer to the pointed memory location
     */
    public Pointer<T> setValues(Buffer values) {
    	#foreach ($prim in $primitivesNoBool)
        if (values instanceof ${prim.BufferName}) {
            set${prim.CapName}s((${prim.BufferName})values);
            return this;
        }
        #end
        throw new UnsupportedOperationException("Unhandled buffer type : " + values.getClass().getName());
    }

    /**
     * Copy bytes from the memory location indicated by this pointer to that of another pointer (with byte offsets for both the source and the destination), using the <a href="http://www.cplusplus.com/reference/clibrary/cstring/memcpy/">memcpy</a> C function.<br>
     * If the destination and source memory locations are likely to overlap, {@link Pointer#moveBytesAtOffsetTo(long, Pointer, long, long)} must be used instead.
     */
    @Deprecated
	public Pointer<T> copyBytesAtOffsetTo(long byteOffset, Pointer<?> destination, long byteOffsetInDestination, long byteCount) {
		#declareCheckedPeerAtOffset("byteOffset" "byteCount")
		JNI.memcpy(destination.getCheckedPeer(byteOffsetInDestination, byteCount), checkedPeer, byteCount);
		return this;
    }
    
    /**
     * Copy bytes from the memory location indicated by this pointer to that of another pointer using the <a href="http://www.cplusplus.com/reference/clibrary/cstring/memcpy/">memcpy</a> C function.<br>
     * If the destination and source memory locations are likely to overlap, {@link Pointer#moveBytesAtOffsetTo(long, Pointer, long, long)} must be used instead.<br>
     * See {@link Pointer#copyBytesAtOffsetTo(long, Pointer, long, long)} for more options.
     */
    @Deprecated
	public Pointer<T> copyBytesTo(Pointer<?> destination, long byteCount) {
    		return copyBytesAtOffsetTo(0, destination, 0, byteCount);
    }
    
    /**
     * Copy bytes from the memory location indicated by this pointer to that of another pointer (with byte offsets for both the source and the destination), using the <a href="http://www.cplusplus.com/reference/clibrary/cstring/memmove/">memmove</a> C function.<br>
     * Works even if the destination and source memory locations are overlapping.
     */
    @Deprecated
	public Pointer<T> moveBytesAtOffsetTo(long byteOffset, Pointer<?> destination, long byteOffsetInDestination, long byteCount) {
		#declareCheckedPeerAtOffset("byteOffset" "byteCount")
		JNI.memmove(destination.getCheckedPeer(byteOffsetInDestination, byteCount), checkedPeer, byteCount);
    		return this;
    }
    
    /**
     * Copy bytes from the memory location indicated by this pointer to that of another pointer, using the <a href="http://www.cplusplus.com/reference/clibrary/cstring/memmove/">memmove</a> C function.<br>
     * Works even if the destination and source memory locations are overlapping.
     */
	public Pointer<T> moveBytesTo(Pointer<?> destination, long byteCount) {
    		return moveBytesAtOffsetTo(0, destination, 0, byteCount);
    }
    
    /**
     * Copy all valid bytes from the memory location indicated by this pointer to that of another pointer, using the <a href="http://www.cplusplus.com/reference/clibrary/cstring/memmove/">memmove</a> C function.<br>
     * Works even if the destination and source memory locations are overlapping.
     */
	public Pointer<T> moveBytesTo(Pointer<?> destination) {
    		return moveBytesTo(destination, getValidBytes("Cannot move an unbounded memory location. Please use validBytes(long)."));
    }
    
    final long getValidBytes(String error) {
    		long rem = getValidBytes();
    		if (rem < 0)
    			throw new IndexOutOfBoundsException(error);

        return rem;
    }
    final long getValidElements(String error) {
    		long rem = getValidElements();
    		if (rem < 0)
    			throw new IndexOutOfBoundsException(error);

        return rem;
    }
    final PointerIO<T> getIO(String error) {
    		PointerIO<T> io = getIO();
        if (io == null)
            throwBecauseUntyped(error);
        return io;
    }
    
    /**
    * Copy remaining bytes from this pointer to a destination using the <a href="http://www.cplusplus.com/reference/clibrary/cstring/memcpy/">memcpy</a> C function (see {@link Pointer#copyBytesTo(Pointer, long)}, {@link Pointer#getValidBytes()})
     */
    public Pointer<T> copyTo(Pointer<?> destination) {
    		return copyTo(destination, getValidElements());
    }
    
    /**
    * Copy remaining elements from this pointer to a destination using the <a href="http://www.cplusplus.com/reference/clibrary/cstring/memcpy/">memcpy</a> C function (see {@link Pointer#copyBytesAtOffsetTo(long, Pointer, long, long)}, {@link Pointer#getValidBytes})
     */
    public Pointer<T> copyTo(Pointer<?> destination, long elementCount) {
    		PointerIO<T> io = getIO("Cannot copy untyped pointer without byte count information. Please use copyBytesAtOffsetTo(offset, destination, destinationOffset, byteCount) instead");
    		return copyBytesAtOffsetTo(0, destination, 0, elementCount * io.getTargetSize());
    }
    
    /**
     * Find the first appearance of the sequence of valid bytes pointed by needle in the memory area pointed to by this bounded pointer (behaviour equivalent to <a href="http://linux.die.net/man/3/memmem">memmem</a>, which is used underneath on platforms where it is available)
     */
    public Pointer<T> find(Pointer<?> needle) {
    		if (needle == null)
    			return null;
    		long firstOccurrence = JNI.memmem(
			getPeer(), 
			getValidBytes("Cannot search an unbounded memory area. Please set bounds with validBytes(long)."), 
			needle.getPeer(), 
			needle.getValidBytes("Cannot search for an unbounded content. Please set bounds with validBytes(long).")
		);
		return pointerToAddress(firstOccurrence, io);
    }
    
    /**
    * Find the last appearance of the sequence of valid bytes pointed by needle in the memory area pointed to by this bounded pointer (also see {@link Pointer#find(Pointer)}).
     */
    public Pointer<T> findLast(Pointer<?> needle) {
    		if (needle == null)
    			return null;
    		long lastOccurrence = JNI.memmem_last(
			getPeer(), 
			getValidBytes("Cannot search an unbounded memory area. Please set bounds with validBytes(long)."), 
			needle.getPeer(), 
			needle.getValidBytes("Cannot search for an unbounded content. Please set bounds with validBytes(long).")
		);
		return pointerToAddress(lastOccurrence, io);
    }


#foreach ($prim in $primitives)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end
//-- primitive: $prim.Name --
	
#docSet(${prim.Name} ${prim.WrapperName})
	public abstract Pointer<T> set${prim.CapName}(${prim.Name} value);
	
#docSetOffset(${prim.Name} ${prim.WrapperName} "Pointer#set${prim.CapName}(${prim.Name})")
	public abstract Pointer<T> set${prim.CapName}AtOffset(long byteOffset, ${prim.Name} value);
	
#docSetIndex(${prim.Name} "set${prim.CapName}AtOffset(valueIndex * $primSize, value)")
	public Pointer<T> set${prim.CapName}AtIndex(long valueIndex, ${prim.Name} value) {
		return set${prim.CapName}AtOffset(valueIndex * $primSize, value);
	}
		
	/**
	 * Write an array of ${prim.Name} values of the specified length to the pointed memory location
	 */
    public Pointer<T> set${prim.CapName}s(${prim.Name}[] values) {
		return set${prim.CapName}sAtOffset(0, values, 0, values.length);
	}	
	
	/**
	 * Write an array of ${prim.Name} values of the specified length to the pointed memory location shifted by a byte offset
	 */
	public Pointer<T> set${prim.CapName}sAtOffset(long byteOffset, ${prim.Name}[] values) {
        return set${prim.CapName}sAtOffset(byteOffset, values, 0, values.length);
    }
    
    /**
	 * Write an array of ${prim.Name} values of the specified length to the pointed memory location shifted by a byte offset, reading values at the given array offset and for the given length from the provided array.
	 */
	public Pointer<T> set${prim.CapName}sAtOffset(long byteOffset, ${prim.Name}[] values, int valuesOffset, int length) {
        #if ($prim.Name == "char")
		if (Platform.WCHAR_T_SIZE == 4)
			return setIntsAtOffset(byteOffset, wcharsToInts(values, valuesOffset, length));
		#end
    	#declareCheckedPeerAtOffset("byteOffset" "${primSize} * length")
        #if ($prim.Name != "byte" && $prim.Name != "boolean")
    	if (!isOrdered()) {
        	JNI.set_${prim.Name}_array_disordered(checkedPeer, values, valuesOffset, length);
        	return this;
    	}
        #end
		JNI.set_${prim.Name}_array(checkedPeer, values, valuesOffset, length);
        return this;
	}
	
#docGet(${prim.Name} ${prim.WrapperName})
    public abstract ${prim.Name} get${prim.CapName}();
    
#docGetOffset(${prim.Name} ${prim.WrapperName} "Pointer#get${prim.CapName}()")
	public abstract ${prim.Name} get${prim.CapName}AtOffset(long byteOffset);
    
#docGetIndex(${prim.Name} "get${prim.CapName}AtOffset(valueIndex * $primSize)")
	public ${prim.Name} get${prim.CapName}AtIndex(long valueIndex) {
		return get${prim.CapName}AtOffset(valueIndex * $primSize);
	}
	
#docGetArray(${prim.Name} ${prim.WrapperName})
	public ${prim.Name}[] get${prim.CapName}s(int length) {
		return get${prim.CapName}sAtOffset(0, length);
    }
    
  
#docGetRemainingArray(${prim.Name} ${prim.WrapperName})
    public ${prim.Name}[] get${prim.CapName}s() {
		long validBytes = getValidBytes("Cannot create array if remaining length is not known. Please use get${prim.CapName}s(int length) instead.");
		return get${prim.CapName}s((int)(validBytes / ${primSize}));
    }

#docGetArrayOffset(${prim.Name} ${prim.WrapperName} "Pointer#get${prim.CapName}s(int)")
	public ${prim.Name}[] get${prim.CapName}sAtOffset(long byteOffset, int length) {
        #if ($prim.Name == "char")
		if (Platform.WCHAR_T_SIZE == 4)
			return intsToWChars(getIntsAtOffset(byteOffset, length));
		#end
    	#declareCheckedPeerAtOffset("byteOffset" "${primSize} * length")
        #if ($prim.Name != "byte" && $prim.Name != "boolean")
    	if (!isOrdered())
        	return JNI.get_${prim.Name}_array_disordered(checkedPeer, length);
        #end
        return JNI.get_${prim.Name}_array(checkedPeer, length);
    }
    
#end
#foreach ($prim in $primitivesNoBool)
#if ($prim.Name == "char") #set ($primSize = "Platform.WCHAR_T_SIZE") #else #set ($primSize = $prim.Size) #end
//-- primitive (no bool): $prim.Name --

    #if ($prim.Name != "char")
    /**
	 * Read ${prim.Name} values into the specified destination array from the pointed memory location
	 */
	public void get${prim.CapName}s(${prim.Name}[] dest) {
    		get${prim.BufferName}().get(dest);
    }
    
    /**
	 * Read ${prim.Name} values into the specified destination buffer from the pointed memory location
	 */
	public void get${prim.CapName}s(${prim.BufferName} dest) {
    		dest.duplicate().put(get${prim.BufferName}());
    }
    
    /**
	 * Read length ${prim.Name} values into the specified destination array from the pointed memory location shifted by a byte offset, storing values after the provided destination offset.
	 */
	public void get${prim.CapName}sAtOffset(long byteOffset, ${prim.Name}[] dest, int destOffset, int length) {
    		get${prim.BufferName}AtOffset(byteOffset, length).get(dest, destOffset, length);
    }
    #end
    
	/**
	 * Write a buffer of ${prim.Name} values of the specified length to the pointed memory location
	 */
    public Pointer<T> set${prim.CapName}s(${prim.BufferName} values) {
		return set${prim.CapName}sAtOffset(0, values, 0, values.capacity());
	}

    /**
	 * Write a buffer of ${prim.Name} values of the specified length to the pointed memory location shifted by a byte offset
	 */
	public Pointer<T> set${prim.CapName}sAtOffset(long byteOffset, ${prim.BufferName} values) {
		return set${prim.CapName}sAtOffset(byteOffset, values, 0, values.capacity());
	}

    /**
	 * Write a buffer of ${prim.Name} values of the specified length to the pointed memory location shifted by a byte offset, reading values at the given buffer offset and for the given length from the provided buffer.
	 */
	public Pointer<T> set${prim.CapName}sAtOffset(long byteOffset, ${prim.BufferName} values, long valuesOffset, long length) {
        if (values == null)
			throw new IllegalArgumentException("Null values");
		#if ($prim.Name == "char")
		if (Platform.WCHAR_T_SIZE == 4) {
			for (int i = 0; i < length; i++)
				setCharAtOffset(byteOffset + i, values.get((int)(valuesOffset + i)));
			return this;
		}
		#end
    	if (values.isDirect()) {
            long len = length * ${primSize}, off = valuesOffset * ${primSize};
            long cap = JNI.getDirectBufferCapacity(values);
            // HACK (TODO?) the JNI spec says size is in bytes, but in practice on mac os x it's in elements !!!
            cap *= ${primSize};
		
            if (cap < off + len)
                throw new IndexOutOfBoundsException("The provided buffer has a capacity (" + cap + " bytes) smaller than the requested write operation (" + len + " bytes starting at byte offset " + off + ")");
            
            
            #declareCheckedPeerAtOffset("byteOffset" "${primSize} * length")
			JNI.memcpy(checkedPeer, JNI.getDirectBufferAddress(values) + off, len);
        }
        #if ($prim.Name != "char")
        else if (values.isReadOnly()) {
            get${prim.BufferName}AtOffset(byteOffset, length).put(values.duplicate());
        } 
        #end
        else {
            set${prim.CapName}sAtOffset(byteOffset, values.array(), (int)(values.arrayOffset() + valuesOffset), (int)length);
        }
        return this;
    }
    
    #if ($prim.Name != "char")
    /**
	 * Get a direct buffer of ${prim.Name} values of the specified length that points to this pointer's target memory location
	 */
	public ${prim.BufferName} get${prim.BufferName}(long length) {
		return get${prim.BufferName}AtOffset(0, length);
	}
	
	/**
	 * Get a direct buffer of ${prim.Name} values that points to this pointer's target memory locations
	 */
	public ${prim.BufferName} get${prim.BufferName}() {
		long validBytes = getValidBytes("Cannot create buffer if remaining length is not known. Please use get${prim.BufferName}(long length) instead.");
		return get${prim.BufferName}AtOffset(0, validBytes / ${primSize});
	}
	
	/**
	 * Get a direct buffer of ${prim.Name} values of the specified length that points to this pointer's target memory location shifted by a byte offset
	 */
	public ${prim.BufferName} get${prim.BufferName}AtOffset(long byteOffset, long length) {
        long blen = ${primSize} * length;
        #declareCheckedPeerAtOffset("byteOffset" "blen")
        ByteBuffer buffer = JNI.newDirectByteBuffer(checkedPeer, blen);
        buffer.order(order()); // mutates buffer order
        #if ($prim.Name == "byte")
        return buffer;
        #else
        return buffer.as${prim.BufferName}();
        #end
    }
    #end
    
#end

	/**
	 * Type of a native character string.<br>
	 * In the native world, there are several ways to represent a string.<br>
	 * See {@link Pointer#getStringAtOffset(long, StringType, Charset)} and {@link Pointer#setStringAtOffset(long, String, StringType, Charset)}
	 */
    public enum StringType {
        /**
		 * C strings (a.k.a "NULL-terminated strings") have no size limit and are the most used strings in the C world.
		 * They are stored with the bytes of the string (using either a single-byte encoding such as ASCII, ISO-8859 or windows-1252 or a C-string compatible multi-byte encoding, such as UTF-8), followed with a zero byte that indicates the end of the string.<br>
		 * Corresponding C types : {@code char* }, {@code const char* }, {@code LPCSTR }<br>
		 * Corresponding Pascal type : {@code PChar }<br>
		 * See {@link Pointer#pointerToCString(String)}, {@link Pointer#getCString()} and {@link Pointer#setCString(String)}
		 */
		C(false, true),
		/**
		 * Wide C strings are stored as C strings (see {@link StringType#C}) except they are composed of shorts instead of bytes (and are ended by one zero short value = two zero byte values). 
		 * This allows the use of two-bytes encodings, which is why this kind of strings is often found in modern Unicode-aware system APIs.<br>
		 * Corresponding C types : {@code wchar_t* }, {@code const wchar_t* }, {@code LPCWSTR }<br>
		 * See {@link Pointer#pointerToWideCString(String)}, {@link Pointer#getWideCString()} and {@link Pointer#setWideCString(String)}
		 */
        WideC(true, true),
    		/**
		 * Pascal strings can be up to 255 characters long.<br>
		 * They are stored with a first byte that indicates the length of the string, followed by the ascii or extended ascii chars of the string (no support for multibyte encoding).<br>
		 * They are often used in very old Mac OS programs and / or Pascal programs.<br>
		 * Usual corresponding C types : {@code unsigned char* } and {@code const unsigned char* }<br>
		 * Corresponding Pascal type : {@code ShortString } (see <a href="http://www.codexterity.com/delphistrings.htm">http://www.codexterity.com/delphistrings.htm</a>)<br>
		 * See {@link Pointer#pointerToString(String, StringType, Charset)}, {@link Pointer#getString(StringType)}, {@link Pointer#setString(String, StringType)}, 
		 */
        PascalShort(false, true),
		/**
		 * Wide Pascal strings are ref-counted unicode strings that look like WideC strings but are prepended with a ref count and length (both 32 bits ints).<br>
		 * They are the current default in Delphi (2010).<br>
		 * Corresponding Pascal type : {@code WideString } (see <a href="http://www.codexterity.com/delphistrings.htm">http://www.codexterity.com/delphistrings.htm</a>)<br>
		 * See {@link Pointer#pointerToString(String, StringType, Charset)}, {@link Pointer#getString(StringType)}, {@link Pointer#setString(String, StringType)}, 
		 */
        PascalWide(true, true),
        /**
		 * Pascal ANSI strings are ref-counted single-byte strings that look like C strings but are prepended with a ref count and length (both 32 bits ints).<br>
		 * Corresponding Pascal type : {@code AnsiString } (see <a href="http://www.codexterity.com/delphistrings.htm">http://www.codexterity.com/delphistrings.htm</a>)<br>
		 * See {@link Pointer#pointerToString(String, StringType, Charset)}, {@link Pointer#getString(StringType)}, {@link Pointer#setString(String, StringType)}, 
		 */
        PascalAnsi(false, true),
        /**
         * Microsoft's BSTR strings, used in COM, OLE, MS.NET Interop and MS.NET Automation functions.<br>
         * See <a href="http://msdn.microsoft.com/en-us/library/ms221069.aspx">http://msdn.microsoft.com/en-us/library/ms221069.aspx</a> for more details.<br>
         * See {@link Pointer#pointerToString(String, StringType, Charset)}, {@link Pointer#getString(StringType)}, {@link Pointer#setString(String, StringType)}, 
		 */
        BSTR(true, true),
        /**
         * STL strings have compiler- and STL library-specific implementations and memory layouts.<br>
         * BridJ support reading and writing to / from pointers to most implementation's STL strings, though.
         * See {@link Pointer#pointerToString(String, StringType, Charset)}, {@link Pointer#getString(StringType)}, {@link Pointer#setString(String, StringType)}, 
		 */
		STL(false, false),
        /**
         * STL wide strings have compiler- and STL library-specific implementations and memory layouts.<br>
         * BridJ supports reading and writing to / from pointers to most implementation's STL strings, though.
         * See {@link Pointer#pointerToString(String, StringType, Charset)}, {@link Pointer#getString(StringType)}, {@link Pointer#setString(String, StringType)}, 
		 */
		WideSTL(true, false);
        //MFCCString,
        //CComBSTR,
        //_bstr_t
        
        final boolean isWide, canCreate;
        StringType(boolean isWide, boolean canCreate) {
			this.isWide = isWide;
			this.canCreate = canCreate;
        }
        
    }
	
    private static void notAString(StringType type, String reason) {
    		throw new RuntimeException("There is no " + type + " String here ! (" + reason + ")");
    }
    
    protected void checkIntRefCount(StringType type, long byteOffset) {
    		int refCount = getIntAtOffset(byteOffset);
		if (refCount <= 0)
			notAString(type, "invalid refcount: " + refCount);
    }
    
	/**
	 * Read a native string from the pointed memory location using the default charset.<br>
	 * See {@link Pointer#getStringAtOffset(long, StringType, Charset)} for more options.
	 * @param type Type of the native String to read. See {@link StringType} for details on the supported types.
	 * @return string read from native memory
	 */
	public String getString(StringType type) {
		return getStringAtOffset(0, type, null);
	}
	
	/**
	 * Read a native string from the pointed memory location, using the provided charset or the system's default if not provided.
	 * See {@link Pointer#getStringAtOffset(long, StringType, Charset)} for more options.
	 * @param type Type of the native String to read. See {@link StringType} for details on the supported types.
	 * @param charset Character set used to convert bytes to String characters. If null, {@link Charset#defaultCharset()} will be used
	 * @return string read from native memory
	 */
	public String getString(StringType type, Charset charset) {
		return getStringAtOffset(0, type, charset);
	}
	 
	
	String getSTLStringAtOffset(long byteOffset, StringType type, Charset charset) {
		// Assume the following layout :
		// - fixed buff of 16 chars
		// - ptr to dynamic array if the string is bigger
		// - size of the string (size_t)
		// - max allowed size of the string without the need for reallocation
		boolean wide = type == StringType.WideSTL;
		
		int fixedBuffLength = 16;
		int fixedBuffSize = wide ? fixedBuffLength * Platform.WCHAR_T_SIZE : fixedBuffLength;
		long length = getSizeTAtOffset(byteOffset + fixedBuffSize + Pointer.SIZE);
		long pOff;
		Pointer<?> p;
		if (length < fixedBuffLength - 1) {
			pOff = byteOffset;
			p = this;
		} else {
			pOff = 0;
			p = getPointerAtOffset(byteOffset + fixedBuffSize + Pointer.SIZE);
		}
		int endChar = wide ? p.getCharAtOffset(pOff + length * Platform.WCHAR_T_SIZE) : p.getByteAtOffset(pOff + length);
		if (endChar != 0)
			notAString(type, "STL string format is not recognized : did not find a NULL char at the expected end of string of expected length " + length);
		return p.getStringAtOffset(pOff, wide ? StringType.WideC : StringType.C, charset);
	}
	
	static <U> Pointer<U> setSTLString(Pointer<U> pointer, long byteOffset, String s, StringType type, Charset charset) {
		boolean wide = type == StringType.WideSTL;
		
		int fixedBuffLength = 16;
		int fixedBuffSize = wide ? fixedBuffLength * Platform.WCHAR_T_SIZE : fixedBuffLength;
		long lengthOffset = byteOffset + fixedBuffSize + Pointer.SIZE;
		long capacityOffset = lengthOffset + Pointer.SIZE;
		
		long length = s.length();
		if (pointer == null)// { && length > fixedBuffLength - 1)
			throw new UnsupportedOperationException("Cannot create STL strings (yet)");
		
		long currentLength = pointer.getSizeTAtOffset(lengthOffset);
		long currentCapacity = pointer.getSizeTAtOffset(capacityOffset);
		
		if (currentLength < 0 || currentCapacity < 0 || currentLength > currentCapacity)
			notAString(type, "STL string format not recognized : currentLength = " + currentLength + ", currentCapacity = " + currentCapacity);
		
		if (length > currentCapacity)
			throw new RuntimeException("The target STL string is not large enough to write a string of length " + length + " (current capacity = " + currentCapacity + ")");
		
		pointer.setSizeTAtOffset(lengthOffset, length);
		
		long pOff;
		Pointer<?> p;
		if (length < fixedBuffLength - 1) {
			pOff = byteOffset;
			p = pointer;
		} else {
			pOff = 0;
			p = pointer.getPointerAtOffset(byteOffset + fixedBuffSize + SizeT.SIZE);
		}
		
		int endChar = wide ? p.getCharAtOffset(pOff + currentLength * Platform.WCHAR_T_SIZE) : p.getByteAtOffset(pOff + currentLength);
		if (endChar != 0)
			notAString(type, "STL string format is not recognized : did not find a NULL char at the expected end of string of expected length " + currentLength);
		
		p.setStringAtOffset(pOff, s, wide ? StringType.WideC : StringType.C, charset);
		return pointer;
	}
    
	
	/**
	 * Read a native string from the pointed memory location shifted by a byte offset, using the provided charset or the system's default if not provided.
	 * @param byteOffset
	 * @param charset Character set used to convert bytes to String characters. If null, {@link Charset#defaultCharset()} will be used
	 * @param type Type of the native String to read. See {@link StringType} for details on the supported types.
	 * @return string read from native memory
	 */
	public String getStringAtOffset(long byteOffset, StringType type, Charset charset) {
        try {
			long len;
			
			switch (type) {
			case PascalShort:
				len = getByteAtOffset(byteOffset) & 0xff;
				return new String(getBytesAtOffset(byteOffset + 1, safeIntCast(len)), charset(charset));
			case PascalWide:
				checkIntRefCount(type, byteOffset - 8);
			case BSTR:
				len = getIntAtOffset(byteOffset - 4);
				if (len < 0 || ((len & 1) == 1))
					notAString(type, "invalid byte length: " + len);
				//len = wcslen(byteOffset);
				if (getCharAtOffset(byteOffset + len) != 0)
					notAString(type, "no null short after the " + len + " declared bytes");
				return new String(getCharsAtOffset(byteOffset, safeIntCast(len / Platform.WCHAR_T_SIZE)));
			case PascalAnsi:
				checkIntRefCount(type, byteOffset - 8);
				len = getIntAtOffset(byteOffset - 4);
				if (len < 0)
					notAString(type, "invalid byte length: " + len);
				if (getByteAtOffset(byteOffset + len) != 0)
					notAString(type, "no null short after the " + len + " declared bytes");
				return new String(getBytesAtOffset(byteOffset, safeIntCast(len)), charset(charset));
			case C:
				len = strlen(byteOffset);
				return new String(getBytesAtOffset(byteOffset, safeIntCast(len)), charset(charset));
			case WideC:
				len = wcslen(byteOffset);
				return new String(getCharsAtOffset(byteOffset, safeIntCast(len)));
			case STL:
			case WideSTL:
				return getSTLStringAtOffset(byteOffset, type, charset);
			default:
				throw new RuntimeException("Unhandled string type : " + type);
			}
		} catch (UnsupportedEncodingException ex) {
            throwUnexpected(ex);
            return null;
        }
	}

	/**
	 * Write a native string to the pointed memory location using the default charset.<br>
	 * See {@link Pointer#setStringAtOffset(long, String, StringType, Charset)} for more options.
	 * @param s string to write
	 * @param type Type of the native String to write. See {@link StringType} for details on the supported types.
	 * @return this
	 */
	public Pointer<T> setString(String s, StringType type) {
		return setString(this, 0, s, type, null);
	}
	
	
    /**
	 * Write a native string to the pointed memory location shifted by a byte offset, using the provided charset or the system's default if not provided.
	 * @param byteOffset
	 * @param s string to write
	 * @param charset Character set used to convert String characters to bytes. If null, {@link Charset#defaultCharset()} will be used
	 * @param type Type of the native String to write. See {@link StringType} for details on the supported types.
	 * @return this
	 */
	public Pointer<T> setStringAtOffset(long byteOffset, String s, StringType type, Charset charset) {
		return setString(this, byteOffset, s, type, charset);
	}
	
	private static String charset(Charset charset) {
		return (charset == null ? Charset.defaultCharset() : charset).name();
	}
			
	static <U> Pointer<U> setString(Pointer<U> pointer, long byteOffset, String s, StringType type, Charset charset) {
        try {
			if (s == null)
				return null;
			
			byte[] bytes;
			char[] chars;
			int bytesCount, headerBytes;
			int headerShift;
			
			switch (type) {
			case PascalShort:
				bytes = s.getBytes(charset(charset));
				bytesCount = bytes.length;
				if (pointer == null)
					pointer = (Pointer<U>)allocateBytes(bytesCount + 1);
				if (bytesCount > 255)
					throw new IllegalArgumentException("Pascal strings cannot be more than 255 chars long (tried to write string of byte length " + bytesCount + ")");
				pointer.setByteAtOffset(byteOffset, (byte)bytesCount);
				pointer.setBytesAtOffset(byteOffset + 1, bytes, 0, bytesCount);
				break;
			case C:
				bytes = s.getBytes(charset(charset));
				bytesCount = bytes.length;
				if (pointer == null)
					pointer = (Pointer<U>)allocateBytes(bytesCount + 1);
				pointer.setBytesAtOffset(byteOffset, bytes, 0, bytesCount);
				pointer.setByteAtOffset(byteOffset + bytesCount, (byte)0);
				break;
			case WideC:
				chars = s.toCharArray();
				bytesCount = chars.length * Platform.WCHAR_T_SIZE;
				if (pointer == null)
					pointer = (Pointer<U>)allocateChars(bytesCount + 2);
				pointer.setCharsAtOffset(byteOffset, chars);
				pointer.setCharAtOffset(byteOffset + bytesCount, (char)0);
				break;
			case PascalWide:
				headerBytes = 8;
				chars = s.toCharArray();
				bytesCount = chars.length * Platform.WCHAR_T_SIZE;
				if (pointer == null) {
					pointer = (Pointer<U>)allocateChars(headerBytes + bytesCount + 2);
					byteOffset = headerShift = headerBytes;
				} else
					headerShift = 0;
				pointer.setIntAtOffset(byteOffset - 8, 1); // refcount
				pointer.setIntAtOffset(byteOffset - 4, bytesCount); // length header
				pointer.setCharsAtOffset(byteOffset, chars);
				pointer.setCharAtOffset(byteOffset + bytesCount, (char)0);
				// Return a pointer to the WideC string-compatible part of the Pascal WideString
				return (Pointer<U>)pointer.offset(headerShift);
			case PascalAnsi:
				headerBytes = 8;
				bytes = s.getBytes(charset(charset));
				bytesCount = bytes.length;
				if (pointer == null) {
					pointer = (Pointer<U>)allocateBytes(headerBytes + bytesCount + 1);
					byteOffset = headerShift = headerBytes;
				} else
					headerShift = 0;
				pointer.setIntAtOffset(byteOffset - 8, 1); // refcount
				pointer.setIntAtOffset(byteOffset - 4, bytesCount); // length header
				pointer.setBytesAtOffset(byteOffset, bytes);
				pointer.setByteAtOffset(byteOffset + bytesCount, (byte)0);
				// Return a pointer to the WideC string-compatible part of the Pascal WideString
				return (Pointer<U>)pointer.offset(headerShift);
			case BSTR:
				headerBytes = 4;
				chars = s.toCharArray();
				bytesCount = chars.length * Platform.WCHAR_T_SIZE;
				if (pointer == null) {
					pointer = (Pointer<U>)allocateChars(headerBytes + bytesCount + 2);
					byteOffset = headerShift = headerBytes;
				} else
					headerShift = 0;
				pointer.setIntAtOffset(byteOffset - 4, bytesCount); // length header IN BYTES
				pointer.setCharsAtOffset(byteOffset, chars);
				pointer.setCharAtOffset(byteOffset + bytesCount, (char)0);
				// Return a pointer to the WideC string-compatible part of the Pascal WideString
				return (Pointer<U>)pointer.offset(headerShift);
			case STL:
			case WideSTL:
				return setSTLString(pointer, byteOffset, s, type, charset);
			default:
				throw new RuntimeException("Unhandled string type : " + type);
			}
	
			return (Pointer<U>)pointer;
		} catch (UnsupportedEncodingException ex) {
            throwUnexpected(ex);
            return null;
        }
    }
	
    /**
     * Allocate memory and write a string to it, using the system's default charset to convert the string (See {@link StringType} for details on the supported types).<br>
	 * See {@link Pointer#setString(String, StringType)}, {@link Pointer#getString(StringType)}.
	 * @param charset Character set used to convert String characters to bytes. If null, {@link Charset#defaultCharset()} will be used
	 * @param type Type of the native String to create.
	 */
	public static Pointer<?> pointerToString(String string, StringType type, Charset charset) {
		return setString(null, 0, string, type, charset);
	}
	
#macro (defPointerToString $string $eltWrapper)
    /**
     * Allocate memory and write a ${string} string to it, using the system's default charset to convert the string.  (see {@link StringType#${string}}).<br>
	 * See {@link Pointer#set${string}String(String)}, {@link Pointer#get${string}String()}.<br>
	 * See {@link Pointer#pointerToString(String, StringType, Charset)} for choice of the String type or Charset.
	 */
	 public static Pointer<$eltWrapper> pointerTo${string}String(String string) {
		return setString(null, 0, string, StringType.${string}, null);
	}
	
	/**
	 * Allocate an array of pointers to strings.
	 */
    public static Pointer<Pointer<$eltWrapper>> pointerTo${string}Strings(final String... strings) {
    	if (strings == null)
    		return null;
        final int len = strings.length;
        final Pointer<$eltWrapper>[] pointers = (Pointer<$eltWrapper>[])new Pointer[len];
        Pointer<Pointer<$eltWrapper>> mem = allocateArray((PointerIO<Pointer<$eltWrapper>>)(PointerIO)PointerIO.getPointerInstance(${eltWrapper}.class), len, new Releaser() {
        	//@Override
        	public void release(Pointer<?> p) {
        		Pointer<Pointer<$eltWrapper>> mem = (Pointer<Pointer<$eltWrapper>>)p;
        		for (int i = 0; i < len; i++) {
        			Pointer<$eltWrapper> pp = pointers[i];
        			if (pp != null)
        				pp.release();
        		}
        }});
        for (int i = 0; i < len; i++)
            mem.set(i, pointers[i] = pointerTo${string}String(strings[i]));

		return mem;
    }
    
#end

#defPointerToString("C" "Byte")
#defPointerToString("WideC" "Character")

	
#foreach ($string in ["C", "WideC"])
//-- StringType: $string --

	/**
	 * Read a ${string} string using the default charset from the pointed memory location (see {@link StringType#${string}}).<br>
	 * See {@link Pointer#get${string}StringAtOffset(long)}, {@link Pointer#getString(StringType)} and {@link Pointer#getStringAtOffset(long, StringType, Charset)} for more options
	 */
	public String get${string}String() {
		return get${string}StringAtOffset(0);
	}
	
	/**
	 * Read a ${string} string using the default charset from the pointed memory location shifted by a byte offset (see {@link StringType#${string}}).<br>
	 * See {@link Pointer#getStringAtOffset(long, StringType, Charset)} for more options
	 */
	public String get${string}StringAtOffset(long byteOffset) {
		return getStringAtOffset(byteOffset, StringType.${string}, null);
	}
	
	/**
	 * Write a ${string} string using the default charset to the pointed memory location (see {@link StringType#${string}}).<br>
	 * See {@link Pointer#set${string}StringAtOffset(long, String)} and {@link Pointer#setStringAtOffset(long, String, StringType, Charset)} for more options
	 */
	public Pointer<T> set${string}String(String s) {
        return set${string}StringAtOffset(0, s);
    }
    /**
	 * Write a ${string} string using the default charset to the pointed memory location shifted by a byte offset (see {@link StringType#${string}}).<br>
	 * See {@link Pointer#setStringAtOffset(long, String, StringType, Charset)} for more options
	 */
	public Pointer<T> set${string}StringAtOffset(long byteOffset, String s) {
        return setStringAtOffset(byteOffset, s, StringType.${string}, null);
    }
	
#end

	/**
	 * Get the length of the C string at the pointed memory location shifted by a byte offset (see {@link StringType#C}).
	 */
	protected long strlen(long byteOffset) {
		#declareCheckedPeerAtOffset("byteOffset" "1")
		return JNI.strlen(checkedPeer);
	}
	
	/**
	 * Get the length of the wide C string at the pointed memory location shifted by a byte offset (see {@link StringType#WideC}).
	 */
	protected long wcslen(long byteOffset) {
		#declareCheckedPeerAtOffset("byteOffset" "Platform.WCHAR_T_SIZE")
		return JNI.wcslen(checkedPeer);
	}
	
	/**
	 * Write zero bytes to all of the valid bytes pointed by this pointer
	 */
	public void clearValidBytes() {
		long bytes = getValidBytes();
    		if (bytes < 0)
    			throw new UnsupportedOperationException("Number of valid bytes is unknown. Please use clearBytes(long) or validBytes(long).");
		clearBytes(bytes);	
	}
	
	/**
	 * Write zero bytes to the first length bytes pointed by this pointer
	 */
	public void clearBytes(long length) {
		clearBytesAtOffset(0, length, (byte)0);	
	}
	/**
	 * Write a byte {@code value} to each of the {@code length} bytes at the address pointed to by this pointer shifted by a {@code byteOffset}
	 */
	public void clearBytesAtOffset(long byteOffset, long length, byte value) {
		#declareCheckedPeerAtOffset("byteOffset" "length")
		JNI.memset(checkedPeer, value, length);
	}
	
	/**
	 * Find the first occurrence of a value in the memory block of length searchLength bytes pointed by this pointer shifted by a byteOffset 
	 */
	public Pointer<T> findByte(long byteOffset, byte value, long searchLength) {
		#declareCheckedPeerAtOffset("byteOffset" "searchLength")
		long found = JNI.memchr(checkedPeer, value, searchLength);	
		return found == 0 ? null : offset(found - checkedPeer);
	}
	
	/**
	 * Alias for {@link Pointer#get(long)} defined for more natural use from the Scala language.
	 */
    public final T apply(long index) {
		return get(index);
	}
	
    /**
	 * Alias for {@link Pointer#set(long, Object)} defined for more natural use from the Scala language.
	 */
	public final void update(long index, T element) {
		set(index, element);
	}
	
    /**
	 * Create an array with all the values in the bounded memory area.<br>
	 * Note that if you wish to get an array of primitives (if T is boolean, char or a numeric type), then you need to call {@link Pointer#getArray()}.
	 * @throws IndexOutOfBoundsException if this pointer's bounds are unknown
	 */
	public T[] toArray() {
		getIO("Cannot create array");
        return toArray((int)getValidElements("Length of pointed memory is unknown, cannot create array out of this pointer"));
	}
	
	T[] toArray(int length) {
        Class<?> c = Utils.getClass(getIO("Cannot create array").getTargetType());
		if (c == null)
			throw new RuntimeException("Unable to get the target type's class (target type = " + io.getTargetType() + ")");
        return (T[])toArray((Object[])Array.newInstance(c, length));
	}
	
    /**
	 * Create an array with all the values in the bounded memory area, reusing the provided array if its type is compatible and its size is big enough.<br>
	 * Note that if you wish to get an array of primitives (if T is boolean, char or a numeric type), then you need to call {@link Pointer#getArray()}.
	 * @throws IndexOutOfBoundsException if this pointer's bounds are unknown
	 */
	public <U> U[] toArray(U[] array) {
		int n = (int)getValidElements();
		if (n < 0)
            throwBecauseUntyped("Cannot create array");
        
        if (array.length != n)
        	return (U[])toArray();
        
        for (int i = 0; i < n; i++)
        	array[i] = (U)get(i);
        return array;
	}
	
	/**
	* Types of pointer-based list implementations that can be created through {@link Pointer#asList()} or {@link Pointer#asList(ListType)}.
	 */
	public enum ListType {
		/**
		 * Read-only list
		 */
        Unmodifiable,
        /**
		 * List is modifiable and can shrink, but capacity cannot be increased (some operations will hence throw UnsupportedOperationException when the capacity is unsufficient for the requested operation)
		 */
        FixedCapacity,
        /**
		 * List is modifiable and its underlying memory will be reallocated if it needs to grow beyond its current capacity.
		 */
        Dynamic
    }
    
	/**
	 * Create a {@link ListType#FixedCapacity} native list that uses this pointer as storage (and has this pointer's pointed valid elements as initial content).<br> 
	 * Same as {@link Pointer#asList(ListType)}({@link ListType#FixedCapacity}).
	 */
	public NativeList<T> asList() {
		return asList(ListType.FixedCapacity);
	}
	/**
	 * Create a native list that uses this pointer as <b>initial</b> storage (and has this pointer's pointed valid elements as initial content).<br>
	 * If the list is {@link ListType#Dynamic} and if its capacity is grown at some point, this pointer will probably no longer point to the native memory storage of the list, so you need to get back the pointer with {@link NativeList#getPointer()} when you're done mutating the list.
	 */
	public NativeList<T> asList(ListType type) {
		return new DefaultNativeList(this, type);
	}
	/**
     * Create a {@link ListType#Dynamic} list with the provided initial capacity (see {@link ListType#Dynamic}).
     * @param io Type of the elements of the list
     * @param capacity Initial capacity of the list
     */
    public static <E> NativeList<E> allocateList(PointerIO<E> io, long capacity) {
        NativeList<E> list = new DefaultNativeList(allocateArray(io, capacity), ListType.Dynamic);
        list.clear();
        return list;
    }
	/**
     * Create a {@link ListType#Dynamic} list with the provided initial capacity (see {@link ListType#Dynamic}).
     * @param type Type of the elements of the list
     * @param capacity Initial capacity of the list
     */
    public static <E> NativeList<E> allocateList(Class<E> type, long capacity) {
        return allocateList((Type)type, capacity);
    }
	/**
     * Create a {@link ListType#Dynamic} list with the provided initial capacity (see {@link ListType#Dynamic}).
     * @param type Type of the elements of the list
     * @param capacity Initial capacity of the list
     */
    public static <E> NativeList<E> allocateList(Type type, long capacity) {
        return (NativeList)allocateList(PointerIO.getInstance(type), capacity);
    }
    
    private static char[] intsToWChars(int[] in) {
    	int n = in.length;
    	char[] out = new char[n];
    	for (int i = 0; i < n; i++)
    		out[i] = (char)in[i];
    	return out;
    }
    private static int[] wcharsToInts(char[] in, int valuesOffset, int length) {
    	int[] out = new int[length];
    	for (int i = 0; i < length; i++)
    		out[i] = in[valuesOffset + i];
    	return out;
    }
}
