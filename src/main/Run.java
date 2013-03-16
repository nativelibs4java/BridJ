
import org.bridj.BridJ;
import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import org.bridj.objc.NSObject;
import org.bridj.objc.ObjCBlock;

/**
 *
 * @author ochafik
 */
public class Run {
    public static class Foundation {
        public static class NSEvent extends NSObject {
           //@Selector("addLocalMonitorForEventsMatchingMask:handler:")
           public static native Pointer addGlobalMonitorForEventsMatchingMask_handler(long mask, Pointer<NSEventGlobalCallback> handler);
        }

        public abstract static class NSEventGlobalCallback extends ObjCBlock {
            public abstract void callback(Pointer<NSEvent> event);
        }
    }

    public static void main(String[] args) throws Exception {
        BridJ.register(Foundation.NSEvent.class);

        final boolean called[] = new boolean[1];
        Foundation.NSEventGlobalCallback handler = new Foundation.NSEventGlobalCallback() {
            @Override
            public void callback(Pointer<Foundation.NSEvent> event) {
                System.out.println("Event: " + event);
                called[0] = true;
            }
        };

        //System.out.println("handler: " + handler);

        Pointer hook = Foundation.NSEvent.addGlobalMonitorForEventsMatchingMask_handler(-1L/*1 << 1*/, pointerTo(handler));

        //System.out.println("hook: " + hook);

        Thread.sleep(10000);
        
        System.out.println("Called : " + called[0]);
        System.in.read();
    }
}
