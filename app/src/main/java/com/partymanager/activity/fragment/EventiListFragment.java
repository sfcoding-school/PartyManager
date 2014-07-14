package com.partymanager.activity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.data.Adapter.EventAdapter;
import com.partymanager.data.DatiEventi;
import com.partymanager.helper.HelperConnessione;
import com.partymanager.helper.HelperFacebook;

public class EventiListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private EventAdapter eAdapter;

    public boolean sonoEntratoInCreate;


    public static EventiListFragment newInstance() {

        return new EventiListFragment();
    }

    public EventiListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String idFacebook = HelperFacebook.getFacebookId(getActivity().getApplicationContext());
        if (idFacebook != null)
            eAdapter = DatiEventi.init(getActivity());
        else
            Log.e("id_FB: ", "id fb null on " + this.getActivity().getLocalClassName());

        sonoEntratoInCreate = true;
        setHasOptionsMenu(true);
    }

    static public boolean progressBar = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflar) {

        super.onCreateOptionsMenu(menu, inflar);
        menu.clear();

        inflar.inflate(R.menu.main, menu);

        MenuItem prova = menu.findItem(R.id.progressBarSmall);
        prova.setVisible(progressBar);

        getActivity().getActionBar().setTitle(MainActivity.drawerIsOpen(inflar, menu) ? "Party Manager" : getString(R.string.title_section0));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventi, container, false);

        // Set the adapter
        ListView listView = (ListView) view.findViewById(R.id.eventList);

        listView.setOnItemClickListener(this);
        listView.setEmptyView(view.findViewById(R.id.txt_emptyE));
        listView.setAdapter(eAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                PopupMenu popup = new PopupMenu(getActivity(), arg1);
                int temp = R.menu.popup_esci_da_evento;
                if (DatiEventi.getPositionItem(pos).admin.equals(HelperFacebook.getFacebookId(getActivity().getApplicationContext())))
                    temp = R.menu.popup_delete;

                popup.getMenuInflater().inflate(temp, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        esciEliminaDaEvento(pos);
                        return true;
                    }
                });

                popup.show();
                return true;
            }
        });

        if (!sonoEntratoInCreate) {
            DatiEventi.notifyDataChange();
        }
        sonoEntratoInCreate = false;

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
        MainActivity.mTitle = getString(R.string.title_section0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            int idEvento = DatiEventi.getPositionItem(position).id;
            Log.e("EVENTLIST", "" + idEvento);
            /*
            String name = DatiEventi.getPositionItem(position).name;
            String admin = DatiEventi.getPositionItem(position).admin;
            String numU = Integer.toString(DatiEventi.getPositionItem(position).numUtenti);
            */
            mListener.onFragmentInteraction(idEvento);
        }
    }

    public static interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id);
    }

    public void esciEliminaDaEvento(final int pos) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return HelperConnessione.httpDeleteConnection("event/" + DatiEventi.getPositionItem(pos).id);
            }

            @Override
            protected void onPostExecute(String ris) {
                if (!ris.equals("fatto")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage(getString(R.string.errEsciDaEvento));

                    alertDialogBuilder.setPositiveButton(getString(R.string.chiudi), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    DatiEventi.removePositionItem(pos);
                }
            }
        }.execute(null, null, null);
    }

}
