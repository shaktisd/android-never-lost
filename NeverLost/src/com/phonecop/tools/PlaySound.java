package com.phonecop.tools;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class PlaySound {
	Context context;
	public PlaySound(Context context){
		this.context = context;
	}
	
	public void playAlarm(){
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	     if(alert == null){
	         // alert is null, using backup
	         alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	         if(alert == null){  // I can't see this ever being null (as always have a default notification) but just incase
	             // alert backup is null, using 2nd backup
	             alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);               
	         }
	     }
	     
	     MediaPlayer player = new MediaPlayer();
	     try {
			player.setDataSource(context, alert);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	     final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	     if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
	                player.setAudioStreamType(AudioManager.STREAM_ALARM);
	                player.setLooping(false);
	                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						int count = 0;
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							 count++;
							 if ( count < 8){
								 mediaPlayer.seekTo(0);
								 mediaPlayer.start();
							 }
						}
					});
	                
	                try {
						player.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                player.start();
	      }


	}
}
