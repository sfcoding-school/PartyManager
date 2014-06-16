package com.partymanager.data;

import android.content.Context;

import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperDataParser;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class DatiFriends {

    public static ArrayList<Friends> ITEMS = new ArrayList<Friends>();
    public static FriendsAdapter eAdapter;

    public static void removeAll() {
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
    }

    public static FriendsAdapter init(String idEvento, Context context) {
        eAdapter = new FriendsAdapter(context, DatiFriends.ITEMS);

        DataProvide.getFriends(idEvento, context);
        return eAdapter;
    }

    public static void addItem(Friends item) {
        ITEMS.add(item);
        eAdapter.notifyDataSetChanged();
    }
}