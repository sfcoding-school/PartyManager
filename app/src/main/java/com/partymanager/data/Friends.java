package com.partymanager.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Friends {

    String code = null;
    String name = null;
    boolean selected = false;
    boolean appInstalled;
    Bitmap foto = null;

    public Friends(String code, String name, boolean selected, boolean appI) {
        super();
        this.code = code;
        this.name = name;
        this.selected = selected;
        this.appInstalled = appI;
        getFacebookProfilePicture();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Boolean getAppInstalled(){return appInstalled; }

    public Bitmap getFoto(){
        return foto;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFoto(Bitmap fotot) {
        this.foto = fotot;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

   private void getFacebookProfilePicture() {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... args) {
                URL imageURL;
                Bitmap bitmap = null;
                try {
                    imageURL = new URL("https://graph.facebook.com/" + code + "/picture?type=small");
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
                // safety check
                if (bitmap != null) {
                    setFoto(bitmap);
                }
            }
        }.execute();
    }



}
