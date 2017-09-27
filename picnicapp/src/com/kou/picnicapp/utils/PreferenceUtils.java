package com.kou.picnicapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * This class contains utility methods , variables and constants, that will be used by other activities.
 * 
 * */
public class PreferenceUtils {

	private static final String PREF_KEY = "pref";
	private static final String PREF_KEY_IsShortcutInstalled = "PREF_KEY_IsShortcutInstalled";
	private static final String PREF_KEY_CHECK_LIST = "PREF_KEY_CHECK_LIST";

	// private static final String PREF_KEY_GUARD_LIST = "PREF_KEY_GUARD_LIST";
	// private static final String PREF_KEY_GUARD_START = "PREF_KEY_GUARD_START";

	private static void saveBooleanPreference(Context context, String key, boolean defValue) {
		SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(key, defValue);
		editor.commit();
	}

	private static boolean loadBooleanPreference(Context context, String key, boolean defValue) {
		SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
		return pref.getBoolean(key, defValue);
	}

	@SuppressWarnings("unused")
	private static void saveIntPreference(Context context, String key, int value) {
		SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	@SuppressWarnings("unused")
	private static int loadIntPreference(Context context, String key, int defValue) {
		SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
		return pref.getInt(key, defValue);
	}

	private static void saveStringPreference(Context context, String key, String stringData) {
		SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, stringData);
		editor.commit();
	}

	private static String loadStringPreference(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);
		return pref.getString(key, "");

	}

	// ///////////////////////// get /////////////////////////
	public static boolean getIsShortcutInstalled(Context applicationContext) {
		return loadBooleanPreference(applicationContext, PREF_KEY_IsShortcutInstalled, false);
	}

	public static String getCheckList(Context applicationContext) {
		return loadStringPreference(applicationContext, PREF_KEY_CHECK_LIST);
	}

	// public static String getGuardList(Context applicationContext) {
	// return loadStringPreference(applicationContext, PREF_KEY_GUARD_LIST);
	// }
	//
	// public static boolean isGuardStart(Context applicationContext) {
	// return loadBooleanPreference(applicationContext, PREF_KEY_GUARD_START, false);
	// }

	// ///////////////////////// set /////////////////////////

	public static void setIsShortcutInstalled(Context applicationContext, boolean value) {
		saveBooleanPreference(applicationContext, PREF_KEY_IsShortcutInstalled, value);
	}

	public static void setCheckList(Context applicationContext, String value) {
		saveStringPreference(applicationContext, PREF_KEY_CHECK_LIST, value);
	}

	// public static void setGuardList(Context applicationContext, String value) {
	// saveStringPreference(applicationContext, PREF_KEY_GUARD_LIST, value);
	// }
	//
	// public static void setIsGuardStart(Context applicationContext, boolean value) {
	// saveBooleanPreference(applicationContext, PREF_KEY_GUARD_START, value);
	// }

}
