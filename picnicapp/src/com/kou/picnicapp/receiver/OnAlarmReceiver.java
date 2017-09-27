package com.kou.picnicapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kou.picnicapp.GuardService;
import com.kou.picnicapp.utils.LogWrapper;

public class OnAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, GuardService.class));
		LogWrapper.d("TAG", "DEBUG OnAlarmReceiver");
	}
}