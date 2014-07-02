package com.partymanager.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.activity.fragment.Evento;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DatiFriends;
import com.partymanager.data.DatiRisposte;
import com.partymanager.data.Friends;

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

    public static void getAttributi(Context context, int eventoId) {
        loadJson("attributi_" + eventoId, context);
        downloadAttributi(eventoId, context);
    }

    public static void getRisposte(int id_evento, int id_attr, Context context) {
        loadJson("risposte_" + id_evento + "_" + id_attr, context);
        downloadRisposte(id_evento, id_attr, context);
    }

    public static void getFriends(int idEvento, Context context) {
        loadJson("friends" + idEvento, context);
        downloadFriends(idEvento, context);
    }

    // <editor-fold defaultstate="collapsed" desc="download...">
    private static void downloadFriends(final int idEvento, final Context context) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(Void... params) {
                String json_string = HelperConnessione.httpGetConnection("friends/" + idEvento);
                return stringToJsonArray("user", json_string);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {

                if (jsonArray != null) {
                    saveJson(jsonArray, "friends" + idEvento, context);
                    loadIntoFriendsAdapter(jsonArray);
                }
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
                String json_string = HelperConnessione.httpGetConnection("event");
                if (json_string.equals("serverOffline") || json_string.equals("connessioneAssente")) {
                    JSONArray a = new JSONArray();
                    JSONObject b = new JSONObject();
                    try {
                        b.put("error", json_string);
                        a.put(b);
                        return a;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return stringToJsonArray("event", json_string);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {

                MainActivity.progressBarVisible = false;
                ((Activity) context).invalidateOptionsMenu();

                if (jsonArray != null) {
                    try {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        if (jsonArray.getJSONObject(0).getString("error").equals("serverOffline")) {
                            alertDialogBuilder.setMessage(context.getString(R.string.serverOffline));
                        } else {
                            alertDialogBuilder.setMessage(context.getString(R.string.connAssente));
                        }
                        alertDialogBuilder.setPositiveButton(context.getString(R.string.chiudi), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } catch (JSONException e) {
                        saveJson(jsonArray, "eventi", context);
                        loadIntoEventiAdapter(jsonArray);
                    }
                }
            }
        }.execute(null, null, null);
    }

    private static void downloadAttributi(final int id, final Context context) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected void onPreExecute() {
                Evento.progressBar = true;
            }

            @Override
            protected JSONArray doInBackground(Void... params) {
                String jsonString = HelperConnessione.httpGetConnection("event/" + id);
                return stringToJsonArray("event/" + id, jsonString);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {

                if (jsonArray != null) {
                    saveJson(jsonArray, "attributi_" + id, context);
                    loadIntoAttributiAdapter(jsonArray);
                }

                Evento.progressBar = false;
                ((Activity) context).invalidateOptionsMenu();
                Evento.checkTemplate();
            }
        }.execute(null, null, null);
    }


    private static void downloadRisposte(final int id_evento, final int id_attr, final Context context) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(Void... params) {
                String jsonString = HelperConnessione.httpGetConnection("event/" + id_evento + "/" + id_attr);
                return stringToJsonArray("event/" + id_evento + "/" + id_attr, jsonString);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                if (jsonArray != null) {
                    saveJson(jsonArray, "risposte_" + id_evento + "_" + id_attr, context);
                    loadIntoRisposteAdapter(jsonArray);
                }
            }
        }.execute(null, null, null);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="loadInto...Adapter">
    private static void loadIntoFriendsAdapter(JSONArray jsonArray) {
        DatiFriends.removeAll();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                DatiFriends.addItem(new Friends(jsonArray.getJSONObject(i).getString("id_user"),
                                jsonArray.getJSONObject(i).getString("name"), false, false)
                );
            }
        } catch (JSONException e) {
            Log.e("DataProvide", "JSONException loadIntoEventiAdapter: " + e);
        } catch (NullPointerException e) {
            Log.e("DataProvide", "NullPointerException loadIntoEventiAdapter: " + e);
        }
    }

    private static void loadIntoEventiAdapter(JSONArray jsonArray) {
        DatiEventi.removeAll();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                DatiEventi.addItem(new DatiEventi.Evento(
                        jsonArray.getJSONObject(i).getInt("id_evento"),
                        jsonArray.getJSONObject(i).getString("nome_evento"),
                        "content",
                        jsonArray.getJSONObject(i).optString("data"), /*Returns the value mapped by name if it exists, coercing it if necessary. Returns the empty string if no such mapping exists.*/
                        jsonArray.getJSONObject(i).getString("admin"),
                        jsonArray.getJSONObject(i).getInt("num_utenti")
                ));
            }
        } catch (JSONException e) {
            Log.e("DataProvide", "JSONException loadIntoEventiAdapter: " + e);
        } catch (NullPointerException e) {
            Log.e("DataProvide", "NullPointerException loadIntoEventiAdapter: " + e);
        }
    }

    private static void loadIntoAttributiAdapter(JSONArray jsonArray) {
        DatiAttributi.removeAll();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                DatiAttributi.addItem(new DatiAttributi.Attributo(
                        jsonArray.getJSONObject(i).getInt("id_attributo"),
                        jsonArray.getJSONObject(i).getString("domanda"),
                        (jsonArray.getJSONObject(i).getString("risposta").equals("null")) ? "" : jsonArray.getJSONObject(i).getString("risposta"),
                        jsonArray.getJSONObject(i).getString("template"),
                        Boolean.valueOf(jsonArray.getJSONObject(i).getString("chiusa")),
                        (jsonArray.getJSONObject(i).getString("numd").equals("null")) ? -1 : jsonArray.getJSONObject(i).getInt("numd"),
                        (jsonArray.getJSONObject(i).getString("numr").equals("null")) ? -1 : jsonArray.getJSONObject(i).getInt("numr"),
                        jsonArray.getJSONObject(i).getString("id_risposta")
                ));
            }
            Evento.checkTemplate();
        } catch (JSONException e) {
            Log.e("DataProvide", "JSONException loadIntoAttributiAdapter: " + e);
        } catch (NullPointerException e) {
            Log.e("DataProvide", "NullPointerException loadIntoAttributiAdapter: " + e);
        }
    }

    private static void loadIntoRisposteAdapter(JSONArray jsonArray) {
        DatiRisposte.removeAll();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                DatiRisposte.addItem(new DatiRisposte.Risposta(
                                jsonArray.getJSONObject(i).getInt("id_risposta"),
                                jsonArray.getJSONObject(i).getString("risposta"),
                                jsonArray.getJSONObject(i).getJSONArray("userList")
                        ), jsonArray.getJSONObject(i).optString("template"), false
                );
            }

            DatiRisposte.ordina();
        } catch (JSONException e) {
            Log.e("DataProvide", "JSONException loadIntoRisposteAdapter: " + e);
        } catch (NullPointerException e) {
            Log.e("DataProvide", "NullPointerException loadIntoRisposteAdapter: " + e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodi JSON">
    private static void loadJson(final String name, final Context context) {
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params) {

                return loadJsonFromFile(name, context);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                if (jsonArray != null) {
                    if (name.equals("eventi"))
                        loadIntoEventiAdapter(jsonArray);
                    if (name.contains("attributi"))
                        loadIntoAttributiAdapter(jsonArray);
                    if (name.contains("risposte"))
                        loadIntoRisposteAdapter(jsonArray);
                    if (name.contains("friends"))
                        loadIntoFriendsAdapter(jsonArray);
                }
            }
        }.execute(null, null, null);
    }

    public static void saveJson(final JSONArray jsonArray, final String name, final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                saveJsonToFile(jsonArray, name, context);
                return null;
            }

        }.execute(null, null, null);
    }

    public static void addElementJson(final JSONObject element, final String jsonName, final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONArray jsonArray = loadJsonFromFile(jsonName, context);
                    if (jsonArray != null) {
                        jsonArray = jsonArray.put(element);
                        saveJsonToFile(jsonArray, jsonName, context);
                    } else {
                        Log.e("DataProvide-addElementJson: ", "jsonArray == null");
                    }
                    return null;
                } catch (NullPointerException e) {
                    Log.e("DataProvide-addElementJson: ", "NullPointerException " + jsonName + " " + e);
                    return null;
                }
            }
        }.execute(null, null, null);
    }

    private static synchronized JSONArray loadJsonFromFile(String fileName, Context context) {
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
            return stringToJsonArrayBefore(sb.toString());

        } catch (IOException e) {
            Log.e("DATA_PROVIDE-loadJsonFromFile ", fileName + " " + e.toString());
        }
        return null;
    }

    private static synchronized void saveJsonToFile(JSONArray jsonArray, String fileName, Context context) {
        try {
            String jsonString = jsonArray.toString();
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("DataProvide", "IOException saveJsonToFile: " + fileName + " " + e);
        } catch (NullPointerException e) {
            Log.e("DataProvide", "NullPointerException saveJsonToFile: " + fileName + " " + e);
        }
    }

    private static JSONArray stringToJsonArray(String fileName, String jsonString) {

        try {
            JSONObject json_data = new JSONObject(jsonString);
            String status = json_data.getString("results");
            return new JSONArray(status);
        } catch (JSONException e) {
            Log.e("DataProvide-stringToJsonArray", "JSONException " + fileName + " " + e);
            return null;
        }

    }

    private static JSONArray stringToJsonArrayBefore(String jsonString) {
        try {
            return new JSONArray(jsonString);
        } catch (JSONException e) {
            Log.e("DataProvide-stringToJsonArrayBefore", "JSONException " + e);
            return null;
        }
    }
    // </editor-fold>
}
