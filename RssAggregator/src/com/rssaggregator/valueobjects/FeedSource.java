package com.rssaggregator.valueobjects;

public class FeedSource {
	String feedSourceName;
	String feedSourceUrl;
	
	public FeedSource(String feedSourceName, String feedSourceUrl) {
		super();
		this.feedSourceName = feedSourceName;
		this.feedSourceUrl = feedSourceUrl;
	}
	public String getFeedSourceName() {
		return feedSourceName;
	}
	public void setFeedSourceName(String feedSourceName) {
		this.feedSourceName = feedSourceName;
	}
	public String getFeedSourceUrl() {
		return feedSourceUrl;
	}
	public void setFeedSourceUrl(String feedSourceUrl) {
		this.feedSourceUrl = feedSourceUrl;
	}
	
}
