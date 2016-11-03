package com.agencia_arrendatur.agenciaarrendatur;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.json.baseclass.SettingsBase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.utility.DBAdapter;
import com.utility.SettingsObj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Manejardor de la BD
    private DBAdapter mDBAdapter;

    private String jsonDefault = "{\"versionDB\":\"0\",\"urls\":{\"domain\":\"http:\\/\\/agencia-arrendatur.com\",\"web_home\":\"\\/inicio\\/\",\"web_inst\":\"\\/sobre-nosotros\\/\",\"web_news\":\"\\/publicaciones\\/\",\"web_tours\":\"\\/turismo\\/\",\"web_rental\":\"\\/inmobiliaria\\/\",\"web_contact\":\"\\/contactanos\\/\"}}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar configuración de la base de datos
        mDBAdapter = new DBAdapter(this);
        mDBAdapter.open();

        // cargar configuración
        onLoadSettings();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Cargar configuracion de la base de datos
    private void onLoadSettings() {
        // Url
        int versionDB;

        // Cargar configuracion de la BD
        SettingsObj settingVersion = mDBAdapter.fetchSettingBykey("versionDB");
        int count = settingVersion.getLength();

        // Existen datos en la BD
        final Boolean existsData = count > 0;

        if (!existsData) {
            // para forzar actualización de la BD
            versionDB = 0;
        } else {
            versionDB = parseInt(settingVersion.getKeyValue());
        }

        // Cargar archivo utilizando esta clase
        AsyncHttpClient client = new AsyncHttpClient();

        // Agregar unos parámetros
        RequestParams paramsMap = new RequestParams();

        // Obtener la versión
        paramsMap.put("load", "settings");
        paramsMap.put("versionCurrent", versionDB);
        paramsMap.put("versionCode", BuildConfig.VERSION_CODE);
        paramsMap.put("versionName", BuildConfig.VERSION_NAME);
        paramsMap.put("aplicationId", BuildConfig.APPLICATION_ID);

        // Cargar archivo por el metodo POST
        String urlLoad = "http://192.168.1.1/arrendatur/appmobile/get-config/";

        client.post(urlLoad, paramsMap, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // se cargó correctamente
                try {
                    // Crear objecto
                    JSONObject object = new JSONObject(new String(responseBody).trim());

                    // Obtener variables
                    boolean load = object.getBoolean("load");
                    String type = object.getString("type");
                    boolean updateVersion = object.getBoolean("update_version");

                    // Si hay que actualizar
                    if (load && type.equals("outdated")) {
                        // Insertar
                        insertConfig(object.getString("data"));

                        // Obtener valor
                        Log.d("appArrendatur", "Configuración: fue Actualizada!");

                    } else if (load && type.equals("updated")) {
                        // la configuracion de la app esta actualizada
                        Log.d("appArrendatur", "Configuración: Actualizada!");

                    } else {
                        // nada que hacer
                        Log.d("appArrendatur", "Configuración: nada!");
                    }

                    // Invitar al usuario a actualizar la app
                    if (updateVersion) {
                        inviteUserUpdate(object.getString("opt_update"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Si no se ha podido cargar el archivo, pero hay configuración, dejarla como este
                if (existsData) {
                    // config por defecto
                    Log.d("appArrendatur", "Configuración: Igual!");
                } else {
                    // Si no se ha podido cargar el archivo, y no hay en la bd, insertar una por default
                    insertConfig(jsonDefault);

                    // config por defecto
                    Log.d("appArrendatur", "Configuración: Default!");
                }
            }
        });
    }

    /**
     * Inserta la configuracion en la BD
     *
     * @param result String, Json para agregar a la base de datos
     */
    public void insertConfig(String result) {
        // Obtener datos
        JSONObject data;

        try {
            // Borrar todos los registros
            mDBAdapter.deleteAllSettings();

            // Covertir a objeto
            data = new JSONObject(result);

            // Recorrer objeto
            Iterator<?> keys = data.keys();

            while (keys.hasNext()) {
                // Obtener clave
                String key = (String) keys.next();
                String val = data.get(key).toString();

                // Agregar a la base de datod
                mDBAdapter.addSettings(key, val, "settings");
            }

            // Organizar menú
            setMenu(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMenu(String result) {
        // Crear objeto
        Gson gson = new GsonBuilder().create();
        SettingsBase settObj = gson.fromJson(result, SettingsBase.class);
    }

    /**
     * Invita al usuario a actualizar la app
     *
     * @param strUpdate, JSON con el título y otras opciones para el dialogo
     */
    public void inviteUserUpdate(String strUpdate) {
        try {
            final JSONObject opts = new JSONObject(strUpdate);
            final Uri _goto = Uri.parse(opts.getString("url"));

            // Crear dialogo
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

            // Agregar título
            dialog.setTitle(opts.getString("title"));
            dialog.setMessage(opts.getString("content"));

            dialog.setPositiveButton(R.string.app_update, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Mandar el usuario para el playstore
                    Intent gotoPlayStore = new Intent(Intent.ACTION_VIEW, _goto);
                    // Enviar
                    startActivity(gotoPlayStore);
                }
            });

            dialog.setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            // Mostrar dialogo
            dialog.create().show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
