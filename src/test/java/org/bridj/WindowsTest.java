package org.bridj;
import org.junit.Test;
import org.junit.BeforeClass;

import org.bridj.ann.Library;
import org.bridj.ann.Optional;

@org.bridj.ann.Runtime(CRuntime.class)
public class WindowsTest {
	@Library("user32")
    @Optional
	public static native void SendMessage(Pointer<?> hwnd, int Msg, int wParam, Pointer<?> lParam);
	
	static final boolean win = Platform.isWindows();
	
	@Test
	public void testUnicodeWorked() {
		if (!win) return;
		
		SendMessage(null, 0, 0, null);
	}
	
	@BeforeClass
	public static void register() {
		if (!win) return;
		
		BridJ.register();
	}
}
