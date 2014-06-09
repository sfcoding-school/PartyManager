
package com.partymanager.data;

import android.content.Context;
import android.util.Log;

import com.partymanager.helper.DataProvide;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class DatiRisposte {

    private static RisposteAdapter eAdapter;
    public static ArrayList<Risposta> ITEMS = new ArrayList<Risposta>();

    public static RisposteAdapter init(Context context, String id_evento, String id_attr) {
        eAdapter = new RisposteAdapter(context, DatiRisposte.ITEMS);
        DataProvide.getRisposte(id_evento, id_attr, context);
        return eAdapter;
    }

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
    }

    public static void addItem(Risposta item) {
        ITEMS.add(item);
        eAdapter.notifyDataSetChanged();
    }

    public static class Risposta {
        public String id;
        public String risposta;
        //public List<Persona> persone;

        public Risposta(String id, String risposta, JSONArray userList) {
            this.id = id;
            this.risposta = risposta;
            //this.persone = creaLista(userList);
            Log.e("TESTRISPOSTA", id + " " + risposta);
        }

        @Override
        public String toString() {
            return id;
        }

        /*
        private List<Persona> creaLista(JSONArray userList) {
            List<Persona> temp = null;
            try {
                for (int i = 0; i < userList.length(); i++) {
                    temp.add(new Persona(userList.getJSONObject(i).getString("id_user"),
                            userList.getJSONObject(i).getString("name")));

                    Log.e("DatiRisposte-creaLista:", userList.getJSONObject(i).getString("id_user"));
                }
                return temp;
            } catch (JSONException e) {
                Log.e("DatiRisposte-creaLista:", "JSONException " + e);
                return null;
            }
        }
        */
    }

    /*
    public static class Persona {
        public String id_fb;
        public String nome;

        public Persona(String id_fb, String nome) {
            this.id_fb = id_fb;
            this.nome = nome;
        }
    }
    */
}

