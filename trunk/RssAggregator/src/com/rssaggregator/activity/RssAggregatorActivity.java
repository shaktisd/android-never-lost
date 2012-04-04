package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.google.ads.AdView;
import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.RssFeed;

public class RssAggregatorActivity extends ExpandableListActivity {
	private static final String NOT_READ = "N";
	private static final String READ = "Y";
	private static final String NAME = "NAME";
	/** Called when the activity is first created. */
	RssAggregatorApplication rssAggregatorApplication;
	private SimpleExpandableListAdapter mAdapter;
	/** The view to show the ad. */
	private AdView adView;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmain);
		rssAggregatorApplication = getRssAggregatorApplication();
		showRssFeeds("onCreate");
		getExpandableListView().setOnChildClickListener(this);
		getExpandableListView().expandGroup(0);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,int childPosition, long id) {
		if (!rssAggregatorApplication.isOnline()){
			Toast.makeText(this, "No INTERNET connection detected", Toast.LENGTH_LONG).show();
			return true;
		}
		HashMap<String,String> title = (HashMap<String,String>)mAdapter.getChild(groupPosition, childPosition);
		 Feed query = new Feed();
		 query.setTitle(extractTitle(title.values().iterator().next()));
		 Feed feed = rssAggregatorApplication.findFeed(query);
		 feed.setFeedRead(true);
		 rssAggregatorApplication.saveFeed(feed);
		 
		 Log.i("RSSAGGREGATOR","group " + groupPosition + " childPosition " + childPosition +  " id " + id + 
				 " title " + title  + " url " + feed.getUrl());
		 rssAggregatorApplication.setFeedUrl(feed.getUrl());
		 rssAggregatorApplication.setFeedDescription(feed.getDescription());
		 //Intent intent = new Intent(this, WebViewActivity.class);
		 Intent intent = new Intent(this, FeedDescriptionActivity.class);
		 startActivity(intent);
		 
		return true;
	}


	private String extractTitle(String str) {
		return str.substring(0,str.indexOf("\n"));
	}

	private void showRssFeeds(String calledFrom) {
		List<RssFeed> rssFeeds = rssAggregatorApplication.findAllFeedsWithFeedSource(rssAggregatorApplication.getFeedSourceName());
		
		//Log.d("RSSAGGREGATOR","RSS FEEDS SIZE IN DB" +rssFeeds.size());
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		for(RssFeed rssFeed : rssFeeds ){
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(NAME, rssFeed.getFeedSource());

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for (Feed feed : rssFeed.getFeeds()) {
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				curChildMap.put(NAME, feed.getTitle() + "\n" + getFormattedTime(feed) );
			}
			childData.add(children);
			
		}
		mAdapter = new SimpleExpandableListAdapter(this, 
				groupData,
				R.layout.custom_expandable_list,
				new String[] {NAME}, 
				new int[] { R.id.customtext1}, 
				childData,
				R.layout.custom_row,
				new String[] {NAME }, 
				new int[] { R.id.customrowtext1});
		setListAdapter(mAdapter);

	}
	
	private String isFeedRead(boolean isFeedRead){
		if (isFeedRead){
			return READ;
		}else {
			return NOT_READ;
		}
	}
	
	private String getFormattedTime(Feed feed) {
		String strDate;
		if (feed.getDate() == null){
			strDate = "";
		}else {
			strDate = feed.getDate().toString();
		}
		
		return strDate;
	}

	private void refreshRssFeeds() {
		Log.i("RSSAGGREGATOR", "Refreshing feeds ");
		List<RssFeed> rssFeeds = rssAggregatorApplication.getAllRssFeedsFromSource();
		rssAggregatorApplication.storeFeeds(rssFeeds);
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
			if (!rssAggregatorApplication.isOnline()){
				Toast.makeText(this, "No INTERNET connection detected", Toast.LENGTH_LONG).show();
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
			Toast.makeText(getApplicationContext(), "Refresh complete", Toast.LENGTH_SHORT).show();
			showRssFeeds("OnDownload Complete");
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