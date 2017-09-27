package com.kou.picnicapp.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class OnBootReceiver extends BroadcastReceiver {
	public static final int PERIOD = 5000;
	public static PendingIntent pi;

	@Override
	public void onReceive(Context context, Intent intent) {

		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, OnAlarmReceiver.class);
		pi = PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + PERIOD, pi);
	}
}