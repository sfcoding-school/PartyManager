package com.partymanager.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.activity.fragment.Evento;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DatiRisposte;
import com.partymanager.helper.HelperDataParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
/*
<<<<<<< HEAD
                String s = extras.getString("type");
                if (s.equals("newEvent")) {
                    if (nessuna_notifica && preferences.getBoolean("checkbox_preference1", true))
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
                    if (nessuna_notifica && preferences.getBoolean("checkbox_preference2", true))
                        sendNotification("Nuova Domanda", extras.getString("userName") + " ha chiesto " + extras.getString("domanda"));

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

                } else if (s.equals("newRis")) {
                    if (nessuna_notifica && preferences.getBoolean("checkbox_preference3", true))
                        sendNotification("Nuova Risposta", extras.getString("userName") + " ha risposto " + extras.getString("risposta") + " alla domanda " + extras.getString("domanda"));
                    //'type':'newRis', 'agg': 0, 'user': user, 'userName': userName, 'id_attributo': idAttributo, 'id_risposta': idRisposta, 'domanda': domanda, 'risposta': risposta}
                    if (extras.getBoolean("agg")) {

                    } else {

                    }

                } else if (s.equals("risp")) {
                    if (nessuna_notifica && preferences.getBoolean("checkbox_preference3", true))
                        sendNotification("Risposta", "anche " + extras.getString("userName") + " ha risposto " + extras.getString("risposta") + " alla domanda " + extras.getString("domanda"));
                    //'type':'newRis', 'agg': 0, 'user': user, 'userName': userName, 'id_attributo': idAttributo, 'id_risposta': idRisposta, 'domanda': domanda, 'risposta': risposta}
                    if (extras.getBoolean("agg")) {

                    } else {

                    }
                } else if (s.equals("delEvent")) {
                    /*
                    'id_evento': str(idEvento),
                    'nome_evento': Database.getEventName(idEvento),
                    'admin_name': getFacebookName(admin)
                     *//*
                    sendNotification("Evento Eliminato", extras.getString("admin_name") + " ha eliminato l'evento " + extras.getString("nome_evento"));

                } else if (s.equals("uscitoEvent")) {
                    /*
                    'id_evento': str(idEvento),
                   'nome_evento': Database.getEventName(idEvento),
                   'id_user': user,
                   'name_user': getFacebookName(admin)
                     *//*
                    sendNotification("Utente uscito", extras.getString("name_user") + " è uscito dall'evento " + extras.getString("nome_evento"));

                } else if (s.equals("addFriends")) {
                    /*
                    'type': 'addFriends',
                    'id_evento': idEvento,
                    'nome_evento': Database.getEventName(idEvento),
                    'user_list': Facebook.getFacebookName(userList)})
                     *//*
                    sendNotification("Aggiunti amici", extras.getString("user_list") + "sono stati aggiunti all'evento" + extras.getString("nome_evento"));
                } else if (s.equals("delFriends")) {
                    /*
                    'type': 'delFriends',
                    'id_evento': str(idEvento),
                    'nome_evento': Database.getEventName(idEvento),
                    'admin_name': getFacebookName(admin),
                    'id_user': idFacebook,
                    'user_name': getFacebookName(idFacebook)
                     */
