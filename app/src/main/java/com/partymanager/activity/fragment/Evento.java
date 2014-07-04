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

import com.partymanager.EventSupport.EventAsync;
import com.partymanager.EventSupport.EventDomanda;
import com.partymanager.EventSupport.EventoHelper;
import com.partymanager.R;
import com.partymanager.activity.MainActivity;
import com.partymanager.data.Adapter.AttributiAdapter;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DatiRisposte;
import com.partymanager.helper.HelperFacebook;

public class Evento extends Fragment {

    // <editor-fold defaultstate="collapsed" desc="Variabili Globali">
    public static final String ID_EVENTO = "id_evento";
    public static final String NOME_EVENTO = "nome_evento";
    public static final String ADMIN_EVENTO = "id_admin";
    public static final String NUM_UTENTI = "num_utenti";

    private int idEvento;
    private String nomeEvento;
    private String adminEvento;
    public static int numUtenti;
    boolean animation;

    AttributiAdapter eAdapter;
    ListView listView;
    View riepilogo;
    EventDomanda eventDialog;
    static TextView luogoE;
    static TextView quando_data;
    static TextView quando_ora;
    static TextView luogoI;
    static TextView oraI;
    int mLastFirstVisibleItem = 0;
    Dialog dialogAddDomanda;

    public static final int DIALOG_DATA = 1;
    public static final int DIALOG_ORARIO_E = 2;
    public static final int DIALOG_ORARIO_I = 3;
    public static final int DIALOG_LUOGO_I = 4;
    public static final int DIALOG_PERSONALLIZATA = 5;
    public static final int DIALOG_LUOGO_E = 6;
    public static final int DIALOG_SINO = 7;

    private boolean sonoEntratoInCreate;

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

    /*
        public static Evento newInstance(Bundle param) {
            Evento fragment = new Evento();

            String tmp =param.getString(NOME_EVENTO);
            Log.e("EVENTO", "il bundle qui è " + param.toString());
            fragment.setArguments(param);

            return fragment;
        }
    */
    public static Evento newInstance(int id, String nomeEvento, String admin, int numUtenti) {
        Evento fragment = new Evento();

        Bundle args = new Bundle();
        args.putInt(ID_EVENTO, id);
        args.putString(NOME_EVENTO, nomeEvento);
        args.putString(ADMIN_EVENTO, admin);
        args.putInt(NUM_UTENTI, numUtenti);
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

        String temp;
        if (DatiEventi.getInizializzata())
            temp = DatiEventi.getIdItem(idEvento).name;
        else
            temp = nomeEvento;

        getActivity().getActionBar().setTitle(MainActivity.drawerIsOpen(inflar, menu) ? "Party Manager" : temp);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle b = getArguments();
            idEvento = b.getInt(ID_EVENTO);
            String tmp = b.getString(NOME_EVENTO);

            if (tmp == null) {
                nomeEvento = DatiEventi.getIdItem(idEvento).name;
                adminEvento = DatiEventi.getIdItem(idEvento).admin;
                numUtenti = DatiEventi.getIdItem(idEvento).numUtenti;
                b.putString(NOME_EVENTO, nomeEvento);
                b.putString(ADMIN_EVENTO, adminEvento);
                b.putInt(NUM_UTENTI, numUtenti);

            } else {
                nomeEvento = b.getString(NOME_EVENTO);
                adminEvento = b.getString(ADMIN_EVENTO);
                numUtenti = b.getInt(NUM_UTENTI);
            }
            sonoEntratoInCreate = true;
        }

        Log.e("EVENTO-onCreate", " IdEvento " + idEvento + " Name " + nomeEvento + " admin " + adminEvento + " numUtenti " + numUtenti);

