package com.partymanager.helper;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HelperConnessione {

    private static HttpClient httpclient = null;

    static public boolean login() {
        String[] name = {"idFacebook", "token"};
        String[] param = {HelperFacebook.getFacebookId() , HelperFacebook.getToken() };
        String ris = HelperConnessione.httpPostConnection("http://androidpartymanager.herokuapp.com/login", name, param);
        Log.e("DATA_PROVIDE", "login " + ris);
        return ris.equals("fatto");
    }


    static public boolean logout() {
        String[] name = {};
        String[] param = {};
        String ris = HelperConnessione.httpPostConnection("http://androidpartymanager.herokuapp.com/logout", name, param);

        return ris.equals("fatto");
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

            String temp = EntityUtils.toString(response.getEntity());

            if(temp.equals("session error")){
                login();
                httpPostConnection(url, name, param);
            } else {
                return temp;
            }
        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-ClientProtocolException: ", e.toString() );
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-UnsupportedEncodingException: ", e.toString() );
        } catch (IOException e) {
            Log.e("HelperConnessione-IOException: ", e.toString() );
        }
        return "error";
    }

    static private HttpClient getHttpclient() {
        if (httpclient == null)
            httpclient = new DefaultHttpClient();
        return httpclient;
    }

    static public String httpGetConnection(String url) {
        httpclient = getHttpclient();
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse response = httpclient.execute(httpget);
            String test_ritorno = EntityUtils.toString(response.getEntity());
            Log.e("httpGetConnection-Ris: ", test_ritorno);

            if(test_ritorno.equals("session error")){
                login();
                return httpGetConnection(url);
            }

            return test_ritorno;

        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-ClientProtocolException: ", e.toString() );
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-UnsupportedEncodingException: ", e.toString() );
        } catch (IOException e) {
            Log.e("HelperConnessione-IOException: ", e.toString() );
        }
        return "error";
    }

    static public String httpPutConnection(String url, String[] name, String[] param) {
        httpclient = getHttpclient();
        HttpPut httpPut = new HttpPut(url);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            for (int i = 0; i < name.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(name[i], param[i]));
            }
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httpPut);

            return EntityUtils.toString(response.getEntity());

        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-ClientProtocolException: ", e.toString() );
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-UnsupportedEncodingException: ", e.toString() );
        } catch (IOException e) {
            Log.e("HelperConnessione-IOException: ", e.toString() );
        }
        return "error";
    }

    static public String httpDeleteConnection(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpDelete httpDel = new HttpDelete(url);

        try {

            HttpResponse response = httpclient.execute(httpDel);

            return EntityUtils.toString(response.getEntity());

        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-ClientProtocolException: ", e.toString() );
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-UnsupportedEncodingException: ", e.toString() );
        } catch (IOException e) {
            Log.e("HelperConnessione-IOException: ", e.toString() );
        }
        return "error";
    }

}