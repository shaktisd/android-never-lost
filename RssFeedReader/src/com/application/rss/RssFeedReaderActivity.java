package com.application.rss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;

import com.application.utils.RssFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;

public class RssFeedReaderActivity extends ExpandableListActivity {

	private static final String NAME = "NAME";

	private ExpandableListAdapter mAdapter;
	/** Called when the activity is first created. */
	RssFeed rssFeed = null;
	Map<String,String> rssSource = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (rssFeed == null) {
			rssFeed = new RssFeed();
		}
		
		if ( rssSource == null){
			rssSource = new HashMap<String,String>();
			rssSource.put("NDTV", "http://feeds.feedburner.com/NdtvNews-TopStories");
			rssSource.put("HT","http://feeds.hindustantimes.com/HT-HomePage-TopStories");
			
		}
		
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		Set<String> source = rssSource.keySet();
		for(String sourceKey : source ){
			String url = rssSource.get(sourceKey);
			SyndFeed syndFeed = rssFeed.getRssFeedForUrl(url);
			String[] feedTitles = rssFeed.getRssTitles(syndFeed);
			
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			curGroupMap.put(NAME, sourceKey);

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for (int j = 0; j < feedTitles.length; j++) {
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				curChildMap.put(NAME, feedTitles[j]);
			}
			childData.add(children);
			
		}
		// Set up our adapter
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
	
}