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



import org.junit.Test;
import java.awt.*;
import org.bridj.jawt.*;

import static org.junit.Assert.*;

public class JAWTTest {
	
	@Test
	public void testWindowPeer() throws Exception {
    if (Platform.isMacOSX() ||
        System.getProperty("java.version").matches("1\\.6\\..*")) {
      // Oracle Java and jawt: it's complicated.
      // See http://forum.lwjgl.org/index.php?topic=4326.0
      // OpenJDK 6 + Linux seem problematic too.
      return;
    }
		assertEquals(6 * Pointer.SIZE, BridJ.sizeOf(JAWT_DrawingSurface.class));
		assertEquals(4 * 4, BridJ.sizeOf(JAWT_Rectangle.class));
		//assertEquals(4 + 5 * Pointer.SIZE, BridJ.sizeOf(JAWT.class));
		//assertEquals(2 * 4 * 4 + 4 + Pointer.SIZE, BridJ.sizeOf(JAWT_DrawingSurfaceInfo.class));
		 
		
		Frame f = new Frame();
		f.pack();
		
		f.setVisible(true);
    Thread.sleep(500);
		long p = JAWTUtils.getNativePeerHandle(f);
		assertTrue(p != 0);
		f.setVisible(false);
	}
}
