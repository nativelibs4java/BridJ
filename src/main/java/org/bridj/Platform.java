package org.bridj;

import org.bridj.util.ProcessUtils;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.io.*;
import java.net.URL;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import org.bridj.util.StringUtils;

/**
 * Information about the execution platform (OS, architecture, native sizes...) and platform-specific actions.
 * <ul>
 * <li>To know if the JVM platform is 32 bits or 64 bits, use {@link Platform#is64Bits()}
 * </li><li>To know if the OS is an Unix-like system, use {@link Platform#isUnix()}
 * </li><li>To open files and URLs in a platform-specific way, use {@link Platform#open(File)}, {@link Platform#open(URL)}, {@link Platform#show(File)}
 * </li></ul>
 * @author ochafik
 */
public class Platform {
    static final String osName = System.getProperty("os.name", "");

    private static boolean inited;
    static final String BridJLibraryName = "bridj";
    
    public static final int 
    		POINTER_SIZE, 
    		WCHAR_T_SIZE, 
    		SIZE_T_SIZE, 
    		TIME_T_SIZE, 
    		CLONG_SIZE;
    	
    /*interface FunInt {
        int apply();
    }
    static int tryInt(FunInt f, int defaultValue) {
        try {
	        return f.apply();
        } catch (Throwable th) {
            return defaultValue;
        }
    }*/
    static final ClassLoader systemClassLoader; 
    public static ClassLoader getClassLoader() {
    		return getClassLoader(BridJ.class);
    }
    public static ClassLoader getClassLoader(Class<?> cl) {
    		ClassLoader loader = cl == null ? null : cl.getClassLoader();
    		if (loader == null)
    			loader = Thread.currentThread().getContextClassLoader();
    		return loader == null ? systemClassLoader : loader;
    }
    public static InputStream getResourceAsStream(String path) {
    	URL url = getResource(path);
    	try {
    		return url != null ? url.openStream() : null;
    	} catch (IOException ex) {
    		if (BridJ.verbose)
    			BridJ.warning("Failed to get resource '" + path + "'", ex);
    		return null;
    	}
    }
    public static URL getResource(String path) {
    	if (!path.startsWith("/"))
    		path = "/" + path;
    	
    	URL in = BridJ.class.getResource(path);
    	if (in != null)
    		return in;
    	
    	ClassLoader[] cls = {
    		BridJ.class.getClassLoader(),
    		Thread.currentThread().getContextClassLoader(),
    		systemClassLoader
    	};
    	for (ClassLoader cl : cls) { 
			if (cl != null && (in = cl.getResource(path)) != null)
				return in;
		}
		return null;
    }
    
    /*
    public static class utsname {
    		public final String sysname, nodename, release, version, machine;
		public utsname(String sysname, String nodename, String release, String version, String machine) {
			this.sysname = sysname;
			this.nodename = nodename;
			this.release = release;
			this.version = version;
			this.machine = machine;
		}
		public String toString() {
			StringBuilder b = new StringBuilder("{\n");
			b.append("\tsysname: \"").append(sysname).append("\",\n");
			b.append("\tnodename: \"").append(nodename).append("\",\n");
			b.append("\trelease: \"").append(release).append("\",\n");
			b.append("\tversion: \"").append(version).append("\",\n");
			b.append("\tmachine: \"").append(machine).append("\"\n");
			return b.append("}").toString();
		}
    }
    public static native utsname uname();
    */
    static final List<String> embeddedLibraryResourceRoots = new ArrayList<String>();
    
