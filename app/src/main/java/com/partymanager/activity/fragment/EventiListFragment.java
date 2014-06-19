package com.partymanager.activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.data.Adapter.EventAdapter;
import com.partymanager.data.DatiEventi;
import com.partymanager.helper.HelperFacebook;

public class EventiListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ProgressBar progressBarLarge;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private EventAdapter eAdapter;


    public static EventiListFragment newInstance() {

        EventiListFragment fragment = new EventiListFragment();
        return fragment;
    }

    public EventiListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        String idFacebook = HelperFacebook.getFacebookId();
        if (idFacebook != null)
            eAdapter = DatiEventi.init(getActivity());
        else
            Log.e("id_FB: ", "id fb null on " + this.getActivity().getLocalClassName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventi, container, false);

        // Set the adapter
        listView = (ListView) view.findViewById(R.id.eventList);
        //((AdapterView<ListAdapter>) mListView).setAdapter(eAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        listView.setOnItemClickListener(this);
        listView.setEmptyView(view.findViewById(R.id.txt_emptyE));
        listView.setAdapter(eAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Log.e("long clicked", "pos: " + pos);
                PopupMenu popup = new PopupMenu(getActivity(), arg1);
                int temp = R.menu.popup_esci_da_evento;
                if (DatiEventi.ITEMS.get(pos).admin.equals(HelperFacebook.getFacebookId()))
                    temp = R.menu.popup_delete;

                popup.getMenuInflater().inflate(temp, popup.getMenu());
                popup.show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        MainActivity.mTitle = "Eventi";

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            String idEvento = Integer.toString(DatiEventi.ITEMS.get(position).id);
            String name = DatiEventi.ITEMS.get(position).name;
            String admin = DatiEventi.ITEMS.get(position).admin;
            String numU = Integer.toString(DatiEventi.ITEMS.get(position).numUtenti);
            mListener.onFragmentInteraction(idEvento, name, admin, numU);
        }
    }

    public static interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id, String name, String admin, String numU);
    }

}
