package com.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Carlos VÉLEZ on 01/11/2016.
 */

public class consoleUtility {
    AlertDialog.Builder dialog;

    public consoleUtility(String msg, Activity activity) {
        // Crear una ventana de dialogo y mostrar
        dialog = new AlertDialog.Builder(activity);

        // Establecer mensaje
        dialog.setMessage(msg);

        // Establecer boton
        setButon();
    }

    public void setButon() {
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    public void show() {
        // Mostrar dialogo
        dialog.create().show();
    }
}
