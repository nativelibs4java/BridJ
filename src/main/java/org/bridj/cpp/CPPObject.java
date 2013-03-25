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
package org.bridj.cpp;

import java.util.Collections;
import java.util.Stack;

import org.bridj.NativeObject;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Runtime;
import org.bridj.cpp.mfc.MFCRuntime;
import org.bridj.BridJ;

import java.util.Map;

/**
 * Base class for C++ structs and classes.
 * @author Olivier
 */
@Runtime(CPPRuntime.class)
public abstract class CPPObject extends StructObject {
	Map<Class<?>, Object[]> templateParameters;
	
	protected CPPObject() {}
    protected CPPObject(Pointer<? extends CPPObject> peer, Object... targs) {
        super(peer);
        templateParameters = (Map)Collections.singletonMap(getClass(), targs);
    }
    protected CPPObject(Void voidArg, int constructorId, Object... args) {
        super(voidArg, constructorId, args);
    }
    /*
    @Override
    protected void finalize() throws Throwable {
    		BridJ.deallocate(this);
    }*/
}
