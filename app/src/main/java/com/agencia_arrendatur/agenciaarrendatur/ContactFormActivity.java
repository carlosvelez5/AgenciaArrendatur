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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.utility.UtilityAnimate;
import com.utility.UtilityNetwork;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ContactFormActivity extends AppCompatActivity {
    boolean _continue = true;
    private LinearLayout mLinearLayoutError;
    private LinearLayout mLinearLayoutAlert;
    private TextView mTextInfo;
    private ProgressDialog mProgressDialog;
    private ScrollView mScrollView;
    private ArrayList<View> listInputError;

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

        // Cargar
        final Button buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llamar función
                sendMsgAction(view, buttonSend);
            }
        });

        // Ocultar teclado
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // comprobar si hay conexion a internet
        if (UtilityNetwork.isOnline(this)) {
            // Oculto el mensaje
            UtilityAnimate.toggleLayout(mLinearLayoutAlert, 0, false);

        } else {
            // Le Agrego el texto y lo muestro por 10 segundos
            TextView textViewAlert = (TextView) findViewById(R.id.wrap_msg_alert_text);
            textViewAlert.setText(R.string.network_offline);

            // Mostrar
            UtilityAnimate.toggleLayout(mLinearLayoutAlert, 800, true, true, 5 * 1000, 600);
        }

        // ocultar
        UtilityAnimate.toggleLayout(mLinearLayoutError, 0, false);

        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    /**
     * Valida y envia el formulario.
     *
     * @param v          Boton
     * @param buttonSend Boton
     */
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
        listInputError = new ArrayList<>();
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
                DBAdapter mDBAdapter = new DBAdapter(this);
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

                                    } else {
                                        if (type.equals("errors")) {
                                            // PHP dijo que no son validos
                                            String errors = object.getString("errors");

                                            mTextInfo.setText(msg + "\n" + errors);
                                            UtilityAnimate.toggleLayout(mLinearLayoutError, 800, true);

                                            resetValues(buttonSend, true);
                                            ConsoleLog.d("Valid form contact: errors-valid");

                                        } else if (type.equals("server")) {
                                            // No hay referencia de la url a cargar
                                            mTextInfo.setText(msg);
                                            UtilityAnimate.toggleLayout(mLinearLayoutError, 800, true, true, 10 * 1000, 500);

                                            resetValues(buttonSend, true);
                                            ConsoleLog.d("Valid form contact: errors-server");
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                // No hay referencia de la url a cargar
                                mTextInfo.setText(R.string.contact_error_network);
                                UtilityAnimate.toggleLayout(mLinearLayoutError, 800, true, true, 7 * 1000, 500);

                                resetValues(buttonSend, true);
                                ConsoleLog.d("Valid form contact: not-network, code:" + statusCode);
                            }
                        });

                    } else {
                        // No hay referencia de la url a cargar
                        mTextInfo.setText(R.string.contact_error_url);
                        UtilityAnimate.toggleLayout(mLinearLayoutError, 800, true, true, 7 * 1000, 500);

                        resetValues(buttonSend);
                        ConsoleLog.d("Valid form contact: url-null");
                    }

                } else {
                    // No hay referencia de la url a cargar
                    mTextInfo.setText(R.string.contact_error_url);
                    UtilityAnimate.toggleLayout(mLinearLayoutError, 800, true, true, 7 * 1000, 500);

                    // No hay referencia de la url a cargar
                    ConsoleLog.d("Valid form contact: url-no-exists");
                    resetValues(buttonSend);
                }

            } else {
                email.setError(getString(R.string.contact_error_email_invalid));
                email.requestFocus();
                YoYo.with(Techniques.Shake).duration(700).playOn((LinearLayout) email.getParent());

                // Errores en el correo
                resetValues(buttonSend);
                ConsoleLog.d("Valid form contact: email-invalid");
            }
        } else {
            // Recorrer y animar
            for (View aListInputError : listInputError) {
                YoYo.with(Techniques.Shake).duration(700).playOn(aListInputError);
            }

            // Obtener primer elemento y ponerle el focus
            LinearLayout mFirstElement = (LinearLayout) listInputError.get(0);
            for (int j = 0; j < mFirstElement.getChildCount(); j++) {
                if (mFirstElement.getChildAt(j) instanceof EditText) {
                    // Poner focus
                    mFirstElement.getChildAt(j).requestFocus();
                    break;
                }
            }

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

        if (closeProgress && mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.setProgress(100);
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void validText(EditText editText, String text, String msg) {
        //Implementamos la validación que queramos
        if (TextUtils.isEmpty(text)) {
            // Añadir error
            editText.setError(msg);

            // Agregar al array para luego animarlo
            listInputError.add((LinearLayout) editText.getParent());

            // No continuar
            _continue = false;
        } else {
            // Quitar mensaje
            editText.setError(null);
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
