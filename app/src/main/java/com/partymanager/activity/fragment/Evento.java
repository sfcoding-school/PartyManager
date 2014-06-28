package com.partymanager.activity.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.partymanager.EventSupport.EventDialog;
import com.partymanager.EventSupport.EventoHelper;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.data.Adapter.AttributiAdapter;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.helper.HelperFacebook;

public class Evento extends Fragment {

    // <editor-fold defaultstate="collapsed" desc="Variabili Globali">
    private static final String ID_EVENTO = "param1";

    private static int idEvento;
    private String nomeEvento;
    private String adminEvento;
    private int numUtenti;
    private TextView bnt_friends;
    boolean animation;

    AttributiAdapter eAdapter;
    ListView listView;
    View riepilogo;
    EventDialog eventDialog;
    static TextView luogo;
    static TextView quando_data;
    static TextView quando_ora;
    static TextView dove;
    int mLastFirstVisibleItem = 0;
    Dialog dialogAddDomanda;


    private static final int DIALOG_DATA = 1;
    private static final int DIALOG_ORARIO_E = 2;
    private static final int DIALOG_ORARIO_I = 3;
    private static final int DIALOG_LUOGO_I = 4;
    private static final int DIALOG_PERSONALLIZATA = 5;
    private static final int DIALOG_LUOGO_E = 6;
    private static final int DIALOG_SINO = 7;

    private OnFragmentInteractionListener mListener;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Init + Grafica">
    public static Evento newInstance(int id) {
        Evento fragment = new Evento();

        Bundle args = new Bundle();
        args.putInt(ID_EVENTO, id);
        fragment.setArguments(args);

        return fragment;
    }

    public Evento() {
    }

    static public boolean progressBar = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflar) {

        super.onCreateOptionsMenu(menu, inflar);
        menu.clear();

        inflar.inflate(R.menu.main_evento, menu);

        MenuItem prova = menu.findItem(R.id.progressBarSmall);
        prova.setVisible(progressBar);

        getActivity().getActionBar().setTitle(MainActivity.drawerIsOpen(inflar, menu) ? "Party Manager" : nomeEvento);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idEvento = getArguments().getInt(ID_EVENTO);
            nomeEvento = DatiEventi.getIdItem(idEvento).name;
            adminEvento = DatiEventi.getIdItem(idEvento).admin;
            numUtenti = DatiEventi.getIdItem(idEvento).numUtenti;
        }
        Log.e("DEBUG", "" + idEvento);

        eventDialog = new EventDialog(getActivity(), dialogMsgHandler, idEvento, adminEvento);
        eAdapter = DatiAttributi.init(getActivity(), idEvento, numUtenti);

        dialogAddDomanda = eventDialog.returnD();

        setHasOptionsMenu(true);
    }

    public static void checkTemplate() {
        String[] template = DatiAttributi.getTemplate();

        quando_data.setText(template[0]);
        luogo.setText(template[1]);
        dove.setText(template[2]);
        quando_ora.setText(template[3]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento, container, false);

        listView = (ListView) view.findViewById(R.id.eventList);
        riepilogo = view.findViewById(R.id.stickyheader);
        luogo = (TextView) view.findViewById(R.id.txt_luogo);
        quando_data = (TextView) view.findViewById(R.id.txt_data);
        quando_ora = (TextView) view.findViewById(R.id.txt_orario);
        dove = (TextView) view.findViewById(R.id.txt_dove_vediamo);

        final View add_domanda = view.findViewById(R.id.circle);

        bnt_friends.setText(""+numUtenti);

        luogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("luogoE", "LuogoEvento", 4);
            }
        });

        quando_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("data", "DataEvento", 5);
            }
        });

        quando_ora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("oraE", "OrarioEvento", 6);
            }
        });

        dove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("luogoI", "LuogoIncontro", 3);
            }
        });

        add_domanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventDialog.renderSpinner();
                eventDialog.which("Personalizzata", 0);
                dialogAddDomanda.show();
            }
        });

        // <editor-fold defaultstate="collapsed" desc="listView">
        listView.setEmptyView(view.findViewById(R.id.txt_empty));
        listView.setAdapter(eAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                EventoHelper.dialogRisposte(adminEvento, arg2, getActivity(), idEvento, numUtenti);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {

                if (adminEvento.equals(HelperFacebook.getFacebookId())) {
                    PopupMenu popup = new PopupMenu(getActivity(), arg1);
                    popup.getMenuInflater().inflate(R.menu.popup_delete, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            EventoHelper.eliminaDomanda(pos, idEvento, getActivity());
                            return true;
                        }
                    });

                    popup.show();
                }

                return true;
            }
        });

        animation = false;

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > mLastFirstVisibleItem && totalItemCount > 9) {
                    if (!animation) {
                        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, +2 * add_domanda.getWidth());
                        anim.setDuration(500);
                        anim.setFillAfter(true);
                        add_domanda.startAnimation(anim);
                        animation = true;
                    }
                }
                if (firstVisibleItem < mLastFirstVisibleItem || listView.getLastVisiblePosition() == totalItemCount - 1) {
                    if (animation) {
                        TranslateAnimation anim = new TranslateAnimation(0, 0, +2 * add_domanda.getWidth(), 0);
                        anim.setDuration(500);
                        anim.setFillAfter(true);
                        add_domanda.startAnimation(anim);
                        animation = false;
                    }
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });
        // </editor-fold>

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DatiAttributi.removeAll(true, idEvento);
        eAdapter.notifyDataSetChanged();
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id);
    }
    // </editor-fold>

    public void templateManager(String template, String quale, int agg) {
        int pos;
        if ((pos = DatiAttributi.cercaTemplate(template)) != -1) {
            EventoHelper.dialogRisposte(adminEvento, pos, getActivity(), idEvento, numUtenti);
        } else {
            eventDialog.which(quale, agg);
            dialogAddDomanda.show();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Handler">
    private Handler dialogMsgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String ris;
            if (msg != null) {
                int who = msg.getData().getInt("who");
                boolean close = msg.getData().getBoolean("close");
                int id_attributo = msg.getData().getInt("id_attributo");
                String ris2 = "";
                switch (who) {
                    case DIALOG_DATA:
                        ris = msg.getData().getString("data");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, "Data Evento", ris, "data", close, 1, 1, null));
                        DatiEventi.addData(idEvento, ris);
                        break;
                    case DIALOG_ORARIO_E:
                        ris = msg.getData().getString("orario");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, "Orario Evento", ris, "oraE", close, 1, 1, null));
                        break;
                    case DIALOG_ORARIO_I:
                        ris = msg.getData().getString("orario");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, "Orario Incontro", ris, "oraI", close, 1, 1, null));
                        break;
                    case DIALOG_LUOGO_I:
                        ris = msg.getData().getString("luogo");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, "Luogo incontro", ris, "luogoI", close, 1, 1, null));
                        break;
                    case DIALOG_LUOGO_E:
                        ris = msg.getData().getString("luogo");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, "Luogo Evento", ris, "luogoE", close, 1, 1, null));
                        break;
                    case DIALOG_PERSONALLIZATA:
                        ris = msg.getData().getString("pers-d");
                        if (close) {
                            ris2 = msg.getData().getString("pers-r");
                        }
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, ris, ris2, null, close, 1, 1, null));
                        break;
                    case DIALOG_SINO:
                        ris = msg.getData().getString("domanda");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, ris, ris2, "sino", false, 1, 1, null));
                        break;
                }
                checkTemplate();
            }
        }
    };
    // </editor-fold>
}