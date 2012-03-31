package com.rssaggregator.valueobjects;

import java.util.Date;

public class Feed {
	
	private String url ;
	private String title;
	private Date date;
	private String description;
	private String feedSource;
	private boolean isFeedRead;
	
	public Feed(){
	}
	
	public Feed(String url, String title, Date date, String description,
			String feedSource, boolean isFeedRead) {
		super();
		this.url = url;
		this.title = title;
		this.date = date;
		this.description = description;
		this.feedSource = feedSource;
		this.isFeedRead = isFeedRead;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFeedSource() {
		return feedSource;
	}

	public void setFeedSource(String feedSource) {
		this.feedSource = feedSource;
	}

	public boolean isFeedRead() {
		return isFeedRead;
	}

	public void setFeedRead(boolean isFeedRead) {
		this.isFeedRead = isFeedRead;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Feed [url=" + url + ", title=" + title + ", date=" + date
				+ ", description=" + description + ", feedSource=" + feedSource
				+ ", isFeedRead=" + isFeedRead + "]";
	}

	
}
