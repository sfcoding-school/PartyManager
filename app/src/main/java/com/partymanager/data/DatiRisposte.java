
package com.partymanager.data;

import android.content.Context;

import com.partymanager.helper.DataProvide;

import org.json.JSONArray;

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
        public JSONArray userList;

        public Risposta(String id, String risposta, JSONArray userList) {
            this.id = id;
            this.risposta = risposta;
            this.userList = userList;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}

