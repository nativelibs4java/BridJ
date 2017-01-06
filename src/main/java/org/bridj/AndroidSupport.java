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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bridj.util.ClassDefiner;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

/**
 *
 * @author ochafik
 */
public class AndroidSupport extends PlatformSupport {

    Application app()
    {
        return AndroidWorkaround.getApplication();
    }

    String adviseToSetApp() {
        return app() == null ? "" : "Please use AndroidSupport.setApplication(Application). ";
    }
    volatile AndroidClassDefiner classDefiner;

    synchronized File getCacheDir() throws FileNotFoundException {
        File cacheDir = null;
        if (app() != null) {
            cacheDir = app().getCacheDir();
        }

        if (cacheDir == null || !cacheDir.isDirectory() || !cacheDir.canWrite()) {
            throw new FileNotFoundException("Failed to find the cache directory. " + adviseToSetApp());
        }

        return cacheDir;
    }

    @Override
    public synchronized ClassDefiner getClassDefiner(ClassDefiner defaultDefiner, ClassLoader parentClassLoader) {
        if (classDefiner == null) {
            try {
                classDefiner = new AndroidClassDefiner(getCacheDir(), parentClassLoader);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to instantiate the Android class definer : " + ex, ex);
            }
        }
        return classDefiner;
    }

    String getLibFileName(String libName) {
        return "lib" + libName + ".so";
    }

    synchronized File getNativeLibraryDir(String someBundledNativeLibraryName) throws FileNotFoundException {
        //String someKnownResource = 
        File f = null;
        if (app() != null) {
            try {
                // ApplicationInfo.nativeLibraryDir is only available from API level 9 and later
                // http://developer.android.com/reference/android/content/pm/ApplicationInfo.html#nativeLibraryDir
                f = (File) ApplicationInfo.class.getField("nativeLibraryDir").get(app().getApplicationInfo());
            } catch (Throwable th) {
            }
        }
        if (f == null) {
            String someKnownResource = "lib/armeabi/" + getLibFileName(someBundledNativeLibraryName);
            f = new File(getApplicationDataDir(someKnownResource), "lib");
        }

        if (f != null && f.isDirectory()) {
            return f;
        }

        throw new FileNotFoundException("Failed to get the native library directory " + (f != null ? "(" + f + " is not a directory). " : ". ") + adviseToSetApp());
    }

    synchronized File getApplicationDataDir(String someKnownResource) throws FileNotFoundException {
        if (app() != null) {
            return new File(app().getApplicationInfo().dataDir);
        } else {
            return new File(new File(Environment.getDataDirectory(), "data"), getPackageName(someKnownResource));
        }
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
        if (app() != null) {
            return app().getPackageName();
        } else {
            URL resource = Platform.getResource(someKnownResource);
            if (resource == null) {
                throw new FileNotFoundException("Resource does not exist : " + someKnownResource);
            }

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
