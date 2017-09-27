package com.kou.picnicapp.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.view.Display;

import com.kou.picnicapp.R;

/**
 * Utils
 * 
 * This class contains utility methods , variables and constants, that will be used by other activities.
 * 
 * */
public class Utils {

	private static final String TAG = Utils.class.getSimpleName();
	public final static String MANUFACTURER_HANAMICRON = "1";
	public final static String MANUFACTURER_SYSZONE = "2";

	public static boolean isInstalled(Context context, String schemUrl) {
		boolean isInstalled = false;
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo packageInfo : packages) {
			if (schemUrl.equalsIgnoreCase(packageInfo.packageName)) {
				LogWrapper.d(TAG, "isInstalled : true");
				isInstalled = true;
				break;
			}
		}
		return isInstalled;
	}

	public static String getApplicationVersionInfo(Context context) {
		PackageInfo pInfo;
		String versionName = "";
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionName = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			LogWrapper.e(TAG, e.toString());
		}
		return versionName;
	}

	public static void installShortcut(Activity activity) {

		boolean isShortcutInstalled = PreferenceUtils.getIsShortcutInstalled(activity);

		if (false == isShortcutInstalled) {

			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN, null);
			shortcutIntent.setClass(activity, activity.getClass());
			shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(R.string.app_name));
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(activity, R.drawable.ic_launcher));
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

			// doesn't works on "GO launcher"
			intent.putExtra("duplicate", false);
			activity.sendBroadcast(intent);

			PreferenceUtils.setIsShortcutInstalled(activity, true);
		}
	}

	public static boolean createDirIfNotExists(Activity activity) {
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory(), activity.getString(R.string.app_name));
		if (!file.exists()) {
			if (!file.mkdirs()) {
				LogWrapper.e(TAG, "Create app folder failed.");
				ret = false;
			}
		}
		return ret;
	}

	public static String getAppStorageFolder(Activity activity) {
		return Environment.getExternalStorageDirectory() + File.separator + activity.getString(R.string.app_name);
	}

	public static int getScreenWidthPercent(Activity activity, int percent) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		return (int) (width * percent * 0.01);
	}

	public static double getDistanceFromRSSI(int rssi) {

		int txPower = -74;

		if (rssi == 0) {
			return -1.0; // if we cannot determine accuracy, return -1.
		}

		double ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			return accuracy;
		}
	}

	public static void playWarningSound(Context context) {
		AssetFileDescriptor afd;
		try {
			MediaPlayer player = new MediaPlayer();
			afd = context.getAssets().openFd("woopwoop2.mp3");
			player.reset();
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player.prepare();
			player.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double calculateAccuracy(double rssi) {
		int txPower = -65;
		if (rssi == 0) {
			return 0.0; // if we cannot determine accuracy, return -1.
		}

		double ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			return accuracy;
		}
	}
}