package com.partymanager.activity.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.partymanager.R;

public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
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
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {

        getPreferenceScreen().findPreference("checkbox_notifiche_all").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setNotifiche(sharedPreferences.getBoolean("checkbox_notifiche_all", false));
                return false;
            }
        });


    }

    synchronized void setNotifiche(Boolean what) {
        CheckBoxPreference pref;

        for (int i = 1; i < 6; i++) {
            pref = (CheckBoxPreference) getPreferenceScreen().findPreference("checkbox_preference" + Integer.toString(i));

            if (pref != null) {
                pref.setChecked(what);
                pref.setEnabled(what);
            }
        }
    }

}