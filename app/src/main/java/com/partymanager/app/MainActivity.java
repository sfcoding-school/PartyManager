package com.partymanager.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.partymanager.R;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (checkPlayServices()) {
            //Controllo se esiste già una sessione FB attiva
            Session session = Session.getActiveSession();
            if (session == null) {
                session = new Session(this);
                Session.setActiveSession(session);
                if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                    session.openForRead(new Session.OpenRequest(this));
                }
            }

            if (!session.isOpened()) {
                Intent newact = new Intent(this, ProfileActivity.class);
                startActivity(newact);
            }
                //Fine controllo sessione



                mNavigationDrawerFragment = (NavigationDrawerFragment)
                        getFragmentManager().findFragmentById(R.id.navigation_drawer);
                //mTitle = getTitle();

                // Set up the drawer.
                mNavigationDrawerFragment.setUp(
                        R.id.navigation_drawer,
                        (DrawerLayout) findViewById(R.id.drawer_layout));

/*

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
*/
            //mDisplay = (TextView) findViewById(R.id.pp);


        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        //FragmentTransaction fTransaction =
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = EventiListFragment.newInstance();
                mTitle = getString(R.string.title_section0);
                break;
            case 1:
                fragment = Archivio.newInstance();
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                fragment = Setting.newInstance();
                mTitle = getString(R.string.title_section2);
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    /*public void onSectionAttached(int number) {

        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }

    }*/

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
        if ( mNavigationDrawerFragment!= null && !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem prova = menu.findItem(R.id.progressBarSmall);
            prova.setVisible(progressBarVisible);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.profile) {
            Intent newact = new Intent(this, ProfileActivity.class);
            newact.putExtra("chiave", "1");
            startActivity(newact);
            return true;
        }
        if (id == R.id.Nuovo_evento) {
            Intent intent = new Intent(MainActivity.this, CreaEventoActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        //DEBUG ONLY
        if (id == R.id.evento) {
            FragmentManager fragmentManager = getFragmentManager();
            //Fragment fragment = Evento.newInstance("niente", "Prova Evento 1", "id");
            Fragment fragment = provaFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        //FINE TEST ZONE
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
                Fragment fragment = Evento.newInstance(ListFriends, nome_evento, id_evento);
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

}
