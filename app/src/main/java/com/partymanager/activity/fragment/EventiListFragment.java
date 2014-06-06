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
import android.widget.ProgressBar;

import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.EventAdapter;
import com.partymanager.helper.HelperFacebook;

public class EventiListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static ProgressBar progressBarLarge;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView listView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private EventAdapter eAdapter;


    public static EventiListFragment newInstance() {

        EventiListFragment fragment = new EventiListFragment();
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

        /*
        eAdapter = new EventAdapter (getActivity(), DatiEventi.ITEMS);
        DatiEventi.eAdapter = eAdapter; */
        String idFacebook = HelperFacebook.getFacebookId();
        if (idFacebook != null)
            eAdapter = DatiEventi.init(getActivity());
        else
            Log.e("id_FB: ", "id fb null on " + this.getActivity().getLocalClassName());

        /*
        mAdapter = new ArrayAdapter<DatiEventi.Evento>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DatiEventi.ITEMS);
        */
        // ProgressBar progressBarLarge = (ProgressBar) getActivity().findViewById(R.id.eventProgressBarLarge);
        //ProgressBar progressBarSmall = (ProgressBar) getActivity().findViewById(R.id.progressBarSmall);

        //progressBarLarge.setVisibility(View.VISIBLE);
        // DataProvide.getEvent(getActivity(), progressBarLarge, progressBarSmall);

        //MainActivity.progressBarVisible = false;


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


        listView.setAdapter(eAdapter);


        //progressBarLarge.setVisibility(View.INVISIBLE);
        //getActivity().invalidateOptionsMenu();

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

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.

     public void setEmptyText(CharSequence emptyText) {
     View emptyView = listView.getEmptyView();

     if (emptyText instanceof TextView) {
     ((TextView) emptyView).setText(emptyText);
     }
     }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public static interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id, String name, String admin, String numU);
    }

}
