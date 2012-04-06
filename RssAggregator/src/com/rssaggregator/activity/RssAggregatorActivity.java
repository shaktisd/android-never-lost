package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.ads.AdView;
import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.RssFeed;

public class RssAggregatorActivity extends Activity {
	private static final String NOT_READ = "N";
	private static final String READ = "Y";
	/** Called when the activity is first created. */
	RssAggregatorApplication rssAggregatorApplication;
	/** The view to show the ad. */
	private AdView adView;
	private ListView listView;
	private ArrayAdapter<String> listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmain);
		rssAggregatorApplication = getRssAggregatorApplication();
		this.setTitle(rssAggregatorApplication.getFeedSourceName());
		final List<String> rssFeeds = getRssFeeds();
		listView = (ListView) findViewById(R.id.listView1);
		listAdapter = new ArrayAdapter<String>(this,R.layout.list_item, rssFeeds);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (!rssAggregatorApplication.isOnline()) {
					Toast.makeText(getApplicationContext(),
							"No INTERNET connection detected",
							Toast.LENGTH_LONG).show();
				}

				String title = rssFeeds.get(position);
				Feed query = new Feed();
				query.setTitle(extractTitle(title));
				Feed feed = rssAggregatorApplication.findFeed(query);
				feed.setFeedRead(true);
				rssAggregatorApplication.saveFeed(feed);

				Log.i("RSSAGGREGATOR", "position " + position + " title " + title + " url " + feed.getUrl());
				rssAggregatorApplication.setFeedUrl(feed.getUrl());
				rssAggregatorApplication.setFeedDescription(feed.getDescription());
				Intent intent = new Intent(getApplicationContext(),FeedDescriptionActivity.class);
				startActivity(intent);
			}

		});
	}

	private String extractTitle(String str) {
		return str.substring(0, str.indexOf("\n"));
	}

	private List<String> getRssFeeds() {
		List<RssFeed> rssFeeds = rssAggregatorApplication.findAllFeedsWithFeedSource(rssAggregatorApplication.getFeedSourceName());
		List<String> feedTitles = new ArrayList<String>();
		if ( rssFeeds.size() > 0 ){
			for (Feed feed : rssFeeds.get(0).getFeeds()) {
				feedTitles.add(feed.getTitle() + "\n" + getFormattedTime(feed));
			}	
		}
		
		return feedTitles;
	}

	private String isFeedRead(boolean isFeedRead) {
		if (isFeedRead) {
			return READ;
		} else {
			return NOT_READ;
		}
	}

	private String getFormattedTime(Feed feed) {
		String strDate;
		if (feed.getDate() == null) {
			strDate = "";
		} else {
			strDate = feed.getDate().toString();
		}

		return strDate;
	}

	private void refreshRssFeeds() {
		Log.i("RSSAGGREGATOR", "Refreshing feeds ");
		List<RssFeed> rssFeeds = rssAggregatorApplication.getAllRssFeedsFromSource();
		rssAggregatorApplication.storeFeeds(rssFeeds);
		listAdapter.notifyDataSetChanged();
	}

	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			if (!rssAggregatorApplication.isOnline()) {
				Toast.makeText(this, "No INTERNET connection detected",
						Toast.LENGTH_LONG).show();
				return true;
			}
			Toast.makeText(this, "Refresh started", Toast.LENGTH_SHORT).show();
			new DownloadFilesTask().execute(getApplicationContext());
			return true;
		case R.id.menu_add_rss_source:
			Intent intent = new Intent(this, FeedSourceActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DownloadFilesTask extends AsyncTask<Context, String, String> {
		protected String doInBackground(Context... context) {
			refreshRssFeeds();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), "Refresh complete",
					Toast.LENGTH_SHORT).show();
		}

	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}

		super.onDestroy();
	}

}