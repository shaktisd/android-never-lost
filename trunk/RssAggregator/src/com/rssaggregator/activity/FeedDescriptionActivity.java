package com.rssaggregator.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class FeedDescriptionActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		WebView webView = (WebView)findViewById(R.id.webview);;
		String feedDescription = getRssAggregatorApplication().getFeedDescription();
		webView.loadData(feedDescription, "text/html", null);
	}
	
	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}
	
}
