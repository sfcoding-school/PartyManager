package com.partymanager.helper;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luca on 5/6/14.
 */
public class HelperConnessione {

    private static HttpClient httpclient = null;

    static public boolean login(String idFacebook, String token) {
        String[] name = {"idFacebook","token"};
        String[] param = {idFacebook,token};
        String ris = HelperConnessione.httpPostConnection("http://androidpartymanager.herokuapp.com/login", name , param);
        Log.e("DATA_PROVIDE", "login " + ris);
        if (ris.equals("fatto"))
            return true;
        else
            return false;
    }


    static public boolean logout() {
        String[] name = {};
        String[] param = {};
        String ris = HelperConnessione.httpPostConnection("http://androidpartymanager.herokuapp.com/logout", name , param);

        if (ris!="fatto")
            return false;
        else
            return true;
    }

    static public String httpPostConnection(String url, String[] name, String[] param) {

        httpclient = getHttpclient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            for (int i = 0; i < name.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(name[i], param[i]));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);

            return EntityUtils.toString(response.getEntity());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
    static private HttpClient getHttpclient(){
        if (httpclient == null)
            httpclient = new DefaultHttpClient();
        return httpclient;
    }

}