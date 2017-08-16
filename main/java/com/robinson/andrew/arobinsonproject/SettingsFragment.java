package com.robinson.andrew.arobinsonproject;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        onSharedPreferenceChanged(sharedPreferences, "numSegs");
        onSharedPreferenceChanged(sharedPreferences, "numRocks");
        onSharedPreferenceChanged(sharedPreferences, "numLives");
    }

    @Override
    public void onResume() {
        super.onResume() ;
        getPreferenceScreen().getSharedPreferences().
                registerOnSharedPreferenceChangeListener(this) ;
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().
                unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        switch(key) {
            case "numSegs":
                double numSegs = Double.parseDouble(sharedPreferences.getString(key, "10"));
                if (numSegs < 5) numSegs = 5;
                if (numSegs > 15) numSegs = 15;
                sharedPreferences.edit().putString(key, Integer.toString((int)numSegs)).commit();
                pref.setSummary("" + (int)numSegs);
                break;

            case "numRocks":
                double numRocks = Double.parseDouble(sharedPreferences.getString(key, "15"));
                if (numRocks < 5) numRocks = 5;
                if (numRocks > 25) numRocks = 25;
                sharedPreferences.edit().putString(key, Integer.toString((int)numRocks)).commit();
                pref.setSummary("" + (int)numRocks);
                break;

            case "numLives":
                double numLives = Double.parseDouble(sharedPreferences.getString(key, "3"));
                if (numLives < 1) numLives = 1;
                if (numLives > 3) numLives = 3;
                sharedPreferences.edit().putString(key, Integer.toString((int)numLives)).commit();
                pref.setSummary("" + (int)numLives);
                break;
        }
    }
}