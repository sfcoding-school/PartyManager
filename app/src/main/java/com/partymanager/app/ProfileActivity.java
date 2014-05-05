package com.partymanager.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.partymanager.R;
import com.partymanager.gcm.Helper_Notifiche;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ProfileActivity extends Activity {

    private TextView textInstructionsOrLink;
    private Button buttonLoginLogout;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private static ImageView foto_profilo = null;
    private int view_profilo = 0;
    public final String REG_USERNAME = "reg_username";
    public final String REG_ID = "reg_id";
    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;
    private String username;
    private String id_fb;
    SharedPreferences prefs;
    Session session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        //Passaggio dati tra activity per click Profilo
        Bundle datipassati = getIntent().getExtras();
        String dato1;
        if (datipassati != null) {
            dato1 = datipassati.getString("chiave");
            view_profilo = Integer.parseInt(dato1);
        }

        prefs = getPreferences();
        //controllo possibilità di accesso/scrittura ExternaleStorage
        checkExternalMedia();

        //Controllo KEY HASH per connessione FB
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.partymanager", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //Log.e("MY KEY HASH:", sign);
                //Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (NameNotFoundException e) {
            Toast.makeText(getApplicationContext(), "error1 - ProfileActivity", Toast.LENGTH_LONG).show();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(getApplicationContext(), "error2 - ProfileActivity", Toast.LENGTH_LONG).show();
        }

        //Inizializzazione componenti layout
        setContentView(R.layout.activity_profilo);
        buttonLoginLogout = (Button) findViewById(R.id.buttonLoginLogout);
        textInstructionsOrLink = (TextView) findViewById(R.id.instructionsOrLink);
        foto_profilo = (ImageView) findViewById(R.id.foto_profilo);

        //Controllo sessione FB
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }


        updateView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void updateView() {
        final Session session = Session.getActiveSession();
        if (session.isOpened()) {

            buttonLoginLogout.setText("Logout");
            foto_profilo.setVisibility(View.VISIBLE);
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onClickLogout();
                }
            });

            if (view_profilo == 0) {

                final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            savePreferences(user.getUsername(), user.getId());
                            Helper_Notifiche.registerInBackground(gcm, getApplicationContext(), user.getId(), user.getUsername());
                        }
                    }
                });


                Intent newact = new Intent(this, MainActivity.class);
                startActivity(newact);
            } else {

                String username_pref = prefs.getString(REG_USERNAME, "");
                textInstructionsOrLink.setText(username_pref);
                if (mExternalStorageAvailable) {

                    File sdCard = Environment.getExternalStorageDirectory();
                    File directory = new File(sdCard.getAbsolutePath() + "/PartyManager");
                    File file = new File(directory, "foto_profilo.jpg");
                    FileInputStream streamIn = null;

                    try {
                        streamIn = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (streamIn != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
                        foto_profilo.setImageBitmap(bitmap);

                        try {
                            streamIn.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            getFacebookProfilePicture(user.getId());
                            textInstructionsOrLink.setText(user.getName());
                            username = user.getName();
                            id_fb = user.getId();
                            savePreferences(username, id_fb);
                        }
                    }
                });
            }
        } else {
            foto_profilo.setVisibility(View.GONE);
            textInstructionsOrLink.setText(R.string.instruction);
            buttonLoginLogout.setText("Login");
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onClickLogin();
                }
            });
        }
    }

    private SharedPreferences getPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(ProfileActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void savePreferences(String username_t, String id_fb_t) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_USERNAME, username_t);
        editor.putString(REG_ID, id_fb_t);
        editor.commit();
    }

    private void getFacebookProfilePicture(final String userID) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... args) {
                URL imageURL;
                Bitmap bitmap = null;
                try {
                    imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                // safety check
                if (bitmap != null) {
                    if (mExternalStorageAvailable && mExternalStorageWriteable) {
                        saveWallpaper(bitmap);
                    }

                    foto_profilo.setImageBitmap(bitmap);
                }
            }

            private void saveWallpaper(Bitmap finalBitmap) {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/PartyManager");
                myDir.mkdirs();

                String fname = "foto_profilo.jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    Log.e("CATCH: ", "saveWallpaper");
                }
            }

        }.execute();

    }

    private void checkExternalMedia() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        //Log.e("DEBUG: ", "External Media: readable=" + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);
    }


    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(Arrays.asList("basic_info"))
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }

    }

    private void onClickLogout() {
        view_profilo = 0;
        foto_profilo.setVisibility(View.GONE);
        textInstructionsOrLink.setText(R.string.instruction);
        savePreferences("", "");
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }

    @Override
    public void onBackPressed() {
        view_profilo = 0;
        if (session != null && session.isOpened())
            finish();

    }
}

 /*
                //Notifiche
                context = getApplicationContext();

                // Check device for Play Services APK.
                if (checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(this);
                    regid = getRegistrationId(context);

                    if (regid.isEmpty()) {
                        registerInBackground();
                    }else{
                        Log.i(TAG,"regid "+regid);
                        //mDisplay.append("reg_id: "+regid);
                    }
                }else{
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }
                */