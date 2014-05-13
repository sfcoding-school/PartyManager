package com.partymanager.helper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.partymanager.activity.MainActivity;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class DataProvide {


    public static void getEvent(Context context,String facebookId) {
        String token = HelperFacebook.getToken();
        loadJson("eventi", context);
        downloadEvent(facebookId, token, context);
    }

    public static void getAttributi(Context context, String eventoId) {

        loadJson("attributi_" + eventoId, context);
        downloadAttributi(eventoId, context);
    }

    private static void loadJson(final String name, final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    FileInputStream fis = context.openFileInput(name);
                    InputStreamReader inputStreamReader = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    fis.close();
                    return sb.toString();

                } catch (IOException e) {
                    String error = e.toString();
                    Log.e("DATA_PROVIDE", error);
                    return "error";
                }
            }

            @Override
            protected void onPostExecute(String json_string) {
                if (!json_string.equals("error")) {
                    if (name.equals("eventi"))
                        loadIntoEventiAdapter(json_string);
                    else
                        loadIntoAttributiAdapter(json_string);
                }
            }


        }.execute(null, null, null);
    }

    private static void saveJson(final String json_string, final String name, final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
                    fos.write(json_string.getBytes());
                    fos.close();

                } catch (IOException e) {
                    String error = e.toString();
                    Log.e("DATA_PROVIDE", error);
                }
                return null;
            }

        }.execute(null, null, null);
    }

    private static void downloadEvent(final String id, final String token, final Context context) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                MainActivity.progressBarVisible = true;
                ((Activity) context).invalidateOptionsMenu();
            }

            @Override
            protected String doInBackground(Void... params) {
                /*
                String[] name = {"idFacebook"};
                String[] param = {id};
                */
                String json_string = HelperConnessione.httpGetConnection("http://androidpartymanager.herokuapp.com/event");
                Log.e("DATA_PROVIDE", json_string);
                /*
                if (json_string.equals("fallito"))
                    if(HelperConnessione.login()){
                        json_string = HelperConnessione.httpPostConnection("http://androidpartymanager.herokuapp.com/getMyEvent", name , param);
                        Log.e("DATA_PROVIDE", json_string);
                    }
                */
                return json_string;

            }

            @Override
            protected void onPostExecute(String json_string) {

                saveJson(json_string, "eventi", context);
                loadIntoEventiAdapter(json_string);

                MainActivity.progressBarVisible = false;
                ((Activity) context).invalidateOptionsMenu();

            }
        }.execute(null, null, null);
    }

    private static void downloadAttributi(final String id, final Context context) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                MainActivity.progressBarVisible = true;
                ((Activity) context).invalidateOptionsMenu();

            }

            @Override
            protected String doInBackground(Void... params) {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://androidpartymanager.herokuapp.com/getAttributi");

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("idEvento", id));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    String json_string = EntityUtils.toString(response.getEntity());
                    //Log.e("DATA_PROVIDE", json_string);

                    return json_string;

                } catch (ClientProtocolException e) {
                    return "error";
                } catch (IOException e) {
                    String error = e.toString();
                    Log.e("DATA_PROVIDE", error);
                    return "error";
                }
            }

            @Override
            protected void onPostExecute(String json_string) {
                saveJson(json_string, "attributi_" + id, context);
                loadIntoAttributiAdapter(json_string);

                MainActivity.progressBarVisible = false;
                ((Activity) context).invalidateOptionsMenu();
            }
        }.execute(null, null, null);
    }

    private static void loadIntoEventiAdapter(String json_string) {
        DatiEventi.removeAll();
        try {
            JSONObject jsonRis = new JSONObject(json_string);
            JSONArray jsonArray = jsonRis.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                String date = jsonArray.getJSONObject(i).getString("data");
                GregorianCalendar gregCalendar = null;
                if (!date.equals("null")) {
                    gregCalendar = HelperDataParser.getCalFromString(date);
                }
                DatiEventi.addItem(new DatiEventi.Evento(
                        jsonArray.getJSONObject(i).getInt("id_evento"),
                        jsonArray.getJSONObject(i).getString("nome_evento"),
                        "content",
                        gregCalendar

                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void loadIntoAttributiAdapter(String json_string) {
        DatiAttributi.removeAll();
        //campo template: data, luogoI, luogoE
        try {
            JSONObject jsonRis = new JSONObject(json_string);
            JSONArray jsonArray = jsonRis.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                DatiAttributi.addItem(new DatiAttributi.Attributo(
                        jsonArray.getJSONObject(i).getString("id_attributo"),
                        jsonArray.getJSONObject(i).getString("domanda"),
                        jsonArray.getJSONObject(i).getString("risposta"),
                        jsonArray.getJSONObject(i).getString("template"), false
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("DEBUG ATTRIBUTI DOWNLOAD: ", "catch JSONException " + e);
        }
    }
}
