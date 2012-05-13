package com.rssaggregator.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.MenuItem;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.rssaggregator.valueobjects.ApplicationConfiguration;
import com.rssaggregator.valueobjects.Category;
import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.FeedSource;
import com.rssaggregator.valueobjects.RssFeed;

public class RssAggregatorApplication extends Application {
	private EmbeddedObjectContainer db;
	private String feedUrl;
	private String feedDescription;
	private String feedSourceName;
	private ProgressDialog progressDialog;
	private boolean refreshMainDataSet;
	
	
	
	public void onCreate() {
		super.onCreate();
		setUpApplication();
	}

	private void setUpApplication() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().updateDepth(2);
		config.common().objectClass(Feed.class).objectField("feedSource").indexed(true);
		config.common().objectClass(FeedSource.class).objectField("feedSourceName").indexed(true);
		config.common().objectClass(Category.class).objectField("categoryName").indexed(true);
		config.common().objectClass(ApplicationConfiguration.class);
		
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
	
	public void storeFeeds(List<RssFeed> rssFeeds){
		for(RssFeed rssFeed : rssFeeds){
			for(Feed feed : rssFeed.getFeeds()){
				Feed queryFeed = new Feed();
				queryFeed.setTitle(feed.getTitle());
				queryFeed.setFeedSource(feed.getFeedSource());
				List<Feed> resultFeed = db.queryByExample(queryFeed);
				if ( resultFeed.size() == 0){
					Log.d("RSSAGGREGATOR","Storing feed " + feed.getFeedSource() + " " + feed.getTitle());
					db.store(feed);
				}
				db.commit();
			}
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
	
	public ApplicationConfiguration getApplicationConfiguration(){
		List<ApplicationConfiguration> appConfigList = db.queryByExample(ApplicationConfiguration.class);
		if(appConfigList != null && appConfigList.size() > 0 ){
			return appConfigList.get(0);
		}
		return new ApplicationConfiguration() ;
	}
	public void saveFeed(Feed feed){
		db.store(feed);
		db.commit();
	}
	
	public void save(Object object){
		db.store(object);
		db.commit();
	}
	
	public void delete(Object object){
		db.delete(object);
		db.commit();
	}
	
	public void deleteCategoryFeedSourceAndFeeds(Category category){
		
		for(FeedSource feedSource : category.getFeedSources()){
			Feed queryFeed = new Feed();
			queryFeed.setFeedSource(feedSource.getFeedSourceName());
			List<Feed> feedResult = db.queryByExample(queryFeed);
			Log.i("RSSAGGREGATOR", "Deleted Feeds " + feedResult.toString());
			for(Feed feed : feedResult){
				db.delete(feed);
				db.commit();
			}
			FeedSource queryFeedSource = new FeedSource();
			queryFeedSource.setFeedSourceName(feedSource.getFeedSourceName());
			Log.i("RSSAGGREGATOR", "Deleted FeedSource " + feedSource.getFeedSourceName());
			FeedSource deleteFeedSource = (FeedSource) db.queryByExample(queryFeedSource).get(0);
			db.delete(deleteFeedSource);
			db.commit();
		}
		Category queryCategory = new Category(category.getCategoryName());
		Log.i("RSSAGGREGATOR", "Deleted Category " + category.getCategoryName());
		Category deleteCategory = (Category) db.queryByExample(queryCategory).get(0);
		db.delete(deleteCategory);
		db.commit();
	}
	
	public void deleteCategoryFeedSourceAndFeeds(Category category,FeedSource feedSource) {
		
		Feed queryFeed = new Feed();
		queryFeed.setFeedSource(feedSource.getFeedSourceName());
		List<Feed> feedResult = db.queryByExample(queryFeed);
		Log.i("RSSAGGREGATOR", "Deleted Feeds " + feedResult.toString());
		for(Feed feed : feedResult){
			db.delete(feed);
			db.commit();
		}
		FeedSource queryFeedSource = new FeedSource();
		queryFeedSource.setFeedSourceName(feedSource.getFeedSourceName());
		queryFeedSource.setFeedSourceUrl(feedSource.getFeedSourceUrl());
		Log.i("RSSAGGREGATOR", "Deleted FeedSource " + feedSource.getFeedSourceName());
		FeedSource deleteFeedSource = (FeedSource) db.queryByExample(queryFeedSource).get(0);
		category.getFeedSources().remove(deleteFeedSource);
		db.store(category);
		db.commit();
		db.delete(deleteFeedSource);
		db.commit();
	}
	
	public List<Category> findAllCategory(){
		List<Category> categoryResult = db.queryByExample(Category.class);
		return categoryResult;
	}
	
	public Category findCategoryByName(String name){
		Category category = new Category(name);
		List<Category> categoryResult = db.queryByExample(category);
		if (categoryResult.size() > 0 ){
			return categoryResult.get(0);
		}else if (categoryResult.size() == 0)  {
			return null;	
		}else {
			return categoryResult.get(0);
		}
	}
	
	public List<RssFeed> findAllFeeds(){
		List<Feed> feedResult = db.queryByExample(Feed.class);
		Map<String,RssFeed> mapFeedSourceAndFeed = new HashMap<String,RssFeed>();
		for(Feed feed : feedResult){
			RssFeed rssFeed = mapFeedSourceAndFeed.get(feed.getFeedSource());
			if (rssFeed == null ){
				rssFeed = new RssFeed();
				rssFeed.setFeedSource(feed.getFeedSource());
				mapFeedSourceAndFeed.put(feed.getFeedSource(), rssFeed);
			}
			rssFeed.getFeeds().add(feed);
		}
		List<RssFeed> returnValue = new ArrayList<RssFeed>();
		for(RssFeed rssFeedElement : mapFeedSourceAndFeed.values()){
			Collections.sort(rssFeedElement.getFeeds(), new Comparator<Feed>() {
				public int compare(Feed o1, Feed o2) {
					return o2.getDate().compareTo(o1.getDate());
				}

			});
			returnValue.add(rssFeedElement);
		}
		
		return returnValue;
	}
	
	public List<RssFeed> findAllFeedsWithFeedSource(String feedSourceName){
		Feed queryFeed = new Feed();
		queryFeed.setFeedSource(feedSourceName);
		List<Feed> feedResult = db.queryByExample(queryFeed);
		Map<String,RssFeed> mapFeedSourceAndFeed = new HashMap<String,RssFeed>();
		for(Feed feed : feedResult){
			RssFeed rssFeed = mapFeedSourceAndFeed.get(feed.getFeedSource());
			if (rssFeed == null ){
				rssFeed = new RssFeed();
				rssFeed.setFeedSource(feed.getFeedSource());
				mapFeedSourceAndFeed.put(feed.getFeedSource(), rssFeed);
			}
			rssFeed.getFeeds().add(feed);
		}
		List<RssFeed> returnValue = new ArrayList<RssFeed>();
		for(RssFeed rssFeedElement : mapFeedSourceAndFeed.values()){
			Collections.sort(rssFeedElement.getFeeds(), new Comparator<Feed>() {
				public int compare(Feed o1, Feed o2) {
					return o2.getDate().compareTo(o1.getDate());
				}

			});
			returnValue.add(rssFeedElement);
		}
		
		return returnValue;
	
	}
	
	public FeedSource findFeedSourceByName(FeedSource feedSource){
		List<FeedSource> feedSourceList = db.queryByExample(feedSource);
		if(feedSourceList != null && feedSourceList.size() > 0 ){
			return feedSourceList.get(0);
		}
		return null;
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
	
	@SuppressWarnings("serial")
	public void deleteFeedsOlderThanDays(int numberOfDays){
		final Calendar cal = Calendar.getInstance();
		cal.add( Calendar.DATE, -numberOfDays );

		ObjectSet<Feed> result = db.query(new Predicate<Feed>() {
			@Override
			public boolean match(Feed feed) {
			return feed.getDate().before(cal.getTime()) ;
			}
			});
		
		for(Feed feed : result){
			//Log.i("RSSAGGREAGTOR","Deleting Feed " + feed + " publish date " + feed.getDate());
			db.delete(feed);
		}
		db.commit();
	}
	
	
	@SuppressWarnings("serial")
	public void deleteFeedsAlreadyRead(){

		ObjectSet<Feed> result = db.query(new Predicate<Feed>() {
			@Override
			public boolean match(Feed feed) {
			return feed.isFeedRead() ;
			}
			});
		
		for(Feed feed : result){
			Log.i("RSSAGGREAGTOR","Deleting Feed " + feed + " is read " + feed.isFeedRead());
			db.delete(feed);
		}
		db.commit();
	}
	
	public List<RssFeed> getRssFeeds(List<FeedSource> feedSources){
		List<RssFeed> rssFeeds = new ArrayList<RssFeed>();
		
		for(FeedSource feedSource : feedSources){
			RssFeed rssFeed = new RssFeed();
			rssFeed.setFeedSource(feedSource.getFeedSourceName());
			try {
				Log.i("RSSAGGREGATOR", " Adding feed from web for source " + rssFeed.getFeedSource());
				SyndFeed syndFeed = new SyndFeedInput().build(new XmlReader(new URL(feedSource.getFeedSourceUrl())));
				for(Object object : syndFeed.getEntries()){
					Feed feed = new Feed();
					SyndEntryImpl rssEntry = (SyndEntryImpl) object;
					feed.setFeedSource(feedSource.getFeedSourceName());
					feed.setTitle(rssEntry.getTitle().trim());
					feed.setUrl(rssEntry.getLink());
					if (rssEntry.getPublishedDate() == null){
						feed.setDate(new Date());
					}else {
						feed.setDate(((SyndEntryImpl) object).getPublishedDate()) ;
					}
					if(rssEntry.getDescription() != null){
						feed.setDescription(rssEntry.getDescription().getValue().trim());
					}else {
						feed.setDescription("No description, visit site for feed details");
					}
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
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
		return rssFeeds;
	}

	public String getFeedDescription() {
		return feedDescription;
	}

	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
	}

	public String getFeedSourceName() {
		return feedSourceName;
	}

	public void setFeedSourceName(String feedSourceName) {
		this.feedSourceName = feedSourceName;
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	public boolean isRefreshMainDataSet() {
		return refreshMainDataSet;
	}

	public void setRefreshMainDataSet(boolean refreshMainDataSet) {
		this.refreshMainDataSet = refreshMainDataSet;
	}
	
}
