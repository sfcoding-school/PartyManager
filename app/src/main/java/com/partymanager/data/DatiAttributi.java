

package com.partymanager.data;

import android.content.Context;

import com.partymanager.data.Adapter.AttributiAdapter;
import com.partymanager.helper.DataProvide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class DatiAttributi {

    private static AttributiAdapter eAdapter;
    private static ArrayList<Attributo> ITEMS = new ArrayList<Attributo>();
    private static Map<Integer,Attributo> MAP = new HashMap<Integer, Attributo>();

    public static AttributiAdapter init(Context context, int id, int num_pers) {
        eAdapter = new AttributiAdapter(context, DatiAttributi.ITEMS, num_pers);
        DataProvide.getAttributi(context, id);
        return eAdapter;
    }

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        MAP = new HashMap<Integer, Attributo>();
        eAdapter.notifyDataSetChanged();
    }

    public static void addItem(Attributo item) {
        ITEMS.add(item);
        MAP.put(item.id,item);
        eAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int idAttributo){
        ITEMS.remove(MAP.get(idAttributo));
        MAP.remove(idAttributo);
        eAdapter.notifyDataSetChanged();
    }

    public static Attributo getItem(int idAttributo){
        return MAP.get(idAttributo);
    }

    public static Attributo getPositionItem(int position){
        return ITEMS.get(position);
    }

    public static String[] getTemplate(){
        String[] ris = new String[]{null,null,null};

        for (Attributo a : ITEMS){
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
