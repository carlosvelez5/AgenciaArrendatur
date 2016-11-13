package com.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Carlos VÉLEZ on 06/11/2016.
 * UtilityNetwork: Clase para verificar si el dispositivo tiene conexiones
 */

public class UtilityNetwork {

    /**
     * Comprueba si el usuario esta conectado a alguna conexión.
     *
     * @param context Contexto
     * @return Booelan
     */
    public static boolean isOnline(Context context) {
        //
        boolean connected = false;

        if (chekWifi(context)) {
            connected = true;
        } else if (chekMobile(context)) {
            connected = true;
        }
        return connected;
    }

    /**
     * Comprueba si hay wi-fi
     *
     * @param context Contexto
     * @return Boolean
     */
    public static boolean chekWifi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Comprueba redes moviles
     *
     * @param context Contexto
     * @return Boolean
     */
    public static boolean chekMobile(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Comprueba si la red a la que esta conectada es WIFI
     *
     * @param context Contexto
     * @return Boolean
     */
    public static boolean isConnectedWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Comprueba si el cel tiene alguna conexion a datos
     *
     * @param context Context
     * @return Boolean
     */
    public static boolean isConnectedMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Comprueba si hay alguna conexión
     * Permisos necesarios
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     *
     * @param context Contexto
     * @return boolean
     */
    public static boolean checkNetwork(Context context) {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (NetworkInfo rede : redes) {
            // Si alguna red tiene conexión, se devuelve true
            if (rede.getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }
}
