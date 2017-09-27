package com.kou.picnicapp.base;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import com.kou.picnicapp.R;
import com.kou.picnicapp.utils.CustomExceptionHandler;
import com.kou.picnicapp.utils.SettingVariables;

/**
 * BaseActivity
 * 
 * */
public class BaseActivity extends FragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (SettingVariables.IS_USE_CRASH_SAVE_TO_FILE) {

			if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
				Thread.setDefaultUncaughtExceptionHandler( //
				new CustomExceptionHandler(//
						Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name)//
						, null));
			}
		}
	}

}
