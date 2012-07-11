/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.logging.Level;
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
                BridJ.log(Level.INFO, "Dex output for class " + className + " : " + out);
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
