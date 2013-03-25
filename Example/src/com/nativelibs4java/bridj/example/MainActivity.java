package com.nativelibs4java.bridj.example;

import java.io.IOException;

import org.bridj.BridJ;
import org.bridj.NativeLibrary;
import org.bridj.demangling.Demangler.Symbol;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	@Library("example")
	public static void helloLog(Pointer<Byte> logThis);
	
	static {
		BridJ.register();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helloLog(pointerToCString("Hello, World!"));
        try {
			NativeLibrary lib = BridJ.getNativeLibrary("example");
			for (Symbol s : lib.getSymbols()) {
				String p = s.getParsedRef() + "";
				System.out.println(p);
			}
		} catch (IOException e) {
			throw new RuntimeException("BridJ loading failed?", e);
		}
        setContentView(R.layout.activity_main);
    }
}
