package com.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Handler;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Created by Carlos VÉLEZ on 07/11/2016.
 */

public class UtilityAnimate {

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
            ViewPropertyAnimator viewPropertyAnimator = view.animate()
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
                                new Handler().postDelayed(
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
