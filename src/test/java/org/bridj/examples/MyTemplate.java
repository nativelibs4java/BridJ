package org.bridj.examples;
import org.bridj.ann.*;
import org.bridj.cpp.*;
import org.bridj.*;
import java.lang.reflect.Type;
import java.nio.*;
import java.util.*;

/**

mvn package -DskipTests=true -o && java -cp target/bridj-0.4-SNAPSHOT-shaded.jar org.bridj.examples.MyTemplate

template <int n, typename T>
class MyTemplate {
public:
	MyTemplate(int arg);
	T someMethod();
}
 
 */ 
@Template({ Integer.class, Class.class })
public class MyTemplate<T> extends CPPObject {
    static {
		BridJ.register();
	}
	
	public final int n;
    
	@Constructor(0)
	public MyTemplate(int n, Type t, int arg) {
		super(null, 0, n, t, arg);
		this.n = n;
	}
	
	public native T someMethod();

    public static void main(String[] args) throws CloneNotSupportedException {
    		Type cppt = CPPType.getCPPType(new Object[] { MyTemplate.class, 10, String.class });
    		System.out.println("type = " + cppt);
        MyTemplate<String> t = new MyTemplate<String>(10, String.class, 4);
        System.out.println(t);
        MyTemplate<String> nt = (MyTemplate<String>) t.clone();
        System.out.println(nt);
    }
}
