package com.rssaggregator.utils;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.rssaggregator.valueobjects.Feed;
import com.rssaggregator.valueobjects.FeedSource;
import com.rssaggregator.valueobjects.RssFeed;



public class RssFeedUtil {

	public List<RssFeed> getRssFeeds(List<FeedSource> feedSources){
		SyndFeed syndFeed = null ;
		List<RssFeed> rssFeeds = new ArrayList<RssFeed>();
		
		for(FeedSource feedSource : feedSources){
			RssFeed rssFeed = new RssFeed();
			rssFeed.setFeedSource(feedSource.getFeedSourceName());
			try {
				syndFeed = new SyndFeedInput().build(new XmlReader(new URL(feedSource.getFeedSourceUrl())));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (FeedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for(Object object : syndFeed.getEntries()){
				Feed feed = new Feed();
				feed.setTitle(((SyndEntryImpl) object).getTitle());
				feed.setUrl(((SyndEntryImpl) object).getLink());
				feed.setDate(((SyndEntryImpl) object).getPublishedDate());
				rssFeed.getFeeds().add(feed);
			}	
			rssFeeds.add(rssFeed);
		}
		
		return rssFeeds;

	
	}
	

}
