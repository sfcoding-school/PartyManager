package com.partymanager.EventSupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.partymanager.R;
import com.partymanager.activity.fragment.Evento;
import com.partymanager.data.Adapter.FbFriendsAdapter;
import com.partymanager.data.Adapter.RisposteAdapter;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DatiFriends;
import com.partymanager.data.DatiRisposte;
import com.partymanager.data.Friends;
import com.partymanager.helper.HelperConnessione;
import com.partymanager.helper.HelperFacebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventAsync {

    static ProgressDialog progressDialog;

    public static void addDomanda(final boolean chiusura, final Context context, final int who, final String domanda, final int idEvento, final String template, final String risposta) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {

                InputMethodManager inputManager = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (((Activity) context).getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(context.getString(R.string.creazDom));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {

                String chiusa = String.valueOf(chiusura ? 1 : 0);
                String[] name, param;

                if (template.equals("")) {
                    name = new String[]{"domanda", "risposta", "chiusa"};
                    param = new String[]{domanda, risposta, chiusa};
                } else {
                    name = new String[]{"domanda", "template", "risposta", "chiusa"};
                    param = new String[]{domanda, template, risposta, chiusa};
                }

                String ris = HelperConnessione.httpPostConnection("event/" + idEvento, name, param);

                Log.e("addDomanda-ris: ", ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String ris) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                try {
                    int ris_temp = Integer.parseInt(ris);

                    Message m = new Message();
                    Bundle b = new Bundle();

                    switch (who) {
                        case Evento.DIALOG_DATA:
                            b.putInt("who", 1);
                            b.putString("data", risposta);
                            break;
                        case Evento.DIALOG_ORARIO_E:
                            b.putInt("who", 2);
                            b.putString("orario", risposta);
                            break;
                        case Evento.DIALOG_ORARIO_I:
                            b.putInt("who", 3);
                            b.putString("orario", risposta);
                            break;
                        case Evento.DIALOG_LUOGO_I:
                            b.putInt("who", 4);
                            b.putString("luogo", risposta);
                            break;
                        case Evento.DIALOG_LUOGO_E:
                            b.putInt("who", 6);
                            b.putString("luogo", risposta);
                            break;
                        case Evento.DIALOG_PERSONALLIZATA:
                            b.putInt("who", 5);
                            b.putString("pers-d", domanda);
                            b.putString("pers-r", risposta);
                            break;
                        case Evento.DIALOG_SINO:
                            b.putInt("who", 7);
                            b.putString("domanda", domanda);
                            break;
                    }

                    b.putBoolean("close", chiusura);
                    b.putInt("id_attributo", ris_temp);
                    m.setData(b);
                    EventDomanda.mResponseHandler.sendMessage(m);

                } catch (NumberFormatException e) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.problInsDom);

                    alertDialogBuilder.setPositiveButton(R.string.chiudi, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        }.execute(null, null, null);
    }

    public static void eliminaUser(final TextView bnt_friends, final int idEvento, final Activity activity, final int i, final ProgressBar pb_buttaFuori) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... args) {
                String ris;

                ris = HelperConnessione.httpDeleteConnection("friends/" + idEvento + "/" + DatiFriends.ITEMS.get(i).getCode());

                Log.e("Evento-eliminaUser-ris: ", ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String result) {
                pb_buttaFuori.setVisibility(View.GONE);
                if (!result.equals("fatto")) {
                    Toast.makeText(activity, activity.getString(R.string.errDeleteFriend), Toast.LENGTH_LONG).show();
                } else {
                    DatiFriends.removeItem(i);
                    DatiEventi.getIdItem(idEvento).numUtenti--;
                    bnt_friends.setText("" + DatiEventi.getIdItem(idEvento).numUtenti + activity.getString(R.string.membri));
                }
            }
        }.execute();
    }

    public static void modificaChiusaAsync(final int idAttributo, final Activity activity, final int pos, final String nuova, final int idEvento) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String[] name, param;
                name = new String[]{"risposta"};
                param = new String[]{nuova};

                return HelperConnessione.httpPutConnection("event/" + idEvento + "/" + idAttributo + "/" + DatiRisposte.getPositionItem(pos).id, name, param);
            }

            @Override
            protected void onPostExecute(String ris) {
                if (ris.equals("fatto")) {
                    EventoHelper.modificaGrafica(false);
                    DatiRisposte.modificaRisposta(pos, nuova);
                } else {
                    Toast.makeText(activity, activity.getString(R.string.errModChiusa), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    public static void eliminaRisposta(final int pos, final int idEvento, final int idRisposta, final Activity activity) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String ris = HelperConnessione.httpDeleteConnection("event/" + idEvento + "/" + DatiAttributi.getPositionItem(pos).id + "/" + idRisposta);

                Log.e("Evento-Helper-eliminaRisposta-ris: ", " \nrisposta: " + ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String ris) {
                if (ris.equals("fatto")) {
                    DatiRisposte.removePositionItem(pos);
                    Evento.checkTemplate();
                } else {
                    Toast.makeText(activity, activity.getString(R.string.errDeleteRisposta), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    public static void eliminaDomanda(final int posAttr, final int idEvento, final Activity activity) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String ris = HelperConnessione.httpDeleteConnection("event/" + idEvento + "/" + DatiAttributi.getPositionItem(posAttr).id);

                Log.e("eliminaDomanda-ris: ", "event/" + idEvento + "/" + DatiAttributi.getPositionItem(posAttr).id + " \nrisposta: " + ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String ris) {
                if (ris.equals("fatto")) {
                    DatiAttributi.removePositionItem(posAttr);
                    Evento.checkTemplate();
                } else {
                    Toast.makeText(activity, activity.getString(R.string.errDeleteDomanda), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    public static void addRisposta(final EditText edt, final Activity activity, final int idEvento, final int id_attributo, final String risposta, final String template, final ProgressBar pb_add, final ImageButton dialogButton) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String[] name, param;
                name = new String[]{"risposta"};
                param = new String[]{risposta};
                String ris = HelperConnessione.httpPostConnection("event/" + idEvento + "/" + id_attributo, name, param);

                Log.e("addRisposta-ris: ", ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String ris) {
                pb_add.setVisibility(View.GONE);

                if (dialogButton != null)
                    dialogButton.setVisibility(View.VISIBLE);

                try {
                    JSONObject pers = new JSONObject();
                    JSONArray userL = new JSONArray();
                    pers.put("id_user", HelperFacebook.getFacebookId());
                    pers.put("name", HelperFacebook.getFacebookUserName());
                    userL.put(pers);
                    DatiRisposte.addItem(new DatiRisposte.Risposta(Integer.parseInt(ris), risposta, userL), template, true);
                    Evento.checkTemplate();
                    edt.setText("");

                } catch (JSONException e) {
                    Log.e("Evento-addRisposta", "JSONException " + e);
                    Toast.makeText(activity, activity.getString(R.string.errInsertDomanda), Toast.LENGTH_LONG).show();
                } catch (NumberFormatException e) {
                    Log.e("Evento-addRisposta", "risposta non numerica " + e);
                    Toast.makeText(activity, activity.getString(R.string.errInsertDomanda), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    public static void vota(final int idEvento, final RisposteAdapter adapter, final Button vota, final int idRisposta, final int position, final ProgressBar pb_vota) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                pb_vota.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                return HelperConnessione.httpPutConnection("event/" + idEvento + "/" + adapter.getId() + "/" + idRisposta, null, null);
            }

            @Override
            protected void onPostExecute(String ris) {
                pb_vota.setVisibility(View.GONE);
                if (vota != null)
                    vota.setVisibility(View.VISIBLE);

                Log.e("Evento-vota-ris:", ris);

                if (ris.equals("aggiornato")) {
                    EventoHelper.graficaVota(position, adapter.getArg2());
                    Evento.checkTemplate();
                }
            }
        }.execute(null, null, null);
    }

    public static void addFriendsToEvent(final ArrayList<String> id_toSend, final ArrayList<String> name_toSend, final TextView bnt_friends, final Activity activity, final int idEvento) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage(activity.getApplicationContext().getString(R.string.aggiuntaAmico));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... args) {
                String ris;

                Log.e("Evento-AddFriends-Persone prima di invio: ", (new JSONArray(id_toSend)).toString());

                ris = HelperConnessione.httpPostConnection("friends/" + idEvento, new String[]{"userList"}, new String[]{(new JSONArray(id_toSend)).toString()});

                Log.e("Evento-addFriendsToEvent-ris: ", ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (!result.equals("fatto")) {
                    Toast.makeText(activity, activity.getString(R.string.errAggAmico), Toast.LENGTH_LONG).show();
                } else {
                    FbFriendsAdapter.svuotaLista();

                    for (int i=0; i< id_toSend.size(); i++){
                        DatiFriends.addItem(new Friends(id_toSend.get(i), name_toSend.get(i), false, false));
                    }

                    DatiEventi.getIdItem(idEvento).numUtenti += id_toSend.size();
                    bnt_friends.setText("" + DatiEventi.getIdItem(idEvento).numUtenti + activity.getString(R.string.membri));
                }
            }
        }.execute();
    }
}
