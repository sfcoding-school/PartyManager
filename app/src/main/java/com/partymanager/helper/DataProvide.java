package com.partymanager.helper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.partymanager.activity.MainActivity;
import com.partymanager.activity.fragment.Evento;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataProvide {


    public static void getEvent(Context context) {
        loadJson("eventi", context);
        downloadEvent(context);
    }

    public static void getAttributi(Context context, String eventoId) {

        loadJson("attributi_" + eventoId, context);
        downloadAttributi(eventoId, context);
    }

    private static void loadJson(final String name, final Context context) {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params) {

                return loadJsonFromFile(name,context);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                if (jsonArray!=null) {
                    if (name.equals("eventi"))
                        loadIntoEventiAdapter(jsonArray);
                    else
                        loadIntoAttributiAdapter(jsonArray);
                }
            }


        }.execute(null, null, null);
    }

    private static void saveJson(final JSONArray jsonArray, final String name, final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                saveJsonToFile(jsonArray, name, context);
                return null;
            }

        }.execute(null, null, null);
    }

    private static void downloadEvent(final Context context) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected void onPreExecute() {
                MainActivity.progressBarVisible = true;
                ((Activity) context).invalidateOptionsMenu();
            }

            @Override
            protected JSONArray doInBackground(Void... params) {
                String json_string = HelperConnessione.httpGetConnection("http://androidpartymanager.herokuapp.com/event");

                Log.e("DATA_PROVIDE", json_string);

                return stringToJsonArray(json_string);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {

                saveJson(jsonArray, "eventi", context);
                loadIntoEventiAdapter(jsonArray);

                MainActivity.progressBarVisible = false;
                ((Activity) context).invalidateOptionsMenu();

            }
        }.execute(null, null, null);
    }

    private static void downloadAttributi(final String id, final Context context) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected void onPreExecute() {
                MainActivity.progressBarVisible = true;
                ((Activity) context).invalidateOptionsMenu();

            }

            @Override
            protected JSONArray doInBackground(Void... params) {
                String jsonString = HelperConnessione.httpGetConnection("http://androidpartymanager.herokuapp.com/event/" + id);
                return stringToJsonArray(jsonString);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                saveJson(jsonArray, "attributi_" + id, context);
                loadIntoAttributiAdapter(jsonArray);

                MainActivity.progressBarVisible = false;
                ((Activity) context).invalidateOptionsMenu();

                Evento.checkTemplate();
            }
        }.execute(null, null, null);
    }

    private static void loadIntoEventiAdapter(JSONArray jsonArray) {
        DatiEventi.removeAll();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                DatiEventi.addItem(new DatiEventi.Evento(
                        jsonArray.getJSONObject(i).getInt("id_evento"),
                        jsonArray.getJSONObject(i).getString("nome_evento"),
                        "content",
                        jsonArray.getJSONObject(i).getString("data"),
                        jsonArray.getJSONObject(i).getString("admin"),
                        jsonArray.getJSONObject(i).getInt("num_utenti")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }
    }

    private static void loadIntoAttributiAdapter(JSONArray jsonArray) {
        DatiAttributi.removeAll();
        //campo template: data, luogoI, luogoE
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                DatiAttributi.addItem(new DatiAttributi.Attributo(
                        jsonArray.getJSONObject(i).getString("id_attributo"),
                        jsonArray.getJSONObject(i).getString("domanda"),
                        jsonArray.getJSONObject(i).getString("risposta"),
                        jsonArray.getJSONObject(i).getString("template"),
                        Boolean.valueOf(jsonArray.getJSONObject(i).getString("chiusa")),
                        jsonArray.getJSONObject(i).getInt("numd"),
                        jsonArray.getJSONObject(i).getInt("numr")
                ));
            }
            Evento.checkTemplate();
        } catch (JSONException e) {
            Log.e("DEBUG ATTRIBUTI DOWNLOAD: ", "catch JSONException " + e);
        } catch (NullPointerException e){

        }
    }


    public static void addElementJson (final JSONObject element, final String jsonName, final Context context){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                JSONArray jsonArray = loadJsonFromFile(jsonName,context);
                jsonArray = jsonArray.put(element);
                saveJsonToFile(jsonArray, jsonName,context);
                return null;
            }

        }.execute(null, null, null);
    }

    private static synchronized JSONArray loadJsonFromFile(String fileName, Context context){
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            return stringToJsonArray(sb.toString());

        } catch (IOException e) {
            String error = e.toString();
            Log.e("DATA_PROVIDE", error);
            return null;
        }
    }

    private static synchronized void saveJsonToFile (JSONArray jsonArray, String fileName, Context context){
        try {
            String jsonString = jsonArray.toString();
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (IOException e) {
            String error = e.toString();
            Log.e("DATA_PROVIDE", error);
        } catch (NullPointerException e){

        }
    }

    private static JSONArray stringToJsonArray (String jsonString){
        try{
            JSONObject jsonRis = new JSONObject(jsonString);
            return jsonRis.getJSONArray("results");
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }
}
