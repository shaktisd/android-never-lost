package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.rssaggregator.utils.RssFeedUtil;
import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.FeedSource;
import com.rssaggregator.valueobjects.RssFeed;

public class RssAggregatorApplication extends Application {
	private EmbeddedObjectContainer db;
	private RssFeedUtil rssFeedUtil;
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
			Log.i("DBLOAD", "Opening DB : " + path);
			db = Db4oEmbedded.openFile(config, path);
			Log.d("DBLOAD", "Finished Opening DB : " + path );
		}
		//init rssFeedUtil
		rssFeedUtil = new RssFeedUtil();
	}
	
	private String db4oDBFullPath(Context ctx) {
		return ctx.getDir("data", 0) + "/" + "rss.db4o";
	}
	
	public void updateRssFeeds(List<RssFeed> rssFeeds){
		for(RssFeed rssFeed : rssFeeds){
			RssFeed dummy = new RssFeed();
			dummy.setFeedSource(rssFeed.getFeedSource());
			ObjectSet<Object> result = db.queryByExample(dummy);
			Log.d("RESULT","size " + result.size());
			if (result.size() > 0){
				RssFeed updateRssFeed = (RssFeed)result.next();	
				db.delete(updateRssFeed);
			}
			db.store(rssFeed);
		}
		db.commit();
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
		return db.queryByExample(RssFeed.class);
	}
	
	public List<FeedSource> findAllFeedSource(){
		return db.queryByExample(FeedSource.class);
	}
	
	@Override
	public void onTerminate() {
		Log.i("DBLOAD","Closing db connection");
		super.onTerminate();
		if(db != null ){
			db.close();	
		}
		Log.i("DBLOAD","Closed db connection");
	}
	
	public List<RssFeed> getAllRssFeedsFromSource(){
		return rssFeedUtil.getRssFeeds(findAllFeedSource());
		/*List<FeedSource> feedSources = new ArrayList<FeedSource>();
		FeedSource feedSource1 = new FeedSource("NDTV","http://feeds.feedburner.com/NdtvNews-TopStories");
		FeedSource feedSource2 = new FeedSource("TOI","http://timesofindia.indiatimes.com/rssfeedstopstories.cms");
		FeedSource feedSource3 = new FeedSource("BLOG","http://feeds.feedburner.com/TheCodeOfANinja");
		feedSources.add(feedSource1);
		feedSources.add(feedSource2);
		feedSources.add(feedSource3);
		return rssFeedUtil.getRssFeeds(feedSources);*/
	}

	public String getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}
}
