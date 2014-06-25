package com.partymanager.data;

import android.content.Context;

import com.partymanager.data.Adapter.EventAdapter;
import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperDataParser;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatiEventi{

    private static ArrayList<Evento> ITEMS = new ArrayList<Evento>();
    private static Map<Integer,Evento> MAP  = new HashMap<Integer, Evento>();
    public static EventAdapter eAdapter;

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        MAP = new HashMap<Integer, Evento>();
        eAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int idEvento){
        ITEMS.remove(MAP.get(idEvento));
        MAP.remove(idEvento);
        eAdapter.notifyDataSetChanged();
    }

    public static void removePositionItem(int position){
        ITEMS.remove(position);
        eAdapter.notifyDataSetChanged();
    }

    public static Evento getItem(int idEvento){
        return MAP.get(idEvento);
    }

    public static Evento getPositionItem(int position){
        return ITEMS.get(position);
    }

    public static EventAdapter init(Context context) {
        eAdapter = new EventAdapter(context, DatiEventi.ITEMS);

        DataProvide.getEvent(context);
        return eAdapter;
    }

    public static void addItem(Evento item) {
        ITEMS.add(item);
        MAP.put(item.id,item);
        eAdapter.notifyDataSetChanged();
    }

    public static class Evento {
        public int id;
        public String name;
        public String details;
        public GregorianCalendar date;
        public String admin;
        public int numUtenti;

        public Evento(int id, String name, String details, String date, String admin, int numUtenti) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.date = HelperDataParser.getCalFromString(date);
            this.admin = admin;
            this.numUtenti = numUtenti;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
