package com.rssaggregator.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {


	private static final String LOADING = "Loading";


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		String url = getRssAggregatorApplication().getFeedUrl();
		WebView webView = new WebView(this);
		setContentView(webView);
		// Makes Progress bar Visible
		getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		webView.getSettings().setJavaScriptEnabled(true);
		final Activity activity = this;
		activity.setTitle(LOADING);
		webView.setWebChromeClient(new WebChromeClient() {
		    public void onProgressChanged(WebView view, int progress) {
		        activity.setProgress(progress * 100);
		        if(progress == 100 ){
		        	 activity.setTitle(R.string.app_name);
		        }else if (activity.getTitle() != LOADING ){
		        	activity.setTitle(LOADING);
		        }
		    }
		});

		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		
		});
		webView.loadUrl(url);
	}


	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}

}