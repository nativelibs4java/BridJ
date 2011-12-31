/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
