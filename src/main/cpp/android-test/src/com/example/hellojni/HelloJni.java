/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import android.app.Activity;

import android.widget.TextView;
import android.os.Bundle;
import java.io.StringWriter;
import java.io.PrintWriter;
import org.bridj.*;
import static org.bridj.Pointer.*;

public class HelloJni extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        TextView  tv = new TextView(this);
        StringWriter text = new StringWriter();
        PrintWriter pout = new PrintWriter(text);
        pout.println(stringFromJNI());
        
        AndroidSupport.setApplication(getApplication());
        
        //pout.println(HelloJni.class.getClassLoader().getResource("lib/armeabi/libbridj.so"));
        try {
        	
        		//BridJ.register(BridJLib.class);
        		
        		int a = 10, b = 100;
        		pout.println(a + " + " + b + " = " + BridJLib.addTwoInts(a, b) + " (computed in BridJ-bound native function !)");
        		pout.println("Access(.) = " + BridJLib.access(pointerToCString("."), 0));
        		/*
        		for (Symbol sym : BridJ.getNativeLibrary("unistd").getSymbols()) {
        			if (sym.getSymbol().contains("access"))
        				pout.println(sym.getSymbol());
        		}
        		*/
        		final int fa = 2, fb = 3;
        		Pointer<?> pcb;
        		/*
        		final BridJLib.passTwoIntsToCallback_cb cb = new BridJLib.passTwoIntsToCallback_cb() {
        			public int apply(int a, int b) {
        				return a * fa + b * fb;	
        			}
        		};
        		pcb = pointerTo(cb);
        		//*/
        		///*
        		pcb = allocateDynamicCallback(
        			new DynamicCallback<Integer>() {
        				public Integer apply(Object... args) {
        					int a = (Integer)args[0];
        					int b = (Integer)args[1];
        					return a * fa + b * fb;	
        					//return cb.apply(a, b);
        				}
        			},
        			null,
        			int.class,
        			int.class,
        			int.class
        		);
        		//*/
        		pout.println(a + " * " + fa + " + " + b + " * " + fb + " = " + BridJLib.passTwoIntsToCallback(a, b, pcb) + " (through BridJ callback !)");
        		
        } catch (Throwable th) {
        		while (th.getCause() != null)
        			th = th.getCause();
        		th.printStackTrace(pout);
        }
        tv.setText(text.toString());
        setContentView(tv);
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();

    /* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.HelloJni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }
}
