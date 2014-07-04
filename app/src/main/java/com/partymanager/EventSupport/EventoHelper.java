package com.partymanager.EventSupport;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.partymanager.data.Adapter.RisposteAdapter;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiRisposte;
import com.partymanager.data.Friends;
import com.partymanager.helper.HelperFacebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventoHelper {

    private static EditText edt;
    static ProgressBar pb;
    static ArrayList<Friends> friendList;
    static FbFriendsAdapter dataAdapter = null;
    static ArrayList<Friends> friendsList;
    static List<GraphUser> friends;
    static EditText inputSearch;
    static ListView amiciFB;
    static Dialog dialogAddFriends;
    static ArrayList<String> id_toSend;
    static int posAttributi;
    static Button si;
    static Button no;
    static ImageButton dialogButton;
    static DatePicker dateR;
    static Button add;
    static int idAttributo;
    static private Dialog dialog;
    static LinearLayout normal;
    static LinearLayout sino;
    static LinearLayout dataL;
    private static ArrayList<String> name_toSend;
    static public ListView risp;

    private static Dialog getRisposteDialog(Activity activity) {
        if (dialog == null) {
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_risposte);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        return dialog;
    }

    public static int getIdAttributo() {
        if (dialog != null && dialog.isShowing())
            return idAttributo;
        else return -1;
    }

    public static void dialogRisposte(final String adminEvento, int posAttr, final Activity activity, final int idEvento) {
        posAttributi = posAttr;

        dialog = getRisposteDialog(activity);
        idAttributo = DatiAttributi.getPositionItem(posAttributi).id;

        risp = (ListView) dialog.findViewById(R.id.listView_risposte);
        final RisposteAdapter adapter = DatiRisposte.init(activity.getApplicationContext(), idEvento, DatiAttributi.getPositionItem(posAttributi).id, posAttributi, DatiAttributi.getPositionItem(posAttributi).close);
        risp.setEmptyView(dialog.findViewById(R.id.pb_risposteEmptyView));
        risp.setAdapter(adapter);

        risp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (adminEvento.equals(HelperFacebook.getFacebookId())) {

                    PopupMenu popup = new PopupMenu(activity, view);
                    popup.getMenuInflater().inflate(R.menu.popup_delete, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            EventAsync.eliminaRisposta(i, idEvento, DatiRisposte.getPositionItem(i).id, activity);
                            return true;
                        }
                    });

                    popup.show();
                }
            }
        });

        TextView text = (TextView) dialog.findViewById(R.id.txt_domanda_dialog);
        text.setText(DatiAttributi.getPositionItem(posAttributi).domanda);

        dialogButton = (ImageButton) dialog.findViewById(R.id.imgBSend);
        edt = (EditText) dialog.findViewById(R.id.edtxt_nuovaRisposta);
        edt.setVisibility(View.VISIBLE);
        edt.setText("");
        edt.setHint("Scrivi qui la tua risposta");

        final ProgressBar pb_add = (ProgressBar) dialog.findViewById(R.id.pb_addRisposta);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(edt.getText().toString())) {
                    dialogButton.setVisibility(View.GONE);
                    pb_add.setVisibility(View.VISIBLE);

                    if (!DatiAttributi.getPositionItem(posAttributi).close) {
                        EventAsync.addRisposta(edt, activity, idEvento, DatiAttributi.getPositionItem(posAttributi).id, edt.getText().toString(), DatiAttributi.getPositionItem(posAttributi).template, pb_add, dialogButton);
                    } else {
                        EventAsync.modificaChiusaAsync(idAttributo, activity, 0, edt.getText().toString(), idEvento);
                    }
                }
            }
        });

        normal = (LinearLayout) dialog.findViewById(R.id.risposta_stringa);
        sino = (LinearLayout) dialog.findViewById(R.id.linearL_sino);
        dataL = (LinearLayout) dialog.findViewById(R.id.linearL_data);

        if (DatiAttributi.getPositionItem(posAttributi).template != null) {
            if (DatiAttributi.getPositionItem(posAttributi).template.equals("sino")) {


                final ProgressBar pb_sino = (ProgressBar) dialog.findViewById(R.id.pb_sino);

                no = (Button) dialog.findViewById(R.id.btn_risp_no);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pb_sino.setVisibility(View.VISIBLE);

                        if (!DatiAttributi.getPositionItem(posAttributi).close) {
                            addDomandaSino(activity, adapter, idEvento, "no", pb_sino, posAttributi);
                        } else {
                            EventAsync.modificaChiusaAsync(idAttributo, activity, 0, "no", idEvento);
                        }
                    }
                });

                si = (Button) dialog.findViewById(R.id.btn_risp_si);
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pb_sino.setVisibility(View.VISIBLE);
                        if (!DatiAttributi.getPositionItem(posAttributi).close) {
                            addDomandaSino(activity, adapter, idEvento, "si", pb_sino, posAttributi);
                        } else {
                            EventAsync.modificaChiusaAsync(idAttributo, activity, 0, "si", idEvento);
                        }
                    }
                });

            }

            if (DatiAttributi.getPositionItem(posAttributi).template.equals("data")) {

                final ProgressBar pb_data = (ProgressBar) dialog.findViewById(R.id.pb_data);
                dateR = (DatePicker) dialog.findViewById(R.id.datePicker_risposta);
                add = (Button) dialog.findViewById(R.id.button_rispndi_data);

                /*
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                dateR.setMinDate(cal.getTimeInMillis() - 10000);
                dateR.updateDate(year, month, day);
                */
                setMinDate(dateR, new GregorianCalendar().getTimeInMillis() - 1000);

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String temp = Integer.toString(dateR.getDayOfMonth()) + "/" + Integer.toString(dateR.getMonth() + 1) + "/" + Integer.toString(dateR.getYear());
                        pb_data.setVisibility(View.VISIBLE);

                        if (DatiAttributi.getPositionItem(posAttributi).close) {
                            EventAsync.modificaChiusaAsync(idAttributo, activity, 0, temp, idEvento);
                        } else {
                            EventAsync.addRisposta(edt, activity, idEvento, DatiAttributi.getPositionItem(posAttributi).id, temp, "data", pb_data, null);
                        }
                    }
                });
            }
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    DatiRisposte.removeAll(idEvento, DatiAttributi.getPositionItem(posAttributi).id);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("Evento-dialog.setOnDismissListener", "IndexOutOfBoundsException " + e);
                }
            }
        });

        modificaGrafica(false);

        dialog.show();
    }

    public static void setMinDate(DatePicker dateR, long time) {
        try {
            dateR.setMinDate(time);
        }catch(IllegalArgumentException e){
            setMinDate(dateR, time - 1000);
        }
    }

    public static void closeDialog() {
        dialog.dismiss();
    }

    public static void modificaGrafica(boolean modifica) {

        if (DatiAttributi.getPositionItem(posAttributi).close && !modifica) {
            normal.setVisibility(View.GONE);
            dataL.setVisibility(View.GONE);
            sino.setVisibility(View.GONE);
        } else {
            if (DatiAttributi.getPositionItem(posAttributi).template != null) {
                if (DatiAttributi.getPositionItem(posAttributi).template.equals("data")) {
                    normal.setVisibility(View.GONE);
                    normal.setVisibility(View.GONE);
                    dataL.setVisibility(View.VISIBLE);
                }
                if (DatiAttributi.getPositionItem(posAttributi).template.equals("sino")) {
                    normal.setVisibility(View.GONE);
                    dataL.setVisibility(View.GONE);
                    sino.setVisibility(View.VISIBLE);
                }
                if (!DatiAttributi.getPositionItem(posAttributi).template.equals("data") && !DatiAttributi.getPositionItem(posAttributi).template.equals("sino")) {
                    normal.setVisibility(View.VISIBLE);
                    dataL.setVisibility(View.GONE);
                    sino.setVisibility(View.GONE);
                }
            } else {
                normal.setVisibility(View.VISIBLE);
                dataL.setVisibility(View.GONE);
                sino.setVisibility(View.GONE);
                sino.setVisibility(View.GONE);
            }
        }
    }

    public static void addDomandaSino(Activity activity, RisposteAdapter adapter, int idEvento, String cosa, ProgressBar pb_sino, int attuale) {

        if (DatiRisposte.getLenght() == 1) {
            EventAsync.addRisposta(edt, activity, idEvento, DatiAttributi.getPositionItem(attuale).id, cosa, "sino", pb_sino, null);
        } else {
            if (cosa.equals("si"))
                EventAsync.vota(idEvento, adapter, null, DatiRisposte.getPositionItem(0).id, 0, pb_sino);
            else {
                EventAsync.vota(idEvento, adapter, null, DatiRisposte.getPositionItem(1).id, 1, pb_sino);
            }
        }
    }

    public static void graficaVota(int position, int attuale) {

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

        DatiAttributi.getPositionItem(attuale).changeRisposta(risposta_max, idMax);

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
    public static void dialogAddFriends(final TextView bnt_friends, final int idEvento, final Activity activity) {
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
                name_toSend = new ArrayList<String>();

                StringBuilder id_to_invite = new StringBuilder();
                List<Friends> finali = dataAdapter.getFinali();

                for (Friends aFinali : finali) {
                    if (aFinali.getAppInstalled()) {
                        id_toSend.add(aFinali.getCode());
                        name_toSend.add(aFinali.getName());
                    } else {
                        String temp = aFinali.getCode();
                        if (id_to_invite.length() != 0)
                            temp = ", " + temp;

                        id_to_invite.append(temp);
                    }
                }

                EventAsync.addFriendsToEvent(new ArrayList<String>(id_toSend), new ArrayList<String>(name_toSend), bnt_friends, activity, idEvento);

                if (!id_to_invite.toString().equals(""))
                    sendInviti(id_to_invite.toString(), activity, dialogAddFriends);

                dialogAddFriends.dismiss();


            }
        });

        dialogAddFriends.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (id_toSend != null) id_toSend.clear();
                if (name_toSend != null) name_toSend.clear();
                FbFriendsAdapter.svuotaLista();
            }
        });

        dialogAddFriends.show();

    }
    // </editor-fold">
}