/*
                    sendNotification("Amico eliminato ", extras.getString("user_name") + " è stato rimosso dall'evento " + extras.getString("nome_evento"));
                } else if (s.equals("test")) {
                    Log.e(Helper_Notifiche.TAG, "test " + extras.toString());
                    if (nessuna_notifica)
                        sendNotification("TEST", extras.getString("msg"));

                    Message m = new Message();
                    m.setData(extras);
                    MainActivity.handlerService.sendMessage(m);
=======*/
                Log.e("NOTIFICHE-DEBUG", extras.toString());
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
                                        extras.getString("adminName") + getString(R.string.NotMsgNuovoEvento) + extras.getString("nome_evento"),
                                        "checkbox_eventi",
                                        extras);
                                //boolean dio = DatiEventi.getInizializzata();
                                if (DatiEventi.getInizializzata()) {
                                    DatiEventi.addItem(new DatiEventi.Evento(
                                            Integer.parseInt(extras.getString(code.evento.id)),
                                            extras.getString(code.evento.nome),
                                            "", "",
                                            extras.getString(code.user.idAdmin),
                                            Integer.parseInt(extras.getString(code.evento.num))
                                    ));
                                }
                                break;

                            //uscito
                            case 4:
                                sendNotification("Utente uscito",
                                        extras.getString("name_user") + getString(R.string.NotMsgUtenteUscito) + extras.getString("nome_evento"),
                                        "checkbox_eventi",
                                        extras);
                                if (DatiEventi.getInizializzata()) {
                                    DatiEventi.removeIdItem(Integer.parseInt(extras.getString(code.evento.id)));
                                }

                                break;
                            case 2:
                                sendNotification("Evento rinominato",
                                        extras.getString("name_user") + getString(R.string.NotMsgEventoRinominato) + extras.getString("nome_evento") + getString(R.string.NotMsgEventoRinominato2) + extras.getString("nome_evento_vec"),
                                        "checkbox_eventi",
                                        extras);
                                if (DatiEventi.getInizializzata()) {
                                    DatiEventi.getIdItem(Integer.parseInt(extras.getString(code.evento.id))).name = extras.getString(code.evento.nome);
                                }
                                break;
                        }
                        break;

                    //attr
                    case 2:
                        switch (method) {
                            //new
                            case 1:
                                sendNotification("Nuova Domanda",
                                        extras.getString("userName") + getString(R.string.NotMsgDomanda) + extras.getString("domanda"),
                                        "checkbox_domande",
                                        extras);

                                if (DatiEventi.getInizializzata()){
                                    if (extras.getString(code.attributo.template).equals("data")){
                                        DatiEventi.getIdItem(Integer.parseInt(extras.getString(code.evento.id))).date = HelperDataParser.getCalFromString(extras.getString(code.risposta.nome));
                                    }
                                }

                                if (DatiAttributi.getIdEvento() == Integer.parseInt(extras.getString(code.evento.id))) {
                                    DatiAttributi.addItem(new DatiAttributi.Attributo(
                                            Integer.parseInt(extras.getString(code.attributo.id)),
                                            extras.getString(code.attributo.nome),
                                            extras.getString(code.risposta.nome),
                                            extras.getString(code.attributo.template),
                                            Boolean.parseBoolean(extras.getString(code.attributo.chiusa)),
                                            Integer.parseInt(extras.getString(code.attributo.num)),
                                            Integer.parseInt(extras.getString(code.risposta.num)),
                                            extras.getString(code.risposta.nome)
                                    ));
                                }
                                break;

                        }
                        break;

                    //risp
                    case 3:
                        switch (method) {
                            //new
                            case 1:
                                sendNotification("Nuova Risposta",
                                        extras.getString("userName") + getString(R.string.NotMsgRisposta) + extras.getString("risposta") + getString(R.string.NotMsgRisposta2) + extras.getString("domanda"),
                                        "checkbox_risposte",
                                        extras);

                                if (DatiEventi.getInizializzata()){
                                    if (extras.getString(code.attributo.template).equals("data")){

                                    }

                                }

                                if (DatiAttributi.getIdEvento() == Integer.parseInt(extras.getString(code.evento.id))) {

                                    DatiAttributi.Attributo attr = DatiAttributi.getIdItem(Integer.parseInt(extras.getString(code.attributo.id)));
                                    attr.numd++;
                                    if (attr.numr < 1) {
                                        attr.risposta = extras.getString(code.risposta.nome);
                                        attr.id_risposta = extras.getString(code.risposta.id);
                                        attr.numr = 1;
                                    }

                                }
                                if (DatiRisposte.getIdAttributo() == Integer.parseInt(extras.getString(code.attributo.id))) {
                                    if (Boolean.parseBoolean(extras.getString("agg"))) {
                                        DatiRisposte.addIdPersona(
                                                Integer.parseInt(extras.getString(code.risposta.id)),
                                                extras.getString(code.user.id),
                                                extras.getString(code.user.nome),
                                                true
                                        );
                                    } else {
                                        List<DatiRisposte.Persona> userList = new ArrayList<DatiRisposte.Persona>();
                                        userList.add(new DatiRisposte.Persona(
                                                extras.getString(code.user.id),
                                                extras.getString(code.user.nome)
                                        ));
                                        DatiRisposte.addItem(new DatiRisposte.Risposta(
                                                Integer.parseInt(extras.getString(code.risposta.id)),
                                                extras.getString(code.risposta.nome),
                                                userList
                                        ));
                                    }
                                }
                                break;

                            //mod
                            case 2:
                                sendNotification("Risposta",
                                        getString(R.string.NotMsgRisposta3) + extras.getString("userName") + getString(R.string.NotMsgRisposta) + extras.getString("risposta") + getString(R.string.NotMsgRisposta2) + extras.getString("domanda"),
                                        "checkbox_risposte",
                                        extras);


                                if (DatiEventi.getInizializzata()){
                                    if (extras.getString(code.attributo.template).equals("data")){

                                    }

                                }

                                if (DatiAttributi.getIdEvento() == Integer.parseInt(extras.getString(code.evento.id))){
                                    DatiAttributi.getIdItem(Integer.parseInt(extras.getString(code.attributo.id))).risposta = extras.getString(code.risposta.nome);
                                }

                                if (DatiRisposte.getIdAttributo() == Integer.parseInt(extras.getString(code.attributo.id))){
                                    DatiRisposte.getIdItem(Integer.parseInt(extras.getString(code.risposta.id))).risposta = extras.getString(code.risposta.nome);
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
                                        extras.getString("user_list") + getString(R.string.NotMsgAddFriends) + extras.getString("nome_evento"),
                                        "checkbox_utenti",
                                        extras);
                                if (DatiEventi.getInizializzata()){
                                    DatiEventi.getIdItem(Integer.parseInt(extras.getString(code.evento.id))).numUtenti++;
                                }
                                // da implementare
                                break;

                            //del
                            case 3:
                                sendNotification("Amico eliminato",
                                        extras.getString("user_name") + getString(R.string.NotMsgDeleteFriend) + extras.getString("nome_evento"),
                                        "checkbox_utenti",
                                        extras);
                                if (DatiEventi.getInizializzata()){
                                    DatiEventi.getIdItem(Integer.parseInt(extras.getString(code.evento.id))).numUtenti--;
                                }

                                break;
                        }
                        break;
                    case 5:
                        //test
                        Log.e(Helper_Notifiche.TAG, "test " + extras.toString());
                        sendNotification("TEST", extras.getString("msg"), "checkbox_notifiche_all", extras);
                        break;
