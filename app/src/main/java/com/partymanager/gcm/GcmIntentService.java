package com.partymanager.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.helper.DataProvide;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

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

                String s = extras.getString("type").toString();
                if (s.equals("newEvent")) {
                    sendNotification("Nuovo Evento", extras.getString("adminName") + " ti ha invitato a " + extras.getString("nome_evento"));

                    try {
                        JSONObject element = new JSONObject();
                        element.put("id_evento", extras.getInt("id_evento"));
                        element.put("nome_evento", extras.getString("nome_evento"));
                        element.put("data", "");
                        element.put("admin", extras.getString("admin"));
                        element.put("num_utenti", extras.getInt("num_utenti"));

                        DataProvide.addElementJson(element, "eventi", getApplicationContext());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (s.equals("newAttr")) {
                    sendNotification("Nuova Domanda", extras.getString("user") + " ha chiesto " + extras.getString("domanda"));

                    try {
                        JSONObject element = new JSONObject();
                        element.put("id_attributo", extras.getInt("id_attributo"));
                        element.put("domanda", extras.getString("domanda"));
                        element.put("risposta", extras.getString("risposta"));
                        element.put("template", extras.getString("template"));
                        element.put("chiusa", extras.getBoolean("chiusa"));
                        element.put("numd", extras.getInt("numd"));
                        element.put("numr", extras.getInt("numr"));

                        DataProvide.addElementJson(element, "attributi" + extras.getInt("idEvento"), getApplicationContext());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (s.equals("test")) {
                    Log.e(Helper_Notifiche.TAG, "test " + extras.toString());

                    sendNotification("TEST", extras.getString("msg"));

                    Message m = new Message();
                    m.setData(extras);
                    MainActivity.handlerService.sendMessage(m);
                }

                if (MainActivity.handlerService != null) {
                    Message m = new Message();
                    m.setData(extras);
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
    private void sendNotification(String title, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_partymanagernotificationicon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))

                        .setContentText(msg)
                        .setDefaults(android.app.Notification.DEFAULT_ALL)
                //.setSound(alarmSound);
                ;
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}