package com.partymanager.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.partymanager.activity.MainActivity;
import com.partymanager.activity.ProfileActivity;
import com.partymanager.gcm.Helper_Notifiche;

/**
 * Created by luca on 5/1/14.
 */
public class HelperFacebook {

    private static String facebookId;
    private static Activity activity = MainActivity.getActivity();

    public static String getFacebookId(){
        if (facebookId != null)
            return facebookId;
        else{
            SharedPreferences prefs = activity.getSharedPreferences(ProfileActivity.class.getSimpleName(),activity.MODE_PRIVATE);
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

    private static Session session = null;

    public static Session getSession(Activity activity){
        if (session == null){
            session = Session.getActiveSession();
            if (session == null) {
                session = new Session(activity);
                Session.setActiveSession(session);
                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                    session.openForRead(new Session.OpenRequest(activity));
                }
            }
        }
        return session;
    }

    public static String getToken(){
        Session session = getSession(activity);
        String token = session.getAccessToken();
        Log.e("TOKEN - getToken", token);
        return token;
    }

    public static WebDialog inviteFriends(final Context context, String friendsTo){

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
        return  requestsDialog;
    }

}

