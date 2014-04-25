package com.partymanager.app;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.partymanager.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ProfileActivity extends Activity {
    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

    private TextView textInstructionsOrLink;
    private Button buttonLoginLogout;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private static ImageView foto_profilo = null;
    private int view_profilo = 0;
    public String url;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Passaggio dati tra activity per click Profilo
        Bundle datipassati = getIntent().getExtras();
        String dato1 = "non ha funzionato";
        if (datipassati != null) {
            dato1 = datipassati.getString("chiave");
            view_profilo = Integer.parseInt(dato1);
        }

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
        setContentView(R.layout.activity_main_activity3);
        buttonLoginLogout = (Button) findViewById(R.id.buttonLoginLogout);
        textInstructionsOrLink = (TextView) findViewById(R.id.instructionsOrLink);
        foto_profilo = (ImageView) findViewById(R.id.foto_profilo);

        //Controllo sessione FB
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
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
            //textInstructionsOrLink.setText(URL_PREFIX_FRIENDS + session.getAccessToken());
            buttonLoginLogout.setText("Logout");
            foto_profilo.setVisibility(View.VISIBLE);
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onClickLogout();
                }
            });

            if (view_profilo == 0) {
                Intent newact = new Intent(this, MainActivity.class);
                startActivity(newact);
            } else {
                // Request user data and show the results
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Display the parsed user info
                            textInstructionsOrLink.setText(buildUserInfoDisplay(user));
                        }
                    }
                });
            }

        } else {
            foto_profilo.setVisibility(View.INVISIBLE);
            textInstructionsOrLink.setText(R.string.instruction);
            buttonLoginLogout.setText("Login");
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onClickLogin();
                }
            });
        }
    }

    private String buildUserInfoDisplay(GraphUser user) {
        String userInfo = user.getName();
        getFacebookProfilePicture(user.getId());
        return userInfo;
    }

    private static void getFacebookProfilePicture(final String userID){
        new AsyncTask<Void, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(Void... args){
                URL imageURL = null;
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
            protected void onPostExecute(Bitmap bitmap){
                // safety check
                if (bitmap != null){
                    foto_profilo.setImageBitmap(bitmap);
                }
            }
        }.execute();
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
        finish();
        return;
    }

}