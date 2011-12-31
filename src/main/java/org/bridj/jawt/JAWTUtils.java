package org.bridj.jawt;
import static org.bridj.jawt.JawtLibrary.*;
import org.bridj.BridJ;
import org.bridj.JNI;
import org.bridj.NativeLibrary;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import java.awt.*;
import java.io.File;
import org.bridj.Platform;

import org.bridj.ann.Convention;
/**
 * Contains a method that returns the native peer handle of an AWT component : BridJ JAWT utilities {@link org.bridj.jawt.JAWTUtils#getNativePeerHandle(java.awt.Component)}
 */
public class JAWTUtils {
    
	public static JNIEnv getJNIEnv() {
		return new JNIEnv(JNI.getEnv());
	}
		
	public static JAWT getJAWT(JNIEnv env) {
		if (GraphicsEnvironment.isHeadless())
			throw new HeadlessException("No native peers in headless mode.");
	
		JAWT awt = new JAWT().version(JAWT_VERSION_1_4);
		Pointer<JAWT> pAwt = pointerTo(awt);
		if (!JAWT_GetAWT(env, pAwt))
			throw new RuntimeException("Failed to get JAWT pointer !");
			
		return pAwt.get();
	}
	
	public interface LockedComponentRunnable {
		void run(Component component, long peer);
	}
	
	public static void withLockedSurface(JNIEnv env, JAWT awt, Component component, LockedComponentRunnable runnable) {
		if (component.isLightweight())
			throw new IllegalArgumentException("Lightweight components do not have native peers.");
	
		if (!component.isDisplayable()) 
			throw new IllegalArgumentException("Component that are not displayable do not have native peers.");
		
		Pointer<?> componentPointer = JNI.getGlobalPointer(component);
		
		Pointer<JAWT_DrawingSurface> pSurface = awt.GetDrawingSurface().get().invoke(env, componentPointer).as(JAWT_DrawingSurface.class);
		if (pSurface == null)
			throw new RuntimeException("Cannot get drawing surface from " + component);
		
		JAWT_DrawingSurface surface = pSurface.get();

		try {
			int lock = surface.Lock().get().invoke(pSurface);
			if ((lock & JAWT_LOCK_ERROR) != 0)
				throw new RuntimeException("Cannot lock drawing surface of " + component);
			try {
				Pointer<JAWT_DrawingSurface.GetDrawingSurfaceInfo_callback> cb = surface.GetDrawingSurfaceInfo().as(JAWT_DrawingSurface.GetDrawingSurfaceInfo_callback.class);
				Pointer<org.bridj.jawt.JAWT_DrawingSurfaceInfo > pInfo = cb.get().invoke(pSurface);
				if (pInfo != null)
					pInfo = pInfo.as(JAWT_DrawingSurfaceInfo.class);
				Pointer<?> platformInfo = pInfo.get().platformInfo();
				long peer = platformInfo.getSizeT(); // on win, mac, x11 platforms, the relevant field is the first in the struct !
				runnable.run(component, peer); 
			} finally {
				surface.Unlock().get().invoke(pSurface);
			}
		} finally {
			awt.FreeDrawingSurface().get().invoke(pSurface);
		}
	}
	/**
	 * 
	 */
	public static long getNativePeerHandle(Component component) {
		try {
			JNIEnv env = getJNIEnv();
			JAWT awt = getJAWT(env);
			final long ret[] = new long[1];
			withLockedSurface(env, awt, component, new LockedComponentRunnable() { 
				public void run(Component component, long peer) {
					ret[0] = peer;	
				}
			});
			return ret[0];
		} catch (Throwable ex) {
			ex.printStackTrace();
			return 0;
		}
	}
    
}