package com.rssaggregator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tapfortap.AdView;
import com.tapfortap.TapForTap;

public class FeedDescriptionActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TapForTap.setDefaultAppId("031a4b50-89fd-012f-29a4-40405d9d80d6");
        TapForTap.checkIn(this);
		
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
				" </head><br><a href=\"" + getRssAggregatorApplication().getFeedUrl()  +" \">Visit Site</a> " +
						"<br> </html>";
		this.setTitle("Home > "+getRssAggregatorApplication().getFeedSourceName()+" > Article");
		webView.loadData(html+feedDescription, "text/html", null);
		
		AdView adView = (AdView) findViewById(R.id.ad_view);
        adView.loadAds();
	}
	
	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.homemenuwithshare, menu);
		return true;
	}
	
	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent ;
		switch (item.getItemId()) {
		case R.id.menu_home:
			intent = new Intent(this, MainRssAggregatorActivity.class);
			startActivity(intent);
			return true;	
		case R.id.menu_back:
			finish();
			return true;
		case R.id.menu_share:
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getRssAggregatorApplication().getFeedTitle() + "(shared via Rss Aggregator)");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getRssAggregatorApplication().getFeedUrl() );
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent,"Share article"));
			return true;			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
