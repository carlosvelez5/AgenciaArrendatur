package com.agencia_arrendatur.agenciaarrendatur;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.json.baseclass.SettingsUrl;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.utility.ConsoleLog;
import com.utility.DBAdapter;
import com.utility.SettingsObj;
import com.utility.Utility;
import com.utility.UtilityNetwork;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ContactFormActivity extends AppCompatActivity {
    boolean _continue = true;
    private DBAdapter mDBAdapter;
    private LinearLayout mLinearLayoutError;
    private LinearLayout mLinearLayoutAlert;
    private TextView mTextInfo;
    private ProgressDialog mProgressDialog;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Iniciar inconos
        Utility.iniIcons();

        setContentView(R.layout.contact_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        // layout para mostrar los errores
        mLinearLayoutError = (LinearLayout) findViewById(R.id.wrap_msg_error);
        mLinearLayoutAlert = (LinearLayout) findViewById(R.id.wrap_msg_alert);
        mTextInfo = (TextView) findViewById(R.id.wrap_msg_error_text);
        mProgressDialog = new ProgressDialog(ContactFormActivity.this);
        mScrollView = (ScrollView) findViewById(R.id.scrollContact);

        // comprobar si hay conexion a internet
        if (UtilityNetwork.isOnline(this)) {
            // Oculto el mensaje
            Utility.toggleLayout(mLinearLayoutAlert, 0, false);
        } else {
            // Le Agrego el texto y lo muestro por 10 segundos
            TextView textViewAlert = (TextView) findViewById(R.id.wrap_msg_alert_text);
            textViewAlert.setText(R.string.network_offline);

            // Ocultar
            Utility.toggleLayout(mLinearLayoutAlert, 0, true, true, 10*1000, 800);
        }

        // ocultar
        Utility.toggleLayout(mLinearLayoutError, 0, false);

        // Cargar
        final Button buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llamar función
                sendMsgAction(view, buttonSend);
            }
        });
    }

    public void sendMsgAction(View v, final Button buttonSend) {
        // Desactivar boton
        buttonSend.setEnabled(false);

        //Ocultar teclado y subir scroll
        Utility.toogleBoard(false, v, this);
        mScrollView.scrollTo(0, mScrollView.getTop());

        // vars
        EditText nameuser = (EditText) findViewById(R.id.form_nameuser);
        EditText email = (EditText) findViewById(R.id.form_email);
        EditText subjet = (EditText) findViewById(R.id.form_subjet);
        EditText text = (EditText) findViewById(R.id.form_text);

        //Verificar datos
        validText(nameuser, nameuser.getText().toString().trim(), getString(R.string.contact_error_name));
        validText(email, email.getText().toString().trim(), getString(R.string.contact_error_email));
        validText(subjet, subjet.getText().toString().trim(), getString(R.string.contact_error_subjet));
        validText(text, text.getText().toString().trim(), getString(R.string.contact_error_text));

        if (_continue) {
            // Validar email
            _continue = Utility.isValidEmail(email.getText().toString());

            //está bien
            if (_continue) {
                // Cargar configuración de la base de datos
                mDBAdapter = new DBAdapter(this);
                mDBAdapter.open();

                // Cargar configuracion de la BD
                SettingsObj urlsSetting = mDBAdapter.fetchSettingBykey("urls");
                int count = urlsSetting.getLength();

                // Existen datos en la BD
                boolean existsData = count > 0;

                if (existsData) {
                    // existe el string con las urls
                    Gson gson = new GsonBuilder().create();
                    SettingsUrl urls = gson.fromJson(urlsSetting.getKeyValue(), SettingsUrl.class);

                    if (urls != null) {
                        // Cargar archivo
                        AsyncHttpClient client = new AsyncHttpClient();

                        // Agregar unos parámetros
                        RequestParams paramsMap = new RequestParams();

                        // Obtener la versión
                        paramsMap.put("load", "settings");
                        paramsMap.put("versionCode", BuildConfig.VERSION_CODE);
                        paramsMap.put("versionName", BuildConfig.VERSION_NAME);
                        paramsMap.put("aplicationId", BuildConfig.APPLICATION_ID);

                        // parametros
                        paramsMap.put("nameuser", nameuser.getText().toString().trim());
                        paramsMap.put("emailuser", email.getText().toString().trim());
                        paramsMap.put("subjet", subjet.getText().toString().trim());
                        paramsMap.put("comment", text.getText().toString().trim());

                        //  Url a cargar
                        String urlLoad = urls.domain + urls.app_contact;

                        // Mostrar progreso
                        mProgressDialog.setMessage(":) Nos estamos conectando...\n¡No te vallas!");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setProgress(0);
                        mProgressDialog.onStart();
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();

                        client.post(urlLoad, paramsMap, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    JSONObject object = new JSONObject(new String(responseBody).trim());

                                    // Obtener variables
                                    boolean load = object.getBoolean("load");
                                    String type = object.getString("type");
                                    String msg = object.getString("info");

                                    if (load) {
                                        // Se envío la información correctamente
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(ContactFormActivity.this);
                                        dialog.setMessage(msg);

                                        // Agregar botón
                                        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                // Volver a la activity anterior
                                                onBackPressed();
                                            }
                                        });

                                        // Mostrar
                                        dialog.create().show();

                                        resetValues(buttonSend, true);
                                        ConsoleLog.d("Valid form contact: success!");

                                    } else if (!load && type.equals("errors")) {
                                        // PHP dijo que no son validos
                                        String errors = object.getString("errors");

                                        mTextInfo.setText(msg + "\n" + errors);
                                        Utility.toggleLayout(mLinearLayoutError, 800, true);

                                        resetValues(buttonSend, true);
                                        ConsoleLog.d("Valid form contact: errors-valid");

                                    } else if (!load && type.equals("server")) {
                                        // No hay referencia de la url a cargar
                                        mTextInfo.setText(msg);
                                        Utility.toggleLayout(mLinearLayoutError, 800, true, true, 10 * 1000, 500);

                                        resetValues(buttonSend, true);
                                        ConsoleLog.d("Valid form contact: errors-server");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                // No hay referencia de la url a cargar
                                mTextInfo.setText(R.string.contact_error_network);
                                Utility.toggleLayout(mLinearLayoutError, 800, true, true, 7 * 1000, 500);

                                resetValues(buttonSend, true);
                                ConsoleLog.d("Valid form contact: not-network");
                            }
                        });

                    } else {
                        // No hay referencia de la url a cargar
                        mTextInfo.setText(R.string.contact_error_url);
                        Utility.toggleLayout(mLinearLayoutError, 800, true, true, 7 * 1000, 500);

                        resetValues(buttonSend);
                        ConsoleLog.d("Valid form contact: url-null");
                    }

                } else {
                    // No hay referencia de la url a cargar
                    mTextInfo.setText(R.string.contact_error_url);
                    Utility.toggleLayout(mLinearLayoutError, 800, true, true, 7 * 1000, 500);

                    // No hay referencia de la url a cargar
                    ConsoleLog.d("Valid form contact: url-no-exists");
                    resetValues(buttonSend);
                }

            } else {
                email.setError(getString(R.string.contact_error_email_invalid));

                // Errores en el correo
                resetValues(buttonSend);
                ConsoleLog.d("Valid form contact: email-invalid");
            }
        } else {
            // Errores en los campos
            resetValues(buttonSend);
            ConsoleLog.d("Valid form contact: inputs-emptys");
        }
    }

    private void resetValues(Button buttonSend) {
        resetValues(buttonSend, false);
    }

    private void resetValues(Button buttonSend, boolean closeProgress) {
        // Activar boton de nuevo
        buttonSend.setEnabled(true);
        _continue = true;

        if (closeProgress && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.setProgress(100);
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }
        }
    }

    public void validText(EditText editText, String text, String msg) {
        //Implementamos la validación que queramos
        if (TextUtils.isEmpty(text)) {
            editText.setError(msg);
            _continue = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
