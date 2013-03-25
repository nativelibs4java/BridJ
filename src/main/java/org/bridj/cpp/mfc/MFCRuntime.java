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
package org.bridj.cpp.mfc;

import org.bridj.BridJ;
import org.bridj.Callback;
import org.bridj.NativeObject;
import org.bridj.Pointer;
import org.bridj.util.Utils;
import org.bridj.cpp.CPPRuntime;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Olivier
 */
public class MFCRuntime extends CPPRuntime {
    Method mfcGetMessageMap;
	String mfcGetMessageMapMangling;
	Callback mfcGetMessageMapCallback;

	Set<Class<?>> hasMessageMap = new HashSet<Class<?>>();

	@Override
	public <T extends NativeObject> Class<? extends T> getActualInstanceClass(Pointer<T> pInstance, Type officialType) {
        Class officialTypeClass = Utils.getClass(officialType);
		// For MFC classes, use GetRuntimeClass()
		if (CObject.class.isAssignableFrom(officialTypeClass)) {
			Pointer<CRuntimeClass> pClass = new CObject((Pointer)pInstance, this).GetRuntimeClass();
			if (pClass != null) {
				CRuntimeClass rtc = pClass.get();
				try {
					Class<? extends T> type = (Class)getMFCClass(rtc.m_lpszClassName());
					if (officialTypeClass == null || officialTypeClass.isAssignableFrom(type))
						return type;
				} catch (ClassNotFoundException ex) {}
				return officialTypeClass;
			}
		}
		
		// TODO Auto-generated method stub
		return super.getActualInstanceClass(pInstance, officialType);
	}
	
	private Class<?> getMFCClass(Pointer<Byte> mLpszClassName) throws ClassNotFoundException {
		throw new ClassNotFoundException(mLpszClassName.getCString());
	}

	public void getExtraFieldsOfNewClass(Class<?> type, Map<String, Type> out) {
		//super.getExtraFieldsOfNewClass(type, out);
		if (!hasMessageMap.contains(type))
			return;

		out.put("messageMap", Pointer.class);
	}
	
	public void getOverriddenVirtualMethods(Map<String, Pointer<?>> out) {
		//super.getVirtualMethodBindings(out);
		out.put("mfcGetMessageMap", Pointer.pointerTo(mfcGetMessageMapCallback));
	}

	@Override
	public void register(Type type) {
		super.register(type);
        Class typeClass = Utils.getClass(type);

		MessageMapBuilder map = new MessageMapBuilder();
		for (Method method : typeClass.getMethods()) {

			OnCommand onCommand = method.getAnnotation(OnCommand.class);
			if (onCommand != null)
				map.add(method, onCommand);

			OnCommandEx onCommandEx = method.getAnnotation(OnCommandEx.class);
			if (onCommandEx != null)
				map.add(method, onCommandEx);

			OnUpdateCommand onUpdateCommand = method.getAnnotation(OnUpdateCommand.class);
			if (onUpdateCommand != null)
				map.add(method, onUpdateCommand);

			OnRegisteredMessage onRegisteredMessage = method.getAnnotation(OnRegisteredMessage.class);
			if (onRegisteredMessage != null)
				map.add(method, onRegisteredMessage);

			OnMessage onMessage = method.getAnnotation(OnMessage.class);
			if (onMessage != null)
				map.add(method, onMessage);
		}
		if (!map.isEmpty())
			map.register(this, typeClass);

	}
}
