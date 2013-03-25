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
