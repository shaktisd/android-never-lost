package com.rssaggregator.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rssaggregator.valueobjects.Category;
import com.rssaggregator.valueobjects.FeedSource;
import com.rssaggregator.valueobjects.OpmlFeed;
import com.rssaggregator.valueobjects.OpmlKey;

public class ImportOpmlFeedsActivity extends ExpandableListActivity {
	
	private static final String NAME = "NAME";
	private SimpleExpandableListAdapter expListAdapter;
	List<OpmlFeed> opmlFeeds ;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opml_main);
		opmlFeeds = parseOpmlXml();
		expListAdapter =
			new SimpleExpandableListAdapter(
				this,
				createGroupList(opmlFeeds),	// groupData describes the first-level entries
				R.layout.custom_expandable_list,	// Layout for the first-level entries
				new String[] { NAME },	// Key in the groupData maps to display
				new int[] { R.id.customtext1 },		// Data under "colorName" key goes into this TextView
				createChildList(opmlFeeds),	// childData describes second-level entries
				R.layout.custom_row,	// Layout for second-level entries
				new String[] { NAME },	// Keys in childData maps to display
				new int[] { R.id.customrowtext1}	// Data under the keys above go into these TextViews
			);
		
		setListAdapter( expListAdapter );
		getExpandableListView().setOnChildClickListener(this);
	}
	
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,int childPosition, long id) {
		String opmlFeedSourceName = ((TextView)(v.findViewById(R.id.customrowtext1))).getText().toString();
		
		OpmlKey opmlKey = getOpmlKey(opmlFeedSourceName);
		StringBuffer message = new StringBuffer();
		Category storedCategory = getRssAggregatorApplication().findCategoryByName(opmlKey.getOpmlCategoryName());
		if(storedCategory == null){
			storedCategory = new Category(opmlKey.getOpmlCategoryName());
			storedCategory.setFeedSources(new ArrayList<FeedSource>());
			getRssAggregatorApplication().save(storedCategory);
			message.append("Saved Category " + opmlKey.getOpmlCategoryName());
		}
		FeedSource feedSource = new FeedSource(opmlFeedSourceName,opmlKey.getOpmlUrl());
		FeedSource storedFeedSource = getRssAggregatorApplication().findFeedSourceByName(feedSource);
		
		if(storedFeedSource == null){
			storedCategory = getRssAggregatorApplication().findCategoryByName(opmlKey.getOpmlCategoryName());
			storedCategory.getFeedSources().add(feedSource);
			getRssAggregatorApplication().save(storedCategory);
			getRssAggregatorApplication().setRefreshMainDataSet(true);
			message.append("Saved FeedSource " + opmlFeedSourceName);
		}else {
			message.append("FeedSource already exists " + opmlFeedSourceName);
		}
		Toast.makeText(getApplicationContext(), message  , Toast.LENGTH_SHORT).show();
		return true;
	}
	
	private OpmlKey getOpmlKey(String opmlFeedSourceName){
		for(OpmlFeed opmlFeed : opmlFeeds){
			if(opmlFeed.getOpmlFeedSourceUrl().containsKey(opmlFeedSourceName)){
				return new OpmlKey(opmlFeed.getOpmlCategoryName(), opmlFeed.getOpmlFeedSourceUrl().get(opmlFeedSourceName),opmlFeedSourceName );
			}
		}
		return null;
	}
	
	
	
	
	private List<List<Map<String, String>>> createChildList(List<OpmlFeed> opmlFeeds) {
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		for(OpmlFeed opmlFeed : opmlFeeds){
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for(String opmlFeedSourceName : opmlFeed.getOpmlFeedSourceUrl().keySet()){
				Map<String, String> curChildMap = new HashMap<String, String>();
				curChildMap.put(NAME, opmlFeedSourceName);
				children.add(curChildMap);
			}
			childData.add(children);
		}
		return childData;
	}

	private List<Map<String, String>> createGroupList(List<OpmlFeed> opmlFeeds) {
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		for(OpmlFeed opmlFeed : opmlFeeds){
			Map<String, String> groupMap = new HashMap<String, String>();
			groupMap.put(NAME, opmlFeed.getOpmlCategoryName());
			groupData.add(groupMap);
			
		}
		return groupData;
	}

	private List<OpmlFeed> parseOpmlXml(){
		final List<OpmlFeed> opmlFeeds = new ArrayList<OpmlFeed>();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			DefaultHandler handler = new DefaultHandler() {
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					if (qName.equalsIgnoreCase("outline") && attributes.getValue("xmlUrl") == null) {
						OpmlFeed opmlFeed = new OpmlFeed();
						opmlFeed.setOpmlCategoryName(attributes.getValue("title"));
						opmlFeed.setOpmlFeedSourceUrl(new HashMap<String,String>());
						opmlFeeds.add(opmlFeed);
					}else if (qName.equalsIgnoreCase("outline") && attributes.getValue("xmlUrl") != null ){
						OpmlFeed opmlFeed = opmlFeeds.get(opmlFeeds.size()-1);
						Map<String, String> opmlFeedSourceUrl = opmlFeed.getOpmlFeedSourceUrl();
						opmlFeedSourceUrl.put(attributes.getValue("title"), attributes.getValue("xmlUrl"));
						//Log.i("RSSAGGREGATOR",opmlFeedSourceUrl + "");
					}
				}
			};
			InputStream is = getResources().openRawResource(R.raw.rss_subscriptions);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(is));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.i("RSSAGGREGATOR",opmlFeeds + "");
		Collections.sort(opmlFeeds, new Comparator<OpmlFeed>() {
			@Override
			public int compare(OpmlFeed lhs, OpmlFeed rhs) {
				return lhs.getOpmlCategoryName().compareTo(rhs.getOpmlCategoryName());
			}
		});
		
		return opmlFeeds;
	}
	
	
	
	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}
	
	

}
