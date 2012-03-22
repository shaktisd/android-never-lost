package com.rssaggregator.valueobjects;

import java.util.Date;

public class Feed {
	
	private String url ;
	private String title;
	private Date date;
	
	public Feed(){
	}
	
	public Feed(String url, String title, Date date) {
		super();
		this.url = url;
		this.title = title;
		this.date = date;
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
		return "Feed [url=" + url + ", title=" + title + ", date=" + date + "]";
	}
}
