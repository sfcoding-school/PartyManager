package com.partymanager.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.partymanager.data.Adapter.EventAdapter;
import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperDataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class DatiEventi {

    public static ArrayList<Evento> ITEMS = new ArrayList<Evento>();
    public static EventAdapter eAdapter;
    private static Context context_global;

    public static void removeAll(Boolean salva_anche) {
        if (salva_anche) {
            toJson(new ArrayList<Evento>(ITEMS));
        }
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
    }

    private static void toJson(final ArrayList<Evento> ITEMS_temp) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(Void... params) {
                JSONArray jsonArr = new JSONArray();
                try {
                    for (Evento aITEMS_temp : ITEMS_temp) {
                        JSONObject pnObj = new JSONObject();

                        pnObj.put("admin", aITEMS_temp.admin);
                        pnObj.put("data", aITEMS_temp.date);
                        pnObj.put("id_evento", aITEMS_temp.id);
                        pnObj.put("nome_evento", aITEMS_temp.name);
                        pnObj.put("num_utenti", aITEMS_temp.numUtenti);

                        jsonArr.put(pnObj);
                    }
                } catch (JSONException e) {
                    Log.e("DatiEventi-toJson", "JSONException " + e);
                    return null;
                } catch (NullPointerException e) {
                    Log.e("DatiEventi-toJson", "NullPointerException " + e);
                    return null;
                }
                return jsonArr;
            }

            @Override
            protected void onPostExecute(JSONArray js) {
                Log.e("DatiEventi-toJson-onPost", js.toString());
                if (js != null && js.length() > 0) {
                    DataProvide.saveJson(js, "eventi", context_global);
                } else {
                    Log.e("DatiEventi-toJson", "Non ho salvato array vuoto");
                }
            }
        }.execute(null, null, null);
    }

    public static EventAdapter init(Context context) {
        eAdapter = new EventAdapter(context, DatiEventi.ITEMS);
        context_global = context;
        DataProvide.getEvent(context);
        return eAdapter;
    }

    public static void addItem(Evento item) {
        ITEMS.add(item);
        eAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int pos){
        ITEMS.remove(pos);
        eAdapter.notifyDataSetChanged();
    }

    public static class Evento {
        public int id;
        public String name;
        public String details;
        public GregorianCalendar date;
        public String admin;
        public int numUtenti;

        public Evento(int id, String name, String details, String date, String admin, int numUtenti) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.date = HelperDataParser.getCalFromString(date);
            this.admin = admin;
            this.numUtenti = numUtenti;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
