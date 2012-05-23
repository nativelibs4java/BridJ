/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ochafik
 */
public class AndroidSupport extends PlatformSupport {
    private volatile Application app;
    
    AndroidSupport() {}
    
    
    synchronized void setApp(Application application) {
        if (this.app != null && application != null && this.app != application)
            throw new IllegalArgumentException("Android Application has already been set to a different value : " + this.app);
        
        this.app = application;
    }

    public static void setApplication(Application application) {
        ((AndroidSupport)PlatformSupport.getInstance()).setApp(application);
    }
    
    String adviseToSetApp() {
        return app == null ? "" : "Please use AndroidSupport.setApplication(Application). ";
    }
    

    volatile AndroidClassDefiner classDefiner;

    synchronized File getCacheDir() throws FileNotFoundException {
        File cacheDir = null;
        if (app != null)
            cacheDir = app.getCacheDir();
        
        if (cacheDir == null || !cacheDir.isDirectory() || !cacheDir.canWrite())
            throw new FileNotFoundException("Failed to find the cache directory. " + adviseToSetApp());
                    
        return cacheDir;
    }
    @Override
    public synchronized ClassDefiner getClassDefiner(ClassDefiner defaultDefiner, ClassLoader parentClassLoader) {
        if (classDefiner == null)
            try {
                classDefiner = new AndroidClassDefiner(getCacheDir(), parentClassLoader);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to instantiate the Android class definer : " + ex, ex);
            }
        return classDefiner;
    }

    String getLibFileName(String libName) {
        return "lib" + libName + ".so";
    }
    synchronized File getNativeLibraryDir(String someBundledNativeLibraryName) throws FileNotFoundException {
        //String someKnownResource = 
        File f = null;
        if (app != null) {
            try {
                // ApplicationInfo.nativeLibraryDir is only available from API level 9 and later
                // http://developer.android.com/reference/android/content/pm/ApplicationInfo.html#nativeLibraryDir
                f = (File)ApplicationInfo.class.getField("nativeLibraryDir").get(app.getApplicationInfo());
            } catch (Throwable th) {}
        }
        if (f == null) {
            String someKnownResource = "lib/armeabi/" + getLibFileName(someBundledNativeLibraryName);
            f = new File(getApplicationDataDir(someKnownResource), "lib");
        }
        
        if (f != null && f.isDirectory())
            return f;
    
        throw new FileNotFoundException("Failed to get the native library directory " + (f != null ? "(" + f + " is not a directory). " : ". ") + adviseToSetApp());
    }
    
	synchronized File getApplicationDataDir(String someKnownResource) throws FileNotFoundException {
		if (app != null)
			return new File(app.getApplicationInfo().dataDir);
		else
			return new File(new File(Environment.getDataDirectory(), "data"), getPackageName(someKnownResource));
	}
    
    @Override
    public synchronized NativeLibrary loadNativeLibrary(String name) throws IOException {
        File f = new File(getNativeLibraryDir(name), getLibFileName(name));
        if (f.exists()) {
            return NativeLibrary.load(f == null ? name : f.toString());
        } else {
            throw new RuntimeException("File not found : " + f);
        }
    }
    
    synchronized String getPackageName(String someKnownResource) throws FileNotFoundException {
        if (app != null)
            return app.getPackageName();
        else {
            URL resource = Platform.getResource(someKnownResource);
            if (resource == null)
                throw new FileNotFoundException("Resource does not exist : " + someKnownResource);

            return getAndroidPackageNameFromResourceURL(resource.toString());
        }
    }
    
    static String getAndroidPackageNameFromResourceURL(String url) {
		Pattern p = Pattern.compile("jar:file:/data/[^/]+/([^/]*?)\\.apk!.*");
		Matcher m = p.matcher(url);
		String packageName = null;
		if (!m.matches()) {
			p = Pattern.compile("jar:file:/.*?/([^/]+)/pkg\\.apk!.*");
			m = p.matcher(url);
		}
		if (m.matches()) {
			packageName = m.group(1);
			if (packageName.matches(".*?-\\d+")) {
				int i = packageName.lastIndexOf("-");
				packageName = packageName.substring(0, i);
			}
		}
		return packageName;
    }
    
}
