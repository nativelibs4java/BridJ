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

import org.bridj.BridJ;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import org.bridj.objc.NSObject;
import org.bridj.objc.ObjCBlock;

/**
 *
 * @author ochafik
 */
public class Run {
    public static class Foundation {
        public static class NSEvent extends NSObject {
           //@Selector("addLocalMonitorForEventsMatchingMask:handler:")
           public static native Pointer addGlobalMonitorForEventsMatchingMask_handler(long mask, Pointer<NSEventGlobalCallback> handler);
        }

        public abstract static class NSEventGlobalCallback extends ObjCBlock {
            public abstract void callback(Pointer<NSEvent> event);
        }
    }

    public static void main(String[] args) throws Exception {
        BridJ.register(Foundation.NSEvent.class);

        final boolean called[] = new boolean[1];
        Foundation.NSEventGlobalCallback handler = new Foundation.NSEventGlobalCallback() {
            @Override
            public void callback(Pointer<Foundation.NSEvent> event) {
                System.out.println("Event: " + event);
                called[0] = true;
            }
        };

        //System.out.println("handler: " + handler);

        Pointer hook = Foundation.NSEvent.addGlobalMonitorForEventsMatchingMask_handler(-1L/*1 << 1*/, getPointer(handler));

        //System.out.println("hook: " + hook);

        Thread.sleep(10000);
        
        System.out.println("Called : " + called[0]);
        System.in.read();
    }
}
