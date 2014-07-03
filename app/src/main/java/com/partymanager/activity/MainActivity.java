package com.partymanager.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.partymanager.R;
import com.partymanager.activity.fragment.Archivio;
import com.partymanager.activity.fragment.EventiListFragment;
import com.partymanager.activity.fragment.Evento;
import com.partymanager.activity.fragment.InfoEvento;
import com.partymanager.activity.fragment.PrefsFragment;
import com.partymanager.data.Adapter.DrawerAdapter;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DatiFriends;
import com.partymanager.data.DatiRisposte;
import com.partymanager.gcm.GcmIntentService;
import com.partymanager.helper.HelperFacebook;

public class MainActivity extends Activity
        implements EventiListFragment.OnFragmentInteractionListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private FragmentManager fragmentManager;
    private boolean noMenuActionBar = false;
    private static boolean drawerAperto = false;
    public static CharSequence mTitle;
    private static Activity mContext;
    RelativeLayout leftRL;
    DrawerLayout drawerLayout;
    ListView mDrawerListView;
    ActionBarDrawerToggle mDrawerToggle;
    public static Handler handlerService = null;
    public static boolean progressBarVisible = false;

    Fragment fragment = null;
    private final String eventListTAG = "eventList";
    public final String eventTAG = "evento";
    private final String archivioTAG = "archivio";
    private final String impostazioniTAG = "impostazioni";
    private boolean infoEventoAperto = false;
    private final String infoTAG = "infoEvento";

    private FragmentManager.OnBackStackChangedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        //fragmentManager = getFragmentManager();


        handlerService = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("SERVICEHANDLER", "arrivato il messaggio " + msg.toString());
                Bundle b = msg.getData();
                int type = b.getInt(GcmIntentService.NOTIFY);
                //int method = Integer.parseInt(b.getString("method"));
                switch (type) {
                    case GcmIntentService.EVENTI:
                        DatiEventi.notifyDataChange();
                        break;
                    case GcmIntentService.ATTRIBUTI:
                        DatiAttributi.notifyDataChange();
                        DatiEventi.notifyDataChange();
                        break;
                    /*case GcmIntentService.RISPOSTE:
                        DatiEventi.notifyDataChange();
                        DatiAttributi.notifyDataChange();
                        DatiRisposte.notifyDataChange();
                        break;*/
                    case GcmIntentService.FRIENDS:
                        DatiEventi.notifyDataChange();
                        DatiFriends.notifyDataChange();
                        break;
                }
