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
package org.bridj.cpp.std;

import org.bridj.ann.Template;
import org.bridj.cpp.CPPObject;


import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Field;
import org.bridj.ann.Struct;
import org.bridj.cpp.CPPRuntime;

import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import org.bridj.BridJRuntime;
import org.bridj.JNI;

import static org.bridj.Pointer.*;
import org.bridj.ann.Array;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;

/**
 * Binding for <a href="http://www.sgi.com/tech/stl/Vector.html">STL's std::vector</a> class.
 * @author ochafik
 * @param <T>
 */
@Template({ Type.class })
@Struct(customizer = STL.class)
public class list<T> extends CPPObject {
    @Library("c") protected static native @Ptr long malloc(@Ptr long size);
    @Library("c") protected static native void free(@Ptr long address);
    
    @Template({ Type.class })
	public static class list_node<T> extends CPPObject {
		@Deprecated
		@Field(0)
		public Pointer<list_node<T>> next() {
			return io.getPointerField(this, 0);
		}
	
        @Deprecated
		@Field(0)
        public void next(Pointer<list_node<T>> value) {
            io.setPointerField(this, 0, value);
        }
		@Deprecated
		@Field(1)
		public Pointer<list_node<T>> prev() {
			return io.getPointerField(this, 1);
		}
        @Field(1)
        public void prev(Pointer<list_node<T>> value) {
            io.setPointerField(this, 1, value);
        }
		@Deprecated
		@Field(2)
		@Array(1)
		public Pointer<T> data() {
			return io.getPointerField(this, 2);
		}
		public list_node(Type t) {
			super((Void)null, CPPRuntime.SKIP_CONSTRUCTOR, t);
		}
		public list_node(Pointer<? extends list_node> peer, Type t) {
			super(peer, t);
			if (!isValid())
				throw new RuntimeException("Invalid list internal data ! Are you trying to use an unsupported version of the STL ?");
		}
		protected boolean isValid() {
			long next = getPeer(next());
			long prev = getPeer(prev());
			if (next == 0 && prev == 0)
				return false;
			return true; // TODO other checks?
		}
		public T get() {
			return data().get();
		}
		public void set(T value) {
			data().set(value);
		}
	}
	protected volatile Type _T;
	protected Type T() {
		if (_T == null) {
			_T = (Type)CPPRuntime.getInstance().getTemplateParameters(this, list.class)[0];
		}
		return _T;
	}
	protected list_node<T> createNode() {
		Type T = T();
		long size = BridJ.sizeOf(T);
        return new list_node<T>((Pointer)pointerToAddress(malloc(size)), T); 
	}
	protected void deleteNode(list_node<T> node) {
		free(Pointer.getAddress(node, list_node.class));
	}
	
	@Deprecated
    @Field(0)
	public Pointer<list_node<T>> next() {
		return io.getPointerField(this, 0);
	}
	
	@Deprecated
    @Field(0)
	public void next(Pointer<list_node<T>> value) {
		io.setPointerField(this, 0, value);
	}
    
	@Deprecated
    @Field(1)
	public Pointer<list_node<T>> prev() {
		return io.getPointerField(this, 1);
	}
	
	@Deprecated
    @Field(1)
	public void prev(Pointer<list_node<T>> value) {
		io.setPointerField(this, 1, value);
	}
	
	//@Constructor(-1)
	public list(Type t) {
		super((Void)null, CPPRuntime.SKIP_CONSTRUCTOR, t);
	}
	public list(Pointer<? extends list<T>> peer, Type t) {
		super(peer, t);
	}
	private void checkNotEmpty() {
		if (isRoot(next()) && isRoot(prev())) 
			throw new NoSuchElementException();
	}
	public boolean empty() {
		return next() != null;
	}
	public T front() {
		checkNotEmpty();
		return next().get().get();
	}
	public T back() {
		checkNotEmpty();
		return prev().get().get();
	}
	
    private boolean same(Pointer a, Pointer b) {
        return getPeer(a) == getPeer(b);
    }
    private boolean isRoot(Pointer a) {
        return same(a, pointerTo(this));
    }
	protected void hook(Pointer<list_node<T>> prev, Pointer<list_node<T>> next, T value) {
		list_node<T> tmp = createNode();
		Pointer<list_node<T>> pTmp = pointerTo(tmp);
		tmp.set(value);
        tmp.next(next);
        tmp.prev(prev);
        if (!isRoot(next))
            next.get().prev(pTmp);
        if (!isRoot(prev))
            prev.get().next(pTmp);
	}
	public void push_back(T value) {
		hook(prev(), null, value);
	}
	public void push_front(T value) {
		hook(null, next(), value);
	}
}
