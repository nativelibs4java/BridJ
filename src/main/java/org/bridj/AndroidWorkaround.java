package org.bridj;

import android.app.Application;

/**
 * Created by atsushi on 15/06/28.
 */
public class AndroidWorkaround {
    private static Application app;

    public static void setApplication(Application application) {
        app = application;
    }

    public static Application getApplication()
    {
        return app;
    }
}
