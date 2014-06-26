package com.partymanager.EventSupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.partymanager.R;
import com.partymanager.data.Adapter.FbFriendsAdapter;
import com.partymanager.data.Adapter.FriendsAdapter;
import com.partymanager.data.Adapter.RisposteAdapter;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DatiFriends;
import com.partymanager.data.DatiRisposte;
import com.partymanager.data.Friends;
import com.partymanager.helper.DataProvide;
import com.partymanager.helper.HelperConnessione;
import com.partymanager.helper.HelperFacebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventoHelper {

    private static EditText edt;
    static Dialog dialogFriends;
    static ProgressBar pb;
    static ArrayList<Friends> friendList;
    static FbFriendsAdapter dataAdapter = null;
    static ArrayList<Friends> friendsList;
    static List<GraphUser> friends;
    static EditText inputSearch;
    static ListView amiciFB;
    static Dialog dialogAddFriends;
    static ArrayList<String> id_toSend;
    static ProgressDialog progressDialog;
    static int arg2;
    static Button si;
    static Button no;
    static ImageButton dialogButton;
    static DatePicker dateR;
    static Button add;

    static private Dialog dialog;

    private static Dialog getRisposteDialog(Activity activity) {
        if (dialog == null) {
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_risposte);
        }
        return dialog;
    }

    /*
    public boolean isVisible(){
        if (dialog != null && dialog.isShowing())
            return DatiAttributi.ITEMS.get(arg2).id;
        else return false;
    }
    */

    public static int getIdAttributo() {
        if (dialog != null && dialog.isShowing())
            return Integer.valueOf(idAttributo);
        else return -1;
    }

    private static int idAttributo;

    //<<<<<<< HEAD
    public static void dialogRisposte(final String adminEvento, int arg, final Activity activity, final int idEvento, String numUtenti) {
        arg2 = arg;
/*
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_risposte);*/

        dialog = getRisposteDialog(activity);
        idAttributo = DatiAttributi.getPositionItem(arg2).id;

        final ListView risp = (ListView) dialog.findViewById(R.id.listView_risposte);
        RisposteAdapter adapter = DatiRisposte.init(activity.getApplicationContext(), idEvento, DatiAttributi.getPositionItem(arg2).id, Integer.parseInt(numUtenti), arg2, DatiAttributi.getPositionItem(arg2).close);
/*=======

    public static void dialogRisposte(final int arg2, Activity activity, final int idEvento, String numUtenti) {


        final ListView risp = (ListView) dialog.findViewById(R.id.listView_risposte);
        RisposteAdapter adapter = DatiRisposte.init(activity.getApplicationContext(), idEvento, idAttributo, Integer.parseInt(numUtenti), arg2);
>>>>>>> agg-notifiche*/
        risp.setAdapter(adapter);

        risp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Log.e("provaRISP", "click");
                if (adminEvento.equals(HelperFacebook.getFacebookId())) {

                    PopupMenu popup = new PopupMenu(activity, view);
                    popup.getMenuInflater().inflate(R.menu.popup_delete, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            eliminaRisposta(i, idEvento, activity);
                            return true;
                        }
                    });

                    popup.show();
                }
            }
        });

        TextView text = (TextView) dialog.findViewById(R.id.txt_domanda_dialog);
        text.setText(DatiAttributi.getPositionItem(arg2).domanda);

        dialogButton = (ImageButton) dialog.findViewById(R.id.imgBSend);
        edt = (EditText) dialog.findViewById(R.id.edtxt_nuovaRisposta);

        if (DatiAttributi.getPositionItem(arg2).close) {
            edt.setVisibility(View.GONE);
            dialogButton.setVisibility(View.GONE);
        } else {
            edt.setHint("Scrivi qui la tua risposta");
        }

        final ProgressBar pb_add = (ProgressBar) dialog.findViewById(R.id.pb_addRisposta);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(edt.getText().toString())) {
                    dialogButton.setVisibility(View.GONE);
                    pb_add.setVisibility(View.VISIBLE);
//<<<<<<< HEAD
                    if (DatiAttributi.getPositionItem(arg2).close) {

                    } else {
                        addRisposta(idEvento, DatiAttributi.getPositionItem(arg2).id, edt.getText().toString(), DatiAttributi.getPositionItem(arg2).template, pb_add, dialogButton);
                    }
/*=======
                    addRisposta(idEvento, DatiAttributi.getPositionItem(arg2).id, edt.getText().toString(), DatiAttributi.getPositionItem(arg2).template, pb_add, dialogButton);
>>>>>>> agg-notifiche*/
                }
            }
        });

        if (DatiAttributi.getPositionItem(arg2).template != null) {
            if (DatiAttributi.getPositionItem(arg2).template.equals("sino")) {
                LinearLayout normal = (LinearLayout) dialog.findViewById(R.id.risposta_stringa);
                normal.setVisibility(View.GONE);
                LinearLayout sino = (LinearLayout) dialog.findViewById(R.id.linearL_sino);
                sino.setVisibility(View.VISIBLE);

                final ProgressBar pb_sino = (ProgressBar) dialog.findViewById(R.id.pb_sino);

                no = (Button) dialog.findViewById(R.id.btn_risp_no);
                final RisposteAdapter finalAdapter = adapter;
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pb_sino.setVisibility(View.VISIBLE);
                        if (DatiAttributi.getPositionItem(arg2).close) {

                        } else {
                            addDomandaSino(finalAdapter, idEvento, "no", pb_sino, arg2);
                        }
                    }
                });

                si = (Button) dialog.findViewById(R.id.btn_risp_si);
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pb_sino.setVisibility(View.VISIBLE);
                        if (DatiAttributi.getPositionItem(arg2).close) {

                        } else {
                            addDomandaSino(finalAdapter, idEvento, "si", pb_sino, arg2);
                        }
                    }
                });

                if (DatiAttributi.getPositionItem(arg2).close) {
                    si.setVisibility(View.GONE);
                    no.setVisibility(View.GONE);
                }
            }
            if (DatiAttributi.getPositionItem(arg2).template.equals("data")) {
                LinearLayout normal = (LinearLayout) dialog.findViewById(R.id.risposta_stringa);
                normal.setVisibility(View.GONE);
                LinearLayout dataL = (LinearLayout) dialog.findViewById(R.id.linearL_data);
                dataL.setVisibility(View.VISIBLE);

                final ProgressBar pb_data = (ProgressBar) dialog.findViewById(R.id.pb_data);

                dateR = (DatePicker) dialog.findViewById(R.id.datePicker_risposta);

                add = (Button) dialog.findViewById(R.id.button_rispndi_data);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String temp = Integer.toString(dateR.getDayOfMonth()) + "/" + Integer.toString(dateR.getMonth() + 1) + "/" + Integer.toString(dateR.getYear());
                        pb_data.setVisibility(View.VISIBLE);
//<<<<<<< HEAD
                        if (DatiAttributi.getPositionItem(arg2).close) {
//=======
                        addRisposta(idEvento, DatiAttributi.getPositionItem(arg2).id, temp, "data", pb_data, null);
//>>>>>>> agg-notifiche

                        } else {

                            addRisposta(idEvento, DatiAttributi.getPositionItem(arg2).id, temp, "data", pb_data, null);
                        }
                    }
                });

                if (DatiAttributi.getPositionItem(arg2).close) {
                    add.setVisibility(View.GONE);
                    dateR.setVisibility(View.GONE);
                }
            }
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    DatiRisposte.removeAll(true, idEvento, DatiAttributi.getPositionItem(arg2).id);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("Evento-dialog.setOnDismissListener", "IndexOutOfBoundsException " + e);
                }
            }
        });

        dialog.show();
    }

