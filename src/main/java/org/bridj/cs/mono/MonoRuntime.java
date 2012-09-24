/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cs.mono;

import org.bridj.AbstractBridJRuntime;
import org.bridj.BridJ;
import static org.bridj.BridJ.*;
import org.bridj.NativeLibrary;
import org.bridj.NativeObject;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cs.CSharpRuntime;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;

/**
 * Stub, not implemented (see <a href="http://ochafik.com/blog/?p=165">this blog entry</a> for a proof of concept).
 * @author Olivier
 */
@Library("mono")
public class MonoRuntime extends AbstractBridJRuntime implements CSharpRuntime {

    public MonoRuntime() {
        try {
            BridJ.register();
        } catch (Exception ex) {
            // Accept failure
            info("Failed to register " + getClass().getName(), ex);
        }
    }

    //@Override
    public boolean isAvailable() {
        return getMonoLibrary() != null;
    }

    //@Override
    public <T extends NativeObject> Class<? extends T> getActualInstanceClass(Pointer<T> pInstance, Type officialType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public void register(Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    NativeLibrary monoLibrary;
    boolean fetchedLibrary;
    private synchronized NativeLibrary getMonoLibrary() {
        if (!fetchedLibrary && monoLibrary == null) {
            try {
                fetchedLibrary = true;
                monoLibrary = BridJ.getNativeLibrary("mono");
            } catch (Exception ex) {
                info(null, ex);
            }
        }
        return monoLibrary;
    }

    //@Override
    public <T extends NativeObject> TypeInfo<T> getTypeInfo(Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