    /**
     * BridJ is able to automatically extract native binaries bundled in the application's JARs, using a customizable root path and a predefined architecture-dependent subpath. This method adds an alternative root path to the search list.<br>
     * For instance, if you want to load library "mylib" and call <code>addEmbeddedLibraryResourceRoot("my/company/lib/")</code>, BridJ will look for library in the following locations :
     * <ul>
     * <li>"my/company/lib/darwin_universal/libmylib.dylib" on MacOS X (or darwin_x86, darwin_x64, darwin_ppc if the binary is not universal)</li>
     * <li>"my/company/lib/win32/mylib.dll" on Windows (use win64 on 64 bits architectures)</li>
     * <li>"my/company/lib/linux_x86/libmylib.so" on Linux (use linux_x64 on 64 bits architectures)</li>
     * <li>"my/company/lib/sunos_x86/libmylib.so" on Solaris (use sunos_x64 / sunos_sparc on other architectures)</li>
     * <li>"lib/armeabi/libmylib.so" on Android (for Android-specific reasons, only the "lib" sub-path can effectively contain loadable binaries)</li>
     * </ul>
     * For other platforms or for an updated list of supported platforms, please have a look at BridJ's JAR contents (under "org/bridj/lib") and/or to its source tree, browsable online. 
     */
    public static synchronized void addEmbeddedLibraryResourceRoot(String root) {
    		embeddedLibraryResourceRoots.add(0, root);
    }
    
    static Set<File> temporaryExtractedLibraryCanonicalFiles = Collections.synchronizedSet(new LinkedHashSet<File>());
	static void addTemporaryExtractedLibraryFileToDeleteOnExit(File file) throws IOException {
        File canonicalFile = file.getCanonicalFile();
        
        // Give a chance to NativeLibrary.release() to delete the file :
        temporaryExtractedLibraryCanonicalFiles.add(canonicalFile);
        
        // Ask Java to delete the file upon exit if it still exists
        canonicalFile.deleteOnExit();
    }
    
    
    private static final String arch;
    private static boolean is64Bits;
    private static File extractedLibrariesTempDir;
    
