package com.rssaggregator.valueobjects;

import java.util.List;

public class Category {
	
	private String categoryName;
	private List<FeedSource> feedSources;
	
	public Category(String categoryName, List<FeedSource> feedSources) {
		super();
		this.categoryName = categoryName;
		this.feedSources = feedSources;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public List<FeedSource> getFeedSources() {
		return feedSources;
	}
	public void setFeedSources(List<FeedSource> feedSources) {
		this.feedSources = feedSources;
	}
	
	
	
	
	

}
