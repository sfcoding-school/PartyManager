package com.partymanager.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.helper.DataProvide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
                /*
                 * Filter messages based on message type. Since it is likely that GCM
                 * will be extended in the future with new message types, just ignore
                 * any message types you're not interested in, or that you don't
                 * recognize.
                 */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(Helper_Notifiche.TAG, "Send error: " + extras.toString());
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(Helper_Notifiche.TAG, "Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                int type = Integer.parseInt(extras.getString("type"));
                int method = Integer.parseInt(extras.getString("method"));
                /*
                "checkbox_eventi"
                "checkbox_domande"
                "checkbox_risposte"
                "checkbox_allarme"
                "checkbox_utenti"
                 */
                /*
                  t = {'event': '1',
                     'attr': '2',
                     'risp': '3',
                     'user': '4',
                     'test': '5'
                     }

                m = {'new': '1',
                     'mod': '2',
                     'del': '3',
                     'uscito': '4'
                     }
                 */
                switch (type) {
                    //event
                    case 1:
                        switch (method) {
                            //new
                            case 1:
                                sendNotification("Nuovo Evento",
                                        extras.getString("adminName") + " ti ha invitato a " + extras.getString("nome_evento"),
                                        "checkbox_eventi");
                                break;

                            //uscito
                            case 4:
                                sendNotification("Utente uscito",
                                        extras.getString("name_user") + " è uscito dall'evento " + extras.getString("nome_evento"),
                                        "checkbox_utenti");
                                break;
                        }
                        break;

                    //attr
                    case 2:
                        switch (method) {
                            //new
                            case 1:
                                sendNotification("Nuova Domanda",
                                        extras.getString("userName") + " ha chiesto " + extras.getString("domanda"),
                                        "checkbox_domande");
                                break;

                        }
                        break;

                    //risp
                    case 3:
                        switch (method) {
                            //new
                            case 1:
                                sendNotification("Nuova Risposta",
                                        extras.getString("userName") + " ha risposto " + extras.getString("risposta") + " alla domanda " + extras.getString("domanda"),
                                        "checkbox_risposte");

                                if (extras.getBoolean("agg")) {

                                } else {

                                }
                                break;

                            //mod
                            case 4:
                                sendNotification("Risposta",
                                        "anche " + extras.getString("userName") + " ha risposto " + extras.getString("risposta") + " alla domanda " + extras.getString("domanda"),
                                        "checkbox_risposte");

                                if (extras.getBoolean("agg")) {

                                } else {

                                }
                                break;
                        }
                        break;

                    //user
                    case 4:
                        switch (method) {

                            //new
                            case 1:
                                sendNotification("Aggiunti amici",
                                        extras.getString("user_list") + "sono stati aggiunti all'evento" + extras.getString("nome_evento"),
                                        "checkbox_utenti");
                                break;

                            //del
                            case 3:
                                sendNotification("Amico eliminato",
                                        extras.getString("user_name") + " è stato rimosso dall'evento " + extras.getString("nome_evento"),
                                        "checkbox_utenti");
                                break;
                        }
                        break;
                    case 5:
                        //test
                        Log.e(Helper_Notifiche.TAG, "test " + extras.toString());
                        sendNotification("TEST", extras.getString("msg"), "checkbox_notifiche_all");
                        break;
                }


                if (MainActivity.handlerService != null) {
                    Message m = new Message();
                    m.setData(extras);
                    Bundle b = new Bundle();
                    MainActivity.handlerService.sendMessage(m);
                }

                Log.i(Helper_Notifiche.TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String msg, String impostazione) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (preferences.getBoolean("checkbox_notifiche_all", true) && preferences.getBoolean(impostazione, true)) {

            int colorLed;
            colorLed = Integer.parseInt(preferences.getString("downloadType", null));

            /* //Per ISSUE #23 (se dovesse ricapitare si mette)
            try{
                colorLed = Integer.parseInt(preferences.getString("downloadType", null));
            } catch (NumberFormatException e){
                Log.e("GcmIntentService", "NumberFormatException " + e);
                colorLed = 16777215;
            }
            */
            boolean prova = preferences.getBoolean("checkbox_vibrate", true);
            long[] vibr = null;
            if (prova) {
                vibr = new long[]{1000, 1000, 1000};
            }

            prova = preferences.getBoolean("checkbox_sound", true);
            int sound = 0;
            if (prova) {
                sound = Notification.DEFAULT_SOUND;
            }

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)

                            .setSmallIcon(R.drawable.ic_stat_partymanagernotificationicon)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))

                            .setContentText(msg)
                            .setDefaults(sound)
                            .setLights(colorLed, 500, 500)
                            .setVibrate(vibr);

            mBuilder.setAutoCancel(true);
            mBuilder.setContentIntent(contentIntent);


            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}