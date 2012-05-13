package com.rssaggregator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class RssPreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Preference bundledFeedPreference = (Preference) findPreference("bundledFeed");
		Intent intent = new Intent(this, ImportOpmlFeedsActivity.class);
		bundledFeedPreference.setIntent(intent);
	}
}
