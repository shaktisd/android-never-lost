package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.content.Intent;
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
		showRssFeeds();
		getExpandableListView().setOnChildClickListener(this);
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,int childPosition, long id) {
		HashMap<String,String> title = (HashMap<String,String>)mAdapter.getChild(groupPosition, childPosition);
		 Feed query = new Feed();
		 query.setTitle(extractTitle(title.values().iterator().next()));
		 Feed feed = rssAggregatorApplication.findFeed(query);
		 Log.i("RssAggregatorActivity","group " + groupPosition + " childPosition " + childPosition +  " id " + id + 
				 " title " + title  + " url " + feed.getUrl());
		 rssAggregatorApplication.setFeedUrl(feed.getUrl());
		 Intent intent = new Intent(this, WebViewActivity.class);
		 startActivity(intent);
		 
		return true;
	}


	private String extractTitle(String str) {
		return str.substring(0,str.indexOf("\n"));
	}

	private void showRssFeeds() {
		List<RssFeed> rssFeeds = rssAggregatorApplication.findAllRssFeeds();
		Log.d("FEEDS","RSS FEEDS SIZE " +rssFeeds.size());
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
		StringBuffer sb = new StringBuffer();
		if (feed.getDate() == null){
			sb.append("");
		}else {
			sb.append((feed.getDate()));
		}
		return sb.toString();
	}

	private void refreshRssFeeds() {
		Log.i("FEEDS", "Refreshing feeds ");
		List<RssFeed> rssFeeds = rssAggregatorApplication.getAllRssFeedsFromSource();
		rssAggregatorApplication.updateRssFeeds(rssFeeds);
		Log.d("FEEDS", "Refreshed feeds ");
		showRssFeeds();
	}

	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}

	/* Initiating Menu XML file (menu.xml) */
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
			refreshRssFeeds();
			return true;
		case R.id.menu_add_rss_source:
			Intent intent = new Intent(this, FeedSourceActivity.class);
			startActivity(intent);
			return true;			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}