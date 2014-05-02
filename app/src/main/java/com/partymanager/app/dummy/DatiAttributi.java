

package com.partymanager.app.dummy;

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


public class DatiAttributi {


    public static ArrayList<Attributo> ITEMS = new ArrayList<Attributo>();


    static{

    }

    public static void removeAll(){
        ITEMS.removeAll(ITEMS);
    }

    public static void addItem(Attributo item) {
        ITEMS.add(item);
    }

    public static class Attributo {
        public String id;
        public String domanda;
        public String risposta;
        public String template;

        public Attributo (String id, String domanda, String risposta, String template) {
            this.id = id;
            this.domanda = domanda;
            this.risposta = risposta;
            this.template = template;
        }
        @Override
        public String toString() {
            return id;
        }
    }
}
