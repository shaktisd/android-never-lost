package com.phonecop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Added sample java docs
 * 
 * 
 */
public class ChangePasswordActivity extends Activity {
	Button submitPasswordButton ;
	TextView passwordText1;
	TextView passwordText2;
	TextView originalPasswordText1;
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.changepassword);
		 registerViewItems();
		 registerClickListenersToButtons();
	}

	private void registerClickListenersToButtons() {
		submitPasswordButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				StringBuffer message  = new StringBuffer();
				SharedPreferences mySharedPreferences = getSharedPreferences("PASSWORD_PREFS", Activity.MODE_PRIVATE);
				String password = mySharedPreferences.getString("PASSWORD", null);
				
				if ( passwordText1.getText().toString().equals("")  || passwordText2.getText().toString().equals("")){
					message.append("Either of the passwords can not be null / blank");
				}else if (!( passwordText1.getText().toString().equalsIgnoreCase(passwordText2.getText().toString()))){
					message.append("Passwords do not match");
				}
				if (password == null || (password != null && password.equals(originalPasswordText1.getText().toString()))){
					if( message.toString().length() == 0){
				   	 	SharedPreferences.Editor editor = mySharedPreferences.edit();
				   	 	editor.putString("PASSWORD", passwordText1.getText().toString());
				   	 	editor.commit();
						message.append("Password updated");
						Intent intent = new Intent(ChangePasswordActivity.this,MainMenuActivity.class);
						startActivity(intent);
					}	
				}else {
					message.append("Incorrect Original password");
				}
				
				
				Toast.makeText(getApplicationContext(),message.toString(),Toast.LENGTH_LONG).show();
			}
		});
		
	}

	private void registerViewItems() {
		submitPasswordButton = (Button) findViewById(R.id.submitpasswordbutton);
		passwordText1 = (TextView) findViewById(R.id.passwordText1);
		passwordText2 = (TextView) findViewById(R.id.passwordText2);
		originalPasswordText1 = (TextView) findViewById(R.id.originalPasswordText1);
	}

}
