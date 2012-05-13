package com.rssaggregator.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	protected void onResume() {
		super.onResume();
		if(rssAggregatorApplication.isRefreshMainDataSet()){
			showCategories();
			mAdapter.notifyDataSetChanged();	
			rssAggregatorApplication.setRefreshMainDataSet(false);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("RSS Aggregator Home");
		setContentView(R.layout.mainrssaggregator);
		rssAggregatorApplication = getRssAggregatorApplication();
		if ( rssAggregatorApplication.findAllCategory().size() == 0 ){
			List<FeedSource> feedSources1 = new ArrayList<FeedSource>();
			feedSources1.add(new FeedSource("Ndtv","http://feeds.feedburner.com/NdtvNews-TopStories"));
			feedSources1.add(new FeedSource("BBC News - Home","http://newsrss.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml"));
			feedSources1.add(new FeedSource("CNN.com","http://rss.cnn.com/rss/cnn_topstories.rss"));
			 
			Category category1 = new Category("News",feedSources1);
			rssAggregatorApplication.save(category1);
			
			List<FeedSource> feedSources4 = new ArrayList<FeedSource>();
			feedSources4.add(new FeedSource("Engadget","http://www.engadget.com/rss.xml"));
			feedSources4.add(new FeedSource("Slashdot","http://rss.slashdot.org/Slashdot/slashdot"));
			Category category4 = new Category("Technology",feedSources4);
			rssAggregatorApplication.save(category4);
			
			
			List<FeedSource> feedSources5 = new ArrayList<FeedSource>();
			feedSources5.add(new FeedSource("Dilbert","http://feed.dilbert.com/dilbert/daily_strip"));
			Category category5 = new Category("Webcomics",feedSources5);
			rssAggregatorApplication.save(category5);
			
			final AlertDialog alertDialog = new AlertDialog.Builder(MainRssAggregatorActivity.this).create();
			alertDialog.setTitle("Application startup");
			alertDialog.setMessage("Running application for the first time. Click on Menu then Refresh button to download articles. Rss articles will be visible once download is complete.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  alertDialog.dismiss();
			    } }); 
			alertDialog.show();
		}
		
		showCategories();
		getExpandableListView().setOnChildClickListener(this);
	}
	
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,int childPosition, long id) {
		HashMap<String,String> feedSourceDetails = (HashMap<String,String>)mAdapter.getChild(groupPosition, childPosition);
		String feedSource = (String)feedSourceDetails.values().iterator().next();
		rssAggregatorApplication.setFeedSourceName(feedSource);
		ProgressDialog progressDialog = ProgressDialog.show(MainRssAggregatorActivity.this, "", "Loading...");
		rssAggregatorApplication.setProgressDialog(progressDialog);
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



	private class DownloadFilesTask extends AsyncTask<Context, String, String> {
		NotificationManager notificationManager;
		int icon;
		String tickerText;
		long time;
		Notification notification;
		String contentTitle;
		String contentText;
		PendingIntent contentIntent;
		int HELLO_ID = 1;

		@Override
		protected void onPreExecute() {
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			icon = R.drawable.readrss;
			// the text that appears first on the status bar
			tickerText = "Downloading...";
			time = System.currentTimeMillis();
			notification = new Notification(icon, tickerText, time);
			// the bold font
			contentTitle = "RSS Feed download in progress";
			// the text that needs to change
			contentText = "0% complete";
			Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
			contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
			notificationManager.notify(HELLO_ID, notification);

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Context... context) {
			Log.i("RSSAGGREGATOR", "Refreshing feeds ");
			List<FeedSource> allFeedSources = rssAggregatorApplication.findAllFeedSource();
			int totalFeeds = allFeedSources.size();
			int storedFeeds = 0;
			publishProgress(storedFeeds + "/" + totalFeeds);
			for (FeedSource feedSource : allFeedSources) {
				List<FeedSource> listFeedSource = new ArrayList<FeedSource>();
				listFeedSource.add(feedSource);
				List<RssFeed> rssFeeds = rssAggregatorApplication.getRssFeeds(listFeedSource);
				rssAggregatorApplication.storeFeeds(rssFeeds);
				storedFeeds++;
				publishProgress(storedFeeds + "/" + totalFeeds);
			}
			Log.i("RSSAGGREGATOR", "Refresh complete");
			
			
			//ApplicationConfiguration applicationConfiguration = getRssAggregatorApplication().getApplicationConfiguration();
			Log.i("RSSAGGREATOR"," Deleting feeds");
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context[0]);
			String numberOfDaysForOldArticles = sharedPrefs.getString("numberOfDaysForOldArticles", "0");
			Boolean deleteReadRssArticlesCheckbox = sharedPrefs.getBoolean("deleteReadRssArticlesCheckbox", true); 
			
			Log.i("RSSAGGREATOR"," Deleting feeds older than " + numberOfDaysForOldArticles);
			publishProgress("Deleting feeds older than " + numberOfDaysForOldArticles + " days ");
			rssAggregatorApplication.deleteFeedsOlderThanDays(Integer.parseInt(numberOfDaysForOldArticles));
			publishProgress("Deleted feeds older than " + numberOfDaysForOldArticles + " days ");
			if ( deleteReadRssArticlesCheckbox){
				Log.i("RSSAGGREATOR"," Deleting already read feeds ");
				publishProgress("Deleting already read feeds" );
				rssAggregatorApplication.deleteFeedsAlreadyRead();
				publishProgress("Deleted already read feeds" );
			}
			
			Log.i("RSSAGGREATOR"," Deleted feeds");

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), "Refresh complete", Toast.LENGTH_SHORT).show();
			notificationManager.cancel(HELLO_ID);
			// notificationManager.notify(HELLO_ID, notification);
		}

		@Override
		public void onProgressUpdate(String... progress) {
			contentText = progress[0] + " complete";
			notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
			notificationManager.notify(HELLO_ID, notification);
			super.onProgressUpdate(progress);
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
	
	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's id
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent ;
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			if (!rssAggregatorApplication.isOnline()){
				Toast.makeText(this, "No INTERNET connection detected", Toast.LENGTH_LONG).show();
				return true;
			}
			Toast.makeText(this, "Refresh started", Toast.LENGTH_SHORT).show();
			new DownloadFilesTask().execute(getApplicationContext());
			return true;
		case R.id.menu_add_rss_category:
			intent = new Intent(this, CategoryActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_add_rss_source:
			intent = new Intent(this, FeedSourceActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_settings:
			intent = new Intent(this, RssPreferencesActivity.class);
			startActivity(intent);
			return true;			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
