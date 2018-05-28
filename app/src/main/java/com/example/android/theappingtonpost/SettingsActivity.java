package com.example.android.theappingtonpost;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.widget.EditText;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;


public class SettingsActivity extends AppCompatActivity {

    private ArrayList<Map.Entry<String, String>> mSections;


    @SuppressWarnings("unused")
    public static final String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            ListPreference section = (ListPreference) findPreference(getString(R.string
                    .settings_section_key));
            section.setEntries(new CharSequence[]{getString(R.string.settings_section_none_label)});
            section.setEntryValues(new CharSequence[]{getString(R.string
                    .settings_section_none_value)});
            bindPreferenceSummaryToValue(section);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference pageSize = findPreference(getString(R.string.settings_page_size_key));
            EditText pageSizeEdit = ((EditTextPreference) pageSize).getEditText();
            pageSizeEdit.setFilters(new InputFilter[]{new MinMaxFilter(getString(R.string
                    .settings_page_size_min), getString(R.string.settings_page_size_max))});
            bindPreferenceSummaryToValue(pageSize);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }

    /**
     * A LoaderManager with the purpose of reading the available sections from the server.
     * This won't need to be refreshed as (hopefully) sections don't change a lot.
     */
    private class SectionCallback implements LoaderManager.LoaderCallbacks<ArrayList<Map
            .Entry<String, String>>> {

        Context mContext;

        SectionCallback(Context context) {
            super();
            mContext = context;
        }

        @Override
        public Loader<ArrayList<Map.Entry<String, String>>> onCreateLoader(int i, Bundle bundle) {
            //URL for news data from the guardian api
            String GUARDIAN_SECTION_URL = "https://content.guardianapis.com/sections?";
            Uri baseUri = Uri.parse(GUARDIAN_SECTION_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("api-key", NewsActivity.KEY);
            // Create a new loader for the given URL
            return new SectionLoader(mContext, uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Map.Entry<String, String>>> loader,
                                   ArrayList<Map.Entry<String, String>> sections) {
            sectionClear();
            mSections.addAll(sections);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Map.Entry<String, String>>> loader) {
            // Loader reset, so we can clear out our existing data.
            sectionClear();
        }

    }


}
