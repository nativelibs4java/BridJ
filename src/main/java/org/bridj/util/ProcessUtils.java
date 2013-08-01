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
package org.bridj.util;

import org.bridj.ann.Library;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bridj.BridJ;
import org.bridj.Platform;
import org.bridj.ann.Convention;
import static java.lang.System.getProperty;

/**
 * Util methods to : query the process id from the current process, launch
 * processes (including JVM processes)
 *
 * @author Olivier
 */
public class ProcessUtils {

    @Library("kernel32")
    @Convention(Convention.Style.StdCall)
    static class Kernel32 {

        public static native int GetCurrentProcessId();
    }

    @Library("c")
    static class LibC {

        public static native int getpid();
    }

    /**
     * Get the current process native id
     */
    public static int getCurrentProcessId() {
        if (Platform.isWindows()) {
            BridJ.register(Kernel32.class);
            return Kernel32.GetCurrentProcessId();
        } else {
            BridJ.register(LibC.class);
            return LibC.getpid();
        }
    }

    public static String[] computeJavaProcessArgs(Class<?> mainClass, List<?> mainArgs) {
        List<String> args = new ArrayList<String>();
        args.add(new File(new File(getProperty("java.home")), "bin" + File.separator + "java").toString());
        args.add("-cp");
        args.add(getProperty("java.class.path"));
        args.add(mainClass.getName());
        for (Object arg : mainArgs) {
            args.add(arg.toString());
        }

        return args.toArray(new String[args.size()]);
    }

    public static Process startJavaProcess(Class<?> mainClass, List<?> mainArgs) throws IOException {
        ProcessBuilder b = new ProcessBuilder();
        b.command(computeJavaProcessArgs(mainClass, mainArgs));
        b.redirectErrorStream(true);
        return b.start();
    }
}
