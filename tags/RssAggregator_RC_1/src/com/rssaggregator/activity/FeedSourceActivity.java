package com.rssaggregator.activity;


import com.rssaggregator.valueobjects.FeedSource;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FeedSourceActivity extends Activity {

	Button saveRssSourceButton ;
	TextView rssSourceText;
	TextView rssUrlText;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_source);
		registerViewItems();
		registerClickListenersToButtons();
	}


	private void registerClickListenersToButtons() {
		saveRssSourceButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg0) {
				
				FeedSource feedSource = new FeedSource(rssSourceText.getText().toString(),rssUrlText.getText().toString());
				RssAggregatorApplication rssAggregatorApplication = getRssAggregatorApplication();
				rssAggregatorApplication.updateFeedSource(feedSource);
			}
		});
	}


	private void registerViewItems() {
			saveRssSourceButton = (Button) findViewById(R.id.saveRssSource);
			rssSourceText = (TextView) findViewById(R.id.rssSourceName);
			rssUrlText = (TextView) findViewById(R.id.rssUrl);
	}


	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}

}