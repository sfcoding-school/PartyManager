package com.partymanager.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.partymanager.R;

public class DrawerAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private String[] mStrings;
    private TypedArray mIcons;
    private Boolean alto;

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
        }

        return convertView;
    }
}
