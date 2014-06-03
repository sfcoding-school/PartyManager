

package com.partymanager.data;

import android.content.Context;

import com.partymanager.helper.DataProvide;

import java.util.ArrayList;


public class DatiRisposte {

    //private static RisposteAdapter eAdapter;
    public static ArrayList<Risposta> ITEMS = new ArrayList<Risposta>();

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        //eAdapter.notifyDataSetChanged();
    }

    public static void addItem(Risposta item) {
        ITEMS.add(item);
        //eAdapter.notifyDataSetChanged();
    }

    public static class Risposta {
        public String id;
        public String risposta;

        public Risposta(String id, String risposta) {
            this.id = id;
            this.risposta = risposta;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}

