package com.partymanager.app.dummy;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.partymanager.app.EventiListFragment;
import com.partymanager.app.MainActivity;

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
import java.util.List;

/**
 * Created by luca on 4/26/14.
 */
public class DataProvide {



    public static void getEvent (Context context){

        loadJson("eventi", context);
        downloadEvent(20,context);
    }


    private static void loadJson(final String name,final Context context){
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
                    //Log.i(TAG,"error "+error);
                    //mDisplay.append("error");

                    return "error";
                }
            }

            @Override
            protected void onPostExecute(String json_string) {
                if (json_string!="error") {
                    loadIntoEventiAdapter(json_string);
                }
            }


        }.execute(null, null, null);
    }

    private static void saveJson(final String json_string, final String name, final Context context){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
                    fos.write(json_string.getBytes());
                    fos.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    String error = e.toString();
                    //Log.i(TAG,"error "+error);
                    //mDisplay.append("error");
                }
                return null;
            }

        }.execute(null, null, null);
    }

    private static void downloadEvent(final int id,final Context context ) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                MainActivity.progressBarVisible = true;
                ((Activity) context).invalidateOptionsMenu();

            }

            @Override
            protected String doInBackground(Void... params) {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://androidpartymanager.herokuapp.com/getMyEvent");

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));
                    //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    String json_string = EntityUtils.toString(response.getEntity());


                    //JSONObject myObject = new JSONObject(response);
                    //Log.i("DATI EVENTI", "risposta... " + response.toString());


                    return json_string;

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    //mDisplay.append("error");
                    return "error";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    String error = e.toString();
                    //Log.i(TAG,"error "+error);
                    //mDisplay.append("error");
                    return "error";
                }
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

    private static void loadIntoEventiAdapter(String json_string){
        DatiEventi.removeAll();
        try {
            JSONObject jsonRis = new JSONObject(json_string);
            JSONArray jsonArray= jsonRis.getJSONArray("results");
            for (int i=0;i<jsonArray.length();i++){
                DatiEventi.addItem(new DatiEventi.Evento(String.valueOf(i),jsonArray.getJSONObject(i).getString("event")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
