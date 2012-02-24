package com.phonecop.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedDataAccess {
	Context context;
	SharedPreferences mySharedPreferences;
	public SharedDataAccess(Context context){
		this.context = context;
		this.mySharedPreferences = context.getSharedPreferences("PASSWORD_PREFS", Activity.MODE_PRIVATE);
	}
	
	public String getAttributeValue(String attribute) {
		return mySharedPreferences.getString(attribute, null);
	}
	
	public void setAttributeValue(String attribute,String value) {
		
		SharedPreferences.Editor editor = mySharedPreferences.edit();
   	 	editor.putString(attribute, value);
   	 	editor.commit();
	}
}
