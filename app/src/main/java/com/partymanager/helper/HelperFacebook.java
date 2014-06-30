package com.partymanager.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.activity.ProfileActivity;
import com.partymanager.data.Adapter.FbFriendsAdapter;
import com.partymanager.data.Adapter.FriendsAdapter;
import com.partymanager.data.Friends;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class HelperFacebook {

    private static String facebookId;
    private static String facebookUserName;
    private static Activity activity = MainActivity.getActivity();

    public static String getFacebookId() {
        if (facebookId != null)
            return facebookId;
        else {
            SharedPreferences prefs = activity.getSharedPreferences("profilo", Context.MODE_PRIVATE);
            String id = prefs.getString("reg_id", "");
            if (id.isEmpty()) {
                Log.e("HELPER_FACEBOOK", "id facebook not found.");
                return null;
            } else {
                facebookId = id;
                return facebookId;
            }
        }
    }

    public static String getFacebookUserName() {
        if (facebookUserName != null)
            return facebookUserName;
        else {
            SharedPreferences prefs = activity.getSharedPreferences("profilo", activity.MODE_PRIVATE);
            String name = prefs.getString("reg_username", "");
            if (name.isEmpty()) {
                Log.e("HELPER_FACEBOOK", "username facebook not found.");
                return null;
            } else {
                facebookUserName = name;
                return facebookUserName;
            }
        }
    }

    public static void getFacebookProfilePicture(final Friends friends, final Adapter adapter, final int chi) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... args) {
                Bitmap bitmap = friends.getFoto();
                if (bitmap == null) {
                    URL imageURL;

                    try {
                        imageURL = new URL("https://graph.facebook.com/" + friends.getCode() + "/picture?type=small");
                        bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    friends.setFoto(bitmap);
                    if (chi == 0) {
                        ((FbFriendsAdapter) adapter).notifyDataSetChanged();
                    } else {
                        ((FriendsAdapter) adapter).notifyDataSetChanged();
                    }
                }
            }
        }.execute();
    }

    public static Session getSession(Activity activity) {
        Session session = Session.getActiveSession();
        if (session == null) {
            session = new Session(activity);
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(activity));
            }
        }
        return session;
    }

    public static String getToken() {
        Session session = getSession(activity);
        String token = session.getAccessToken();
        Log.e("TOKEN - getToken", token);
        return token;
    }

    public static WebDialog inviteFriends(final Context context, String friendsTo) {

        Bundle parameters = new Bundle();
        parameters.putString("to", friendsTo);
        parameters.putString("message", context.getString(R.string.msgWebDialog));

        //Ritorno il WEBDialog
        return (
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
                                        context.getString(R.string.rqstCancelled),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,
                                        context.getString(R.string.ntwError),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.rqstSend),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,
                                        context.getString(R.string.rqstCancelled),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }).build();
    }

}

