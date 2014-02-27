package com.rssaggregator.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdView;
import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.RssFeed;

public class RssAggregatorActivity extends ListActivity {
	private static final char NOT_READ = 'N';
	private static final char READ = 'Y';
	/** Called when the activity is first created. */
	RssAggregatorApplication rssAggregatorApplication;
	/** The view to show the ad. */
	private AdView adView;
	private List<String> rssFeeds ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmain);
		rssAggregatorApplication = getRssAggregatorApplication();
		this.setTitle("Home > "+rssAggregatorApplication.getFeedSourceName());
		EfficientAdapter efficientAdapter = new EfficientAdapter(RssAggregatorActivity.this);
		setListAdapter(efficientAdapter);
		rssFeeds =  getRssFeeds();
		rssAggregatorApplication.getProgressDialog().dismiss();
	}
	
	private class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public EfficientAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            // Icons bound to the rows.
		}

		@Override
		public int getCount() {
			return rssFeeds != null ? rssFeeds.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.customitemtext1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String itemText = rssFeeds.get(position);
			if (isFeedRead(itemText)){
				holder.text.setTypeface(Typeface.MONOSPACE);
				//holder.text.setTextColor(R.color.readfeed);
				holder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.readrss, 0, 0, 0);
			}else if( holder.text.getTypeface().equals(Typeface.MONOSPACE)) {
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.customitemtext1);
				convertView.setTag(holder);
			}
			holder.text.setText(removeFeedReadFlag(itemText));
			convertView.setOnClickListener(new OnItemClickListener(position,convertView));
			convertView.setOnLongClickListener(new OnLongItemClickListener(position,convertView));
			return convertView;
		}
		
		private CharSequence removeFeedReadFlag(String itemText) {
			return itemText.substring(0, itemText.length()-1); 
		}
		
		private class OnLongItemClickListener implements OnLongClickListener {
			private int position;
			View convertView;

			OnLongItemClickListener(int position, View convertView) {
				this.position = position;
				this.convertView = convertView;
			}

			@Override
			public boolean onLongClick(View v) {
				String title = rssFeeds.get(position);
				Feed query = new Feed();
    			query.setTitle(extractTitle(title));
    			query.setFeedSource(rssAggregatorApplication.getFeedSourceName());
    			Feed feed = rssAggregatorApplication.findFeed(query);
    			String content = feed.getUrl();
    			
    			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, extractTitle(title) + " (shared via Rss Aggregator)");
                shareIntent.putExtra(Intent.EXTRA_TEXT, content);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent,"Share article"));
    			
				return false;
			}
		}

		private class OnItemClickListener implements OnClickListener{           
	        private int position;
	        View convertView ;
	        OnItemClickListener(int position, View convertView){
	                this.position = position;
	                this.convertView = convertView;
	        }
	        @Override
	        public void onClick(View listItemView) {
	                //Log.v("RSSAGGREGATOR", "onItemClick at position" + position);
	    			if (!rssAggregatorApplication.isOnline()) {Toast.makeText(getApplicationContext(),"No INTERNET connection detected",Toast.LENGTH_LONG).show();}
	    			
	    			ViewHolder holder = (ViewHolder) convertView.getTag();
	    			holder.text.setTypeface(Typeface.MONOSPACE);
	    			//holder.text.setTextColor(R.color.readfeed);
	    			holder.text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.readrss, 0, 0, 0);
	    			convertView.setTag(holder);
	    			
	    			String title = rssFeeds.get(position);
	    			Feed query = new Feed();
	    			query.setTitle(extractTitle(title));
	    			query.setFeedSource(rssAggregatorApplication.getFeedSourceName());
	    			Feed feed = rssAggregatorApplication.findFeed(query);
	    			feed.setFeedRead(true);
	    			rssAggregatorApplication.saveFeed(feed);
	    			rssFeeds.set(position, markFeedAsRead(title));

	    			//Log.i("RSSAGGREGATOR", "position " + position + " title " + title + " url " + feed.getUrl());
	    			rssAggregatorApplication.setFeedUrl(feed.getUrl());
	    			rssAggregatorApplication.setFeedDescription(feed.getDescription());
	    			rssAggregatorApplication.setFeedTitle(feed.getTitle());
	    			Intent intent = new Intent(getApplicationContext(),FeedDescriptionActivity.class);
	    			startActivity(intent);
	    			                
	        }
			private String markFeedAsRead(String title) {
				return title.substring(0, title.length()-2) + READ;
			}               
	    }

		
		private boolean isFeedRead(String itemText) {
			if(itemText.charAt(itemText.length()-1) == READ){
				return true;
			}
			return false;
		}

		class ViewHolder {
            TextView text;
        }

		
	}

	private String extractTitle(String str) {
		return str.substring(0, str.indexOf("\n"));
	}
	

	private List<String> getRssFeeds() {
		List<RssFeed> rssFeeds = rssAggregatorApplication.findAllFeedsWithFeedSource(rssAggregatorApplication.getFeedSourceName());
		List<String> feedTitles = new ArrayList<String>();
		if ( rssFeeds.size() > 0 ){
			for (Feed feed : rssFeeds.get(0).getFeeds()) {
				feedTitles.add(feed.getTitle() + "\n" + getFormattedTime(feed) 
						 + (feed.isFeedRead() == true ? 'Y' : 'N'));
			}	
		}
		
		return feedTitles;
	}

	private String getFormattedTime(Feed feed) {
		String strDate;
		if (feed.getDate() == null) {
			strDate = "";
		} else {
			//Log.i("RSSAGGREAGTOR","Published DateTime " + feed.getDate() + " long " +  feed.getDate().getTime());
			strDate = DateUtils.getRelativeTimeSpanString(feed.getDate().getTime()).toString();
		}

		return strDate;
	}


	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.homemenu, menu);
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}