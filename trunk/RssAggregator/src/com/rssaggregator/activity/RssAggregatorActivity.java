package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.RssFeed;

public class RssAggregatorActivity extends ExpandableListActivity {
	private static final String NAME = "NAME";
	/** Called when the activity is first created. */
	RssAggregatorApplication rssAggregatorApplication;
	private SimpleExpandableListAdapter mAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		rssAggregatorApplication = getRssAggregatorApplication();
		showRssFeeds("onCreate");
		getExpandableListView().setOnChildClickListener(this);
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
		 Log.i("RSSAGGREGATOR","group " + groupPosition + " childPosition " + childPosition +  " id " + id + 
				 " title " + title  + " url " + feed.getUrl());
		 rssAggregatorApplication.setFeedUrl(feed.getUrl());
		 Intent intent = new Intent(this, WebViewActivity.class);
		 startActivity(intent);
		 
		return true;
	}


	private String extractTitle(String str) {
		return str.substring(0,str.indexOf("\n"));
	}

	private void showRssFeeds(String calledFrom) {
		//Log.d("RSSAGGREGATOR" , "showRssFeeds" + calledFrom);
		List<RssFeed> rssFeeds = rssAggregatorApplication.findAllRssFeeds();
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
				curChildMap.put(NAME, feed.getTitle() + "\n" + getFormattedTime(feed));
			}
			childData.add(children);
			
		}
		mAdapter = new SimpleExpandableListAdapter(this, 
				groupData,
				android.R.layout.simple_expandable_list_item_1, 
				new String[] {NAME}, 
				new int[] { android.R.id.text1}, 
				childData,
				R.layout.custom_row,
				new String[] {NAME }, 
				new int[] { android.R.id.text1 });
		setListAdapter(mAdapter);

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
		/*for(RssFeed r1 : rssFeeds){
			Log.i("RSSAGGREGATOR","Before Update feeds " + r1.getFeeds());
		}*/
		
		rssAggregatorApplication.updateRssFeeds(rssFeeds);
		//List<RssFeed> allRssFeedsAfterUpdate = rssAggregatorApplication.findAllRssFeeds();
		//Log.i("RSSAGGREGATOR","After update size " + allRssFeedsAfterUpdate.size());
		/*for(RssFeed r2 : allRssFeedsAfterUpdate){
			Log.i("RSSAGGREGATOR","After Update feeds " + r2.getFeeds());
		}*/
		//Log.d("RSSAGGREGATOR", "Refreshed feeds ");
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

}