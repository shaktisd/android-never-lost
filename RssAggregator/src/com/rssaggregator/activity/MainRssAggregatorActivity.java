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

import com.rssaggregator.valueobjects.Category;
import com.rssaggregator.valueobjects.FeedSource;
import com.rssaggregator.valueobjects.RssFeed;

public class MainRssAggregatorActivity extends ExpandableListActivity {
	private static final String NAME = "NAME";
	private RssAggregatorApplication rssAggregatorApplication;
	private SimpleExpandableListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainrssaggregator);
		rssAggregatorApplication = getRssAggregatorApplication();
		if ( rssAggregatorApplication.findAllCategory().size() == 0 ){
			List<FeedSource> feedSources1 = new ArrayList<FeedSource>();
			feedSources1.add(new FeedSource("NDTV","http://feeds.feedburner.com/NdtvNews-TopStories"));
			feedSources1.add(new FeedSource("HINDUSTAN TIMES","http://feeds.hindustantimes.com/HT-HomePage-TopStories"));
			feedSources1.add(new FeedSource("TIME OF INDIA","http://timesofindia.indiatimes.com/rssfeedstopstories.cms"));
			Category category1 = new Category("NEWS",feedSources1);
			rssAggregatorApplication.save(category1);
			
			List<FeedSource> feedSources2 = new ArrayList<FeedSource>();
			feedSources2.add(new FeedSource("ECONOMIC TIMES","http://economictimes.indiatimes.com/rssfeedsdefault.cms"));
			feedSources2.add(new FeedSource("CNN MONEY","http://rss.cnn.com/rss/money_latest.rss"));
			Category category2 = new Category("FINANCE",feedSources2);
			rssAggregatorApplication.save(category2);
			
			List<FeedSource> feedSources3 = new ArrayList<FeedSource>();
			feedSources3.add(new FeedSource("CRIC INFO WORLD","http://www.espncricinfo.com/rss/content/feeds/news/0.xml"));
			feedSources3.add(new FeedSource("CRIC INFO INDIA","http://www.espncricinfo.com/rss/content/feeds/news/6.xml"));
			Category category3 = new Category("SPORTS",feedSources3);
			rssAggregatorApplication.save(category3);
			
			List<FeedSource> feedSources4 = new ArrayList<FeedSource>();
			feedSources4.add(new FeedSource("ENGADGET","http://www.engadget.com/rss.xml"));
			feedSources4.add(new FeedSource("SLASHDOT","http://rss.slashdot.org/Slashdot/slashdotDevelopers"));
			Category category4 = new Category("TECHNOLOGY",feedSources4);
			rssAggregatorApplication.save(category4);
			
			
		}
		
		showCategories();
		getExpandableListView().setOnChildClickListener(this);
	}
	
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,int childPosition, long id) {
		if (!rssAggregatorApplication.isOnline()){
			Toast.makeText(this, "No INTERNET connection detected", Toast.LENGTH_LONG).show();
			return true;
		}
		HashMap<String,String> feedSourceDetails = (HashMap<String,String>)mAdapter.getChild(groupPosition, childPosition);
		String feedSource = (String)feedSourceDetails.values().iterator().next();
		rssAggregatorApplication.setFeedSourceName(feedSource);
		Intent intent = new Intent(this, RssAggregatorActivity.class);
		startActivity(intent);
		return true;
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
		/*case R.id.menu_add_rss_source:
			Intent intent = new Intent(this, FeedSourceActivity.class);
			startActivity(intent);
			return true;	*/		
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
			showCategories();
		}
	     
	     
	 }

	
	private void showCategories(){
		List<Category> categories = rssAggregatorApplication.findAllCategory();
		
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		for(Category category : categories ){
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(NAME, category.getCategoryName());

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for (FeedSource feedSource: category.getFeedSources()) {
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				curChildMap.put(NAME, feedSource.getFeedSourceName());
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
	
	private void refreshRssFeeds() {
		Log.i("RSSAGGREGATOR", "Refreshing feeds ");
		List<RssFeed> rssFeeds = rssAggregatorApplication.getAllRssFeedsFromSource();
		rssAggregatorApplication.storeFeeds(rssFeeds);
	}
}