/*
                fragmentManager = getFragmentManager();
                Fragment eventList = fragmentManager.findFragmentByTag(eventListTAG);
                Fragment event = fragmentManager.findFragmentByTag(eventTAG);
                int dialogIdAttributo = EventoHelper.getIdAttributo();

                //EVENTLIST VISIBILE
                if (eventList != null && eventList.isVisible()) {
                    switch (type) {
                        //event
                        case 1:
                            int idEvento = Integer.parseInt(b.getString("id_evento"));

                            switch (method) {
                                //new
                                case 1:
                                    DatiEventi.addItem(
                                            new DatiEventi.Evento(
                                                    idEvento,
                                                    b.getString("nome_evento"),
                                                    "",
                                                    "",
                                                    b.getString("admin"),
                                                    Integer.parseInt(b.getString("num_utenti"))
                                            )
                                    );
                                    break;

                                //'del'
                                case 3:
                                    DatiEventi.removeIdItem(idEvento);
                                    break;
                                //'uscito'
                                case 4:
                                    DatiEventi.getIdItem(idEvento).numUtenti -= 1;
                                    DatiEventi.notifyDataChange();
                                    break;
                            }
                            break;

                        //attr
                        case 2:
                            switch (method) {
                                //new
                                case 1:
                                    idEvento = Integer.parseInt(b.getString("id_evento"));
                                    //String idAttributo = b.getString("id_attributo");
                                    String idRisposta = b.getString("id_risposta");
                                    //String domanda = b.getString("domanda");
                                    String risposta = b.getString("risposta");
                                    String template = b.getString("template");
                                    //boolean chiusa = Boolean.parseBoolean(b.getString("chiusa"));
                                    //int numD = Integer.parseInt(b.getString("numd"));
                                    //int numR = Integer.parseInt(b.getString("numr"));

                                    if (template != null && idRisposta != null && template.equals("data") && !idRisposta.equals("None")) {
                                        DatiEventi.getIdItem(idEvento).date = HelperDataParser.getCalFromString(risposta);
                                        DatiEventi.notifyDataChange();
                                    }
                                    break;

                                //del
                                case 3:
                                    //aggingere l'eliminazione della data se viene eliminato un attributo con template uguale a data
                                    break;
                            }

                            break;

                        //risp
                        case 3:
                            //aggiornare la data dell'evento se viene votata o aggiunta una nuova risposta su un attributo con template data
                            switch (method) {
                                //new
                                case 1:

                                    break;

                                //mod
                                case 2:
                                    break;
                            }
                            break;

                        //user
                        case 4:
                            idEvento = b.getInt("id_evento");

                            switch (method) {
                                //new
                                case 1:
                                    int numAddUser = 0;
                                    try {
                                        numAddUser = new JSONArray(b.getString("user_list")).length();
                                    } catch (JSONException e) {
                                        Log.e("AGG-NOTIFICHE", e.toString());
                                    }
                                    DatiEventi.getIdItem(idEvento).numUtenti += numAddUser;
                                    DatiEventi.notifyDataChange();
                                    break;

                                //del
                                case 3:
                                    DatiEventi.getIdItem(idEvento).numUtenti--;
                                    DatiEventi.notifyDataChange();
                                    break;
                            }


                            break;
                    }
                } else //EVENTO VISIBILE
                    if (event != null && event.isVisible()) {
                        switch (type) {
                            //attr
                            case 2:
                                if (event.getArguments().getString("param1").equals(b.getString("id_evento"))) {
                                    int idAttr = Integer.parseInt(b.getString("id_attributo"));
                                    switch (method) {
                                        //new
                                        case 1:
                                            //boolean chiusa = Boolean.parseBoolean();
                                            //String id, String domanda, String risposta, String template, chiusa, int numd, int numr, String id_risposta
                                            DatiAttributi.addItem(
                                                    new DatiAttributi.Attributo(
                                                            idAttr,
                                                            b.getString("domanda"),
                                                            b.getString("risposta"),
                                                            b.getString("template"),
                                                            b.getBoolean("chiusa"),
                                                            Integer.parseInt(b.getString("numd")),
                                                            Integer.parseInt(b.getString("numr")),
                                                            b.getString("id_risposta")
                                                    )
                                            );
                                            break;
                                        //del
                                        case 3:
                                            DatiAttributi.removeIdItem(idAttr);
                                    }
                                }

                                break;

                            //risp
                            case 3:
                                //int idAttributo = EventoHelper.getIdAttributo();
                                //int idAttrNotifica = Integer.parseInt(b.getString("id_attributo"));
                                if (event.getArguments().getString(Evento.ID_EVENTO).equals(b.getString("id_evento"))) {
                                    int idAttr = Integer.parseInt(b.getString("id_attributo"));
                                    int idRisposta = b.getInt("id_risposta");
                                    String risposta = b.getString("risposta");
                                    DatiAttributi.Attributo attr = DatiAttributi.getIdItem(idAttr);

                                    switch (method) {
                                        //new
                                        case 1:
                                            //String id, String risposta, String template, JSONArray userList
                                            if (attr.numr <= 1) {
                                                attr.id_risposta = String.valueOf(idRisposta);
                                                attr.risposta = risposta;
                                                attr.numr = 1;
                                                DatiAttributi.notifyDataChange();
                                            }

                                            break;

                                        //delete
                                        case 3:
                                            //problemi implementativi

                                            break;

                                        //mod
                                        case 2:
                                            int numr = Integer.parseInt(b.getString("numr"));
                                            if (numr >= attr.numr) {
                                                attr.id_risposta = String.valueOf(idRisposta);
                                                attr.risposta = risposta;
                                                attr.numr = numr;
                                                DatiAttributi.notifyDataChange();
                                            }

                                            break;

                                    }
                                    break;
                                }
                                //user
                            case 4:
                                switch (method) {

                                    //new
                                    case 1:
                                        //da implementare
                                        break;

                                    //del
                                    case 3:
                                        //da implementare
                                        break;
                                }
                                break;
                        }
                    } //RISPOSTE VISIBILE
                if (dialogIdAttributo != -1) {
                    switch (type) {
                        //risp
                        case 3:
                            //int idAttributo = EventoHelper.getIdAttributo();
                            //int idAttrNotifica = Integer.parseInt(b.getString("id_attributo"));
                            if (dialogIdAttributo == Integer.parseInt(b.getString("id_attributo"))) {
                                String user = b.getString("user");
                                String userName = b.getString("userName");
                                int idRisposta = Integer.parseInt(b.getString("id_risposta"));
                                boolean controllo = b.getString("agg") != null && b.getString("agg").equals("1"); // ? true : false;
                                switch (method) {
                                    //new
                                    case 1:
                                        String risposta = b.getString("risposta");

                                        JSONArray userList = null;
                                        try {
                                            JSONObject json = new JSONObject();
                                            json.put("id_user", user);
                                            json.put("name", userName);
                                            userList = new JSONArray();
                                            userList.put(json);
                                        } catch (JSONException e) {
                                            Log.e("AGG-NOTIFICHE", e.toString());
                                        }

                                        //String id, String risposta, String template, JSONArray userList
                                        DatiRisposte.addItem(
                                                new DatiRisposte.Risposta(
                                                        idRisposta,
                                                        risposta,
                                                        userList
                                                ), controllo
                                        );
                                        break;

                                    //del
                                    case 3:
                                        DatiRisposte.removeIdItem(idRisposta);
                                        break;
                                    //mod
                                    case 2:
                                        //int numr = Integer.parseInt(b.getString("numr"));
                                        DatiRisposte.addIdPersona(idRisposta, user, userName, controllo);
                                        break;
                                }
                                break;
                            }


                    }


                }*/
            }
        };
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.fragment_nav_drawer_custom);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setNavigationDrawer();

        setUp();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (checkPlayServices()) {
            //Controllo se esiste già una sessione FB attiva
            Session session = HelperFacebook.getSession(this);

            if (!session.isOpened()) {
                Intent newact = new Intent(this, ProfileActivity.class);
                startActivity(newact);
            } else {
                HelperFacebook.getToken();
                fragmentManager = getFragmentManager();

                Intent inte = getIntent();

                String action = inte.getAction();


                if (action != null && action.equals(GcmIntentService.NOTIFICA_EVENTO)) {
                    //Bundle b = inte.getBundleExtra("bundle");
                    //Log.e("MAINACTIVITY-DEBUG", "sono entrato in action "+b.toString());
                    //changeFragment(0);
                    //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    int id = inte.getIntExtra(Evento.ID_EVENTO, -1);
                    String nome = inte.getStringExtra(Evento.NOME_EVENTO);
                    String admin = inte.getStringExtra(Evento.ADMIN_EVENTO);
                    int num = inte.getIntExtra(Evento.NUM_UTENTI, -1);

                    Fragment tmp = Evento.newInstance(
                            id, nome, admin, num
                    );
                    if (fragmentManager.findFragmentByTag(eventTAG) != null)
                        fragmentManager.popBackStackImmediate(eventTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, tmp, eventTAG)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(eventTAG)
                            .commit();

                    listener = new FragmentManager.OnBackStackChangedListener() {
                        public void onBackStackChanged() {
                            if (fragmentManager.getBackStackEntryCount() == 0) {
                                if (fragmentManager.findFragmentByTag(eventListTAG) != null)
                                    fragmentManager.popBackStackImmediate(eventListTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                changeFragment(0);
                                invalidateOptionsMenu();
                            }
                        }
                    };
                    fragmentManager.addOnBackStackChangedListener(listener);

                } else if (action != null && action.equals(GcmIntentService.NOTIFICA_EVENTLIST)) {
                    changeFragment(0);

                } else {
                    int countBackStack = fragmentManager.getBackStackEntryCount();

                    if (countBackStack == 0 && fragment != null) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment, fragment.getTag())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    } else if (countBackStack != 0) {
                        String id = fragmentManager.getBackStackEntryAt(0).getName();
                        Fragment tmp = fragmentManager.findFragmentByTag(id);
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, tmp, id)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    } else
                        changeFragment(0);

                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DatiEventi.save();
    }

    protected void showAbout() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    // <editor-fold defaultstate="collapsed" desc="NavigationDrawer">
    private void setUp() {
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                drawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerAperto = false;
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerAperto = true;
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void setNavigationDrawer() {
        leftRL = (RelativeLayout) findViewById(R.id.whatYouWantInLeftDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerListView = (ListView) findViewById(R.id.left_expandableListView);
        ListView bottomListview = (ListView) findViewById(R.id.bottom_listview);

        TextView txt_version = (TextView) findViewById(R.id.txt_version);
        try {
            txt_version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Drawer", "errore txt_version versione applicazione");
        }

        bottomListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (!mTitle.equals(getString(R.string.title_section2))) {
                            changeFragment(2);
                        } else {
                            drawerLayout.closeDrawer(leftRL);
                        }
                        break;
                    case 1:
                        Intent Email = new Intent(Intent.ACTION_SEND);
                        PackageInfo pInfo = null;
                        try {
                            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e("Drawer", "errore sendmail versione applicazione");
                        }
                        Email.setType("text/email");
                        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"fedo.coro@gmail.com", "lucarin91@gmail.com"});
                        Email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSubject) + " (" + (pInfo != null ? pInfo.versionName : null) + ")");
                        Email.putExtra(Intent.EXTRA_TEXT, getString(R.string.txtMail));
                        startActivity(Intent.createChooser(Email, "Send Feedback:"));
                        break;
                }
            }
        });

        bottomListview.setAdapter(new DrawerAdapter(
                getActionBar().getThemedContext(),
                R.layout.drawer_line,
                getResources().getStringArray(R.array.list_names),
                getResources().obtainTypedArray(R.array.list_icons),
                false
        ));

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent newact = new Intent(getApplicationContext(), ProfileActivity.class);
                    newact.putExtra("chiave", "1");
                    startActivity(newact);
                } else {

                    if (!((position == 1 &&
                            fragmentManager.findFragmentByTag(eventListTAG) != null &&
                            fragmentManager.findFragmentByTag(eventListTAG).isVisible()
                    ) ||
                            (position == 2 &&
                                    fragmentManager.findFragmentByTag(archivioTAG) != null &&
                                    fragmentManager.findFragmentByTag(archivioTAG).isVisible()
                            )
                    )
                            ) {
                        changeFragment(position - 1);
                    }
                    drawerLayout.closeDrawer(leftRL);
                }
            }
        });

        mDrawerListView.setDivider(null);

        mDrawerListView.setAdapter(new DrawerAdapter(
                getActionBar().getThemedContext(),
                R.layout.drawer_line,
                getResources().getStringArray(R.array.list_names2),
                getResources().obtainTypedArray(R.array.list_icons2),
                true
        ));

        LinearLayout LL = (LinearLayout) findViewById(R.id.about);
        LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAbout();
            }
        });

        leftRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Serve per disabilitare il touch alla subview */
            }
        });
    }
    // </editor-fold>

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void changeFragment(int pos) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (listener != null) fragmentManager.removeOnBackStackChangedListener(listener);
        String tag = null;
        switch (pos) {
            case 0:
                fragment = EventiListFragment.newInstance();
                tag = eventListTAG;
                mTitle = getString(R.string.title_section0);
                /*
                fragment = Evento.newInstance(18, "gesu", "123123123", 2);
                tag = eventTAG;
                mTitle = getString(R.string.title_section0);
                */
                break;
            case 1:
                fragment = Archivio.newInstance();
                tag = archivioTAG;
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                fragment = new PrefsFragment();
                tag = impostazioniTAG;
                mTitle = getString(R.string.title_section2);
                break;
        }

        if (tag.equals(impostazioniTAG)) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(impostazioniTAG)
                    .commit();

        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        drawerLayout.closeDrawer(leftRL);
    }

    public static Activity getActivity() {
        return mContext;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    public static boolean drawerIsOpen(MenuInflater inflair, Menu menu) {
        if (drawerAperto) {
            menu.clear();
            inflair.inflate(R.menu.main_no_menu, menu);

            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        int temp = R.menu.main;

        if (drawerAperto) {

            temp = R.menu.main_no_menu;

        }

        getMenuInflater().inflate(temp, menu);

        if (temp == R.menu.main) {
            MenuItem prova = menu.findItem(R.id.progressBarSmall);
            prova.setVisible(progressBarVisible);
        }
        restoreActionBar();

        String title = (String) mTitle;
        if (drawerAperto)
            title = getString(R.string.app_name);

        getActionBar().setTitle(title);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Nuovo_evento) {
            Intent intent = new Intent(MainActivity.this, CreaEventoActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        if (id == R.id.infoEvento) {
            //deve aprire il nuovo Fragment
            //TEST
            infoEventoAperto = true;
            Bundle b = fragmentManager.findFragmentByTag(eventTAG).getArguments();
            Fragment fragment = InfoEvento.newInstance(b);
            //fragmentManager.saveFragmentInstanceState(fragment);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, infoTAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(infoTAG)
                    .commit();

            mTitle = getString(R.string.titleInfoEvento);
            invalidateOptionsMenu();
            //END TEST
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item) && !drawerLayout.isDrawerOpen(leftRL)) {
            drawerLayout.openDrawer(leftRL);
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item) && drawerLayout.isDrawerOpen(leftRL)) {
            drawerLayout.closeDrawer(leftRL);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 0) {
            if (data != null) {
                //String num_utenti = data.getStringExtra("num_utenti");
                //String nome_evento = data.getStringExtra("nome_evento");
                int id_evento = data.getIntExtra("id_evento", -1);

                Log.e("DEBUG ACTIVITY RESULT: ", " " + id_evento);

                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = Evento.newInstance(id_evento);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, eventTAG)
                        .addToBackStack(eventTAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        }

    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("DEBUG: ", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(int id) {
        if (listener != null) fragmentManager.removeOnBackStackChangedListener(listener);
        //noMenuActionBar = true;
        Fragment fragment = Evento.newInstance(id);
        //fragmentManager.popBackStackImmediate();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, eventTAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(eventTAG)
                .commit();
        Log.e("MAINACTIVITY_DEBUG", "sono entrato in on FragmetInteraction con id" + id);
        MainActivity.this.invalidateOptionsMenu();

        /*
        fragmentManager.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        if (fragmentManager.getBackStackEntryCount() == 0 || !fragmentManager.getBackStackEntryAt(0).getName().equals(eventTAG)) {
                            noMenuActionBar = false;

                        }

                        //fragment = fragmentManager.findFragmentById(fragmentManager.getBackStackEntryAt(0).getId());
                        invalidateOptionsMenu();
                    }

                }
        );
        */


    }
}