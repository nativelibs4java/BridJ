/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * Util methods to : query the process id from the current process, launch processes (including JVM processes)
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
        for (Object arg : mainArgs)
            args.add(arg.toString());
        
        return args.toArray(new String[args.size()]);
    }
    public static Process startJavaProcess(Class<?> mainClass, List<?> mainArgs) throws IOException {
        ProcessBuilder b = new ProcessBuilder();
        b.command(computeJavaProcessArgs(mainClass, mainArgs));
        b.redirectErrorStream(true);
        return b.start();
    }
    
}
