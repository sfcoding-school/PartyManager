package com.partymanager.data;

import android.content.Context;

import com.partymanager.data.Adapter.FriendsAdapter;
import com.partymanager.helper.DataProvide;

import java.util.ArrayList;

public class DatiFriends {

    public static ArrayList<Friends> ITEMS = new ArrayList<Friends>();
    public static FriendsAdapter eAdapter;
    private static int idEvento;

    public static void removeAll(boolean cancella) {
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
        if (cancella)
            idEvento = -1;
    }

    public static void removeAll() {
        removeAll(false);
    }

    public static int getIdEvento() {
        return idEvento;
    }

    public static FriendsAdapter init(int idEvento, Context context) {
        eAdapter = new FriendsAdapter(context, DatiFriends.ITEMS, DatiEventi.getIdItem(idEvento).admin);
        DatiFriends.idEvento = idEvento;
        DataProvide.getFriends(idEvento, context);
        return eAdapter;
    }

    public static void addItem(Friends item, boolean notify) {
        ITEMS.add(item);
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    public static void addItem(Friends item) {
        addItem(item, true);
    }

    public static void removeItem(int i, boolean notify) {
        ITEMS.remove(i);
        if (notify)
            eAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int i) {
        removeItem(i, true);
    }
}