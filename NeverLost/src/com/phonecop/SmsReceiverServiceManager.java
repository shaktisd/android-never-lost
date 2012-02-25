package com.phonecop;

import com.phonecop.constants.Constants;
import com.phonecop.tools.PlaySound;
import com.phonecop.tools.SharedDataAccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiverServiceManager extends BroadcastReceiver {
	
	
	SharedDataAccess sharedData;
	PlaySound playSound ;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if( "android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
			// ---get the SMS message passed in---
			Bundle bundle = intent.getExtras();
			SmsMessage[] msgs = null;
			String str = "";
			String messageFromPhoneNumber = null;
			if (bundle != null) {
				// ---retrieve the SMS message received---
				Object[] pdus = (Object[]) bundle.get("pdus");
				msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < msgs.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					str += "SMS from " + msgs[i].getOriginatingAddress();
					str += " :";
					str += msgs[i].getMessageBody().toString();
					str += "\n";
					messageFromPhoneNumber = msgs[i].getOriginatingAddress();
				}
				// ---display the new SMS message---
				
				sharedData = new SharedDataAccess(context);
				playSound = new PlaySound(context);
				String savedPhoneNumber = sharedData.getAttributeValue(Constants.PHONENUMBER);
				String alarmText = sharedData.getAttributeValue(Constants.ALARMTEXT);
				String alarmOnSmsFromAnyNumber = sharedData.getAttributeValue(Constants.ALARMONSMSFROMANYNUMBER);
				boolean fireAlarm = false;
				if (alarmText != null && alarmText.trim() != "" && str.toLowerCase().contains(alarmText.toLowerCase())){
					if (alarmOnSmsFromAnyNumber != null && alarmOnSmsFromAnyNumber.trim() != "" && alarmOnSmsFromAnyNumber.equals("true") ){
						fireAlarm = true;
					}else {
						if (messageFromPhoneNumber != null && savedPhoneNumber != null && messageFromPhoneNumber.contains(savedPhoneNumber)){
							fireAlarm = true;
						}	
					}
					
				}
				
				if ( fireAlarm){
					playSound.playAlarm();
					Log.e("SmsReceiverServiceManager","Msg From:" + messageFromPhoneNumber + " Saved Number:" + savedPhoneNumber);
					Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		

	}
}
