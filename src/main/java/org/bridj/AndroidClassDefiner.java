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

import org.bridj.util.ClassDefiner;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.DexFile;
import com.android.dx.dex.DexOptions;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author ochafik
 */
class AndroidClassDefiner implements ClassDefiner {

    protected final File cacheDir;
    protected final ClassLoader parentClassLoader;
    public AndroidClassDefiner(File cacheDir, ClassLoader parentClassLoader) throws IOException {
        this.cacheDir = cacheDir;
        this.parentClassLoader = parentClassLoader;
    }

    static void delete(File f) {
        File[] fs = f.listFiles();
        if (fs != null)
            for (File ff : fs)
                delete(ff);
        f.delete();
    }
    
    private static final byte[] dex(String className, byte[] classData) throws ClassFormatError {
        try {
            DexOptions dexOptions = new DexOptions();
            DexFile dxFile = new DexFile(dexOptions);
            CfOptions cfOptions = new CfOptions();
            dxFile.add(CfTranslator.translate(className.replace('.', '/') + ".class", classData, cfOptions, dexOptions));
            StringWriter out = BridJ.debug ? new StringWriter() : null;
            byte[] dexData = dxFile.toDex(out, false);
            if (BridJ.debug)
                BridJ.info("Dex output for class " + className + " : " + out);
            return dexData;
        } catch (IOException ex) {
            throw new ClassFormatError("Unable to convert class data to Dalvik code using Dex : " + ex);
        }
    }
    public Class<?> defineClass(String className, byte[] classData) throws ClassFormatError {
        byte[] dexData = dex(className, classData);

        File tempDir = null;
        File apkFile = null;
        try {
            tempDir = File.createTempFile("bridj.", ".tmp", cacheDir).getAbsoluteFile();
            tempDir.delete();
            tempDir.mkdir();

            apkFile = File.createTempFile("dynamic.", ".apk", tempDir).getAbsoluteFile();
            apkFile.getParentFile().mkdirs();

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(apkFile));
            out.putNextEntry(new ZipEntry("classes.dex"));
            out.write(dexData);
            out.closeEntry();
            out.close();

            // http://stackoverflow.com/questions/2903260/android-using-dexclassloader-to-load-apk-file
            //PathClassLoader classLoader = new PathClassLoader(apkFile.toString(), getClass().getClassLoader());
            DexClassLoader classLoader = new DexClassLoader(apkFile.toString(), tempDir.toString(), null, parentClassLoader);
            return classLoader.loadClass(className);
        } catch (Throwable th) {
            throw new RuntimeException("Failed with tempFile = " + apkFile + " : " + th, th);
        } finally {
            if (apkFile != null)
                delete(apkFile);

            if (tempDir != null)
                delete(tempDir);
        }
        
    }
}
