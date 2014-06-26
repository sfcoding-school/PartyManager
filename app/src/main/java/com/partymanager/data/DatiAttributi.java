

package com.partymanager.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.partymanager.data.Adapter.AttributiAdapter;
import com.partymanager.helper.DataProvide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DatiAttributi {

    private static AttributiAdapter eAdapter;
    //<<<<<<< HEAD
    //  public static ArrayList<Attributo> ITEMS = new ArrayList<Attributo>();
    private static Context context_global;
    //=======
    private static ArrayList<Attributo> ITEMS = new ArrayList<Attributo>();
    private static SparseArray<Attributo> MAP = new SparseArray<Attributo>();

//>>>>>>> agg-notifiche

    public static AttributiAdapter init(Context context, int id, int num_pers) {
        eAdapter = new AttributiAdapter(context, DatiAttributi.ITEMS, num_pers);
        DataProvide.getAttributi(context, id);
        context_global = context;
        return eAdapter;
    }

    //<<<<<<< HEAD
    public static void removeAll(boolean salva_anche, int id_evento) {
        if (salva_anche) {
            toJson(new ArrayList<Attributo>(ITEMS), id_evento);
        }
        removeAll();
    }

    //======
    public static void removeAll() {
//>>>>>>> agg-notifiche
        ITEMS.removeAll(ITEMS);
        MAP = new SparseArray<Attributo>();
        eAdapter.notifyDataSetChanged();
    }

    private static void toJson(final ArrayList<Attributo> ITEMS_temp, final int id_evento) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(Void... params) {
                JSONArray jsonArr = new JSONArray();
                try {
                    for (Attributo aITEMS_temp : ITEMS_temp) {
                        JSONObject pnObj = new JSONObject();

                        pnObj.put("chiusa", aITEMS_temp.close);
                        pnObj.put("domanda", aITEMS_temp.domanda);
                        pnObj.put("id_attributo", aITEMS_temp.id);
                        pnObj.put("id_risposta", aITEMS_temp.id_risposta);
                        pnObj.put("numd", aITEMS_temp.numd);
                        pnObj.put("numr", aITEMS_temp.numr);
                        pnObj.put("risposta", aITEMS_temp.risposta);
                        pnObj.put("template", aITEMS_temp.template);

                        jsonArr.put(pnObj);
                    }
                } catch (JSONException e) {
                    Log.e("DatiAttributi-toJson", "JSONException " + e);
                    return null;
                } catch (NullPointerException e) {
                    Log.e("DatiAttributi-toJson", "NullPointerException " + e);
                    return null;
                }
                return jsonArr;
            }

            @Override
            protected void onPostExecute(JSONArray js) {
                Log.e("DatiAttributi-toJson-onPost", js.toString());
                if (js != null && js.length() > 0) {
                    DataProvide.saveJson(js, "attributi_" + id_evento, context_global);
                } else {
                    Log.e("DatiAttributi-toJson", "Non ho salvato array vuoto");
                }
            }
        }.execute(null, null, null);
    }

    public static void addItem(Attributo item) {
        ITEMS.add(item);
        MAP.put(item.id, item);
        eAdapter.notifyDataSetChanged();
    }


    public static void removePositionItem(int pos) {
        int i = ITEMS.get(pos).id;
        ITEMS.remove(pos);
        MAP.remove(i);
        eAdapter.notifyDataSetChanged();
    }

    public static void removeIdItem(int idAttributo) {
        ITEMS.remove(MAP.get(idAttributo));
        MAP.remove(idAttributo);
        eAdapter.notifyDataSetChanged();
    }

    public static void notifyDataChange(){
        eAdapter.notifyDataSetChanged();
    }

    public static Attributo getIdItem(int idAttributo) {
        return MAP.get(idAttributo);
    }

    public static Attributo getPositionItem(int position) {
        return ITEMS.get(position);
    }

    public static String[] getTemplate() {
        String[] ris = new String[]{null, null, null};

        for (Attributo a : ITEMS) {
            if (a.template.equals("data"))
                ris[0] = a.risposta;

            if (a.template.equals("luogoE"))
                ris[1] = a.risposta;

            if (a.template.equals("luogoI"))
                ris[2] = a.risposta;
        }
        return ris;
    }

    public static class Attributo {
        public int id;
        public String domanda;
        public String risposta;
        public String template;
        public Boolean close;
        public int numd;
        public int numr;
        public String id_risposta;

        public Attributo(int id, String domanda, String risposta, String template, Boolean close, int numd, int numr, String id_risposta) {
            this.id = id;
            this.domanda = domanda;
            this.risposta = risposta;
            this.template = template;
            this.close = close;
            this.numd = numd; /* Quanti hanno risposto/votato in questo attributo */
            this.numr = numr; /* Quanti hanno risposto alla domanda più votata */
            this.id_risposta = id_risposta;
        }


        /*
        @Override
        public String toString() {
            return id;
        }
        */

        public void changeRisposta(String risposta_max, int idMax) {
            this.id_risposta = String.valueOf(idMax);
            this.risposta = risposta_max;
            eAdapter.notifyDataSetChanged();
        }
    }
}
