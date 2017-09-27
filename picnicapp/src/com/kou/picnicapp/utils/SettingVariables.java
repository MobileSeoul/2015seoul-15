package com.kou.picnicapp.utils;

import com.kou.picnicapp.BuildConfig;

public class SettingVariables {
	static {
		if (BuildConfig.DEBUG) {
			IS_USE_CRASH_SAVE_TO_FILE = true;
			LOG_WRAPPER_LEVEL = LogWrapper.ALL;
		} else {
			IS_USE_CRASH_SAVE_TO_FILE = false;
			LOG_WRAPPER_LEVEL = LogWrapper.NONE;
		}
	}

	public static int LOG_WRAPPER_LEVEL = LogWrapper.ALL;

	public static final boolean IS_USE_CRASH_SAVE_TO_FILE; // Crash 발생 시 로그 저장 기능
	public static final boolean IS_USE_KALMANFILTER = false;

}
