package com.partymanager.activity.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.partymanager.R;
import com.partymanager.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflar) {

        super.onCreateOptionsMenu(menu, inflar);
        menu.clear();

        inflar.inflate(R.menu.main_no_menu, menu);

        getActivity().getActionBar().setTitle(MainActivity.drawerIsOpen(inflar, menu) ? "Party Manager" : getString(R.string.title_section2));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        MainActivity.mTitle = getString(R.string.title_section2);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {

        getPreferenceScreen().findPreference("checkbox_notifiche_all").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setNotifiche(sharedPreferences.getBoolean("checkbox_notifiche_all", true));
                return false;
            }
        });


    }

    synchronized void setNotifiche(Boolean what) {
        CheckBoxPreference pref;

        List<String> imp = new ArrayList<String>();
        imp.add("checkbox_eventi");
        imp.add("checkbox_domande");
        imp.add("checkbox_risposte");
        imp.add("checkbox_allarme");
        imp.add("checkbox_utenti");

        for (String s : imp) {
            pref = (CheckBoxPreference) getPreferenceScreen().findPreference(s);

            if (pref != null) {
                pref.setChecked(what);
                pref.setEnabled(what);
            }
        }

        pref = (CheckBoxPreference) getPreferenceScreen().findPreference("checkbox_vibrate");
        pref.setEnabled(what);

        pref = (CheckBoxPreference) getPreferenceScreen().findPreference("checkbox_sound");
        pref.setEnabled(what);

        ListPreference pref2 = (ListPreference) getPreferenceScreen().findPreference("downloadType");
        pref2.setEnabled(what);
    }

}