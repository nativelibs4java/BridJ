/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.cs.dotnet;

import org.bridj.AbstractBridJRuntime;
import org.bridj.JNI;
import org.bridj.NativeObject;
import org.bridj.Pointer;
import org.bridj.cs.CSharpRuntime;
import java.lang.reflect.Type;
import org.bridj.Platform;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/system.runtime.interopservices.marshal.getdelegateforfunctionpointer(VS.80).aspx">http://msdn.microsoft.com/en-us/library/system.runtime.interopservices.marshal.getdelegateforfunctionpointer(VS.80).aspx</a>
 * @author Olivier
 */
public class DotNetRuntime extends AbstractBridJRuntime implements CSharpRuntime {

    //@Override
    public boolean isAvailable() {
        return Platform.isWindows();
    }

    //@Override
    public <T extends NativeObject> Class<? extends T> getActualInstanceClass(Pointer<T> pInstance, Type officialType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public void register(Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public <T extends NativeObject> TypeInfo<T> getTypeInfo(Type type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
