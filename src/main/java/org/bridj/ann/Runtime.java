/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridj.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bridj.BridJRuntime;

/**
* Specify the runtime that should be used to bind native methods (default is {@link org.bridj.CRuntime} if no annotation is provided).
<br>
Also see @see org.bridj.Bridj.register().
 * @author Olivier
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Runtime {
    Class //<? extends BridJRuntime>
            value();
}
