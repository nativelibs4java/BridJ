/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.objc;

import java.util.HashMap;
import org.bridj.Pointer;
import java.util.Map;
import org.bridj.BridJ;
import static org.bridj.objc.FoundationLibrary.*;
import static org.bridj.Pointer.*;

/**
 *
 * @author ochafik
 */
public class NSDictionary extends NSObject {
    static {
        BridJ.register();
    }
    public NSDictionary() {
        super();
    }
//    public NSDictionary(Map<String, NSObject> map) {
//        super(pointerToNSDictionary(map));
//    }
    
    public native Pointer<NSObject> valueForKey(Pointer<NSString> key);
    public native Pointer<NSObject> objectForKey(Pointer<NSObject> key);
    public native int count();
    
    public native void getObjects_andKeys(Pointer<Pointer<NSObject>> objects, Pointer<Pointer<NSObject>> keys);
    public static native Pointer<NSDictionary> dictionaryWithContentsOfFile(Pointer<NSString> path);
    
    public static native Pointer<NSDictionary> dictionaryWithObjects_forKeys_count(Pointer<Pointer<NSObject>> objects, Pointer<Pointer<NSObject>> keys, int count);
    
    public static Pointer<NSDictionary> pointerToNSDictionary(Map<String, NSObject> map) {
        int n = map.size();
        Pointer<Pointer<NSObject>> objects = allocatePointers(NSObject.class, n);
        Pointer<Pointer<NSObject>> keys = allocatePointers(NSObject.class, n);
        
        int i = 0;
        for (Map.Entry<String, NSObject> e : map.entrySet()) {
            keys.set(i, (Pointer)pointerToNSString(e.getKey()));
            objects.set(i, pointerTo(e.getValue()));
            i++;
        }
        
        return dictionaryWithObjects_forKeys_count(objects, keys, n);
    }
    public static NSDictionary valueOf(Map<String, NSObject> map) {
    		return pointerToNSDictionary(map).get();
    }
    public Map<String, NSObject> toMap() {
        int n = count();
        Pointer<Pointer<NSObject>> objects = allocatePointers(NSObject.class, n);
        Pointer<Pointer<NSString>> keys = allocatePointers(NSString.class, n);
        
        getObjects_andKeys(objects, (Pointer)keys);
        
        Map<String, NSObject> ret = new HashMap<String, NSObject>();
        for (int i = 0; i < n; i++) {
            Pointer<NSString> key = keys.get(i);
            Pointer<NSObject> value = objects.get(i);
            
            ret.put(key.get().toString(), value == null ? null : value.get());
        }
        return ret;
    }
}
