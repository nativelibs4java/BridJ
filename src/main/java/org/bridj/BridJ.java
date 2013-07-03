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
package org.bridj;

import org.bridj.ann.Forwardable;
import java.util.Set;
import java.util.HashSet;
import org.bridj.util.Utils;
import static org.bridj.util.AnnotationUtils.*;
import static org.bridj.util.Utils.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

import org.bridj.BridJRuntime.TypeInfo;
import org.bridj.demangling.Demangler.Symbol;
import org.bridj.demangling.Demangler.MemberRef;
import org.bridj.ann.Library;
import java.util.Stack;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URL;
import org.bridj.util.StringUtils;
import static org.bridj.Platform.*;
import static java.lang.System.*;
import org.bridj.util.ClassDefiner;
import org.bridj.util.ASMUtils;

/// http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html
/**
 * BridJ's central class.<br>
 * <ul>
 * <li>To register a class with native methods (which can be in inner classes),
 * just add the following static block to your class :
 * <pre>{@code
 *      static {
 *          BridJ.register();
 *      }
 * }</pre>
 * </li><li>You can also register a class explicitely with
 * {@link BridJ#register(java.lang.Class)}
 * </li><li>To alter the name of a library, use
 * {@link BridJ#setNativeLibraryActualName(String, String)} and
 * {@link BridJ#addNativeLibraryAlias(String, String)}
 * </li>
 * </ul>
 *
 * @author ochafik
 */
public class BridJ {

    static final Map<AnnotatedElement, NativeLibrary> librariesByClass = new HashMap<AnnotatedElement, NativeLibrary>();
    static final Map<String, File> librariesFilesByName = new HashMap<String, File>();
    static final Map<File, NativeLibrary> librariesByFile = new HashMap<File, NativeLibrary>();
    private static NativeEntities orphanEntities = new NativeEntities();
    static final Map<Class<?>, BridJRuntime> classRuntimes = new HashMap<Class<?>, BridJRuntime>();
    static final Map<Long, NativeObject> strongNativeObjects = new HashMap<Long, NativeObject>(),
            weakNativeObjects = new WeakHashMap<Long, NativeObject>();

    public static long sizeOf(Type type) {
        Class c = Utils.getClass(type);
        if (c.isPrimitive()) {
            return StructUtils.primTypeLength(c);
        } else if (Pointer.class.isAssignableFrom(c)) {
            return Pointer.SIZE;
        } else if (c == CLong.class) {
            return CLong.SIZE;
        } else if (c == TimeT.class) {
            return TimeT.SIZE;
        } else if (c == SizeT.class) {
            return SizeT.SIZE;
        } else if (c == Integer.class || c == Float.class) {
            return 4;
        } else if (c == Character.class || c == Short.class) {
            return 2;
        } else if (c == Long.class || c == Double.class) {
            return 8;
        } else if (c == Boolean.class || c == Byte.class) {
            return 1;
        } else if (NativeObject.class.isAssignableFrom(c)) {
            return getRuntime(c).getTypeInfo(type).sizeOf();
        } else if (IntValuedEnum.class.isAssignableFrom(c)) {
            return 4;
        }
        /*if (o instanceof NativeObject) {
         NativeObject no = (NativeObject)o;
         return no.typeInfo.sizeOf(no);
         }*/
        throw new RuntimeException("Unable to compute size of type " + Utils.toString(type));
    }

    static synchronized void registerNativeObject(NativeObject ob) {
        weakNativeObjects.put(Pointer.getAddress(ob, null), ob);
    }
    /// Caller should display message such as "target was GC'ed. You might need to add a BridJ.protectFromGC(NativeObject), BridJ.unprotectFromGC(NativeObject)

    static synchronized NativeObject getNativeObject(long peer) {
        NativeObject ob = weakNativeObjects.get(peer);
        if (ob == null) {
            ob = strongNativeObjects.get(peer);
        }
        return ob;
    }

    static synchronized void unregisterNativeObject(NativeObject ob) {
        long peer = Pointer.getAddress(ob, null);
        weakNativeObjects.remove(peer);
        strongNativeObjects.remove(peer);
    }

    /**
     * Keep a hard reference to a native object to avoid its garbage
     * collection.<br>
     * See {@link BridJ#unprotectFromGC(NativeObject)} to remove the GC
     * protection.
     */
    public static synchronized <T extends NativeObject> T protectFromGC(T ob) {
        long peer = Pointer.getAddress(ob, null);
        weakNativeObjects.remove(peer);
        strongNativeObjects.put(peer, ob);
        return ob;
    }

    /**
     * Drop the hard reference created with
     * {@link BridJ#protectFromGC(NativeObject)}.
     */
    public static synchronized <T extends NativeObject> T unprotectFromGC(T ob) {
        long peer = Pointer.getAddress(ob, null);
        if (strongNativeObjects.remove(peer) != null) {
            weakNativeObjects.put(peer, ob);
        }
        return ob;
    }

