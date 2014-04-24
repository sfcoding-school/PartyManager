package com.partymanager.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.partymanager.R;
import com.partymanager.app.dummy.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the callback
 * interface.
 */
public class EventiListFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static EventiListFragment newInstance() {
        EventiListFragment fragment = new EventiListFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventiListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DatiEventi.Evento>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DatiEventi.ITEMS);

        downloadEvent(10);

    }

    private void downloadEvent(final int id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://androidpartymanager.herokuapp.com/getMyEvent");

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));
                    //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    String json_string = EntityUtils.toString(response.getEntity());


                    //JSONObject myObject = new JSONObject(response);
                    //Log.i("DATI EVENTI", "risposta... " + response.toString());


                    return json_string;

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    //mDisplay.append("error");
                    return "error";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    String error = e.toString();
                    //Log.i(TAG,"error "+error);
                    //mDisplay.append("error");
                    return "error";
                }
            }

            @Override
            protected void onPostExecute(String json_string) {
                try {
                    saveJsonToCache(json_string);
                    JSONObject jsonRis = new JSONObject(json_string);
                    JSONArray jsonArray= jsonRis.getJSONArray("results");
                    for (int i=0;i<jsonArray.length();i++){
                        DatiEventi.addItem(new DatiEventi.Evento(String.valueOf(i),jsonArray.getJSONObject(i).getString("event")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(null, null, null);
    }

    private void saveJsonToCache(final String json_string) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(getActivity().getApplication().getCacheDir(),"")+"cacheListEvent.json"));
                out.writeObject( json_string );
                out.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    String error = e.toString();
                    //Log.i(TAG,"error "+error);
                    //mDisplay.append("error");

                }
                return null;
            }
        }.execute(null, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventilist, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DatiEventi.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
