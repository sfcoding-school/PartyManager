package com.partymanager.data.Adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
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
import com.partymanager.activity.ProfileActivity;
import com.partymanager.helper.HelperFacebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DrawerAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private String[] mStrings;
    private TypedArray mIcons;
    private Boolean alto;
    private int mViewResourceId;
    Context context;
    ImageView iv;

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

        iv = (ImageView) convertView.findViewById(R.id.imgV_drawer_line);
        iv.setImageDrawable(mIcons.getDrawable(position));

        final TextView tv = (TextView) convertView.findViewById(R.id.txt_drawer_line);
        tv.setText(mStrings[position]);
        tv.setTextColor(Color.BLACK);
        if (!alto) {
            tv.setTextSize(20);
        } else {
            tv.setTextSize(25);
            if (position == 0) {
                String username = HelperFacebook.getFacebookUserName(context);
                tv.setText(username);
                Request.executeMeRequestAsync(HelperFacebook.getSession(MainActivity.getActivity()), new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            tv.setText(user.getName());
                        }
                    }
                });

                loadImageFromStorage();
                iv.setMaxWidth(50);
                iv.setMaxWidth(50);
            } else {
                iv.setMaxWidth(30);
                iv.setMaxWidth(30);
            }

        }

        return convertView;

    }

    private void loadImageFromStorage() {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f = new File(directory, "profilelarge.jpg");
            iv.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
        } catch (FileNotFoundException e) {
            getFacebookProfilePicture(HelperFacebook.getFacebookId(context), "large");
        }
    }

    private void getFacebookProfilePicture(final String userID, final String quale) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... args) {
                URL imageURL;
                Bitmap bitmap = null;
                try {
                    imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=" + quale);
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                Log.e("DrawerAdapter-onPost", String.valueOf(bitmap));
                if (bitmap != null) {
                    new ProfileActivity().saveToInternalStorage(bitmap, quale);
                    iv.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

}
