package com.partymanager.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

    Button add_friends;
    ImageButton finito;
    EditText nome_evento;
    TextView container_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_evento);

        add_friends = (Button) findViewById(R.id.btn_add_friends);
        finito = (ImageButton) findViewById(R.id.imageButton);
        nome_evento = (EditText) findViewById(R.id.etxt_nome_evento);
        container_friends = (TextView) findViewById(R.id.txt_container_friends);

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            requestMyAppFacebookFriends(session);
        } else {
            Toast.makeText(getApplicationContext(), "session is not opened", Toast.LENGTH_LONG).show();
        }

        updateView();
    }

    List<GraphUser> friends;

    private void requestMyAppFacebookFriends(Session session) {
        Request friendsRequest = createRequest(session);
        friendsRequest.setCallback(new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                friends = getResults(response);

                //GraphUser user = friends.get(0);
                //Log.e("TEST", Integer.toString(friends.size()) );

                Log.e("TEST", Integer.toString(friends.size()));
                friendsList = new ArrayList<Friends>();
                for (int i = 0; i < friends.size(); i++) {
                    GraphUser user = friends.get(i);
                    Friends friend = new Friends(user.getId(), user.getName(), false);
                    friendsList.add(friend);
                }
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

    MyCustomAdapter dataAdapter = null;//////////////////////////////////////////////
    ArrayList<Friends> friendsList;

    private void updateView() {

        add_friends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (friends != null && friendsList != null) {
                    dialog_open();
                } else {
                    Toast.makeText(getApplicationContext(), "Waiting for FriendsList", Toast.LENGTH_LONG).show();
                }
            }
        });
        finito.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "click check", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dialog_open() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom);
        dialog.setTitle("Aggiungi amici");


        // set the custom dialog components - text, image and button
        dataAdapter = new MyCustomAdapter(this, R.layout.fb_friends, friendsList);
        ListView listView = (ListView) dialog.findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Friends friends1 = (Friends) parent.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), "Clicked on Row: " + friends1.getName(), Toast.LENGTH_LONG).show();
            }
        });

        Button myButton = (Button) dialog.findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                container_friends.append("");

                ArrayList<Friends> friendList = dataAdapter.friendList;
                for (int i = 0; i < friendList.size(); i++) {
                    Friends friends1 = friendList.get(i);
                    if (friends1.isSelected()) {
                        container_friends.append("\n" + friends1.getName());
                    }
                }
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crea_evento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
        return;
    }


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
                        //Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                        friends1.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Friends friends1 = friendList.get(position);
            holder.name.setText("" + friends1.getName());
            holder.name.setChecked(friends1.isSelected());
            holder.name.setTag(friends1);
            holder.foto_profilo.setImageBitmap(friends1.foto);
            return convertView;

        }
    }
}