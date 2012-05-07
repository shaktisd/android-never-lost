package com.rssaggregator.valueobjects;

public class ApplicationConfiguration {
	
	private int deleteFeedAfterNumberOfDays = 1;
	private boolean deleteReadFeeds;
	public int getDeleteFeedAfterNumberOfDays() {
		return deleteFeedAfterNumberOfDays;
	}
	public void setDeleteFeedAfterNumberOfDays(int deleteFeedAfterNumberOfDays) {
		this.deleteFeedAfterNumberOfDays = deleteFeedAfterNumberOfDays;
	}
	public boolean isDeleteReadFeeds() {
		return deleteReadFeeds;
	}
	public void setDeleteReadFeeds(boolean deleteReadFeeds) {
		this.deleteReadFeeds = deleteReadFeeds;
	}
	

}
