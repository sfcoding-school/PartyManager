
package com.partymanager.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.partymanager.data.Adapter.RisposteAdapter;
import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperFacebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatiRisposte {

    private static RisposteAdapter eAdapter;
    private static ArrayList<Risposta> ITEMS = new ArrayList<Risposta>();
    private static Map<Integer,Risposta> MAP = new HashMap<Integer, Risposta>();
    public static String template = null;
    private static Context context_global;

    public static RisposteAdapter init(Context context, int id_evento, int id_attr, int num_pers, int id_attributo) {
        context_global = context;
        eAdapter = new RisposteAdapter(id_evento, context, DatiRisposte.ITEMS, num_pers, id_attributo);
        DataProvide.getRisposte(id_evento, id_attr, context);
        return eAdapter;
    }

    public static void removeAll(boolean salva_anche, int id_evento, int id_attributo) {
        if (salva_anche) {
            Log.e("DatiRisposte-toJson-prima di invio", ITEMS.toString() + " " + ITEMS.size());
            toJson(new ArrayList<Risposta>(ITEMS), id_evento, id_attributo);
        }
      removeAll();
    }

    public static void removeAll (){
        template = null;
        ITEMS.removeAll(ITEMS);
        MAP = new HashMap<Integer, Risposta>();
        eAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int idRisposta){
        ITEMS.remove(MAP.get(idRisposta));
        MAP.remove(idRisposta);
    }

    private static void toJson(final ArrayList<Risposta> ITEMS_temp, final int id_evento, final int id_attributo) {
        new AsyncTask<Void, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(Void... params) {
                Log.e("DatiRisposte-toJson-appenaArrivato", ITEMS_temp.toString() + " " + ITEMS_temp.size());
                JSONArray jsonArr = new JSONArray();
                try {
                    for (Risposta aITEMS_temp : ITEMS_temp) {
                        JSONObject pnObj = new JSONObject();
                        pnObj.put("id_risposta", aITEMS_temp.id);
                        pnObj.put("risposta", aITEMS_temp.risposta);
                        //pnObj.put("template", aITEMS_temp.template);

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

    public static Risposta getItemPosition(int position){
        return ITEMS.get(position);
    }

    public static void addItem(Risposta item, String template) {
        DatiRisposte.template=template;
        addItem(item);
    }
    public static int getLenght(){
        return ITEMS.size();
    }
    public static void addItem(Risposta item) {
        ITEMS.add(item);
        MAP.put(item.id,item);
        eAdapter.notifyDataSetChanged();
    }

    public static void addItem(Risposta item, boolean controllo) {
        if (controllo) cercami();
        addItem(item);
    }

    public static void addPersona (int idRisposta, String idUser, String name, boolean controllo){
        if (controllo) cercami();
        MAP.get(idRisposta).addPersona(new Persona(idUser,name));
    }

    public static void addPositionPersona (int position, String idUser, String name, boolean controllo){
        if (controllo) cercami();
        ITEMS.get(position).addPersona(new Persona(idUser,name));
    }

    private static void cercami() {
        Boolean trovato = false;
        for (int i = 0; i < DatiRisposte.ITEMS.size() && !trovato; i++) {
            for (int j = 0; DatiRisposte.ITEMS.get(i).persone != null && j < DatiRisposte.ITEMS.get(i).persone.size() && !trovato; j++) {
                if (DatiRisposte.ITEMS.get(i).persone.get(j).id_fb.equals(HelperFacebook.getFacebookId())) {
                    DatiRisposte.ITEMS.get(i).persone.remove(j);
                    trovato = true;
                }
            }
        }
    }

    public static class Risposta {
        public int id;
        public String risposta;
        public List<Persona> persone;
        //public String template;

        public Risposta(int id, String risposta, /*String template,*/ JSONArray userList) {
            this.id = id;
            this.risposta = risposta;
            this.persone = creaLista(userList);
            //this.template = template;
        }

/*
        @Override
        public String toString() {
            return id;
        }
*/
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

