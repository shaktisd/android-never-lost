package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.rssaggregator.valueobjects.Category;
import com.rssaggregator.valueobjects.FeedSource;

public class FeedSourceActivity extends Activity {
	Spinner spinner ;
	TextView feedSourceName;
	TextView feedSourceURL;
	Button feedSourceSave;
	Button feedSourceDelete;
	
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
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
	    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(dataAdapter);
	}
	
	private void registerClickListenersToButtons() {
		feedSourceSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String selectedCategory = String.valueOf(spinner.getSelectedItem());
				String name = feedSourceName.getText().toString();
				String url = feedSourceURL.getText().toString();
				
				Category categoryByName = getRssAggregatorApplication().findCategoryByName(selectedCategory);
				categoryByName.getFeedSources().add(new FeedSource(name,url));
				getRssAggregatorApplication().save(categoryByName);
			}
		});
	}

	private void registerViewItems() {
		spinner = (Spinner) findViewById(R.id.categoryspinner);		
		feedSourceName = (TextView) findViewById(R.id.feedsourcenametext);
		feedSourceURL = (TextView) findViewById(R.id.feedsourceurltext);
		feedSourceSave = (Button) findViewById(R.id.saveFeedSource);
		feedSourceDelete = (Button) findViewById(R.id.deleteFeedSource);
	}

	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}
}
