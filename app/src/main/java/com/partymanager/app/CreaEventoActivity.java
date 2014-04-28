package com.partymanager.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.partymanager.R;

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
    MyCustomAdapter dataAdapter = null;
    ArrayList<Friends> friendsList;
    List<GraphUser> friends;
    List<Friends> finali = null;

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

        //Controllo sessione FB
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            //se c'è la sessione richiedo subito la lista amici
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                requestMyAppFacebookFriends(session);
            } else {
                Toast.makeText(getApplicationContext(), "Connessione assente", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "session is not opened", Toast.LENGTH_LONG).show();
        }

        finali = new ArrayList<Friends>();

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
                for (int i = 0; i < friends.size(); i++) {
                    GraphUser user = friends.get(i);
                    Friends friend = new Friends(user.getId(), user.getName(), false);
                    friendsList.add(friend);
                }

                pb.setVisibility(ProgressBar.INVISIBLE);

                dataAdapter = new MyCustomAdapter(CreaEventoActivity.this, R.layout.fb_friends, friendsList);
                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);
                friendList = dataAdapter.friendList;

            }
        });
        friendsRequest.executeAsync();
    }

    private Request createRequest(Session session) {
        Request request = Request.newGraphPathRequest(session, "me/friends", null);

        Set<String> fields = new HashSet<String>();
        String[] requiredFields = new String[]{"id", "name", "picture"};
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

        //Event Listener
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Friends friends1 = (Friends) parent.getItemAtPosition(position);
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                int textlength = cs.length();
                ArrayList<Friends> tempArrayList = new ArrayList<Friends>();
                for (int i = 0; i < friendList.size(); i++) {
                    Friends friends1 = friendList.get(i);
                    if (textlength <= friends1.getName().length()) {
                        if (friends1.getName().toLowerCase().contains(cs.toString().toLowerCase())) {
                            tempArrayList.add(friends1);
                        }
                    }
                }
                MyCustomAdapter mAdapter = new MyCustomAdapter(CreaEventoActivity.this, R.layout.fb_friends, tempArrayList);
                listView.setAdapter(mAdapter);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        finito.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if ("".equals(nome_evento.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Devi inserire un nome per l'evento", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "click check", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Click pulsante indietro
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreaEventoActivity.this);
        alertDialogBuilder.setMessage("Eliminare nuovo evento?");

        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                CreaEventoActivity.this.finish();
            }
        });

        // set negative button: No message
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return;
    }

    //Adapter per ListView
    private class MyCustomAdapter extends ArrayAdapter<Friends> {

        private ArrayList<Friends> friendList;

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Friends> friendList) {
            super(context, textViewResourceId, friendList);
            this.friendList = new ArrayList<Friends>();
            this.friendList.addAll(friendList);
        }

        private class ViewHolder {
            CheckBox name;
            ImageView foto_profilo;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.fb_friends, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.foto_profilo = (ImageView) convertView.findViewById(R.id.img_profilo);
                convertView.setTag(holder);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Friends friends1 = (Friends) cb.getTag();
                        friends1.setSelected(cb.isChecked());

                        if (cb.isChecked()) {
                            if (container_friends.getText().length() == 0)
                                container_friends.setText(friends1.getName());
                            else {
                                container_friends.append(", " + friends1.getName());
                            }
                            finali.add(friends1);
                        } else {
                            delete_friend_to_activity(friends1.getName());
                        }
                        inputSearch.setText("");
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Friends friends1 = friendList.get(position);
            holder.name.setText(friends1.getName());
            holder.name.setChecked(friends1.isSelected());
            holder.name.setTag(friends1);
            holder.foto_profilo.setImageBitmap(friends1.foto);

            return convertView;
        }

        //Aggiungo gli amici scelti all'activity e alla lista "finali"
        private void delete_friend_to_activity(String toDelete) {
            container_friends.setText("");
            Friends friends1 = null;

            for (int i = 0; i < finali.size(); i++) {
                friends1 = finali.get(i);
                if (friends1.getName().equals(toDelete)) {
                    finali.remove(i);
                }
            }
            for (int i = 0; i < finali.size(); i++) {
                friends1 = finali.get(i);
                if (i == 0) {
                    container_friends.append(friends1.getName());
                } else {
                    container_friends.append(", " + friends1.getName());
                }
            }
        }
    }
}