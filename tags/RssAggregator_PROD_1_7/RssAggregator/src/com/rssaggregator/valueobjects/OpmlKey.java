package com.rssaggregator.valueobjects;

public class OpmlKey {
	
	
	public OpmlKey(String opmlCategoryName, String opmlUrl, String opmlFeedSourceName) {
		super();
		this.opmlCategoryName = opmlCategoryName;
		this.opmlUrl = opmlUrl;
		this.opmlFeedSourceName = opmlFeedSourceName;
	}
	String opmlCategoryName ;
	String opmlUrl;
	String opmlFeedSourceName;
	public String getOpmlCategoryName() {
		return opmlCategoryName;
	}
	public void setOpmlCategoryName(String opmlCategoryName) {
		this.opmlCategoryName = opmlCategoryName;
	}
	public String getOpmlUrl() {
		return opmlUrl;
	}
	public void setOpmlUrl(String opmlUrl) {
		this.opmlUrl = opmlUrl;
	}
	public String getOpmlFeedSourceName() {
		return opmlFeedSourceName;
	}
	public void setOpmlFeedSourceName(String opmlFeedSourceName) {
		this.opmlFeedSourceName = opmlFeedSourceName;
	}
	
	
}
