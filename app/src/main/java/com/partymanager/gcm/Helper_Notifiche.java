package com.partymanager.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.partymanager.activity.MainActivity;
import com.partymanager.helper.HelperConnessione;

import java.io.IOException;

public class Helper_Notifiche {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final String TAG = "PM-NOTIFICHE";
    static String SENDER_ID = "924450140207";

    static String regid;

    public static void registerInBackground(final GoogleCloudMessaging gcm, final Context context, final String username) {

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                Log.i(TAG, "registerInBackground start..");
                String msg;
                try {

                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    String[] name = {"idCell", "username"};
                    String[] param = {regid, username};
                    String ris = HelperConnessione.httpPostConnection("user", name, param);
                    Log.e(TAG, "sendRegistrationIdToBackend-ris: " + ris);

                    storeRegistrationId(context, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }
        }.execute();
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

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}
