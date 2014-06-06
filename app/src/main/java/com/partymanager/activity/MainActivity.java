package com.partymanager.activity;

import android.app.ActionBar;
import android.app.Activity;
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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.partymanager.activity.fragment.PrefsFragment;
import com.partymanager.data.DatiAttributi;
import com.partymanager.data.DatiEventi;
import com.partymanager.data.DrawerAdapter;
import com.partymanager.helper.HelperFacebook;

public class MainActivity extends Activity
        implements EventiListFragment.OnFragmentInteractionListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private FragmentManager fragmentManager;
    private boolean noMenuActionBar = false;
    public static CharSequence mTitle;
    private static Activity mContext;
    RelativeLayout leftRL;
    DrawerLayout drawerLayout;
    ListView mDrawerListView;
    ActionBarDrawerToggle mDrawerToggle;
    public static Handler handlerService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handlerService = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("SERVICEHANDLER", "arrivato il messaggio " + msg.toString());
                Bundle b = msg.getData();
                String type = b.getString("type");
                if (type.equals("newEvent")) {
                    if (fragmentManager.findFragmentByTag("Eventi").isVisible()) {
                        DatiEventi.addItem(new DatiEventi.Evento(b.getInt("id"), b.getString("name"), "", "", b.getString("adminId"), b.getInt("numUtenti")));
                    }
                } else if (type.equals("newAttr")) {
                    if (fragmentManager.findFragmentByTag("Evento").isVisible()) {
                        DatiAttributi.addItem(new DatiAttributi.Attributo("id", "doma", "risposta", "template", false, /*numd*/1, /*numr*/ 2));
                    }
                }
            }
        };

        mContext = this;
        fragmentManager = getFragmentManager();

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
                changeFragment(0);
            }
        }
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
                        changeFragment(2);
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
                        Email.putExtra(Intent.EXTRA_SUBJECT, R.string.mailSubject + " (" + (pInfo != null ? pInfo.versionName : null) + ")");
                        Email.putExtra(Intent.EXTRA_TEXT, R.string.txtMail);
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
                    changeFragment(position - 1);
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
    }

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
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void changeFragment(int pos) {
        Fragment fragment = null;
        switch (pos) {
            case 0:
                fragment = EventiListFragment.newInstance();
                mTitle = getString(R.string.title_section0);
                break;
            case 1:
                fragment = Archivio.newInstance();
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                fragment = new PrefsFragment();
                mTitle = getString(R.string.title_section2);
                break;
        }


        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, mTitle.toString())
                .commit();
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

    public static boolean progressBarVisible = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!noMenuActionBar) {
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_no_menu, menu);
        }
        MenuItem prova = menu.findItem(R.id.progressBarSmall);
        prova.setVisible(progressBarVisible);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.Nuovo_evento) {
            Intent intent = new Intent(MainActivity.this, CreaEventoActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        if (id == R.id.debug) {
            Fragment fragment = Evento.newInstance(null, null, null, null);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, String.valueOf(mTitle))
                    .addToBackStack("evento")
                    .commit();
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
                String ListFriends = data.getStringExtra("listfriend");
                String nome_evento = data.getStringExtra("nome_evento");
                String id_evento = data.getStringExtra("id_evento");
                Log.e("DEBUG ACTIVITY RESULT: ", ListFriends + " " + nome_evento + " " + id_evento);

                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = Evento.newInstance(null, null, null, null);
                mTitle = nome_evento;
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
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
    public void onFragmentInteraction(String id, String name, String admin, String num) {
        mTitle = name;
        noMenuActionBar = true;
        Fragment fragment = Evento.newInstance(id, name, admin, num);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, "Evento")
                .addToBackStack("evento")
                .commit();

        fragmentManager.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        if (fragmentManager.getBackStackEntryCount() == 0 || !fragmentManager.getBackStackEntryAt(0).getName().equals("evento")) {
                            noMenuActionBar = false;
                        }
                        invalidateOptionsMenu();
                    }

                }
        );


    }
}