//<<<<<<< HEAD
    private static void modificaChiusaAsync(final int pos, final String nuova) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                /*
                String[] name, param;

                name = new String[]{"idRisposta"};
                param = new String[]{idRisposta};

                return HelperConnessione.httpPutConnection("event/" + idEvento + "/" + adapter.getId(), name, param);
            */
                return null;
            }

            @Override
            protected void onPostExecute(String ris) {
                if (ris.equals("aggiornato")) {
                    DatiRisposte.modificaRisposta(pos, nuova);
                }
            }
        }.execute(null, null, null);
    }

    public static void modificaChiusa() {
        if (DatiAttributi.getPositionItem(arg2).template.equals("data")) {
            add.setVisibility(View.VISIBLE);
            dateR.setVisibility(View.VISIBLE);
        }
        if (DatiAttributi.getPositionItem(arg2).template.equals("sino")) {
            si.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
        }
        if (!DatiAttributi.getPositionItem(arg2).template.equals("data") && !DatiAttributi.getPositionItem(arg2).template.equals("sino")) {
            edt.setVisibility(View.VISIBLE);
            dialogButton.setVisibility(View.VISIBLE);
        }
    }

    private static void eliminaRisposta(final int pos, final int idEvento, final Activity activity) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String ris = HelperConnessione.httpDeleteConnection("qualcosa/" + idEvento + "/" + DatiAttributi.getPositionItem(pos).id);

                Log.e("Evento-Helper-eliminaRisposta-ris: ", " \nrisposta: " + ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String ris) {
                if (ris.equals("fatto")) {
                    DatiRisposte.removePositionItem(pos);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setMessage(activity.getString(R.string.errDeleteRisposta));

                    alertDialogBuilder.setPositiveButton(activity.getString(R.string.chiudi), new DialogInterface.OnClickListener() {
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

    public static void eliminaDomanda(final int pos, final int idEvento, final Activity activity) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String ris = HelperConnessione.httpDeleteConnection("event/" + idEvento + "/" + DatiAttributi.getPositionItem(pos).id);

                Log.e("eliminaDomanda-ris: ", "event/" + idEvento + "/" + DatiAttributi.getPositionItem(pos).id + " \nrisposta: " + ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String ris) {
                if (ris.equals("fatto")) {
                    DatiAttributi.removePositionItem(pos);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setMessage(activity.getString(R.string.errDeleteDomanda));

                    alertDialogBuilder.setPositiveButton(activity.getString(R.string.chiudi), new DialogInterface.OnClickListener() {
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

    private static void addRisposta(final int idEvento, final int id_attributo, final String risposta, final String template, final ProgressBar pb_add, final ImageButton dialogButton) {
/*=======
    private static void addRisposta(final int idEvento, final int id_attributo, final String risposta, final String template, final ProgressBar pb_add, final ImageButton dialogButton) {
>>>>>>> agg-notifiche*/
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
                    DatiRisposte.addItem(new DatiRisposte.Risposta(Integer.parseInt(ris), risposta, userL), template);
                    edt.setText("");
                } catch (JSONException e) {
                    Log.e("Evento-addRisposta", "JSONException " + e);
                } catch (NumberFormatException e) {
                    Log.e("Evento-addRisposta", "risposta non numerica " + e);
                }

/*
            private boolean isInteger(String s) {
                try {
                    Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
*/
            }
        }.execute(null, null, null);
    }

    /*
    private static void cercami() {
        Boolean trovato = false;
        for (int i = 0; i < DatiRisposte.ITEMS.size() && !trovato; i++) {
            for (int j = 0; DatiRisposte.ITEMS.get(i).persone != null && j < DatiRisposte.ITEMS.get(i).persone.size() && !trovato; j++) {
                if (DatiRisposte.ITEMS.get(i).persone.get(j).id_fb.equals(HelperFacebook.getFacebookId())) {
                    DatiRisposte.ITEMS.get(i).persone.remove(j);
                    trovato = true;
                }
            }
        }
    }
    */

    public static void addDomandaSino(RisposteAdapter adapter, int idEvento, String cosa, ProgressBar pb_sino, int attuale) {

        if (DatiRisposte.getLenght() == 1) {
            addRisposta(idEvento, DatiAttributi.getPositionItem(attuale).id, cosa, "sino", pb_sino, null);
        } else {
            if (cosa.equals("si"))
                vota(idEvento, adapter, null, DatiRisposte.getPositionItem(0).id, 0, pb_sino);
            else {
                vota(idEvento, adapter, null, DatiRisposte.getPositionItem(1).id, 1, pb_sino);
            }
        }
    }

    public static void vota(final int idEvento, final RisposteAdapter adapter, final Button vota, final int idRisposta, final int position, final ProgressBar pb_vota) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                pb_vota.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String[] name, param;

                name = new String[]{"idRisposta"};
                param = new String[]{String.valueOf(idRisposta)};

//<<<<<<< HEAD
                return HelperConnessione.httpPutConnection("event/" + idEvento + "/" + adapter.getId(), name, param);
/*=======
                return HelperConnessione.httpPutConnection("event/" + idEvento + "/" + DatiAttributi.getPositionItem(adapter.getId()).id, name, param);
>>>>>>> agg-notifiche*/
            }

            @Override
            protected void onPostExecute(String ris) {
                pb_vota.setVisibility(View.GONE);
                if (vota != null)
                    vota.setVisibility(View.VISIBLE);

                Log.e("Evento-vota-ris:", ris);

                if (ris.equals("aggiornato")) {
                    graficaVota(position, adapter.getArg2());
                }
            }
        }.execute(null, null, null);
    }

    public static void graficaVota(int position, int attuale) {
        //cercami();

        if (DatiRisposte.getLenght() > position) //serve come controllo di sicurezza ma non dovrebbe mai capitare
            DatiRisposte.addPositionPersona(position, HelperFacebook.getFacebookId(), HelperFacebook.getFacebookUserName(), true);

        int temp = 0;
        String risposta_max = null;
         int idMax = -1;

        for (int i = 0; i < DatiRisposte.getLenght(); i++) {
            if (DatiRisposte.getPositionItem(i).persone.size() > temp) {
                temp = DatiRisposte.getPositionItem(i).persone.size();
                idMax = DatiRisposte.getPositionItem(i).id;
                risposta_max = DatiRisposte.getPositionItem(i).risposta;
            }
        }
/*<<<<<<< HEAD

        DatiAttributi.ITEMS.get(attuale).changeRisposta(risposta_max, idMax);
=======*/
        DatiAttributi.getPositionItem(attuale).changeRisposta(risposta_max, idMax);
//>>>>>>> agg-notifiche
    }

    public static void dialogEventUsers(final String numUtenti, final TextView bnt_friends, final int idEvento, final Activity activity, final String adminEvento) {
        DataProvide.getFriends(idEvento, activity.getApplicationContext());
        dialogFriends = new Dialog(activity);
        dialogFriends.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFriends.setContentView(R.layout.dialog_friends);

        ProgressBar pb = (ProgressBar) dialogFriends.findViewById(R.id.progressBar_addFriends);
        pb.setVisibility(View.VISIBLE);

        ListView utenti = (ListView) dialogFriends.findViewById(R.id.listView_friends);
        FriendsAdapter adapter = DatiFriends.init(idEvento, activity.getApplicationContext());
        utenti.setEmptyView(pb);
        utenti.setAdapter(adapter);

        dialogFriends.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DatiFriends.removeAll();
            }
        });

        Button addFriends = (Button) dialogFriends.findViewById(R.id.btn_addFriends);

        final ProgressBar pb_buttaFuori = (ProgressBar) dialogFriends.findViewById(R.id.pb_deleteUser);

        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddFriends(bnt_friends, numUtenti, idEvento, activity);
            }
        });

        utenti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (adminEvento.equals(HelperFacebook.getFacebookId())) {
                    PopupMenu popup = new PopupMenu(activity, view);
                    popup.getMenuInflater().inflate(R.menu.popup_butta_fuori, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            pb_buttaFuori.setVisibility(View.VISIBLE);
                            eliminaUser(bnt_friends, idEvento, activity, i, pb_buttaFuori);
                            return true;
                        }
                    });
                    popup.show();
                }
            }
        });

        dialogFriends.show();
    }

    private static void sendInviti(String temp, Activity activity, final Dialog dialogAddFriends) {
        WebDialog f = HelperFacebook.inviteFriends(activity, temp);
        f.show();
        f.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogAddFriends.dismiss();
            }
        });
    }

    private static void addFriendsToEvent(final TextView bnt_friends, final String numUtenti, final Activity activity, final int idEvento, final String List, final int quanti_aggiunti) {
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

                ris = HelperConnessione.httpPostConnection("friends/" + idEvento, new String[]{"userList"}, new String[]{List});

                Log.e("Evento-addFriendsToEvent-ris: ", ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String result) {
                id_toSend.clear();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (!result.equals("fatto")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setMessage(activity.getString(R.string.errAggAmico));

                    alertDialogBuilder.setPositiveButton(activity.getString(R.string.chiudi), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    dialogFriends.dismiss();
                    FbFriendsAdapter.svuotaLista();

                    DatiEventi.getIdItem(idEvento).numUtenti+=quanti_aggiunti;
                    bnt_friends.setText(""+DatiEventi.getIdItem(idEvento).numUtenti);

                    /*
                    for (int i = 0; i < DatiEventi.ITEMS.size(); i++) {
                        if (DatiEventi.ITEMS.get(i).id == Integer.parseInt(idEvento)) {
                            DatiEventi.ITEMS.get(i).numUtenti += quanti_aggiunti;
                            int temp = Integer.parseInt(numUtenti) + quanti_aggiunti;
                            bnt_friends.setText("" + temp);
                            break;
                        }
                    }
                    */
                }
            }
        }.execute();
    }

    private static void eliminaUser(final TextView bnt_friends, final int idEvento, final Activity activity, final int i, final ProgressBar pb_buttaFuori) {
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
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setMessage(activity.getString(R.string.errDeleteFriend));

                    alertDialogBuilder.setPositiveButton(activity.getString(R.string.chiudi), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    DatiFriends.removeItem(i);

                    DatiEventi.getIdItem(idEvento).numUtenti--;
                    bnt_friends.setText("" + DatiEventi.getIdItem(idEvento).numUtenti);
                    /*
                    for (int i = 0; i < DatiEventi.ITEMS.size(); i++) {
                        if (DatiEventi.ITEMS.get(i).id == Integer.parseInt(idEvento)) {
                            DatiEventi.ITEMS.get(i).numUtenti -= 1;
                            bnt_friends.setText("" + DatiEventi.ITEMS.get(i).numUtenti);
                            break;
                        }
                    }*/
                }
            }
        }.execute();
    }

    // <editor-fold defaultstate="collapsed" desc="Richiesta amici FB">
    private static void requestMyAppFacebookFriends(Session session, final Activity activity) {
        Request friendsRequest = createRequest(session);
        friendsRequest.setCallback(new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                friends = getResults(response);
                friendsList = new ArrayList<Friends>();

                for (GraphUser user : friends) {
                    //controllo chi ha l'app installata
                    Boolean install = false;
                    if (user.getProperty("installed") != null && user.getProperty("installed").toString().equals("true")) {
                        install = true;
                    }

                    Friends friend = new Friends(user.getId(), user.getName(), false, install);
                    friendsList.add(friend);
                }

                pb.setVisibility(ProgressBar.GONE);

                dataAdapter = new FbFriendsAdapter(activity.getApplicationContext(), null, inputSearch, R.layout.fb_friends, friendsList);
                amiciFB.setAdapter(dataAdapter);
                dataAdapter.setAdapter(dataAdapter);
                friendList = dataAdapter.friendList;
            }
        });
        friendsRequest.executeAsync();
    }

    private static Request createRequest(Session session) {
        Request request = Request.newGraphPathRequest(session, "me/friends", null);

        Set<String> fields = new HashSet<String>();
        String[] requiredFields = new String[]{"id", "name", "installed"};
        fields.addAll(Arrays.asList(requiredFields));

        Bundle parameters = request.getParameters();
        parameters.putString("fields", TextUtils.join(",", fields));
        request.setParameters(parameters);

        return request;
    }

    private static List<GraphUser> getResults(Response response) {
        GraphMultiResult multiResult = response
                .getGraphObjectAs(GraphMultiResult.class);
        GraphObjectList<GraphObject> data = multiResult.getData();
        return data.castToListOf(GraphUser.class);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dialogAddFriends">
    public static void dialogAddFriends(final TextView bnt_friends, final String numUtenti, final int idEvento, final Activity activity) {
        requestMyAppFacebookFriends(HelperFacebook.getSession(activity), activity);
        dialogAddFriends = new Dialog(activity);
        dialogAddFriends.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddFriends.setContentView(R.layout.dialog_friends);
        dialogAddFriends.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        pb = (ProgressBar) dialogAddFriends.findViewById(R.id.progressBar_addFriends);
        pb.setVisibility(View.VISIBLE);

        inputSearch = (EditText) dialogAddFriends.findViewById(R.id.editText_search_dialog_friends);
        inputSearch.setVisibility(View.VISIBLE);

        amiciFB = (ListView) dialogAddFriends.findViewById(R.id.listView_friends);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                int textlength = cs.length();
                ArrayList<Friends> tempArrayList = new ArrayList<Friends>();
                if (friendList != null && friendList.size() > 0) {
                    for (Friends friends1 : friendList) {
                        if (textlength <= friends1.getName().length()) {
                            if (friends1.getName().toLowerCase().contains(cs.toString().toLowerCase())) {
                                tempArrayList.add(friends1);
                            }
                        }
                    }
                    dataAdapter = new FbFriendsAdapter(activity.getApplicationContext(), null, inputSearch, R.layout.fb_friends, tempArrayList);
                    amiciFB.setAdapter(dataAdapter);
                    dataAdapter.setAdapter(dataAdapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        Button addFriends = (Button) dialogAddFriends.findViewById(R.id.btn_addFriends);
        addFriends.setText(activity.getString(R.string.addFriends));
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_toSend = new ArrayList<String>();

                StringBuilder id_to_invite = new StringBuilder();
                List<Friends> finali = dataAdapter.getFinali();

                for (Friends aFinali : finali) {
                    if (aFinali.getAppInstalled()) {
                        id_toSend.add(aFinali.getCode());
                    } else {
                        String temp = aFinali.getCode();
                        if (id_to_invite.length() != 0)
                            temp = ", " + temp;

                        id_to_invite.append(temp);
                    }
                }
                JSONArray jsArray = new JSONArray(id_toSend);

                Log.e("Evento-AddFriends-Persone prima di invio: ", jsArray.toString());
                addFriendsToEvent(bnt_friends, numUtenti, activity, idEvento, jsArray.toString(), jsArray.length());

                if (!id_to_invite.toString().equals(""))
                    sendInviti(id_to_invite.toString(), activity, dialogAddFriends);

                dialogAddFriends.dismiss();
            }
        });

        dialogAddFriends.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (id_toSend != null)
                    id_toSend.clear();
            }
        });

        dialogAddFriends.show();

    }
    // </editor-fold">

}
