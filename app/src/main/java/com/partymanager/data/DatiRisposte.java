
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

    public static RisposteAdapter init(Context context, String id_evento, String id_attr, int num_pers) {
        eAdapter = new RisposteAdapter(context, DatiRisposte.ITEMS, num_pers);
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
        public List<Persona> persone;
        public String template;

        public Risposta(String id, String risposta, String template,  JSONArray userList) {
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
                    Log.e("TESTRISPOSTA", "error creaLista");
                    return null;
                }
            }
            return list;
        }

        public void addPersona(Persona item){
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