    public static void delete(NativeObject nativeObject) {
        unregisterNativeObject(nativeObject);
        Pointer.pointerTo(nativeObject, null).release();
    }

    /**
     * Registers the native methods of the caller class and all its inner types.
     * <pre>{@code
     * \@Library("mylib")
     * public class MyLib {
     * static {
     * BridJ.register();
     * }
     * public static native void someFunc();
     * }
     * }</pre>
     */
    public static synchronized void register() {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (stackTrace.length < 2) {
            throw new RuntimeException("No useful stack trace : cannot register with register(), please use register(Class) instead.");
        }
        String name = stackTrace[1].getClassName();
        try {
            Class<?> type = Class.forName(name);
            register(type);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to register class " + name, ex);
        }
    }

    /**
     * Create a subclass of the provided original class with synchronized
     * overrides for all native methods. Non-default constructors are not
     * currently handled.
     *
     * @param <T>
     * @param original
     * @throws IOException
     */
    public static <T> Class<? extends T> subclassWithSynchronizedNativeMethods(Class<T> original) throws IOException {
        ClassDefiner classDefiner = getRuntimeByRuntimeClass(CRuntime.class).getCallbackNativeImplementer();
        return ASMUtils.createSubclassWithSynchronizedNativeMethodsAndNoStaticFields(original, classDefiner);
    }

    enum CastingType {

        None, CastingNativeObject, CastingNativeObjectReturnType
    }
    static ThreadLocal<Stack<CastingType>> currentlyCastingNativeObject = new ThreadLocal<Stack<CastingType>>() {
        @Override
        protected java.util.Stack<CastingType> initialValue() {
            Stack<CastingType> s = new Stack<CastingType>();
            s.push(CastingType.None);
            return s;
        }
    ;

    };

    @Deprecated
    public static boolean isCastingNativeObjectInCurrentThread() {
        return currentlyCastingNativeObject.get().peek() != CastingType.None;
    }

    @Deprecated
    public static boolean isCastingNativeObjectReturnTypeInCurrentThread() {
        return currentlyCastingNativeObject.get().peek() == CastingType.CastingNativeObjectReturnType;
    }
    private static WeakHashMap<Long, NativeObject> knownNativeObjects = new WeakHashMap<Long, NativeObject>();

    public static synchronized <O extends NativeObject> void setJavaObjectFromNativePeer(long peer, O object) {
        if (object == null) {
            knownNativeObjects.remove(peer);
        } else {
            knownNativeObjects.put(peer, object);
        }
    }

    public static synchronized Object getJavaObjectFromNativePeer(long peer) {
        return knownNativeObjects.get(peer);
    }

    private static <O extends NativeObject> O createNativeObjectFromPointer(Pointer<? super O> pointer, Type type, CastingType castingType) {
        Stack<CastingType> s = currentlyCastingNativeObject.get();
        s.push(castingType);
        try {
            BridJRuntime runtime = getRuntime(Utils.getClass(type));
            TypeInfo<O> typeInfo = getTypeInfo(runtime, type);
            O instance = typeInfo.cast(pointer);
            if (BridJ.debug) {
                BridJ.info("Created native object from pointer " + pointer);
            }
            return instance;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to cast pointer to native object of type " + Utils.getClass(type).getName(), ex);
        } finally {
            s.pop();
        }
    }

    public static <O extends NativeObject> void copyNativeObjectToAddress(O value, Type type, Pointer<O> ptr) {
        BridJRuntime runtime = getRuntime(Utils.getClass(type));
        getTypeInfo(runtime, type).copyNativeObjectToAddress(value, (Pointer) ptr);
    }

    public static <O extends NativeObject> O createNativeObjectFromPointer(Pointer<? super O> pointer, Type type) {
        return (O) createNativeObjectFromPointer(pointer, type, CastingType.CastingNativeObject);
    }

    public static <O extends NativeObject> O createNativeObjectFromReturnValuePointer(Pointer<? super O> pointer, Type type) {
        return (O) createNativeObjectFromPointer(pointer, type, CastingType.CastingNativeObjectReturnType);
    }
    private static Map<Class<? extends BridJRuntime>, BridJRuntime> runtimes = new HashMap<Class<? extends BridJRuntime>, BridJRuntime>();

