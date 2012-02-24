package com.phonecop;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NeverLostActivity extends Activity {
    /** Called when the activity is first created. */
	TextView password ;
	TextView phoneNumber;
	Button registerSim;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        password = (TextView) findViewById(R.id.passwordText);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        registerSim = (Button) findViewById(R.id.registerSim);
        registerSim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ( isPasswordCorrect(password.getText())) {
					saveToSharedPref(getApplicationContext(),getSimSerialNumber(),phoneNumber.getText().toString());
					Toast.makeText(getApplicationContext(),"Saved SIM:" + getSimSerialNumber() + " Phone:" +  phoneNumber.getText().toString(),Toast.LENGTH_LONG).show();
					Intent intent = new Intent(NeverLostActivity.this,MainMenuActivity.class);
					startActivity(intent);
				}else {
					Toast.makeText(getApplicationContext(),"Incorrect Password",Toast.LENGTH_LONG).show();
				}
			}
			
		});
      
    }
    
    private String getSimSerialNumber(){
    	TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    	return tm.getSimSerialNumber();
    }
    
    private boolean isPasswordCorrect(CharSequence text) {
    	SharedPreferences mySharedPreferences = getSharedPreferences("PASSWORD_PREFS", Activity.MODE_PRIVATE);
   	 	String password = mySharedPreferences.getString("PASSWORD", null);
   	 	if(password == null){
   	 		// save as new password
   	 		SharedPreferences.Editor editor = mySharedPreferences.edit();
   	 		editor.putString("PASSWORD", text.toString());
   	 		editor.commit();
   	 		return true;
   	 	}else {
   	 		return password.equalsIgnoreCase(text.toString());
   	 	}
	}
    
    private void saveToSharedPref(Context context,String simSerialNumber,String phoneNumber){
    	SharedPreferences mySharedPreferences =  context.getSharedPreferences("PASSWORD_PREFS", Activity.MODE_PRIVATE);
    	SharedPreferences.Editor editor = mySharedPreferences.edit();
    	editor.putString("SIMSERIAL", simSerialNumber);
    	editor.putString("PHONENUMBER", phoneNumber);
    	editor.commit();

    }
}