//>>>>>>> agg-notifiche
                }

/*
                if (MainActivity.handlerService != null) {
                    Message m = new Message();
                    m.setData(extras);
                    MainActivity.handlerService.sendMessage(m);
                }
*/
                Log.i(Helper_Notifiche.TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String msg, String impostazione, Bundle extras) {
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
            Intent intent = new Intent("notifica", Uri.EMPTY, this, MainActivity.class);
            Log.e("NOTIFICHE-DEBUG", "prima di impostare l'intant -idevento:"+extras.getString(code.evento.id)+" numUtenti:"+extras.getString(code.evento.num)+" nomeEvento:"+extras.getString(code.evento.nome)+" adminEvento:"+extras.getString(code.user.idAdmin));

            intent.putExtra(Evento.ID_EVENTO, Integer.parseInt(extras.getString(code.evento.id)));
            intent.putExtra(Evento.NUM_UTENTI, Integer.parseInt(extras.getString(code.evento.num)));
            intent.putExtra(Evento.NOME_EVENTO, extras.getString(code.evento.nome));
            intent.putExtra(Evento.ADMIN_EVENTO, extras.getString(code.user.idAdmin));

            int id = intent.getIntExtra(Evento.ID_EVENTO, -1);
            String nome = intent.getStringExtra(Evento.NOME_EVENTO);
            String admin = intent.getStringExtra(Evento.ADMIN_EVENTO);
            int num = intent.getIntExtra(Evento.NUM_UTENTI, -1);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

    static class code {
        static class type {
            static String evento = "1";
            static String attributo = "2";
            static String risposta = "3";
            static String user = "4";
            static String test = "5";
        }

        static class method {
            static String newM = "1";
            static String modify = "2";
            static String delete = "3";
            static String uscito = "4";
        }

        static class evento {
            static String id = "id_evento";
            static String nome = "nome_evento";
            static String num = "num_utenti";
            static String nomeVecchio = "nome_evento_vec";
        }

        static class attributo {
            static String id = "id_attributo";
            static String nome = "domanda";
            static String template = "template";
            static String chiusa = "chiusa";
            static String num = "numd";
        }

        static class risposta {
            static String id = "id_risposta";
            static String nome = "nome_risposta";
            static String agg = "agg";
            static String num = "numr";
        }

        static class user {
            static String id = "id_user";
            static String nome = "nome_user";
            static String idAdmin = "id_admin";
            static String nomeAdmin = "nomeAdmin";
            static String list = "user_list";
            static String idDelete = "id_user_delete";
            static String nomeDelete = "nome_user_delete";
        }
    }
}