    public static synchronized <R extends BridJRuntime> R getRuntimeByRuntimeClass(Class<R> runtimeClass) {
        R r = (R) runtimes.get(runtimeClass);
        if (r == null) {
            try {
                runtimes.put(runtimeClass, r = runtimeClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate runtime " + runtimeClass.getName(), e);
            }
        }

        return r;
    }

    /**
     * Get the runtime class associated with a class (using the
     * {@link org.bridj.ann.Runtime} annotation, if any, looking up parents and
     * defaulting to {@link org.bridj.CRuntime}).
     */
    public static Class<? extends BridJRuntime> getRuntimeClass(Class<?> type) {
        org.bridj.ann.Runtime runtimeAnn = getInheritableAnnotation(org.bridj.ann.Runtime.class, type);
        Class<? extends BridJRuntime> runtimeClass = null;
        if (runtimeAnn != null) {
            runtimeClass = runtimeAnn.value();
        } else {
            runtimeClass = CRuntime.class;
        }

        return runtimeClass;
    }

    /**
     * Get the runtime associated with a class (using the
     * {@link org.bridj.ann.Runtime} annotation, if any, looking up parents and
     * defaulting to {@link org.bridj.CRuntime}).
     */
    public static BridJRuntime getRuntime(Class<?> type) {
        synchronized (classRuntimes) {
            BridJRuntime runtime = classRuntimes.get(type);
            if (runtime == null) {
                Class<? extends BridJRuntime> runtimeClass = getRuntimeClass(type);
                runtime = getRuntimeByRuntimeClass(runtimeClass);
                classRuntimes.put(type, runtime);

                if (veryVerbose) {
                    info("Runtime for " + type.getName() + " : " + runtimeClass.getName());
                }
            }
            return runtime;
        }
    }

    /**
     * Registers the native method of a type (and all its inner types).
     * <pre>{@code
     * \@Library("mylib")
     * public class MyLib {
     * static {
     * BridJ.register(MyLib.class);
     * }
     * public static native void someFunc();
     * }
     * }</pre>
     */
    public static BridJRuntime register(Class<?> type) {
        BridJRuntime runtime = getRuntime(type);
        if (runtime == null) {
            for (Class<?> child : type.getClasses()) {
                register(child);
            }
        } else {
            runtime.register(type);
        }
        return runtime;
    }

    public static void unregister(Class<?> type) {
        BridJRuntime runtime = getRuntime(type);
        if (runtime == null) {
            for (Class<?> child : type.getClasses()) {
                register(child);
            }
        } else {
            runtime.unregister(type);
        }
    }
    static Map<Type, TypeInfo<?>> typeInfos = new HashMap<Type, TypeInfo<?>>();

    static <T extends NativeObject> TypeInfo<T> getTypeInfo(BridJRuntime runtime, Type t) {
        synchronized (typeInfos) {
            TypeInfo info = typeInfos.get(t);
            if (info == null) {
                // getRuntime(Utils.getClass(t))
                info = runtime.getTypeInfo(t);
                typeInfos.put(t, info);
            }
            return info;
        }
    }

    enum Switch {

        Debug("bridj.debug", "BRIDJ_DEBUG", false,
        "Debug mode (implies high verbosity)"),
        DebugNeverFree("bridj.debug.neverFree", "BRIDJ_DEBUG_NEVER_FREE", false,
        "Never free allocated pointers (deprecated)"),
        DebugPointers("bridj.debug.pointers", "BRIDJ_DEBUG_POINTERS", false,
        "Trace pointer allocations & deallocations (to debug memory issues)"),
        VeryVerbose("bridj.veryVerbose", "BRIDJ_VERY_VERBOSE", false,
        "Highly verbose mode"),
        Verbose("bridj.verbose", "BRIDJ_VERBOSE", false,
        "Verbose mode"),
        Quiet("bridj.quiet", "BRIDJ_QUIET", false,
        "Quiet mode"),
        AlignDouble("bridj.alignDouble", "BRIDJ_ALIGN_DOUBLE", false,
        "Align doubles on 8 bytes boundaries even on Linux 32 bits (see -malign-double GCC option)."),
        LogCalls("bridj.logCalls", "BRIDJ_LOG_CALLS", false,
        "Log each native call performed (or call from native to Java callback)"),
        Protected("bridj.protected", "BRIDJ_PROTECTED", false,
        "Protect all native calls (including memory accesses) against native crashes (disables assembly optimizations and adds quite some overhead)."),
        Destructors("bridj.destructors", "BRIDJ_DESTRUCTORS", true,
        "Enable destructors (in languages that support them, such as C++)"),
        Direct("bridj.direct", "BRIDJ_DIRECT", true,
        "Direct mode (uses optimized assembler glue when possible to speed up calls)"),
        StructsByValue("bridj.structsByValue", "BRIDJ_STRUCT_BY_VALUE", false,
        "Enable experimental support for structs-by-value arguments and return values for C/C++ functions and methods.");
        public final boolean enabled, enabledByDefault;
        public final String propertyName, envName, description;

        /**
         * Important : keep full property name and environment variable name to
         * enable full-text search of options !!!
         */
        Switch(String propertyName, String envName, boolean enabledByDefault, String description) {
            if (enabledByDefault) {
                enabled = !("false".equals(getProperty(propertyName)) || "0".equals(getenv(envName)));
            } else {
                enabled = "true".equals(getProperty(propertyName)) || "1".equals(getenv(envName));
            }

            this.enabledByDefault = enabledByDefault;
            this.propertyName = propertyName;
            this.envName = envName;
            this.description = description;
        }

        public String getFullDescription() {
            return envName + " / " + propertyName + " (" + (enabledByDefault ? "enabled" : "disabled") + " by default) :\n\t" + description.replaceAll("\n", "\n\t");
        }
    }

    static {
        checkOptions();
    }

    static void checkOptions() {
        Set<String> props = new HashSet<String>(), envs = new HashSet<String>();
        for (Switch s : Switch.values()) {
            props.add(s.propertyName);
            envs.add(s.envName);
        }
        boolean hasUnknown = false;
        for (String n : System.getenv().keySet()) {
            if (!n.startsWith("BRIDJ_") || envs.contains(n)) {
                continue;
            }

            if (n.endsWith("_LIBRARY")) {
                continue;
            }

            if (n.endsWith("_DEPENDENCIES")) {
                continue;
            }

            error("Unknown environment variable : " + n + "=\"" + System.getenv(n) + "\"");
            hasUnknown = true;
        }

        for (Enumeration<String> e = (Enumeration) System.getProperties().propertyNames(); e.hasMoreElements();) {
            String n = e.nextElement();
            if (!n.startsWith("bridj.") || props.contains(n)) {
                continue;
            }

            if (n.endsWith(".library")) {
                continue;
            }

            if (n.endsWith(".dependencies")) {
                continue;
            }

            error("Unknown property : " + n + "=\"" + System.getProperty(n) + "\"");
            hasUnknown = true;
        }
        if (hasUnknown) {
            StringBuilder b = new StringBuilder();
            b.append("Available options (ENVIRONMENT_VAR_NAME / javaPropertyName) :\n");
            for (Switch s : Switch.values()) {
                b.append(s.getFullDescription() + "\n");
            }
            error(b.toString());
        }
    }
    public static final boolean debug = Switch.Debug.enabled;
    public static final boolean debugNeverFree = Switch.DebugNeverFree.enabled;
    public static final boolean debugPointers = Switch.DebugPointers.enabled;
    public static final boolean veryVerbose = Switch.VeryVerbose.enabled;
    public static final boolean verbose = debug || veryVerbose || Switch.Verbose.enabled;
    public static final boolean quiet = Switch.Quiet.enabled;
    public static final boolean logCalls = Switch.LogCalls.enabled;
    public static final boolean protectedMode = Switch.Protected.enabled;
    public static final boolean enableDestructors = Switch.Destructors.enabled;
    public static final boolean alignDoubles = Switch.AlignDouble.enabled;
    static volatile int minLogLevelValue = (verbose ? Level.WARNING : Level.INFO).intValue();

    public static void setMinLogLevel(Level level) {
        minLogLevelValue = level.intValue();
    }

    static boolean shouldLog(Level level) {
        return !quiet && (verbose || level.intValue() >= minLogLevelValue);
    }
    static Logger logger;

    static synchronized Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(BridJ.class.getName());
        }
        return logger;
    }

    public static boolean info(String message) {
        return info(message, null);
    }

    public static boolean info(String message, Throwable ex) {
        return log(Level.INFO, message, ex);
    }

    public static boolean debug(String message) {
        if (!debug) {
            return true;
        }
        return info(message, null);
    }

    public static boolean error(String message) {
        return error(message, null);
    }

    public static boolean error(String message, Throwable ex) {
        return log(Level.INFO, message, ex);
    }

    public static boolean warning(String message) {
        return warning(message, null);
    }

    public static boolean warning(String message, Throwable ex) {
        return log(Level.INFO, message, ex);
    }

    private static boolean log(Level level, String message, Throwable ex) {
        if (!shouldLog(level)) {
            return true;
        }
        getLogger().log(level, message, ex);
        return true;
    }

    static void logCall(Method m) {
        info("Calling method " + m);
    }

    public static synchronized NativeEntities getNativeEntities(AnnotatedElement type) throws IOException {
        NativeLibrary lib = getNativeLibrary(type);
        if (lib != null) {
            return lib.getNativeEntities();
        }
        return getOrphanEntities();
    }

    public static synchronized NativeLibrary getNativeLibrary(AnnotatedElement type) throws IOException {
        NativeLibrary lib = librariesByClass.get(type);
        if (lib == null) {
            Library libraryAnnotation = getLibrary(type);
            if (libraryAnnotation != null) {
                String libraryName = libraryAnnotation.value();
                String dependenciesEnv = getDependenciesEnv(libraryName);
                List<String> dependencies = libraryDependencies.get(libraryName);
                List<String> staticDependencies = Arrays.asList(
                        dependenciesEnv == null ? libraryAnnotation.dependencies() : dependenciesEnv.split(","));
                if (dependencies == null) {
                    dependencies = staticDependencies;
                } else {
                    dependencies.addAll(staticDependencies);
                }

                for (String dependency : dependencies) {
                    if (verbose) {
                        info("Trying to load dependency '" + dependency + "' of '" + libraryName + "'");
                    }
                    NativeLibrary depLib = getNativeLibrary(dependency);
                    if (depLib == null) {
                        throw new RuntimeException("Failed to load dependency '" + dependency + "' of library '" + libraryName + "'");
                    }
                }
                lib = getNativeLibrary(libraryName);
                if (lib != null) {
                    librariesByClass.put(type, lib);
                }
            }
        }
        return lib;
    }

    /**
     * Reclaims all the memory allocated by BridJ in the JVM and on the native
     * side.
     */
    public synchronized static void releaseAll() {
        strongNativeObjects.clear();
        weakNativeObjects.clear();
        gc();

        for (NativeLibrary lib : librariesByFile.values()) {
            lib.release();
        }
        librariesByFile.clear();
        librariesByClass.clear();
        getOrphanEntities().release();
        gc();
    }
    //public synchronized static void release(Class<?>);

    public synchronized static void releaseLibrary(String name) {
        File file = librariesFilesByName.remove(name);
        if (file != null) {
            releaseLibrary(file);
        }
    }

    public synchronized static void releaseLibrary(File library) {
        NativeLibrary lib = librariesByFile.remove(library);
        if (lib != null) {
            lib.release();
        }
    }
    static Map<String, NativeLibrary> libHandles = new HashMap<String, NativeLibrary>();
    static volatile List<String> nativeLibraryPaths;
    static List<String> additionalPaths = new ArrayList<String>();

    public static synchronized void addLibraryPath(String path) {
        additionalPaths.add(path);
        nativeLibraryPaths = null; // invalidate cached paths
    }

    private static void addPathsFromEnv(List<String> out, String name) {
        String env = getenv(name);
        if (BridJ.verbose) {
            BridJ.info("Environment var " + name + " = " + env);
        }
        addPaths(out, env);
    }

    private static void addPathsFromProperty(List<String> out, String name) {
        String env = getProperty(name);
        if (BridJ.verbose) {
            BridJ.info("Property " + name + " = " + env);
        }
        addPaths(out, env);
    }

    private static void addPaths(List<String> out, String env) {
        if (env == null) {
            return;
        }

        String[] paths = env.split(File.pathSeparator);
        if (paths.length == 0) {
            return;
        }
        if (paths.length == 1) {
            out.add(paths[0]);
            return;
        }
        out.addAll(Arrays.asList(paths));
    }

    static synchronized List<String> getNativeLibraryPaths() {
        if (nativeLibraryPaths == null) {
            nativeLibraryPaths = new ArrayList<String>();
            nativeLibraryPaths.addAll(additionalPaths);
            nativeLibraryPaths.add(null);
            nativeLibraryPaths.add(".");

            addPathsFromEnv(nativeLibraryPaths, "LD_LIBRARY_PATH");
            addPathsFromEnv(nativeLibraryPaths, "DYLD_LIBRARY_PATH");
            addPathsFromEnv(nativeLibraryPaths, "PATH");
            addPathsFromProperty(nativeLibraryPaths, "java.library.path");
            addPathsFromProperty(nativeLibraryPaths, "sun.boot.library.path");
            addPathsFromProperty(nativeLibraryPaths, "gnu.classpath.boot.library.path");

            File javaHome = new File(getProperty("java.home"));
            nativeLibraryPaths.add(new File(javaHome, "bin").toString());
            if (isMacOSX()) {
                nativeLibraryPaths.add(new File(javaHome, "../Libraries").toString());
            }


            if (isUnix()) {
                String bits = is64Bits() ? "64" : "32";
                if (isLinux()) {
                    // First try Ubuntu's multi-arch paths (cf. https://wiki.ubuntu.com/MultiarchSpec)
                    String abi = isArm() ? "gnueabi" : "gnu";
                    String multiArch = getMachine() + "-linux-" + abi;
                    nativeLibraryPaths.add("/lib/" + multiArch);
                    nativeLibraryPaths.add("/usr/lib/" + multiArch);

                    // Add /usr/lib32 and /lib32
                    nativeLibraryPaths.add("/usr/lib" + bits);
                    nativeLibraryPaths.add("/lib" + bits);
                } else if (isSolaris()) {
                    // Add /usr/lib/32 and /lib/32
                    nativeLibraryPaths.add("/usr/lib/" + bits);
                    nativeLibraryPaths.add("/lib/" + bits);
                }

                nativeLibraryPaths.add("/usr/lib");
                nativeLibraryPaths.add("/lib");
                nativeLibraryPaths.add("/usr/local/lib");
            }
        }
        return nativeLibraryPaths;
    }
    static Map<String, String> libraryActualNames = new HashMap<String, String>();

    /**
     * Define the actual name of a library.<br>
     * Works only before the library is loaded.<br>
     * For instance, library "OpenGL" is actually named "OpenGL32" on Windows :
     * BridJ.setNativeLibraryActualName("OpenGL", "OpenGL32");
     *
     * @param name
     * @param actualName
     */
    public static synchronized void setNativeLibraryActualName(String name, String actualName) {
        libraryActualNames.put(name, actualName);
    }
    static Map<String, List<String>> libraryAliases = new HashMap<String, List<String>>();

    /**
     * Add a possible alias for a library.<br>
     * Aliases are prioritary over the library (or its actual name, see
     * {@link BridJ#setNativeLibraryActualName(String, String)}), in the order
     * they are defined.<br>
     * Works only before the library is loaded.<br>
     *
     * @param name
     * @param alias
     */
    public static synchronized void addNativeLibraryAlias(String name, String alias) {
        List<String> list = libraryAliases.get(name);
        if (list == null) {
            libraryAliases.put(name, list = new ArrayList<String>());
        }
        if (!list.contains(alias)) {
            list.add(alias);
        }
    }
    static Map<String, List<String>> libraryDependencies = new HashMap<String, List<String>>();

    /**
     * Add names of library dependencies for a library.<br>
     * Works only before the library is loaded.<br>
     *
     * @param name
     * @param dependencyNames
     */
    public static synchronized void addNativeLibraryDependencies(String name, String... dependencyNames) {
        List<String> list = libraryDependencies.get(name);
        if (list == null) {
            libraryDependencies.put(name, list = new ArrayList<String>());
        }
        for (String dependencyName : dependencyNames) {
            if (!list.contains(dependencyName)) {
                list.add(dependencyName);
            }
        }
    }
    private static final Pattern numPat = Pattern.compile("\\b(\\d+)\\b");

    /**
     * Given "1.2.3", will yield (1 + 2 / 1000 + 3 / 1000000)
     */
    static double parseVersion(String s) {
        Matcher m = numPat.matcher(s);
        double res = 0.0, f = 1;
        while (m.find()) {
            res += Integer.parseInt(m.group(1)) * f;
            f /= 1000;
        }
        return res;
    }

    static File findFileWithGreaterVersion(File dir, String[] files, String baseFileName) {
        Pattern versionPattern = Pattern.compile(Pattern.quote(baseFileName) + "((:?\\.\\d+)+)");
        double maxVersion = 0;
        String maxVersionFile = null;
        for (String fileName : files) {
            Matcher m = versionPattern.matcher(fileName);
            if (m.matches()) {
                double version = parseVersion(m.group(1));
                if (maxVersionFile == null || version > maxVersion) {
                    maxVersionFile = fileName;
                    maxVersion = version;
                }
            }
        }
        if (maxVersionFile == null) {
            return null;
        }

        return new File(dir, maxVersionFile);
    }
    static Map<String, File> nativeLibraryFiles = new HashMap<String, File>();

    /**
     * Given a library name (e.g. "test"), finds the shared library file in the
     * system-specific path ("/usr/bin/libtest.so", "./libtest.dylib",
     * "c:\\windows\\system\\test.dll"...)
     */
    public static File getNativeLibraryFile(String libraryName) {
        if (libraryName == null) {
            return null;
        }

        try {
            synchronized (nativeLibraryFiles) {
                File nativeLibraryFile = nativeLibraryFiles.get(libraryName);
                if (nativeLibraryFile == null) {
                    nativeLibraryFiles.put(libraryName, nativeLibraryFile = findNativeLibraryFile(libraryName));
                }
                return nativeLibraryFile;
            }
        } catch (Throwable th) {
            warning("Library not found : " + libraryName, debug ? th : null);
            return null;
        }
    }

    /**
     * Associate a library name (e.g. "test"), to its shared library file.
     */
    public static void setNativeLibraryFile(String libraryName, File nativeLibraryFile) {
        if (libraryName == null) {
            return;
        }

        synchronized (nativeLibraryFiles) {
            nativeLibraryFiles.put(libraryName, nativeLibraryFile);
        }
    }

    private static String getLibraryEnv(String libraryName) {
        String env = getenv("BRIDJ_" + libraryName.toUpperCase() + "_LIBRARY");
        if (env == null) {
            env = getProperty("bridj." + libraryName + ".library");
        }
        return env;
    }

    private static String getDependenciesEnv(String libraryName) {
        String env = getenv("BRIDJ_" + libraryName.toUpperCase() + "_DEPENDENCIES");
        if (env == null) {
            env = getProperty("bridj." + libraryName + ".dependencies");
        }
        return env;
    }

    static File findNativeLibraryFile(String libraryName) throws IOException {
        //out.println("Getting file of '" + name + "'");
        String actualName = libraryActualNames.get(libraryName);
        List<String> aliases = libraryAliases.get(libraryName);
        List<String> possibleNames = new ArrayList<String>();
        if (Platform.isWindows()) {
            if (libraryName.equals("c") || libraryName.equals("m")) {
                possibleNames.add("msvcrt");
            }
        }
        if (aliases != null) {
            possibleNames.addAll(aliases);
        }

        possibleNames.add(actualName == null ? libraryName : actualName);

        //out.println("Possible names = " + possibleNames);
        List<String> paths = getNativeLibraryPaths();
        if (debug) {
            info("Looking for library '" + libraryName + "' " + (actualName != null ? "('" + actualName + "') " : "") + "in paths " + paths, null);
        }

        for (String name : possibleNames) {
            String env = getLibraryEnv(name);
            if (env != null) {
                File f = new File(env);
                if (f.exists()) {
                    try {
                        return f.getCanonicalFile();
                    } catch (IOException ex) {
                        error(null, ex);
                    }
                }
            }
            List<String> possibleFileNames = getPossibleFileNames(name);
            for (String path : paths) {
                File pathFile = path == null ? null : new File(path);
                File f = new File(name);
                if (!f.isFile() && pathFile != null) {
                    for (String possibleFileName : possibleFileNames) {
                        f = new File(pathFile, possibleFileName);
                        if (f.isFile()) {
                            break;
                        }
                    }

                    if (!f.isFile() && isLinux()) {
                        String[] files = pathFile.list();
                        if (files != null) {
                            for (String possibleFileName : possibleFileNames) {
                                File ff = findFileWithGreaterVersion(pathFile, files, possibleFileName);
                                if (ff != null && (f = ff).isFile()) {
                                    if (verbose) {
                                        info("File '" + possibleFileName + "' was not found, used versioned file '" + f + "' instead.");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!f.isFile()) {
                    continue;
                }

                try {
                    return f.getCanonicalFile();
                } catch (IOException ex) {
                    error(null, ex);
                }
            }
            if (isMacOSX()) {
                for (String s : new String[]{
                    "/System/Library/Frameworks",
                    "/System/Library/Frameworks/ApplicationServices.framework/Frameworks",
                    new File(getProperty("user.home"), "Library/Frameworks").toString()
                }) {
                    try {
                        File f = new File(new File(s, name + ".framework"), name);
                        if (f.isFile()) {
                            return f.getCanonicalFile();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }
            }
            File f;
            if (isAndroid()) {
                f = new File("lib" + name + ".so");
            } else {
                f = extractEmbeddedLibraryResource(name);
            }

            if (f != null && f.isFile()) {
                return f;
            }
        }
        throw new FileNotFoundException(StringUtils.implode(possibleNames, ", "));
    }
    static Boolean directModeEnabled;

    /**
     * Query direct mode.<br>
     * In direct mode, BridJ will <i>attempt</i> to optimize calls with
     * assembler code, so that the overhead of each call is about the same as
     * with plain JNI.<br>
     * Set -Dbridj.direct=false in the command line (or
     * setProperty("bridj.direct", "false")) or environment var BRIDJ_DIRECT=0
     * to disable
     */
    public static boolean isDirectModeEnabled() {
        if (directModeEnabled == null) {
            directModeEnabled =
                    Switch.Direct.enabled
                    && !logCalls
                    && !protectedMode;
            if (veryVerbose) {
                info("directModeEnabled = " + directModeEnabled);
            }
        }
        return directModeEnabled;
    }

    /**
     * Set direct mode.<br>
     * In direct mode, BridJ will <i>attempt</i> to optimize calls with
     * assembler code, so that the overhead of each call is about the same as
     * with plain JNI.<br>
     * Set -Dbridj.direct=false in the command line (or
     * setProperty("bridj.direct", "false")) or environment var BRIDJ_DIRECT=0
     * to disable
     */
    static void setDirectModeEnabled(boolean v) {
        directModeEnabled = v;
    }

    /**
     * Loads the library with the name provided in argument (see
     * {@link #getNativeLibraryFile(String)})
     */
    public static synchronized NativeLibrary getNativeLibrary(String name) throws IOException {
        if (name == null) {
            return null;
        }

        NativeLibrary l = libHandles.get(name);
        if (l != null) {
            return l;
        }

        File f = getNativeLibraryFile(name);
        //if (f == null) {
        //	throw new FileNotFoundException("Couldn't find library file for library '" + name + "'");
        //}

        return getNativeLibrary(name, f);
    }

    /**
     * Loads the shared library file under the provided name. Any subsequent
     * call to {@link #getNativeLibrary(String)} will return this library.
     */
    public static NativeLibrary getNativeLibrary(String name, File f) throws IOException {
        NativeLibrary ll = NativeLibrary.load(f == null ? name : f.toString());;
        if (ll == null) {
            ll = PlatformSupport.getInstance().loadNativeLibrary(name);
            if (ll == null) {
                boolean isWindows = Platform.isWindows();
                if ("c".equals(name) || "m".equals(name) && isWindows) {
                    ll = new NativeLibrary(isWindows ? "mscvrt" : null, 0, 0);
                }
            }
        }

        //if (ll == null && f != null)
        //	ll = NativeLibrary.load(f.getName());
        if (ll == null) {
            if (f != null && f.exists()) {
                throw new RuntimeException("Library '" + name + "' was not loaded successfully from file '" + f + "'");
            } else {
                throw new FileNotFoundException("Library '" + name + "' was not found in path '" + getNativeLibraryPaths() + "'" + (f != null && f.exists() ? " (failed to load " + f + ")" : ""));
            }
        }
        if (verbose) {
            info("Loaded library '" + name + "' from '" + f + "'", null);
        }

        libHandles.put(name, ll);
        return ll;
    }

    /**
     * Gets the name of the library declared for an annotated element. Recurses
     * up to parents of the element (class, enclosing classes) to find any
     * {@link org.bridj.ann.Library} annotation.
     */
    public static String getNativeLibraryName(AnnotatedElement m) {
        Library lib = getLibrary(m);
        return lib == null ? null : lib.value();
    }

    /**
     * Gets the {@link org.bridj.ann.Library} annotation for an annotated
     * element. Recurses up to parents of the element (class, enclosing classes)
     * if needed
     */
    static Library getLibrary(AnnotatedElement m) {
        return getInheritableAnnotation(Library.class, m);
    }

    public static Symbol getSymbolByAddress(long peer) {
        for (NativeLibrary lib : libHandles.values()) {
            Symbol symbol = lib.getSymbol(peer);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    public static void setOrphanEntities(NativeEntities orphanEntities) {
        BridJ.orphanEntities = orphanEntities;
    }

    public static NativeEntities getOrphanEntities() {
        return orphanEntities;
    }

    static void initialize(NativeObject instance) {
        Class<?> instanceClass = instance.getClass();
        BridJRuntime runtime = getRuntime(instanceClass);
        Type type = runtime.getType(instance);
        TypeInfo typeInfo = getTypeInfo(runtime, type);
        instance.typeInfo = typeInfo;
        typeInfo.initialize(instance);
    }

    static void initialize(NativeObject instance, Pointer peer, Object... targs) {
        Class<?> instanceClass = instance.getClass();
        BridJRuntime runtime = getRuntime(instanceClass);
        Type type = runtime.getType(instanceClass, targs, null);
        TypeInfo typeInfo = getTypeInfo(runtime, type);
        instance.typeInfo = typeInfo;
        typeInfo.initialize(instance, peer);
    }

    static void initialize(NativeObject instance, int constructorId, Object[] targsAndArgs) {
        Class<?> instanceClass = instance.getClass();
        BridJRuntime runtime = getRuntime(instanceClass);
        int typeParamCount[] = new int[1];
        Type type = runtime.getType(instanceClass, targsAndArgs, typeParamCount);
        int targsCount = typeParamCount[0];
        Object[] args = takeRight(targsAndArgs, targsAndArgs.length - targsCount);
        // TODO handle template arguments here (or above), with class => ((class, args) => Type) caching
        TypeInfo typeInfo = getTypeInfo(runtime, type);
        instance.typeInfo = typeInfo;
        typeInfo.initialize(instance, constructorId, args);
    }

    static <T extends NativeObject> T clone(T instance) throws CloneNotSupportedException {
        return ((TypeInfo<T>) instance.typeInfo).clone(instance);
    }

    /**
     * Some native object need manual synchronization between Java fields and
     * native memory.<br>
     * An example is JNA-style structures.
     */
    public static <T extends NativeObject> T readFromNative(T instance) {
        ((TypeInfo<T>) instance.typeInfo).readFromNative(instance);
        return instance;
    }

    /**
     * Some native object need manual synchronization between Java fields and
     * native memory.<br>
     * An example is JNA-style structures.
     */
    public static <T extends NativeObject> T writeToNative(T instance) {
        ((TypeInfo<T>) instance.typeInfo).writeToNative(instance);
        return instance;
    }

    /**
     * Creates a string that describes the provided native object, printing
     * generally-relevant internal data (for instance for structures, this will
     * typically display the fields values).<br>
     * This is primarily useful for debugging purposes.
     */
    public static String describe(NativeObject instance) {
        return ((TypeInfo) instance.typeInfo).describe(instance);
    }

    /**
     * Creates a string that describes the provided native object type, printing
     * generally-relevant internal data (for instance for structures, this will
     * typically display name of the fields, their offsets and lengths...).<br>
     * This is primarily useful for debugging purposes.
     */
    public static String describe(Type nativeObjectType) {
        BridJRuntime runtime = getRuntime(Utils.getClass(nativeObjectType));
        TypeInfo typeInfo = getTypeInfo(runtime, nativeObjectType);
        return typeInfo == null ? Utils.toString(nativeObjectType) : typeInfo.describe();
    }

    public static void main(String[] args) {
        List<NativeLibrary> libraries = new ArrayList<NativeLibrary>();
        try {
            File outputDir = new File(".");
            for (int iArg = 0, nArgs = args.length; iArg < nArgs; iArg++) {
                String arg = args[iArg];
                if (arg.equals("-d")) {
                    outputDir = new File(args[++iArg]);
                    continue;
                }
                try {
                    NativeLibrary lib = getNativeLibrary(arg);
                    libraries.add(lib);

                    PrintWriter sout = new PrintWriter(new File(outputDir, new File(arg).getName() + ".symbols.txt"));
                    for (Symbol sym : lib.getSymbols()) {
                        sout.print(sym.getSymbol());
                        sout.print(" // ");
                        try {
                            MemberRef mr = sym.getParsedRef();
                            sout.print(" // " + mr);
                        } catch (Throwable th) {
                            sout.print("?");
                        }
                        sout.println();
                    }
                    sout.close();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
            PrintWriter out = new PrintWriter(new File(outputDir, "out.h"));
            HeadersReconstructor.reconstructHeaders(libraries, out);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            exit(1);
        }
    }
}
