/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj;

/**
 * Internal constants used in BridJ's native library.
 * @author Olivier
 */
class NativeConstants {
    /**
     * BridJ constants
     */
    enum ValueType {
        eVoidValue,
        eWCharValue,
        eCLongValue,
        eCLongObjectValue,
        eSizeTValue,
        eSizeTObjectValue,
        eIntValue,
        eShortValue,
        eByteValue,
        eBooleanValue,
        eLongValue,
        eDoubleValue,
        eFloatValue,
        ePointerValue,
        eEllipsis,
        eIntFlagSet,
        eNativeObjectValue,
        eTimeTObjectValue
    }
    
    /**
     * BridJ constants
     */
    enum CallbackType {
    	eJavaCallbackToNativeFunction,
    	eNativeToJavaCallback,
    	eJavaToNativeFunction,
    	eJavaToVirtualMethod
    }
}
