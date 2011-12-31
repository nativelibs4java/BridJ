
import java.io.IOException;
import org.bridj.BridJ;
import org.bridj.DynamicCallback;
import org.bridj.DynamicFunction;
import org.bridj.Pointer;
import org.bridj.objc.FoundationLibrary;
import org.bridj.objc.NSString;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ochafik
 */
public class Run {
    public static void main(String[] args) throws IOException {
        //Pointer<NSString> ps = FoundationLibrary.pointerToNSString("Hehe");
        //NSString s = ps.get();
        BridJ.getNativeLibrary("src/test/resources/org/bridj/lib/darwin_universal/libtest.dylib").release(); 
        /*
        Pointer dc = Pointer.allocateDynamicCallback(
            new DynamicCallback<Integer>() {

                public Integer apply(Object... args) {
                    int a = (Integer)args[0];
                    int b = (Integer)args[1];
                    return a + b;
                }
                
            }, null, int.class, int.class, int.class
        );
        DynamicFunction<Integer> df = dc.asUntyped().asDynamicFunction(null, int.class, int.class, int.class);
        int ret = df.apply(1, 2);
        */
    }
}
