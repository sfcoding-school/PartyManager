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
    //private static final String URL = "http://apipm.sfcoding.com/";
    private static final String URL = "http://apipm.sfcoding.com/";

    private static HttpClient httpclient = null;

    static public boolean login() {
        String[] name = {"idFacebook", "token"};
        String[] param = {HelperFacebook.getFacebookId(), HelperFacebook.getToken()};
        String ris = HelperConnessione.httpPostConnection("login", name, param);
        Log.e("DATA_PROVIDE", "login " + ris);
        return ris.equals("fatto");
    }


    static public boolean logout() {
        String[] name = {};
        String[] param = {};
        String ris = HelperConnessione.httpPostConnection("logout", name, param);

        return ris.equals("fatto");
    }

    static public String httpPostConnection(String url, String[] name, String[] param) {
        httpclient = getHttpclient();
        HttpPost httppost = new HttpPost(URL + url);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            for (int i = 0; i < name.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(name[i], param[i]));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

            HttpResponse response = httpclient.execute(httppost);

            String temp = EntityUtils.toString(response.getEntity());

            if (temp.equals("session error")) {
                login();
                return httpPostConnection(url, name, param);
            } else {
                return temp;
            }
        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-ClientProtocolException: ", e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-UnsupportedEncodingException: ", e.toString());
        } catch (IOException e) {
            Log.e("HelperConnessione-IOException: ", e.toString());
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
        HttpGet httpget = new HttpGet(URL + url);
        try {
            HttpResponse response = httpclient.execute(httpget);
            String test_ritorno = EntityUtils.toString(response.getEntity());
            Log.e("HelperConnessione-httpGetConnection-Ris: ", URL + url + " risposta:" + test_ritorno);

            if (test_ritorno.equals("session error")) {
                login();
                return httpGetConnection(url);
            }

            return test_ritorno;

        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-httpGetConnection-ClientProtocolException: ", e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-httpGetConnection-UnsupportedEncodingException: ", e.toString());
        } catch (IOException e) {
            Log.e("HelperConnessione-httpGetConnection-IOException: ", e.toString());
            if (e.toString().equals("org.apache.http.conn.HttpHostConnectException: Connection to http://apipm.sfcoding.com refused"))
                return "serverOffline";
            if (e.toString().equals("java.net.UnknownHostException: Unable to resolve host \"apipm.sfcoding.com\": No address associated with hostname"))
                return "connessioneAssente";
        }
        return "error";
    }

    static public String httpPutConnection(String url, String[] name, String[] param) {
        httpclient = getHttpclient();
        HttpPut httpPut = new HttpPut(URL + url);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            for (int i = 0; i < name.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(name[i], param[i]));
            }
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

            HttpResponse response = httpclient.execute(httpPut);
            String temp = EntityUtils.toString(response.getEntity());

            if (temp.equals("session error")) {
                login();
                return httpPutConnection(url, name, param);
            } else
                return temp;


        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-ClientProtocolException: ", e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-UnsupportedEncodingException: ", e.toString());
        } catch (IOException e) {
            Log.e("HelperConnessione-IOException: ", e.toString());
        }
        return "error";
    }

    static public String httpDeleteConnection(String url) {
        httpclient = getHttpclient();
        HttpDelete httpDel = new HttpDelete(URL + url);

        try {
            HttpResponse response = httpclient.execute(httpDel);
            String temp = EntityUtils.toString(response.getEntity());

            if (temp.equals("session error")) {
                login();
                return httpDeleteConnection(url);
            } else
                return temp;

        } catch (ClientProtocolException e) {
            Log.e("HelperConnessione-ClientProtocolException: ", e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("HelperConnessione-UnsupportedEncodingException: ", e.toString());
        } catch (IOException e) {
            Log.e("HelperConnessione-IOException: ", e.toString());
        }
        return "error";
    }

}