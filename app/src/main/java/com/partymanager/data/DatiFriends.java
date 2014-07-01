package com.partymanager.data;

import android.content.Context;

import com.partymanager.data.Adapter.FriendsAdapter;
import com.partymanager.helper.DataProvide;

import java.util.ArrayList;

public class DatiFriends {

    public static ArrayList<Friends> ITEMS = new ArrayList<Friends>();
    public static FriendsAdapter eAdapter;
    private static int idEvento;

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
        idEvento = -1;
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

    public static void addItem(Friends item) {
        ITEMS.add(item);
        eAdapter.notifyDataSetChanged();
    }

    public static void removeItem(int i) {
        ITEMS.remove(i);
        eAdapter.notifyDataSetChanged();
    }

    public static void notifyDataChange() {
        eAdapter.notifyDataSetChanged();
    }
}