        eventDialog = new EventDomanda(getActivity(), dialogMsgHandler, idEvento, adminEvento);
        eAdapter = DatiAttributi.init(getActivity(), idEvento);
        dialogAddDomanda = eventDialog.returnD();
        setHasOptionsMenu(true);
    }

    public static void checkTemplate() {
        String[] template = DatiAttributi.getTemplate();

        quando_data.setText(template[0]);
        luogoE.setText(template[1]);
        luogoI.setText(template[2]);
        quando_ora.setText(template[3]);
        oraI.setText(template[4]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento, container, false);

        listView = (ListView) view.findViewById(R.id.eventList);
        riepilogo = view.findViewById(R.id.stickyheader);
        luogoE = (TextView) view.findViewById(R.id.txt_luogo);
        quando_data = (TextView) view.findViewById(R.id.txt_data);
        quando_ora = (TextView) view.findViewById(R.id.txt_orario);
        luogoI = (TextView) view.findViewById(R.id.txt_dove_vediamo);
        oraI = (TextView) view.findViewById(R.id.txt_orarioI);
        final View add_domanda = view.findViewById(R.id.circle);

        // <editor-fold defaultstate="collapsed" desc="RiepilogoSetup">

        oraI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("oraI", "OrarioIncontro");
            }
        });

        oraI.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cancellaDomanda(DatiAttributi.cercaTemplate("oraI"), view);
                return true;
            }
        });

        luogoE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("luogoE", "LuogoEvento");
            }
        });

        luogoE.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cancellaDomanda(DatiAttributi.cercaTemplate("luogoE"), view);
                return true;
            }
        });

        quando_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("data", "DataEvento");
            }
        });

        quando_data.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cancellaDomanda(DatiAttributi.cercaTemplate("data"), view);
                return true;
            }
        });

        quando_ora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("oraE", "OrarioEvento");
            }
        });

        quando_ora.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cancellaDomanda(DatiAttributi.cercaTemplate("oraE"), view);
                return true;
            }
        });

        luogoI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                templateManager("luogoI", "LuogoIncontro");
            }
        });

        luogoI.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cancellaDomanda(DatiAttributi.cercaTemplate("luogoI"), view);
                return true;
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
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="listView">
        listView.setEmptyView(view.findViewById(R.id.txt_empty));
        listView.setAdapter(eAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                EventoHelper.dialogRisposte(adminEvento, arg2, getActivity(), idEvento);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                cancellaDomanda(pos, arg1);
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
                if (firstVisibleItem > mLastFirstVisibleItem && totalItemCount > 15) {
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

        checkTemplate();
        if (!sonoEntratoInCreate) {
            if (DatiAttributi.getIdEvento() != -1) DatiAttributi.notifyDataChange();
            if (DatiRisposte.getIdAttributo() != -1) DatiRisposte.notifyDataChange();
        }
        sonoEntratoInCreate = false;
        return view;
    }

    private void cancellaDomanda(final int pos, View view) {
        if (adminEvento.equals(HelperFacebook.getFacebookId())) {
            PopupMenu popup = new PopupMenu(getActivity(), view);
            popup.getMenuInflater().inflate(R.menu.popup_delete, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(android.view.MenuItem item) {
                    EventAsync.eliminaDomanda(pos, idEvento, getActivity());
                    return true;
                }
            });

            popup.show();
        }
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
    public void onDestroy() {
        super.onDestroy();
        DatiAttributi.removeAll(idEvento);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id);
    }
    // </editor-fold>

    public void templateManager(String template, String quale) {
        int pos;
        if ((pos = DatiAttributi.cercaTemplate(template)) != -1) {
            EventoHelper.dialogRisposte(adminEvento, pos, getActivity(), idEvento);
        } else {
            eventDialog.which(quale, -1);
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
                String ris2;
                switch (who) {
                    case DIALOG_DATA:
                        ris = msg.getData().getString("data");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, getString(R.string.dataE), ris, "data", close, 1, 1, null));
                        DatiEventi.addData(idEvento, ris);
                        break;
                    case DIALOG_ORARIO_E:
                        ris = msg.getData().getString("orario");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, getString(R.string.orarioE), ris, "oraE", close, 1, 1, null));
                        break;
                    case DIALOG_ORARIO_I:
                        ris = msg.getData().getString("orario");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, getString(R.string.orarioI), ris, "oraI", close, 1, 1, null));
                        break;
                    case DIALOG_LUOGO_I:
                        ris = msg.getData().getString("luogo");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, getString(R.string.luogoI), ris, "luogoI", close, 1, 1, null));
                        break;
                    case DIALOG_LUOGO_E:
                        ris = msg.getData().getString("luogo");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, getString(R.string.luogoE), ris, "luogoE", close, 1, 1, null));
                        break;
                    case DIALOG_PERSONALLIZATA:
                        ris = msg.getData().getString("pers-d");
                        ris2 = msg.getData().getString("pers-r");
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, ris, ris2, null, close, 1, 1, null));
                        break;
                    case DIALOG_SINO:
                        ris = msg.getData().getString("domanda");
                        ris2 = "SI";
                        DatiAttributi.addItem(new DatiAttributi.Attributo(id_attributo, ris, ris2, "sino", close, 1, 1, null));
                        break;
                }
                checkTemplate();
            }
        }
    };
    // </editor-fold>
}