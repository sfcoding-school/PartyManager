package com.partymanager.data.Adapter;

import android.content.Context;
import android.content.ContextWrapper;
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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.helper.HelperFacebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DrawerAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private String[] mStrings;
    private TypedArray mIcons;
    private Boolean alto;
    private int mViewResourceId;
    Context context;

    public DrawerAdapter(Context ctx, int viewResourceId, String[] strings, TypedArray icons, Boolean alto) {
        super(ctx, viewResourceId, strings);

        this.mInflater = (LayoutInflater) ctx.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.mStrings = strings;
        this.mIcons = icons;
        this.mViewResourceId = viewResourceId;
        this.alto = alto;
        this.context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(mViewResourceId, null);

        ImageView iv = (ImageView) convertView.findViewById(R.id.imgV_drawer_line);
        iv.setImageDrawable(mIcons.getDrawable(position));

        final TextView tv = (TextView) convertView.findViewById(R.id.txt_drawer_line);
        tv.setText(mStrings[position]);
        tv.setTextColor(Color.BLACK);
        if (!alto) {
            tv.setTextSize(20);
        } else {
            tv.setTextSize(25);
            if (position == 0) {
                String username = HelperFacebook.getFacebookUserName();
                if (username == null) {
                    Request.executeMeRequestAsync(HelperFacebook.getSession(MainActivity.getActivity()), new Request.GraphUserCallback() {

                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                tv.setText(user.getName());
                            }
                        }
                    });
                } else {
                    tv.setText(username);
                }
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

    private Bitmap loadImageFromStorage() {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f = new File(directory, "profilelarge.jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {

            return null;
        }
    }

}
