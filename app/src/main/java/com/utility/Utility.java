package com.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

    /**
     * Sobre-carga del método
     *
     * @param view     View a Mostrar
     * @param duration Duración de la animación
     * @param show     Boolean, si es verdadero, se muestra, de lo contrario, se oculta el layout
     */
    public static void toggleLayout(View view, final int duration, boolean show) {
        toggleLayout(view, duration, show, false, 0, 0);
    }

    /**
     * Muestra/Oculta un View (fadeIn, fadeOut) (LinearLayout, RelativeLayout...)
     *
     * @param view         View a Mostrar
     * @param duration     Duración de la animación
     * @param show         Boolean, si es verdadero, se muestra, de lo contrario, se oculta el layout
     * @param autoHide     Boolean, Auto ocultar luego de un tiempo
     * @param delay        Int, Tiempo para ocultar
     * @param durationHide Int, Duración para ocultar
     */
    public static void toggleLayout(final View view,
                                    final int duration,
                                    boolean show,
                                    final boolean autoHide,
                                    final int delay,
                                    final int durationHide) {
        // Mostrar
        if (show) {
            view.animate()
                    //.translationY(mLinearLayoutError.getHeight())
                    .alpha(1.0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            view.setVisibility(View.VISIBLE);
                            view.setAlpha(0.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (autoHide) {
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                toggleLayout(view, durationHide, false, false, 0, 0);
                                            }
                                        },
                                        delay);
                            }
                        }
                    });
        } else {
            view.animate()
                    //.translationY(0) //.translationYBy(mLinearLayoutError.getHeight())
                    .alpha(0.0f).setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
