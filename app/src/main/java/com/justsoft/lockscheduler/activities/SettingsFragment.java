package com.justsoft.lockscheduler.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import com.justsoft.lockscheduler.R;
import com.justsoft.lockscheduler.preferences.TimePikerPreference;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragment {

	static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof TimePikerPreference) {
				int v = Integer.valueOf(stringValue);
				preference.setSummary(String.format(Locale.getDefault(), "%02d:%02d", v / 60, v % 60));
			}
			return true;
		}
	};

	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		if (preference instanceof TimePikerPreference) {
			sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
					PreferenceManager
							.getDefaultSharedPreferences(preference.getContext())
							.getInt(preference.getKey(), 0));
		} else {
			sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
					PreferenceManager
							.getDefaultSharedPreferences(preference.getContext())
							.getString(preference.getKey(), ""));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_start_block)));
		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_end_block)));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		View rootView = getView();
		ListView list = (ListView) rootView.findViewById(android.R.id.list);
		list.setDivider(null);
	}
}
