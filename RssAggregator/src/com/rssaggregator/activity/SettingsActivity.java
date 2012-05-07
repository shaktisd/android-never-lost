package com.rssaggregator.activity;

import java.util.ArrayList;
import java.util.List;

import com.rssaggregator.valueobjects.ApplicationConfiguration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {
	private Button bundledFeedsButton;
	private Spinner deleteFeedsAfterDaysSpinner;
	private ArrayAdapter<String> dataAdapter;
	private ToggleButton deleteReadFeedsToggleButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		registerViewItems();
		registerClickListenersToButtons();
		setupSpinner();
	}

	private void setupSpinner() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list.add(i + "");
		}
		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		deleteFeedsAfterDaysSpinner.setAdapter(dataAdapter);
		
		ApplicationConfiguration configuration = getRssAggregatorApplication().getApplicationConfiguration();
		int deleteFeedAfterNumberOfDays = configuration.getDeleteFeedAfterNumberOfDays();
		deleteFeedsAfterDaysSpinner.setSelection(deleteFeedAfterNumberOfDays);
	}

	private void registerClickListenersToButtons() {
		bundledFeedsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), ImportOpmlFeedsActivity.class);
				startActivity(intent);
			}
		});
		
		deleteFeedsAfterDaysSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	ApplicationConfiguration applicationConfiguration = getRssAggregatorApplication().getApplicationConfiguration();
		    	applicationConfiguration.setDeleteFeedAfterNumberOfDays(Integer.parseInt(String.valueOf(deleteFeedsAfterDaysSpinner.getSelectedItem())));
		    	getRssAggregatorApplication().save(applicationConfiguration);
		    	Toast.makeText(getApplicationContext(), "Feeds will be deleted after " + deleteFeedsAfterDaysSpinner.getSelectedItem() + " days " , Toast.LENGTH_SHORT).show();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		deleteReadFeedsToggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ApplicationConfiguration applicationConfiguration = getRssAggregatorApplication().getApplicationConfiguration();
				String status = deleteReadFeedsToggleButton.getText().toString();
				if ( status.equalsIgnoreCase("ON")){
					applicationConfiguration.setDeleteReadFeeds(true);	
				}else {
					applicationConfiguration.setDeleteReadFeeds(false);
				}
				getRssAggregatorApplication().save(applicationConfiguration);
				
				Toast.makeText(getApplicationContext(), "Delete read feeds " + status , Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	
	
	
	private void registerViewItems() {
		bundledFeedsButton = (Button) findViewById(R.id.bundledfeedsbutton);
		deleteFeedsAfterDaysSpinner = (Spinner) findViewById(R.id.deletefeedafterdaysspinner);
		deleteReadFeedsToggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
	}
	
	private RssAggregatorApplication getRssAggregatorApplication() {
		return (RssAggregatorApplication) getApplication();

	}
}
