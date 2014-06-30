package com.partymanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.partymanager.data.DatiEventi;
import com.partymanager.data.Friends;
import com.partymanager.helper.HelperConnessione;
import com.partymanager.helper.HelperFacebook;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreaEventoActivity extends Activity {

    //Layout Elements
    ImageButton finito;
    EditText nome_evento;
    TextView container_friends;
    ListView listView;
    EditText inputSearch;
    ProgressBar pb;

    ArrayList<Friends> friendList;
    FbFriendsAdapter dataAdapter = null;
    ArrayList<Friends> friendsList;
    List<GraphUser> friends;
    List<Friends> finali = null;
    NetworkInfo networkInfo;
    ArrayList<String> id_toSend;
    public final String REG_ID = "reg_id";
    ProgressDialog progressDialog;
    int result_global;
    WebDialog f;
    Toast a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_evento);

        //SET LAYOUT
        finito = (ImageButton) findViewById(R.id.imageButton);
        nome_evento = (EditText) findViewById(R.id.etxt_nome_evento);
        container_friends = (TextView) findViewById(R.id.txt_container_friends);
        container_friends.setText("");
        listView = (ListView) findViewById(R.id.listView1);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        pb = (ProgressBar) findViewById(R.id.progressBar_creaEvento);
        pb.setVisibility(ProgressBar.VISIBLE);

        initView();
    }

    private void initView() {
        //setto networkInfo per controllo accesso a internet
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        //Controllo sessione FB
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            //se c'è la sessione e internet accessibile richiedo subito la lista amici
            if (networkInfo != null && networkInfo.isConnected()) {
                requestMyAppFacebookFriends(session);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreaEventoActivity.this);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setMessage(getString(R.string.connAssente));


                alertDialogBuilder.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (id_toSend != null) id_toSend.clear();
                        container_friends.setText("");
                        FbFriendsAdapter.svuotaLista();
                        CreaEventoActivity.this.finish();
                        dialog.cancel();
                    }
                });

                alertDialogBuilder.setPositiveButton(getString(R.string.riprova), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        initView();
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.errFB), Toast.LENGTH_LONG).show();
        }

        updateView();
    }

    //Richiesta amici FB
    private void requestMyAppFacebookFriends(Session session) {
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

                pb.setVisibility(ProgressBar.INVISIBLE);

                dataAdapter = new FbFriendsAdapter(CreaEventoActivity.this, container_friends, inputSearch, R.layout.fb_friends, friendsList);
                listView.setAdapter(dataAdapter);
                dataAdapter.setAdapter(dataAdapter);
                friendList = dataAdapter.friendList;
            }
        });
        friendsRequest.executeAsync();
    }

    private Request createRequest(Session session) {
        Request request = Request.newGraphPathRequest(session, "me/friends", null);

        Set<String> fields = new HashSet<String>();
        String[] requiredFields = new String[]{"id", "name", "installed"};
        fields.addAll(Arrays.asList(requiredFields));

        Bundle parameters = request.getParameters();
        parameters.putString("fields", TextUtils.join(",", fields));
        request.setParameters(parameters);

        return request;
    }

    private List<GraphUser> getResults(Response response) {
        GraphMultiResult multiResult = response
                .getGraphObjectAs(GraphMultiResult.class);
        GraphObjectList<GraphObject> data = multiResult.getData();
        return data.castToListOf(GraphUser.class);
    }
    //FINE Richiesta amici FB

    private void updateView() {

        finali = new ArrayList<Friends>();

        //Listener EditText per ricerca amici
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
                    dataAdapter = new FbFriendsAdapter(CreaEventoActivity.this, container_friends, inputSearch, R.layout.fb_friends, tempArrayList);
                    listView.setAdapter(dataAdapter);
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

        finito.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finali = dataAdapter.getFinali();
                if ("".equals(nome_evento.getText().toString())/* || finali.isEmpty() */) { //controllo se inserito almeno un amico.. da rimettere poi
                    StringBuilder output = new StringBuilder();
                    if ("".equals(nome_evento.getText().toString())) {
                        output.append(getString(R.string.insrtNameE));
                    }
                    if (finali.isEmpty()) {
                        if (output.length() != 0)
                            output.append("\n");
                        output.append(getString(R.string.insrtAE));
                    }
                    Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
                } else {
                    id_toSend = new ArrayList<String>();
                    StringBuilder id_to_invite = new StringBuilder();

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

                    final SharedPreferences prefs = getPreferences();
                    String registrationId = prefs.getString(REG_ID, "");

                    if (registrationId.isEmpty()) {
                        Log.e(getLocalClassName(), "problema REG_ID vuoto");
                    } else {
                        Log.e("CreaEventoActivity-updateView - Persone prima di invio: ", jsArray.toString());

                        sendNewEvent(nome_evento.getText().toString(), registrationId, jsArray);
                        if (id_to_invite.length() > 0) {
                            Log.e("CreaEventoActivity-updateView - Persone invito FB: ", id_to_invite.toString());
                            sendInviti(id_to_invite.toString(), nome_evento.getText().toString(), jsArray);
                        }
                    }
                }
            }
        });
    }

    private void sendInviti(String temp, final String name, final JSONArray List) {
        f = HelperFacebook.inviteFriends(this, temp);
        f.show();
        f.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                closeActivity(List.length() + 1, name, result_global);
            }
        });
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(ProfileActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void sendNewEvent(final String name, final String ID_FB, final JSONArray List) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... args) {
                String ris;

                ris = HelperConnessione.httpPostConnection("event", new String[]{"name", "userList", "admin"}, new String[]{name, List.toString(), ID_FB});

                Log.e("CreaEventoActivity-sendNewEvent-ris: ", ris);

                return ris;
            }

            @Override
            protected void onPostExecute(String result) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                try {
                    int tmp = Integer.parseInt(result);
                    FbFriendsAdapter.svuotaLista();
                    result_global = tmp;
                    if (f == null || (f != null && !f.isShowing()))
                        closeActivity(List.length() + 1, name, tmp);

                } catch (NumberFormatException e) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreaEventoActivity.this);
                    alertDialogBuilder.setMessage(getString(R.string.errCreazEvento));

                    alertDialogBuilder.setPositiveButton(getString(R.string.chiudi), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            @Override
            protected void onPreExecute() {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                if (getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                progressDialog = new ProgressDialog(CreaEventoActivity.this);
                progressDialog.setMessage(getApplicationContext().getString(R.string.creazioneEvento));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

        }.execute();
    }

    private void closeActivity(int num_utenti, String nome_evento, int id_evento) {
        Intent intent = new Intent();
        intent.putExtra("id_evento", id_evento);
        setResult(0, intent);
        DatiEventi.addItem(new DatiEventi.Evento(id_evento, nome_evento, "", "", HelperFacebook.getFacebookId(), num_utenti));
        finish();
    }

    public void onBackPressed() {
        if (a == null)
            a = Toast.makeText(getApplicationContext(), getString(R.string.esciCreaEvento), Toast.LENGTH_LONG);

        if (a.getView().isShown()) {
            if (id_toSend != null) id_toSend.clear();
            container_friends.setText("");
            FbFriendsAdapter.svuotaLista();
            CreaEventoActivity.this.finish();
        } else {
            a.show();
        }
    }
}