    static {
    	arch = System.getProperty("os.arch");
        {
            String dataModel = System.getProperty("sun.arch.data.model", System.getProperty("com.ibm.vm.bitmode"));
            if ("32".equals(dataModel))
                is64Bits = false;
            else if ("64".equals(dataModel))
                is64Bits = true;
            else {
                is64Bits = 
                    arch.contains("64") ||
                    arch.equalsIgnoreCase("sparcv9");
            }
        }
        systemClassLoader = createClassLoader();
        
        addEmbeddedLibraryResourceRoot("lib/");
        if (!isAndroid()) {
        	addEmbeddedLibraryResourceRoot("org/bridj/lib/");
        	if (!Version.VERSION_SPECIFIC_SUB_PACKAGE.equals(""))
        		addEmbeddedLibraryResourceRoot("org/bridj/" + Version.VERSION_SPECIFIC_SUB_PACKAGE + "/lib/");
        }
        	
        try {
            extractedLibrariesTempDir = createTempDir("BridJExtractedLibraries");
    	    initLibrary();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        POINTER_SIZE = sizeOf_ptrdiff_t();
		WCHAR_T_SIZE = sizeOf_wchar_t();
		SIZE_T_SIZE = sizeOf_size_t();
		TIME_T_SIZE = sizeOf_time_t();
		CLONG_SIZE = sizeOf_long();
        
        is64Bits = POINTER_SIZE == 8;
        
        Runtime.getRuntime().addShutdownHook(new Thread() { public void run() {
            shutdown();
        }});
    }
    private static List<NativeLibrary> nativeLibraries = new ArrayList<NativeLibrary>();
    static void addNativeLibrary(NativeLibrary library) {
        synchronized (nativeLibraries) {
            nativeLibraries.add(library);
        }
    }
    private static void shutdown() {
    	//releaseNativeLibraries();
        deleteTemporaryExtractedLibraryFiles();
    }
    private static void releaseNativeLibraries() {
        synchronized (nativeLibraries) {
            // Release libraries in reverse order :
            for (int iLibrary = nativeLibraries.size(); iLibrary-- != 0;) {
                NativeLibrary lib = nativeLibraries.get(iLibrary);
                try {
                    lib.release();
                } catch (Throwable th) {
                    BridJ.error("Failed to release library '" + lib.path + "' : " + th, th);
                }
            }
        }
    }
    private static void deleteTemporaryExtractedLibraryFiles() {
        synchronized (temporaryExtractedLibraryCanonicalFiles) {
        	temporaryExtractedLibraryCanonicalFiles.add(extractedLibrariesTempDir);
        	
            // Release libraries in reverse order :
            List<File> filesToDeleteAfterExit = new ArrayList<File>();
            for (File tempFile : Platform.temporaryExtractedLibraryCanonicalFiles) {
                if (tempFile.delete()) {
                    if (BridJ.verbose)
                        BridJ.info("Deleted temporary library file '" + tempFile + "'");
                } else
                    filesToDeleteAfterExit.add(tempFile);
            }
            if (!filesToDeleteAfterExit.isEmpty()) {
                if (BridJ.verbose)
                    BridJ.info("Attempting to delete " + filesToDeleteAfterExit.size() + " files after JVM exit : " + StringUtils.implode(filesToDeleteAfterExit, ", "));
                
                try {
                    ProcessUtils.startJavaProcess(DeleteFiles.class, filesToDeleteAfterExit);
                } catch (Throwable ex) {
                    BridJ.error("Failed to launch process to delete files after JVM exit : " + ex, ex);
                }
            }
        }
    }
    public static class DeleteFiles {
        static boolean delete(List<File> files) {
            for (Iterator<File> it = files.iterator(); it.hasNext();) {
                File file = it.next();
                if (file.delete())
                    it.remove();
            }
            return files.isEmpty();
        }
        final static long 
            TRY_DELETE_EVERY_MILLIS = 50,
            FAIL_AFTER_MILLIS = 10000;
        public static void main(String[] args) {
            try {
                List<File> files = new LinkedList<File>();
                for (String arg : args)
                    files.add(new File(arg));

                long start = System.currentTimeMillis();
                while (!delete(files)) {
                    long elapsed = System.currentTimeMillis() - start;
                    if (elapsed > FAIL_AFTER_MILLIS) {
                        System.err.println("Failed to delete the following files : " + StringUtils.implode(files));
                        System.exit(1);
                    }

                    Thread.sleep(TRY_DELETE_EVERY_MILLIS);
                }
            } catch (Throwable th) {
                th.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }
    static ClassLoader createClassLoader()
	{
		List<URL> urls = new ArrayList<URL>();
		for (String propName : new String[] { "java.class.path", "sun.boot.class.path" }) {
			String prop = System.getProperty(propName);
			if (prop == null)
				continue;
			
			for (String path : prop.split(File.pathSeparator)) {
				path = path.trim();
				if (path.length() == 0)
					continue;
				
				URL url;
				try {
					url = new URL(path);
				} catch (MalformedURLException ex) {
					try {
						url = new File(path).toURI().toURL();
					} catch (MalformedURLException ex2) {
						url = null;
					}
				}
				if (url != null)
					urls.add(url);
			}
		}
		//System.out.println("URLs for synthetic class loader :");
		//for (URL url : urls)
		//	System.out.println("\t" + url);
		return new URLClassLoader(urls.toArray(new URL[urls.size()]));
	}
	static String getenvOrProperty(String envName, String javaName, String defaultValue) {
		String value = System.getenv(envName);
		if (value == null)
			value = System.getProperty(javaName);
		if (value == null)
			value = defaultValue;
		return value;
	}

    public static synchronized void initLibrary() {
        if (inited) {
            return;
        }
		inited = true;

        try {
            boolean loaded = false;

            String forceLibFile = getenvOrProperty("BRIDJ_LIBRARY", "bridj.library", null);

            String lib = null;

            if (forceLibFile != null) {
                try {
                    System.load(lib = forceLibFile);
                    loaded = true;
                } catch (Throwable ex) {
                    BridJ.error("Failed to load forced library " + forceLibFile, ex);
                }
            }

            if (!loaded) {
                if (!Platform.isAndroid()) {
                    try {
                        File libFile = extractEmbeddedLibraryResource(BridJLibraryName);
                        if (libFile == null) {
                            throw new FileNotFoundException("Failed to extract embedded library '" + BridJLibraryName + "' (could be a classloader issue, or missing binary in resource path " + StringUtils.implode(embeddedLibraryResourceRoots, ", ") + ")");
                        }

                        if (BridJ.veryVerbose)
                        	BridJ.info("Loading library " + libFile);
                        System.load(lib = libFile.toString());
                        BridJ.setNativeLibraryFile(BridJLibraryName, libFile);
                        loaded = true;
                    } catch (IOException ex) {
                        BridJ.error("Failed to load '" + BridJLibraryName + "'", ex);
                    }
                }
                if (!loaded) {
                    System.loadLibrary("bridj");
                }
            }
            if (BridJ.veryVerbose)
				BridJ.info("Loaded library " + lib);

            init();

            //if (BridJ.protectedMode)
            //		BridJ.info("Protected mode enabled");
            if (BridJ.logCalls) {
                BridJ.info("Calls logs enabled");
            }

        } catch (Throwable ex) {
            throw new RuntimeException("Failed to initialize " + BridJ.class.getSimpleName(), ex);
        }
    }
    private static native void init();

    public static boolean isLinux() {
    	return isUnix() && osName.toLowerCase().contains("linux");
    }
    public static boolean isMacOSX() {
    	return isUnix() && (osName.startsWith("Mac") || osName.startsWith("Darwin"));
    }
    public static boolean isSolaris() {
    	return isUnix() && (osName.startsWith("SunOS") || osName.startsWith("Solaris"));
    }
    public static boolean isBSD() {
    	return isUnix() && (osName.contains("BSD") || isMacOSX());
    }
    public static boolean isUnix() {
    	return File.separatorChar == '/';
    }
    public static boolean isWindows() {
    	return File.separatorChar == '\\';
    }

    public static boolean isWindows7() {
    	return osName.equals("Windows 7");
    }
    
    /**
     * Whether to use Unicode versions of Windows APIs rather than ANSI versions (for functions that haven't been bound yet : has no effect on functions that have already been bound).<br>
     * Some Windows APIs such as SendMessage have two versions : 
     * <ul>
     * <li>one that uses single-byte character strings (SendMessageA, with 'A' for ANSI strings)</li>
     * <li>one that uses unicode character strings (SendMessageW, with 'W' for Wide strings).</li>
     * </ul>
     * <br>
     * In a C/C++ program, this behaviour is controlled by the UNICODE macro definition.<br>
     * By default, BridJ will use the Unicode versions. Set this field to false, set the bridj.useUnicodeVersionOfWindowsAPIs property to "false" or the BRIDJ_USE_UNICODE_VERSION_OF_WINDOWS_APIS environment variable to "0" to use the ANSI string version instead.
     */
    public static boolean useUnicodeVersionOfWindowsAPIs = !(
    		"false".equals(System.getProperty("bridj.useUnicodeVersionOfWindowsAPIs")) ||
    		"0".equals(System.getenv("BRIDJ_USE_UNICODE_VERSION_OF_WINDOWS_APIS"))
	);
    
	private static String getArch() {
		return arch;
	}
	/**
	 * Machine (as returned by `uname -m`, except for i686 which is actually i386), adjusted to the JVM platform (32 or 64 bits)
	 */
	public static String getMachine() {
		String arch = getArch();
		if (arch.equals("amd64") || arch.equals("x86_64")) {
            if (is64Bits())
                return "x86_64";
            else
                return "i386"; // we are running a 32 bits JVM on a 64 bits platform
        }
		return arch;
	}
	
	public static boolean isAndroid() {
		return "dalvik".equalsIgnoreCase(System.getProperty("java.vm.name")) && isLinux();
	}
	public static boolean isArm() {
    		String arch = getArch();
		return "arm".equals(arch);	
	}
	public static boolean isSparc() {
    		String arch = getArch();
		return 
			"sparc".equals(arch) ||
			"sparcv9".equals(arch);
	}
    public static boolean is64Bits() {
        return is64Bits;
    }
    public static boolean isAmd64Arch() {
    		String arch = getArch();
        return arch.equals("x86_64");
    }

    static synchronized Collection<String> getEmbeddedLibraryResource(String name) {
    		Collection<String> ret = new ArrayList<String>();
    		
    		for (String root : embeddedLibraryResourceRoots) {
    			if (root == null)
    				continue;
    			
			if (isWindows())
				ret.add(root + (is64Bits() ? "win64/" : "win32/") + name + ".dll");
			else if (isMacOSX()) {
				String suff = "/lib" + name + ".dylib";
				if (isArm())
					ret.add(root + "iphoneos_arm32_arm" + suff);
				else {
					String pref = root + "darwin_";
					String univ = pref + "universal" + suff;
					if (isAmd64Arch()) {
						ret.add(univ);
						ret.add(pref + "x64" + suff);
					} else
						ret.add(univ);
				}
			} 
			else {
                String path = null;
                if (isAndroid()) {
                    assert root.equals("lib/");
                    path = root + "armeabi/"; // Android SDK + NDK-style .so embedding = lib/armeabi/libTest.so
                } 
                else if (isLinux())
                    path = root + (isArm() ? "linux_arm32_arm/" : is64Bits() ? "linux_x64/" : "linux_x86/");
                else if (isSolaris()) {
                    if (isSparc()) {	
                        path = root + (is64Bits() ? "sunos_sparc64/" : "sunos_sparc/");
                    } else {
                        path = root + (is64Bits() ? "sunos_x64/" : "sunos_x86/");
                    }	
                }
                if (path != null) {
                    ret.add(path + "lib" + name + ".so");
                    ret.add(path + name + ".so");
                }
            }
		}
		if (ret.isEmpty())
			throw new RuntimeException("Platform not supported ! (os.name='" + osName + "', os.arch='" + System.getProperty("os.arch") + "')");
		
		if (BridJ.veryVerbose)
			BridJ.info("Embedded paths for library " + name + " : " + ret);
		return ret;
    }
    
    static void tryDeleteFilesInSameDirectory(final File legitFile, final Pattern fileNamePattern, long atLeastOlderThanMillis) {
        final long maxModifiedDateForDeletion = System.currentTimeMillis() - atLeastOlderThanMillis;
        new Thread(new Runnable() { public void run() {
            File dir = legitFile.getParentFile();
            String legitFileName = legitFile.getName();
            try {
                for (String name : dir.list()) {
                    if (name.equals(legitFileName))
                        continue;

                    if (!fileNamePattern.matcher(name).matches()) 
                        continue;

                    File file = new File(dir, name);
                    if (file.lastModified() > maxModifiedDateForDeletion)
                        continue;

                    if (file.delete() && BridJ.verbose)
                        BridJ.info("Deleted old binary file '" + file + "'");
                }
            } catch (SecurityException ex) {
                // no right to delete files in that directory
                BridJ.warning("Failed to delete files matching '" + fileNamePattern + "' in directory '" + dir + "'", ex);
            } catch (Throwable ex) {
                BridJ.error("Unexpected error : " + ex, ex);
            }
        }}).start();
    }
    static final long DELETE_OLD_BINARIES_AFTER_MILLIS = 24 * 60 * 60 * 1000; // 24 hours
    
    static File extractEmbeddedLibraryResource(String name) throws IOException {
        String firstLibraryResource = null;
		for (String libraryResource : getEmbeddedLibraryResource(name)) {
			if (firstLibraryResource == null)
				firstLibraryResource = libraryResource;
			int i = libraryResource.lastIndexOf('.');
			int len;
			byte[] b = new byte[8196];
			InputStream in = getResourceAsStream(libraryResource);
			if (in == null) {
				File f = new File(libraryResource);
				if (!f.exists())
				f = new File(f.getName());
				if (f.exists())
					return f.getCanonicalFile();
				continue;
			}
            String fileName = new File(libraryResource).getName();
			File libFile = new File(extractedLibrariesTempDir, fileName);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(libFile));
			while ((len = in.read(b)) > 0)
				out.write(b, 0, len);
			out.close();
			in.close();
			
			addTemporaryExtractedLibraryFileToDeleteOnExit(libFile);
			addTemporaryExtractedLibraryFileToDeleteOnExit(libFile.getParentFile());
			
            return libFile;
		}
        return null;
		//throw new FileNotFoundException(firstLibraryResource);
    }
    
    static final int maxTempFileAttempts = 20;
    static File createTempDir(String prefix) throws IOException {
    	File dir;
    	for (int i = 0; i < maxTempFileAttempts; i++) {
    		dir = File.createTempFile(prefix, "");
    		if (dir.delete() && dir.mkdirs()) {
    			return dir;
    		}
    	}
    	throw new RuntimeException("Failed to create temp dir with prefix '" + prefix + "' despite " + maxTempFileAttempts + " attempts!");
    }
    
    /**
     * Opens an URL with the default system action.
     * @param url url to open
     * @throws NoSuchMethodException if opening an URL on the current platform is not supported
     */
	public static final void open(URL url) throws NoSuchMethodException {
        if (url.getProtocol().equals("file")) {
            open(new File(url.getFile()));
        } else {
            if (Platform.isMacOSX()) {
                execArgs("open", url.toString());
            } else if (Platform.isWindows()) {
                execArgs("rundll32", "url.dll,FileProtocolHandler", url.toString());
            } else if (Platform.isUnix() && hasUnixCommand("gnome-open")) {
                execArgs("gnome-open", url.toString());
            } else if (Platform.isUnix() && hasUnixCommand("konqueror")) {
                execArgs("konqueror", url.toString());
            } else if (Platform.isUnix() && hasUnixCommand("mozilla")) {
                execArgs("mozilla", url.toString());
            } else {
                throw new NoSuchMethodException("Cannot open urls on this platform");
		}
	}
    }

    /**
     * Opens a file with the default system action.
     * @param file file to open
     * @throws NoSuchMethodException if opening a file on the current platform is not supported
     */
	public static final void open(File file) throws NoSuchMethodException {
        if (Platform.isMacOSX()) {
			execArgs("open", file.getAbsolutePath());
        } else if (Platform.isWindows()) {
            if (file.isDirectory()) {
                execArgs("explorer", file.getAbsolutePath());
            } else {
                execArgs("start", file.getAbsolutePath());
            }
        } else if (Platform.isUnix() && hasUnixCommand("gnome-open")) {
            execArgs("gnome-open", file.toString());
        } else if (Platform.isUnix() && hasUnixCommand("konqueror")) {
            execArgs("konqueror", file.toString());
        } else if (Platform.isSolaris() && file.isDirectory()) {
            execArgs("/usr/dt/bin/dtfile", "-folder", file.getAbsolutePath());
        } else {
            throw new NoSuchMethodException("Cannot open files on this platform");
	}
    }

    /**
     * Show a file in its parent directory, if possible selecting the file (not possible on all platforms).
     * @param file file to show in the system's default file navigator
     * @throws NoSuchMethodException if showing a file on the current platform is not supported
     */
	public static final void show(File file) throws NoSuchMethodException, IOException {
        if (Platform.isWindows()) {
			exec("explorer /e,/select,\"" + file.getCanonicalPath() + "\"");
        } else {
            open(file.getAbsoluteFile().getParentFile());
    }
    }

    static final void execArgs(String... cmd) throws NoSuchMethodException {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NoSuchMethodException(ex.toString());
		}
	}

	static final void exec(String cmd) throws NoSuchMethodException {
		try {
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NoSuchMethodException(ex.toString());
		}
	}

    static final boolean hasUnixCommand(String name) {
		try {
            Process p = Runtime.getRuntime().exec(new String[]{"which", name});
			return p.waitFor() == 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	static native int sizeOf_size_t();
    static native int sizeOf_time_t();
    static native int sizeOf_wchar_t();
    static native int sizeOf_ptrdiff_t();
	static native int sizeOf_long();
	
	static native int getMaxDirectMappingArgCount();
}
