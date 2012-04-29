package com.rssaggregator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FeedDescriptionActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		WebView webView = (WebView)findViewById(R.id.webview);;
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				getRssAggregatorApplication().setFeedUrl(url);
				Intent intent = new Intent (getBaseContext(),WebViewActivity.class);
				startActivity(intent);
				return true;
			}
		
		});
		
		String feedDescription = getRssAggregatorApplication().getFeedDescription();
		String html = "<html> <head> " +
				"<style type=\"text/css\"> body { font-family: serif; } </style>" +
				" </head><br><a href=\"" + getRssAggregatorApplication().getFeedUrl()  +" \">Visit Site</a> <br> </html>";
		this.setTitle(getRssAggregatorApplication().getFeedSourceName());
		webView.loadData(html+feedDescription, "text/html", null);
		
	}
	
	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}
	
}
