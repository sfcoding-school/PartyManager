package com.partymanager.data;

import android.content.Context;

import com.partymanager.data.Adapter.FriendsAdapter;
import com.partymanager.helper.DataProvide;

import java.util.ArrayList;

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

    public static void removeItem(int i) {
        ITEMS.remove(i);
        eAdapter.notifyDataSetChanged();
    }

}