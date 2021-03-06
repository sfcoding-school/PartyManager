package com.partymanager.activity.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.partymanager.EventSupport.EventAsync;
import com.partymanager.EventSupport.EventoHelper;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.data.Adapter.FriendsAdapter;
import com.partymanager.data.DatiFriends;
import com.partymanager.helper.HelperConnessione;
import com.partymanager.helper.HelperFacebook;

public class InfoEvento extends Fragment {
    private int idEvento;
    private int numUtenti;
    private String adminEvento;
    private String nomeEvento;

    private boolean modifica = false;
    private ProgressBar pb_cambiaNome;
    private ImageButton modificaNomeEvento;
    public static TextView member_label;

    public static InfoEvento newInstance(Bundle b) {
        InfoEvento fragment = new InfoEvento();
        Log.e("INFO-EVENTO", "sono entrato in action " + b.toString());
        fragment.setArguments(b);
        return fragment;
    }

    public InfoEvento() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle b = getArguments();
            idEvento = b.getInt(Evento.ID_EVENTO);
            numUtenti = (b.getInt(Evento.NUM_UTENTI));
            adminEvento = b.getString(Evento.ADMIN_EVENTO);
            nomeEvento = b.getString(Evento.NOME_EVENTO);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflar) {
        super.onCreateOptionsMenu(menu, inflar);
        menu.clear();
        inflar.inflate(R.menu.main_no_menu, menu);

        if (getActivity() != null && getActivity().getActionBar() != null)
            getActivity().getActionBar().setTitle(MainActivity.drawerIsOpen(inflar, menu) ? getString(R.string.app_name) : getString(R.string.titleInfoEvento));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_evento, container, false);

        member_label = (TextView) view.findViewById(R.id.member_label);
        member_label.setText(numUtenti + " " + getString(R.string.membri));

        final TextView TxtnomeEvento = (TextView) view.findViewById(R.id.txtInfo_NomeEvento);
        TxtnomeEvento.setText(nomeEvento);

        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar_addFriends);
        pb.setVisibility(View.VISIBLE);

        pb_cambiaNome = (ProgressBar) view.findViewById(R.id.pb_cambiaNomeEvento);

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
                if (!DatiFriends.ITEMS.get(i).code.equals(adminEvento)) {
                    if (adminEvento.equals(HelperFacebook.getFacebookId(getActivity().getApplicationContext()))) {
                        PopupMenu popup = new PopupMenu(getActivity(), view);
                        popup.getMenuInflater().inflate(R.menu.popup_butta_fuori, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(android.view.MenuItem item) {
                                pb_buttaFuori.setVisibility(View.VISIBLE);
                                EventAsync.eliminaUser(member_label, idEvento, getActivity(), i, pb_buttaFuori);
                                return true;
                            }
                        });
                        popup.show();
                    }
                }
            }
        });

        final EditText cambiaNome = (EditText) view.findViewById(R.id.edt_cambiaNomeEvento);

        modificaNomeEvento = (ImageButton) view.findViewById(R.id.btn_cambiaNome);
        modificaNomeEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifica = (!modifica);

                if (!cambiaNome.getText().toString().equals(nomeEvento) && !cambiaNome.getText().toString().equals("")) {
                    modificaNomeEvento.setVisibility(View.GONE);
                    modificaNomeEvento(TxtnomeEvento, cambiaNome.getText().toString());
                }

                if (modifica) {
                    modificaNomeEvento.setBackground(getResources().getDrawable(R.drawable.ic_action_accept));
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
            protected void onPreExecute() {
                pb_cambiaNome.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {

                String[] name, param;
                name = new String[]{"name"};
                param = new String[]{nuovoNome};

                return HelperConnessione.httpPutConnection("event/" + idEvento, name, param);
            }

            @Override
            protected void onPostExecute(String ris) {
                pb_cambiaNome.setVisibility(View.GONE);
                modificaNomeEvento.setVisibility(View.VISIBLE);
                if (ris.equals("fatto")) {
                    nomeEvento = nuovoNome;
                    txt.setText(nuovoNome);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.errCambioNome), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DatiFriends.removeAll(true);
    }
}
