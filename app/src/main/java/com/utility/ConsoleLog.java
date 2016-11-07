package com.utility;

import android.util.Log;

/**
 * Created by Carlos VÉLEZ on 06/11/2016.
 */

public class ConsoleLog {
    // Tag para filtrar en la consola
    public static String tag = "appArrendatur";

    // Contructor
    public ConsoleLog() {

    }

    public void setTag(String tag) {
        ConsoleLog.tag = tag;
    }

    /**
     * Muestra el mensaje en la consola
     *
     * @param text Texto a mostrar
     */
    public static void d(String text) {
        Log.d(tag, text);
    }
}
