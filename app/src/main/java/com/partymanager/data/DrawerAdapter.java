package com.partymanager.data;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.partymanager.R;
import com.partymanager.activity.ProfileActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DrawerAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private String[] mStrings;
    private TypedArray mIcons;
    private Boolean alto;
    public final String REG_USERNAME = "reg_username";
    private int mViewResourceId;

    public DrawerAdapter(Context ctx, int viewResourceId, String[] strings, TypedArray icons, Boolean alto) {
        super(ctx, viewResourceId, strings);

        this.mInflater = (LayoutInflater) ctx.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.mStrings = strings;
        this.mIcons = icons;
        this.mViewResourceId = viewResourceId;
        this.alto = alto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(mViewResourceId, null);

        ImageView iv = (ImageView) convertView.findViewById(R.id.imgV_drawer_line);
        iv.setImageDrawable(mIcons.getDrawable(position));

        TextView tv = (TextView) convertView.findViewById(R.id.txt_drawer_line);
        tv.setText(mStrings[position]);
        tv.setTextColor(Color.BLACK);
        if (!alto) {
            tv.setTextSize(20);
        } else {
            tv.setTextSize(25);
            if (position == 0) {
                SharedPreferences prefs = getPreferences();
                String username_pref = prefs.getString(REG_USERNAME, "");
                tv.setText(username_pref);
                iv.setImageBitmap(loadImageFromStorage());
                iv.setMaxWidth(50);
                iv.setMaxWidth(50);
            } else {
                iv.setMaxWidth(30);
                iv.setMaxWidth(30);
            }

        }

        return convertView;

    }

    public SharedPreferences getPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return new ContextWrapper(getContext()).getSharedPreferences(ProfileActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private Bitmap loadImageFromStorage() {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f = new File(directory, "profilelarge.jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
