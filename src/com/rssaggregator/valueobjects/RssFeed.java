package com.rssaggregator.valueobjects;

import java.util.ArrayList;
import java.util.List;

public class RssFeed {
	
	private String feedSource;
	private List<Feed> feeds = new ArrayList<Feed>();
	public String getFeedSource() {
		return feedSource;
	}
	public void setFeedSource(String feedSource) {
		this.feedSource = feedSource;
	}
	public List<Feed> getFeeds() {
		return feeds;
	}
	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}
	
	@Override
	public String toString() {
		return "RssFeed [feedSource=" + feedSource + " feeds=" + feeds + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((feedSource == null) ? 0 : feedSource.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RssFeed other = (RssFeed) obj;
		if (feedSource == null) {
			if (other.feedSource != null)
				return false;
		} else if (!feedSource.equals(other.feedSource))
			return false;
		return true;
	}
	
	
}
