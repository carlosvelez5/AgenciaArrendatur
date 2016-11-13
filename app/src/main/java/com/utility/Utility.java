package com.utility;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;

import static com.joanzapata.iconify.Iconify.with;

/**
 * Created by Carlos VÉLEZ on 06/11/2016.
 * Utility Clase con algunas utilidades
 */

public class Utility {

    /**
     * Inicia los iconos
     * url: Ver: https://github.com/JoanZapata/android-iconify
     */
    public static void iniIcons() {
        // Iniciar iconos
        with(new FontAwesomeModule())
                .with(new SimpleLineIconsModule());
    }


    /**
     * Muestra u oculta el teclado virtual
     *
     * @param show Si es verdadero, lo muestra, de lo contrario lo oculta
     * @param view Campo
     */
    public static void toogleBoard(boolean show, View view, Context context) {
        // mostrar
        if (show) {
            // Mostrar
            view.requestFocus(); //Asegurar que editText tiene focus
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        } else {
            // Ocultar teclado
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * Valida si la direccion del email esta bien
     *
     * @param email String, Email a validar
     * @return Boolean
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
