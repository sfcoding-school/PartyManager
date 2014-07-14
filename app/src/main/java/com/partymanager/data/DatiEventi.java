package com.partymanager.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.partymanager.data.Adapter.EventAdapter;
import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperDataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

public class DatiEventi {

    private static ArrayList<Evento> ITEMS = new ArrayList<Evento>();
    private static SparseArray<Evento> MAP = new SparseArray<Evento>();
    public static EventAdapter eAdapter;
    private static Context context_global;
    private static boolean inizializzata;

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        MAP = new SparseArray<Evento>();
        eAdapter.notifyDataSetChanged();
        inizializzata = false;
    }

    public static boolean getInizializzata() {
        return inizializzata;
    }

    public static void notifyDataChange() {
        if (eAdapter != null) eAdapter.notifyDataSetChanged();
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
                        pnObj.put("data", HelperDataParser.getStringFromCal(aITEMS_temp.date));
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
                if (js != null) {
                    Log.e("DatiEventi-toJson-onPost", js.toString());
                    if (js.length() > 0) {
                        DataProvide.saveJson(js, "eventi", context_global);
                    } else {
                        Log.e("DatiEventi-toJson", "Non ho salvato array vuoto");
                    }
                }
            }
        }.execute(null, null, null);
    }

    public static void addData(int idEvento, String data) {
        MAP.get(idEvento).date = HelperDataParser.getCalFromString(data);
        eAdapter.notifyDataSetChanged();
    }

    public static void removeIdItem(int idEvento, boolean notify) {
        ITEMS.remove(MAP.get(idEvento));
        MAP.remove(idEvento);
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    public static void removeIdItem(int idEvento) {
        removeIdItem(idEvento, true);
    }

    public static void removePositionItem(int position, boolean notify) {
        int i = ITEMS.get(position).id;
        ITEMS.remove(position);
        MAP.remove(i);
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    public static void removePositionItem(int position) {
        removePositionItem(position, true);
    }

    public static Evento getIdItem(int idEvento) {
        return MAP.get(idEvento);
    }

    public static Evento getPositionItem(int position) {
        return ITEMS.get(position);
    }

    public static EventAdapter init(Context context) {
        eAdapter = new EventAdapter(context, DatiEventi.ITEMS);
        context_global = context;
        DataProvide.getEvent(context);
        return eAdapter;
    }

    public static void addItem(Evento item) {
        addItem(item, true);
    }

    public static void addItem(Evento item, boolean notify) {
        inizializzata = true;
        ITEMS.add(item);
        MAP.put(item.id, item);
        Collections.sort(ITEMS, comparator);
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    private static Comparator<Evento> comparator = new Comparator<Evento>() {
        @Override
        public int compare(Evento item1, Evento item2) {
            if (item1.date != null && item2.date != null) {
                return item1.date.compareTo(item2.date);
            } else if (item1.date != null) {
                return -1;
            } else if (item2.date != null) {
                return 1;
            } else {
                if (item1.id < item2.id)
                    return 1;
                else if (item1.id == item2.id)
                    return 0;
                else
                    return -1;
            }
        }
    };

    public static void save() {
        toJson(new ArrayList<Evento>(ITEMS));
    }

    public static void modData(int id_evento, String risposta) {
        MAP.get(id_evento).date = HelperDataParser.getCalFromString(risposta);
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
