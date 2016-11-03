package com.agencia_arrendatur.agenciaarrendatur;

import android.os.Environment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Carlos VÉLEZ on 01/11/2016.
 * <p>
 * Para activar esta clase para versiones despues del sdk 23 hay que copiar esta línea en Gradle
 * android {
 * useLibrary 'org.apache.http.legacy'
 * }
 * <p>
 * Para versiones anteriores al sdk 23, estas
 * dependencies {
 * compile group: 'org.apache.httpcomponents' , name: 'httpclient-android' , version: '4.3.5.1'
 * }
 */

public class LoadJson {
    //CLASE PARA RECOJER EL CODIGO EN BRUTO DEL JSON
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    //CONSTRUCTOR
    public LoadJson() {

    }

    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    //METODO PARA ESTABLECER CONEXI�N
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {
        try {
            //HTTP CLIENT
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // AÑADIMOS PARAMETROS AL METODO POST
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // AÑADIMOS PARAMETROS AL METODO GET
                if (params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
                //METODO GET
                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

            //EXCEPCIONES
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //DEVOLVEMOS RESPUESTA
        return response;
    }

    /**
     * Carga un archivo URL, y devuelve el objeto cargado.
     *
     * @param urlLoad
     * @param method
     * @param params  HashMap<String, String> hash = new HashMap<>();
     * @return
     */
    public String loadFile(String urlLoad, int method, HashMap<String, String> params) {
        // Respuesta
        String response = "";

        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(urlLoad);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setRequestMethod(method == GET ? "GET" : "POST");
            urlConnection.setDoOutput(true);

            // Dos minutos
            urlConnection.setConnectTimeout(2 * 60 * 1000);

            // Agregar parámetros
            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (method == POST) {
                params.put("load_from", "android");

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));

                writer.flush();
                writer.close();
                os.close();
            } else {
                //por último establecemos nuestra conexión y cruzamos los dedos
                urlConnection.connect();
            }

            // Obtener código de carga
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }


            /*//params.add(new BasicNameValuePair("secondParam", paramValue2));
            //params.add(new BasicNameValuePair("thirdParam", paramValue3));
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write( getQuery(params ) );
            writer.flush();     writer.close();     os.close();*/

            //por último establecemos nuestra conexión y cruzamos los dedos
            // urlConnection.connect();

            //y gestionamos errores
        } catch (MalformedURLException e) {
            // Url mal formateada
            e.printStackTrace();

        } catch (IOException e) {
            // openConnection() failed
            e.printStackTrace();
        }

        return response;
    }

    /**
     *
     * @param urlLoad
     * @param method
     * @return
     */
    public String loadFile(String urlLoad, int method){
        HashMap<String, String> hash = new HashMap<>();
        return loadFile(urlLoad, method, hash);
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * Carga un archivo y lo guarda en la SDCARD
     * <p>
     * Permisos necesarios
     * android.permission.WRITE_EXTERNAL_STORAGE
     *
     * @param urlLoad String
     * @return Boolean
     */
    public Boolean getAndWrite(String urlLoad) {
        // resultado efectivo
        Boolean result = true;

        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(urlLoad);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //por último establecemos nuestra conexión y cruzamos los dedos
            urlConnection.connect();

            //vamos a establecer la ruta de destino para nuestra descarga
            //para hacerlo sencillo en este ejemplo he decidido descargar en
            //la raíz de la tarjeta SD
            File SDCardRoot = Environment.getExternalStorageDirectory();

            //vamos a crear un objeto del tipo de fichero
            //donde descargaremos nuestro fichero, debemos darle el nombre que
            //queramos, si quisieramos hacer esto mas completo
            //cogeríamos el nombre del origen
            File file = new File(SDCardRoot, "ejemplo.txt");

            //utilizaremos un objeto del tipo fileoutputstream
            //para escribir el archivo que descargamos en el nuevo
            FileOutputStream fileOutput = new FileOutputStream(file);

            //leemos los datos desde la url
            InputStream inputStream = urlConnection.getInputStream();

            //obtendremos el tamaño del archivo y lo asociaremos a una
            //variable de tipo entero
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;

            //creamos un buffer y una variable para ir almacenando el
            //tamaño temporal de este
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            //ahora iremos recorriendo el buffer para escribir el archivo de destino
            //siempre teniendo constancia de la cantidad descargada y el total del tamaño
            //con esto podremos crear una barra de progreso
            while ((bufferLength = inputStream.read(buffer)) > 0) {

                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                //podríamos utilizar una función para ir actualizando el progreso de lo
                //descargado
                //actualizaProgreso(downloadedSize, totalSize);

            }
            //cerramos
            fileOutput.close();

            //y gestionamos errores
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }
}
