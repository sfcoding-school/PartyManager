package com.partymanager.data;

import android.content.Context;

import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperDataParser;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class DatiEventi {

    public static ArrayList<Evento> ITEMS = new ArrayList<Evento>();
    public static EventAdapter eAdapter;

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
    }

    public static EventAdapter init(Context context) {
        eAdapter = new EventAdapter(context, DatiEventi.ITEMS);

        DataProvide.getEvent(context);
        return eAdapter;
    }

    public static void addItem(Evento item) {
        ITEMS.add(item);
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
