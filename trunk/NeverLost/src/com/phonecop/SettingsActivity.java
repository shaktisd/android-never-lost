package com.phonecop;

import com.phonecop.constants.Constants;
import com.phonecop.tools.SharedDataAccess;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {
	TextView alarmText ;
	ToggleButton alarmOnSmsFromAnyNumberToggleButton;
	Button saveSettingsButton;
	SharedDataAccess sharedData;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		sharedData = new SharedDataAccess(this);
		registerButtons();
		registerClickListenersToButtons();
	}

	private void registerClickListenersToButtons() {
		saveSettingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (alarmText.getText().toString() != null && alarmText.getText().toString().trim() != "" ){
					sharedData.setAttributeValue(Constants.ALARMTEXT, alarmText.getText().toString());	
					sharedData.setAttributeValue(Constants.ALARMONSMSFROMANYNUMBER,new Boolean(alarmOnSmsFromAnyNumberToggleButton.isChecked()).toString());	
				}
			}
		});
	}

	private void registerButtons() {
		alarmText = (TextView) findViewById(R.id.alarmText);
		alarmOnSmsFromAnyNumberToggleButton = (ToggleButton) findViewById(R.id.alarmOnSmsFromAnyNumberToggleButton);
		saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
	}

}
