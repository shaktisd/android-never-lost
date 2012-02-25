package com.phonecop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	Button changePasswordButton;
	Button updateSimButton;
	Button settingsButton;
	Button helpButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		registerButtons();
		registerClickListenersToButtons();
	}
	private void registerClickListenersToButtons() {
		changePasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainMenuActivity.this,ChangePasswordActivity.class);
				startActivity(intent);
			}
		});
		
		updateSimButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainMenuActivity.this,NeverLostActivity.class);
				startActivity(intent);
			}
		});
		
		helpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainMenuActivity.this,HelpActivity.class);
				startActivity(intent);
			}
		});
		
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainMenuActivity.this,SettingsActivity.class);
				startActivity(intent);
			}
		});
		
		
	}
	private void registerButtons() {
		changePasswordButton = (Button) findViewById(R.id.changepasswordbutton);
		updateSimButton = (Button) findViewById(R.id.updatesimbutton);
		helpButton = (Button) findViewById(R.id.helpbutton);
		settingsButton = (Button) findViewById(R.id.settingsButton);
	}
	
	

	
}
