

package com.partymanager.data;

import android.content.Context;

import com.partymanager.data.Adapter.AttributiAdapter;
import com.partymanager.helper.DataProvide;

import java.util.ArrayList;


public class DatiAttributi {

    private static AttributiAdapter eAdapter;
    public static ArrayList<Attributo> ITEMS = new ArrayList<Attributo>();

    public static AttributiAdapter init(Context context, String id, int num_pers) {
        eAdapter = new AttributiAdapter(context, DatiAttributi.ITEMS, num_pers);
        DataProvide.getAttributi(context, id);
        return eAdapter;
    }

    static {

    }

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
    }

    public static void addItem(Attributo item) {
        ITEMS.add(item);
        eAdapter.notifyDataSetChanged();
    }

    public static class Attributo {
        public String id;
        public String domanda;
        public String risposta;
        public String template;
        public Boolean close;
        public int numd;
        public int numr;

        public Attributo(String id, String domanda, String risposta, String template, Boolean close, int numd, int numr) {
            this.id = id;
            this.domanda = domanda;
            this.risposta = risposta;
            this.template = template;
            this.close = close;
            this.numd = numd; /* Quanti hanno risposto/votato in questo attributo */
            this.numr = numr; /* Quanti hanno risposto alla domanda più votata */
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
