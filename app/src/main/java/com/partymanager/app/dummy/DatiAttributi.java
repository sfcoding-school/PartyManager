

package com.partymanager.app.dummy;

import android.content.Context;

import java.util.ArrayList;


public class DatiAttributi {

    private static AttributiAdapter eAdapter;
    public static ArrayList<Attributo> ITEMS = new ArrayList<Attributo>();

    public static AttributiAdapter init(Context context, String id) {
        eAdapter = new AttributiAdapter(context, DatiAttributi.ITEMS);
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

        public Attributo(String id, String domanda, String risposta, String template, Boolean close) {
            this.id = id;
            this.domanda = domanda;
            this.risposta = risposta;
            this.template = template;
            this.close = close;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
