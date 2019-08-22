package org.church.rockmobile.service;

import org.church.rockmobile.MainActivity;
import org.church.rockmobile.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver{

//	public static final String BUNDLE_REMINDER_MINUTES = "ReminderMinutes";
	public static final String BUNDLE_MESSAGE = "EventMessage";
	public static final String BUNDLE_ALARM_ID = "AlarmId";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		NotificationManager notificationManager = (NotificationManager)
              context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String title = context.getString(R.string.app_name);
    	String message = intent.getStringExtra(BUNDLE_MESSAGE);
    	
    	int icon = R.drawable.ic_launcher;
		Notification notification = new Notification(icon, message, System.currentTimeMillis());
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
      // set intent so it does not start a new activity
//      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//              Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, message, pendingIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
      
		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;
      
		//notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
      
		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
      
		notificationManager.notify(0, notification);
		
//		//remove registered alarm
//		int newId = intent.getIntExtra(BUNDLE_ALARM_ID, 0);
//		Intent alarmIntent = new Intent("org.church.rockmobile.ALARM_RECEIVER");
////		Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
//		PendingIntent pendingIntent1 = PendingIntent.getActivity(context, newId, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
//		alarmManager.cancel(pendingIntent1);
//		pendingIntent1.cancel();
		
	}
}
