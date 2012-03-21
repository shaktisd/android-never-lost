package com.application.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntryImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;



public class RssFeed {

	public SyndFeed getRssFeedForUrl(String url) {
		SyndFeed feed = null ;	
		try {
			URL feedUrl = new URL(url);
			SyndFeedInput input = new SyndFeedInput();
			feed = input.build(new XmlReader(feedUrl));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return feed;

	}
	
	public String[] getRssTitles(SyndFeed feed){
		String[] titles = new String[feed.getEntries().size()];
		int count = 0;
		for(Object object : feed.getEntries()){
			titles[count++] = ((SyndEntryImpl) object).getTitle();
		}
		return titles;
	}

}
