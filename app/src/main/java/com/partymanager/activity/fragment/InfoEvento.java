package com.partymanager.activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.partymanager.EventSupport.EventoHelper;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.data.Adapter.FriendsAdapter;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DatiFriends;
import com.partymanager.helper.HelperConnessione;
import com.partymanager.helper.HelperFacebook;

public class InfoEvento extends Fragment {
    private static final String ID_EVENTO = "idEvento";

    private int idEvento;
    private int numUtenti;
    private String adminEvento;
    private String nomeEvento;

    private OnFragmentInteractionListener mListener;
    private boolean modifica = false;

    public static InfoEvento newInstance(int idEvento) {
        InfoEvento fragment = new InfoEvento();
        Bundle args = new Bundle();
        args.putInt(ID_EVENTO, idEvento);
        fragment.setArguments(args);
        return fragment;
    }

    public InfoEvento() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idEvento = getArguments().getInt(ID_EVENTO);

            numUtenti = DatiEventi.getIdItem(idEvento).numUtenti;
            adminEvento = DatiEventi.getIdItem(idEvento).admin;
            nomeEvento = DatiEventi.getIdItem(idEvento).name;

        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflar) {
        super.onCreateOptionsMenu(menu, inflar);
        menu.clear();
        inflar.inflate(R.menu.main_no_menu, menu);
        getActivity().getActionBar().setTitle("Info evento");

        getActivity().getActionBar().setTitle(MainActivity.drawerIsOpen(inflar, menu) ? "Party Manager" : nomeEvento);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_evento, container, false);

        final TextView member_label = (TextView) view.findViewById(R.id.member_label);
        member_label.setText(numUtenti + " MEMBRI");

        final TextView TxtnomeEvento = (TextView) view.findViewById(R.id.txtInfo_NomeEvento);
        TxtnomeEvento.setText(nomeEvento);

        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar_addFriends);
        pb.setVisibility(View.VISIBLE);

        ListView utenti = (ListView) view.findViewById(R.id.listView_friends);
        FriendsAdapter adapter = DatiFriends.init(idEvento, getActivity().getApplicationContext());
        utenti.setEmptyView(pb);
        utenti.setAdapter(adapter);

        Button addFriends = (Button) view.findViewById(R.id.btn_addFriends);

        final ProgressBar pb_buttaFuori = (ProgressBar) view.findViewById(R.id.pb_deleteUser);

        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventoHelper.dialogAddFriends(member_label, idEvento, getActivity());
            }
        });

        utenti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (adminEvento.equals(HelperFacebook.getFacebookId())) {
                    PopupMenu popup = new PopupMenu(getActivity(), view);
                    popup.getMenuInflater().inflate(R.menu.popup_butta_fuori, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            pb_buttaFuori.setVisibility(View.VISIBLE);
                            EventoHelper.eliminaUser(member_label, idEvento, getActivity(), i, pb_buttaFuori);
                            return true;
                        }
                    });
                    popup.show();
                }
            }
        });

        final EditText cambiaNome = (EditText) view.findViewById(R.id.edt_cambiaNomeEvento);

        final ImageButton modificaNomeEvento = (ImageButton) view.findViewById(R.id.btn_cambiaNome);
        modificaNomeEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifica = (!modifica);

                if (!cambiaNome.getText().toString().equals(nomeEvento) && !cambiaNome.getText().toString().equals("")) {
                    modificaNomeEvento(TxtnomeEvento, cambiaNome.getText().toString());
                }

                if (modifica) {
                    modificaNomeEvento.setBackground(getResources().getDrawable(R.drawable.checkmark));
                    TxtnomeEvento.setVisibility(View.GONE);
                    cambiaNome.setVisibility(View.VISIBLE);
                    cambiaNome.setText(nomeEvento);
                } else {
                    modificaNomeEvento.setBackground(getResources().getDrawable(android.R.drawable.ic_menu_edit));
                    TxtnomeEvento.setVisibility(View.VISIBLE);
                    cambiaNome.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void modificaNomeEvento(final TextView txt, final String nuovoNome) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String[] name, param;
                name = new String[]{"name"};
                param = new String[]{nuovoNome};

                return HelperConnessione.httpPutConnection("event/" + idEvento, name, param);
            }

            @Override
            protected void onPostExecute(String ris) {
                if (ris.equals("fatto")) {
                    nomeEvento = nuovoNome;
                    txt.setText(nuovoNome);
                    getActivity().getActionBar().setTitle(nuovoNome);
                }
            }
        }.execute();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        DatiFriends.removeAll();
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
