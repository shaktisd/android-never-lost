package com.rssaggregator.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.FeedSource;
import com.rssaggregator.valueobjects.RssFeed;

public class RssAggregatorApplication extends Application {
	private EmbeddedObjectContainer db;
	private String feedUrl;
	
	
	public void onCreate() {
		super.onCreate();
		setUpApplication();
	}

	private void setUpApplication() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(RssFeed.class).objectField("feedSource").indexed(true);
		config.common().objectClass(Feed.class).objectField("url").indexed(true);
		config.common().objectClass(FeedSource.class).objectField("feedSourceName").indexed(true);
		if(db == null || db.close()){
			String path = db4oDBFullPath(this);
			Log.i("RSSAGGREGATOR", "Opening DB : " + path);
			db = Db4oEmbedded.openFile(config, path);
			Log.d("RSSAGGREGATOR", "Finished Opening DB : " + path );
		}
		//init rssFeedUtil
	}
	
	private String db4oDBFullPath(Context ctx) {
		return ctx.getDir("data", 0) + "/" + "rss.db4o";
	}
	
	public void updateRssFeeds(List<RssFeed> rssFeeds){
		
		List<RssFeed> oldRssFeed = db.queryByExample(RssFeed.class);
		for(RssFeed deleteRssFeed : oldRssFeed){
			db.delete(deleteRssFeed);
		}
		
		List<Feed> oldFeed  = db.queryByExample(Feed.class);
		for(Feed deleteFeed : oldFeed){
			db.delete(deleteFeed);
		}
		db.commit();
		
		for(RssFeed rssFeed : rssFeeds){
			db.store(rssFeed);
			db.commit();
		}
	}
	
	public void updateFeedSource(FeedSource feedSource){
		FeedSource dummyFeedSource = new FeedSource();
		dummyFeedSource.setFeedSourceName(feedSource.getFeedSourceName());
		ObjectSet<Object> result = db.queryByExample(dummyFeedSource);
		if(result.size() > 0){
			FeedSource updatedFeedSource = (FeedSource)result.next();
			updatedFeedSource.setFeedSourceUrl(feedSource.getFeedSourceUrl());
			db.store(updatedFeedSource);
		}else {
			db.store(feedSource);
		}
		db.commit();
	}
	
	public Feed findFeed(Feed feed){
		return (Feed)db.queryByExample(feed).get(0);
	}

	public List<RssFeed> findAllRssFeeds() {
		List<RssFeed> resultSet = db.queryByExample(RssFeed.class);
		for (RssFeed obj : resultSet) {
			//Log.i("RSSAGGREGATOR", "Before sorting" + obj.getFeeds());
			RssFeed rssFeed = (RssFeed) obj;
			Collections.sort(rssFeed.getFeeds(), new Comparator<Feed>() {
				public int compare(Feed o1, Feed o2) {
					return o2.getDate().compareTo(o1.getDate());
				}

			});
			//Log.i("RSSAGGREGATOR", "After sorting" + obj.getFeeds());
		}
		return resultSet;
	}
	
	public List<FeedSource> findAllFeedSource(){
		return db.queryByExample(FeedSource.class);
	}
	
	@Override
	public void onTerminate() {
		Log.i("RSSAGGREGATOR","Closing db connection");
		super.onTerminate();
		if(db != null ){
			db.close();	
		}
		Log.i("RSSAGGREGATOR","Closed db connection");
	}
	
	public List<RssFeed> getAllRssFeedsFromSource(){
		return getRssFeeds(findAllFeedSource());
	}

	public String getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	public List<RssFeed> getRssFeeds(List<FeedSource> feedSources){
		List<RssFeed> rssFeeds = new ArrayList<RssFeed>();
		
		for(FeedSource feedSource : feedSources){
			RssFeed rssFeed = new RssFeed();
			rssFeed.setFeedSource(feedSource.getFeedSourceName());
			try {
				Log.i("RSSAGGREGATOR","Input url " + feedSource.getFeedSourceUrl());
				SyndFeed syndFeed = new SyndFeedInput().build(new XmlReader(new URL(feedSource.getFeedSourceUrl())));
				for(Object object : syndFeed.getEntries()){
					Feed feed = new Feed();
					feed.setTitle(((SyndEntryImpl) object).getTitle());
					feed.setUrl(((SyndEntryImpl) object).getLink());
					feed.setDate(((SyndEntryImpl) object).getPublishedDate() == null ? new Date() : ((SyndEntryImpl) object).getPublishedDate() );
					rssFeed.getFeeds().add(feed);
				}	
				rssFeeds.add(rssFeed);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (FeedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return rssFeeds;
	}

}
