package com.partymanager.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.partymanager.app.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ulisse on 29/04/14.
 */
public class Helper_Notifiche {
    /*NOTIFICHE*/
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final String TAG = "PM-NOTIFICHE";
    static String SENDER_ID = "924450140207";

    //TextView mDisplay;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;

    static String regid;
    /*NOTIFICHE*/



    /*FUNZIONI UTILI NOTIFICHE*/
    public static void registerInBackground(final GoogleCloudMessaging gcm, final Context context, final String id_facebook, final String username_facebook) {
        //final GoogleCloudMessaging gcm = new GoogleCloudMessaging();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.i(TAG, "registerInBackground start..");
                String msg = "";
                try {

                    //regid = "prova_regid";
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.

                    sendRegistrationIdToBackend(regid, id_facebook, username_facebook);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return msg;
            }


            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, (msg + "\n"));
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    private static void sendRegistrationIdToBackend(String regid, String ID_FB, String Username) {
        // TODO Auto-generated method stub
        Log.i(TAG, "sendRegistrationIdToBackend");
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://androidpartymanager.herokuapp.com/regUser");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            Log.e("regid: ", regid);
            nameValuePairs.add(new BasicNameValuePair("idCell", regid));
            nameValuePairs.add(new BasicNameValuePair("idFacebook", ID_FB));
            nameValuePairs.add(new BasicNameValuePair("username", Username));
            //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String ris = EntityUtils.toString(response.getEntity());


            Log.e(TAG, "risposta server: " + ris);

            //mDisplay.append(response.toString());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            //mDisplay.append("error");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            String error = e.toString();
            Log.i(TAG,"error "+error);
            //mDisplay.append("error");
        }
    }

    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


  /*FUNZIONI UTILI NOTIFICHE*/

}
