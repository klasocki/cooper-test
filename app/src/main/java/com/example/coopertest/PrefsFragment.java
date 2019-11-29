package com.example.coopertest;

import android.os.Bundle;

//import com.example.util.timereminder.R;
import com.example.coopertest.DatePreferenceDialogFragment;
import com.example.coopertest.DatePreference;

import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

/**
 * Displays different preferences.
 */
public class PrefsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        initSummary(getPreferenceScreen());
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof DatePreference) {
            final DialogFragment f;
            f = DatePreferenceDialogFragment.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), null);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    /**
     * Walks through all preferences.
     *
     * @param p The starting preference to search from.
     */
    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            setPreferenceSummary(p);
        }
    }

    /**
     * Sets up summary providers for the preferences.
     *
     * @param p The preference to set up summary provider.
     */
    private void setPreferenceSummary(Preference p) {
        // No need to set up preference summaries for checkbox preferences because
        // they can be set up in xml using summaryOff and summary On
        if (p instanceof DatePreference) {
            p.setSummaryProvider(DatePreference.SimpleSummaryProvider.getInstance());
        } else if (p instanceof EditTextPreference) {
            p.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        }
    }
}