package org.bridj;



import org.junit.Test;
import java.awt.*;
import org.bridj.jawt.*;

import static org.junit.Assert.*;

public class JAWTTest {
	
	@Test
	public void testWindowPeer() throws Exception {
		assertEquals(6 * Pointer.SIZE, BridJ.sizeOf(JAWT_DrawingSurface.class));
		assertEquals(4 * 4, BridJ.sizeOf(JAWT_Rectangle.class));
		//assertEquals(4 + 5 * Pointer.SIZE, BridJ.sizeOf(JAWT.class));
		//assertEquals(2 * 4 * 4 + 4 + Pointer.SIZE, BridJ.sizeOf(JAWT_DrawingSurfaceInfo.class));
		 
		
		Frame f = new Frame();
		f.pack();
		
		f.setVisible(true);
		long p = JAWTUtils.getNativePeerHandle(f);
		assertTrue(p != 0);
		f.setVisible(false);
	}
}
