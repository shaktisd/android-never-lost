package com.phonecop;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

public class SimCheckerServiceManager extends BroadcastReceiver {
	public static final String TAG = "SimCheckerServiceManager";
	@Override
	public void onReceive(Context context, Intent intent) {
		if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        GsmCellLocation gsmCellLocation = (GsmCellLocation)tm.getCellLocation();
	        StringBuffer sb = new StringBuffer();
	        sb.append("\nDeviceId " + tm.getDeviceId());
	        sb.append("\nNetworkOperatorName " + tm.getNetworkOperatorName());
	        sb.append("\nSimSerialNumber " + tm.getSimSerialNumber());
	        sb.append("\nSubscriberId " + tm.getSubscriberId());
	        sb.append("\nCell Id " + gsmCellLocation.getCid() + " Cell Lac " + gsmCellLocation.getLac());
	        Log.e(TAG, sb.toString());
	        if( !isSimSerialNumberCorrect(context,tm.getSimSerialNumber())){
	        	String phoneNumber = getPhoneNumber(context);
	        	sendSMS(phoneNumber,sb.toString(),context);
	        	Log.e(TAG, "Message sent");
	        }else {
	        	Log.e(TAG, "No Message sent");
	        }
	        
	        
	        Toast.makeText(context,sb.toString(),Toast.LENGTH_LONG).show();
		}
	}
	
	// ---sends an SMS message to another device---
	private void sendSMS(String phoneNumber, String message,Context context) {
		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, pi, null);
	}
	
	private String getPhoneNumber(Context context){
		SharedPreferences mySharedPreferences = context.getSharedPreferences("PASSWORD_PREFS", Activity.MODE_PRIVATE);
   	 	String phoneNumber = mySharedPreferences.getString("PHONENUMBER", null);
   	 	return phoneNumber;
   	 	
	}
	
	 private boolean isSimSerialNumberCorrect(Context context,String newSimSerialNumber){
    	 SharedPreferences mySharedPreferences = context.getSharedPreferences("PASSWORD_PREFS", Activity.MODE_PRIVATE);
    	 String oldSimSerialNumber = mySharedPreferences.getString("SIMSERIAL", null);
    	 Log.e(TAG,"newSimSerial: " + newSimSerialNumber + " oldSimSerialNumber: " + oldSimSerialNumber);
    	 return newSimSerialNumber.equalsIgnoreCase(oldSimSerialNumber);

    }

}
