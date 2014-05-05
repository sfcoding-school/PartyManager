package com.partymanager.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.partymanager.app.Friends;
import com.partymanager.app.ProfileActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by luca on 5/1/14.
 */
public class helperFacebook {

    private static String facebookId;

    public static String getFacebookId(Context context){
        if (facebookId != null)
            return facebookId;
        else{
            SharedPreferences prefs = context.getSharedPreferences(ProfileActivity.class.getSimpleName(),context.MODE_PRIVATE);
            String id = prefs.getString("reg_id","");
            if (id.isEmpty()) {
                Log.e("HELPER_FACEBOOK", "id facebook not found.");
                return null;
            }else{
                facebookId = id;
                return facebookId;
            }
        }
    }


    public static void inviteFriends(final Context context, String friendsTo){

        Bundle parameters = new Bundle();
        parameters.putString("to", friendsTo);
        parameters.putString( "message", "Use my app!");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(context,
                        Session.getActiveSession(),
                        parameters))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(context,
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                Toast.makeText(context,
                                        "Request sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }).build();
        requestsDialog.show();
    }

}

