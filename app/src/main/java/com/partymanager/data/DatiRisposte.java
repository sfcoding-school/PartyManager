
package com.partymanager.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.partymanager.data.Adapter.RisposteAdapter;
import com.partymanager.helper.DataProvide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DatiRisposte {

    private static RisposteAdapter eAdapter;
    public static ArrayList<Risposta> ITEMS = new ArrayList<Risposta>();
    private static Context context_global;

    public static RisposteAdapter init(Context context, String id_evento, String id_attr, int num_pers, int arg2, boolean chiusa) {
        context_global = context;
        eAdapter = new RisposteAdapter(id_evento, context, DatiRisposte.ITEMS, num_pers, Integer.parseInt(id_attr), arg2, chiusa);
        DataProvide.getRisposte(id_evento, id_attr, context);
        return eAdapter;
    }

    public static void removeAll(Boolean salva_anche, String id_evento, String id_attributo) {
        if (salva_anche) {
            toJson(new ArrayList<Risposta>(ITEMS), id_evento, id_attributo);
        }
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
    }

    private static void toJson(final ArrayList<Risposta> ITEMS_temp, final String id_evento, final String id_attributo) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(Void... params) {
                JSONArray jsonArr = new JSONArray();
                try {
                    for (Risposta aITEMS_temp : ITEMS_temp) {
                        JSONObject pnObj = new JSONObject();
                        pnObj.put("id_risposta", aITEMS_temp.id);
                        pnObj.put("risposta", aITEMS_temp.risposta);
                        pnObj.put("template", aITEMS_temp.template);

                        JSONArray userL = new JSONArray();
                        for (int j = 0; j < aITEMS_temp.persone.size(); j++) {
                            JSONObject pers = new JSONObject();
                            pers.put("id_user", aITEMS_temp.persone.get(j).id_fb);
                            pers.put("name", aITEMS_temp.persone.get(j).nome);
                            userL.put(pers);
                        }
                        pnObj.put("userList", userL);
                        jsonArr.put(pnObj);
                    }
                } catch (JSONException e) {
                    Log.e("DatiRisposte-toJson", "JSONException " + e);
                    return null;
                } catch (NullPointerException e) {
                    Log.e("DatiRisposte-toJson", "NullPointerException " + e);
                    return null;
                }
                return jsonArr;
            }

            @Override
            protected void onPostExecute(JSONArray js) {
                Log.e("DatiRisposte-toJson-onPost", js.toString());
                if (js != null && js.length() > 0) {
                    DataProvide.saveJson(js, "risposte_" + id_evento + "_" + id_attributo, context_global);
                } else {
                    Log.e("DatiRisposte-toJson", "Non ho salvato array vuoto");
                }
            }
        }.execute(null, null, null);
    }

    public static void addItem(Risposta item) {
        ITEMS.add(item);
        eAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int pos) {
        ITEMS.remove(pos);
        eAdapter.notifyDataSetChanged();
    }

    public static void modificaRisposta(int pos, String nuova) {
        ITEMS.get(pos).risposta = nuova;
        eAdapter.notifyDataSetChanged();
    }

    public static class Risposta {
        public String id;
        public String risposta;
        public List<Persona> persone;
        public String template;

        public Risposta(String id, String risposta, String template, JSONArray userList) {
            this.id = id;
            this.risposta = risposta;
            this.persone = creaLista(userList);
            this.template = template;
        }

        @Override
        public String toString() {
            return id;
        }

        private List<Persona> creaLista(JSONArray userList) {
            List<Persona> list = new ArrayList<Persona>();
            for (int i = 0; i < userList.length(); i++) {
                try {
                    list.add(new Persona(userList.getJSONObject(i).getString("id_user"), userList.getJSONObject(i).getString("name")));
                } catch (JSONException e) {
                    Log.e("DatiRisposte-creaLista", "error creaLista " + e);
                    return new ArrayList<Persona>();
                }
            }
            return list;
        }

        public void addPersona(Persona item) {
            persone.add(item);
            eAdapter.notifyDataSetChanged();
        }
    }

    public static class Persona {
        public String id_fb;
        public String nome;

        public Persona(String id_fb, String nome) {
            this.id_fb = id_fb;
            this.nome = nome;
        }
    }
}

