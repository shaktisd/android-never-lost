package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rssaggregator.valueobjects.Category;
import com.rssaggregator.valueobjects.FeedSource;

public class FeedSourceActivity extends Activity {
	Spinner spinner ;
	TextView feedSourceName;
	TextView feedSourceURL;
	Button feedSourceSave;
	Button feedSourceDelete;
	ListView feedSourceListView;
	ArrayAdapter<String> adapter;
	Category currentCategory ;
	List<String> values ;
	ArrayAdapter<String> dataAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedsource);
		registerViewItems();
		registerClickListenersToButtons();
		
	    List<String> list = new ArrayList<String>();
	    List<Category> allCategory = getRssAggregatorApplication().findAllCategory();
	    for(Category category : allCategory){
	    	list.add(category.getCategoryName());
	    }
		dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
	    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(dataAdapter);
	    
	    values = getAllFeedSourceByCategoryName(String.valueOf(spinner.getSelectedItem()));
	    adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.customitemtext1, values);
		// Assign adapter to ListView
	    feedSourceListView.setAdapter(adapter);
	}
	
	private List<String> getAllFeedSourceByCategoryName(String categoryName) {
		this.currentCategory = getRssAggregatorApplication().findCategoryByName(categoryName);
		List<String> values = new ArrayList<String>();
		for(FeedSource feedSource : currentCategory.getFeedSources()){
			values.add(feedSource.getFeedSourceName());
		}
		return values;
	}

	private void registerClickListenersToButtons() {
		feedSourceSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = "";
				String selectedCategory = String.valueOf(spinner.getSelectedItem());
				String name = feedSourceName.getText().toString();
				String url = feedSourceURL.getText().toString();
				message = validate(name,url);
				FeedSource feedSource = new FeedSource(name,url);
				if (message.length() == 0){
					Category storedCategory = getRssAggregatorApplication().findCategoryByName(selectedCategory);
					if ( storedCategory.getFeedSources() == null ){
						storedCategory.setFeedSources(new ArrayList<FeedSource>());
					}
					
					FeedSource storedFeedSource = getRssAggregatorApplication().findFeedSourceByName(feedSource);
					
					if(storedFeedSource == null){
						storedCategory.getFeedSources().add(feedSource);
						getRssAggregatorApplication().save(storedCategory);
						values.add(name);
						adapter.notifyDataSetChanged();	
						feedSourceName.setText("");
						feedSourceURL.setText("");
						message = "Saved ";
						
					}else {
						message = "FeedSource already exists ";
					}	
				}
				
				
				Toast.makeText(getApplicationContext(), message + feedSource.getFeedSourceName() , Toast.LENGTH_SHORT).show();
				
			}

			private String validate(String name, String url) {
				StringBuffer message = new StringBuffer();
				if(name == null){
					message.append(" Provide name");
				}
				
				if(name != null && name.trim().length() == 0){
					message.append(" Provide name");
				}
				
				if(url == null){
					message.append(" Provide url");
				}
				
				if(url != null && url.trim().length() == 0){
					message.append(" Provide url");
				}
				
				return message.toString();
				
			}
		});
		
		feedSourceDelete.setOnClickListener(new View.OnClickListener(){
			private RssAggregatorApplication rssAggregatorApplication;

			@Override
			public void onClick(View v) {
				FeedSource queryFeedSource = new FeedSource(feedSourceName.getText().toString(),feedSourceURL.getText().toString());
				rssAggregatorApplication = getRssAggregatorApplication();
				FeedSource storedFeedSource = rssAggregatorApplication.findFeedSourceByName(queryFeedSource);
				StringBuffer message = new StringBuffer();
				if (storedFeedSource == null){
					message.append("FeedSource : " + queryFeedSource.getFeedSourceName() + " doen't exists");
				}else {
					Category categoryByName = rssAggregatorApplication.findCategoryByName(String.valueOf(spinner.getSelectedItem()));
					rssAggregatorApplication.deleteCategoryFeedSourceAndFeeds(categoryByName,storedFeedSource);
					values.remove(storedFeedSource.getFeedSourceName());
					adapter.notifyDataSetChanged();
					message.append("Deleted FeedSource : " + storedFeedSource.getFeedSourceName());
					feedSourceName.setText("");
					feedSourceURL.setText("");
				}
				Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
			
			}
			
		});
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	values.clear();
		    	values.addAll( getAllFeedSourceByCategoryName(String.valueOf(spinner.getSelectedItem()))) ;
		    	adapter.notifyDataSetChanged();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		feedSourceListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				String selectedText = (((TextView)view).getText()).toString();
				for(FeedSource feedSource : currentCategory.getFeedSources()){
					if(feedSource.getFeedSourceName().equalsIgnoreCase(selectedText)){
						feedSourceName.setText(feedSource.getFeedSourceName());
						feedSourceURL.setText(feedSource.getFeedSourceUrl());
					}
				}
		
			}
		});

		
	}

	private void registerViewItems() {
		spinner = (Spinner) findViewById(R.id.categoryspinner);		
		feedSourceName = (TextView) findViewById(R.id.feedsourcenametext);
		feedSourceURL = (TextView) findViewById(R.id.feedsourceurltext);
		feedSourceSave = (Button) findViewById(R.id.saveFeedSource);
		feedSourceDelete = (Button) findViewById(R.id.deleteFeedSource);
		feedSourceListView =  (ListView) findViewById(R.id.feedsourcelist);
	}

	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}
}
