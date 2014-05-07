package com.partymanager.app.dummy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.partymanager.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class DatiEventi {

    /**
     * An array of sample (dummy) items.
     */
    public static ArrayList<Evento> ITEMS = new ArrayList<Evento>();
    public static EventAdapter eAdapter;
    /**
     * A map of sample (dummy) items, by ID.
     */
    //public static Map<String, Evento> ITEM_MAP = new HashMap<String, Evento>();


    static {

        /*
        // Add 3 sample items.
        addItem(new Evento("1", "Item 1"));
        addItem(new Evento("2", "Item 2"));
        addItem(new Evento("3", "Item 3"));
        */
    }
    public static void removeAll(){
        ITEMS.removeAll(ITEMS);
        eAdapter.notifyDataSetChanged();
    }

    public static EventAdapter init(Context context, String id) {
        eAdapter = new EventAdapter(context, DatiEventi.ITEMS);

        DataProvide.getEvent(context, id);
        return eAdapter;
    }

    public static void addItem(Evento item) {
        ITEMS.add(item);
        eAdapter.notifyDataSetChanged();
        //ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Evento {
        public int id;
        public String name;
        public String details;
        public GregorianCalendar date;

        public Evento (int id, String name, String details, GregorianCalendar date) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.date = date;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
