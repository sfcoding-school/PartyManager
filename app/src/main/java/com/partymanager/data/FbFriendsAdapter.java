package com.partymanager.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.partymanager.R;
import com.partymanager.helper.HelperFacebook;

import java.util.ArrayList;
import java.util.List;

public class FbFriendsAdapter extends ArrayAdapter<Friends> {

    public ArrayList<Friends> friendList;
    private Context context;
    private TextView container_friends;
    private EditText inputSearch;
    private static List<Friends> finali = null;
    private FbFriendsAdapter adapter;

    public FbFriendsAdapter(Context context, TextView cointaner, EditText inputSearch, int textViewResourceId, ArrayList<Friends> friendList) {
        super(context, textViewResourceId, friendList);
        this.friendList = new ArrayList<Friends>();
        this.context = context;
        this.friendList.addAll(friendList);
        this.container_friends = cointaner;
        this.inputSearch = inputSearch;
        if (finali == null)
            finali = new ArrayList<Friends>();
    }

    public void setAdapter(FbFriendsAdapter adapter) {
        this.adapter = adapter;
    }

    private class ViewHolder {
        CheckBox name;
        ImageView foto_profilo;
        TextView installed;
    }

    public static void svuotaLista() {
        if (finali != null)
            finali.clear();
    }

    public List<Friends> getFinali() {
        return finali;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.fb_friends, null);

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.foto_profilo = (ImageView) convertView.findViewById(R.id.img_profilo);
            holder.installed = (TextView) convertView.findViewById(R.id.txt_installed);

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
        if (friends1.getAppInstalled())
            holder.installed.setVisibility(View.VISIBLE);
        else
            holder.installed.setVisibility(View.GONE);
        holder.name.setTag(friends1);

        //Gestione foto profilo nella listview
        holder.foto_profilo.setImageBitmap(null);
        holder.foto_profilo.setBackground(context.getResources().getDrawable(R.drawable.com_facebook_profile_default_icon));
        if (friends1.foto != null) {
            holder.foto_profilo.setBackground(null);
            holder.foto_profilo.setImageBitmap(friends1.getFoto());
        } else {
            HelperFacebook.getFacebookProfilePicture(friends1, adapter, 0);
        }

        return convertView;
    }

    //Aggiungo gli amici scelti all'activity e alla lista "finali"
    private void delete_friend_to_activity(String toDelete) {
        container_friends.setText("");
        Friends friends1;

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