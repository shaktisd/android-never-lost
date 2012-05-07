package com.rssaggregator.valueobjects;

import java.util.Map;

public class OpmlFeed {
	
	private String opmlCategoryName;
	private Map<String,String> opmlFeedSourceUrl ;
	public String getOpmlCategoryName() {
		return opmlCategoryName;
	}
	public void setOpmlCategoryName(String opmlCategoryName) {
		this.opmlCategoryName = opmlCategoryName;
	}
	public Map<String, String> getOpmlFeedSourceUrl() {
		return opmlFeedSourceUrl;
	}
	public void setOpmlFeedSourceUrl(Map<String, String> opmlFeedSourceUrl) {
		this.opmlFeedSourceUrl = opmlFeedSourceUrl;
	}
	
	

}
