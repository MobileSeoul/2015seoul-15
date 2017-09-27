package com.kou.picnicapp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import jxl.write.WriteException;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kou.picnicapp.base.BaseActivity;
import com.kou.picnicapp.excel.WriteSampleExcel;
import com.kou.picnicapp.utils.LogWrapper;
import com.kou.picnicapp.utils.Utils;

public class SplashActivity extends BaseActivity {
	private final String TAG = SplashActivity.class.getSimpleName();
	private final int SPLASH_TIME = 600;
	private final int HIDE_DURATION = 500;
	private final int HIDE_START_DELAY = 600;

	private RelativeLayout rlSplash;
	private RelativeLayout rlVersionCode;

	private File mPath = null;
	private static final String FTYPE = ".xls";
	private String[] mFileList;

	private int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogWrapper.d(TAG, TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		rlSplash = (RelativeLayout) findViewById(R.id.rlSplash);
		rlVersionCode = (RelativeLayout) findViewById(R.id.rlVersionCode);

		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

		if (bluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}

		mPath = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));
		loadFileList();

		if (mFileList.length == 0) {
			createSampleExcelFile();
		}
		Utils.installShortcut(this);
		Utils.createDirIfNotExists(this);

		mHandler.postDelayed(mSplashRunnable, SPLASH_TIME);

	}

	private void createSampleExcelFile() {
		WriteSampleExcel sampleExcel = new WriteSampleExcel();
		sampleExcel.setOutputFile(mPath + "/sample.xls");
		try {
			sampleExcel.write();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFileList() {
		try {
			mPath.mkdirs();
		} catch (SecurityException e) {
			LogWrapper.e(TAG, "unable to write on the sd card " + e.toString());
		}
		if (mPath.exists()) {
			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					return filename.contains(FTYPE) || sel.isDirectory();
				}

			};
			mFileList = mPath.list(filter);
		} else {
			mFileList = new String[0];
		}
	}

	private Handler mHandler = new Handler();
	private Runnable mSplashRunnable = new Runnable() {
		@Override
		public void run() {

			// ObjectAnimator splashUpAnim = ObjectAnimator.ofFloat(rlSplash, "translationY", -320).setDuration(1000);
			// splashUpAnim.setStartDelay(1000);
			// splashUpAnim.start();

			ObjectAnimator splashAnim1 = ObjectAnimator.ofFloat(rlVersionCode, "alpha", 1, 0).setDuration(HIDE_DURATION);
			splashAnim1.setStartDelay(HIDE_START_DELAY);

			ObjectAnimator splashAnim2 = ObjectAnimator.ofFloat(rlSplash, "alpha", 1, 0).setDuration(HIDE_DURATION);
			splashAnim2.setStartDelay(HIDE_START_DELAY);
			splashAnim2.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					rlVersionCode.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					rlSplash.setVisibility(View.GONE);
					Intent i = new Intent(SplashActivity.this, TabMainActivity.class);
					i.putExtra(GuardService.GUARD_SERVICE_WARNING, position);
					startActivity(i);
					finish();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});

			splashAnim1.start();
			splashAnim2.start();

			// ObjectAnimator loginPositionAnim = ObjectAnimator.ofFloat(rlLogin, "translationY", 200).setDuration(0);
			// loginPositionAnim.setStartDelay(0);
			// loginPositionAnim.start();

			// ObjectAnimator loginUpAnim = ObjectAnimator.ofFloat(rlLogin, "translationY", -200).setDuration(1000);
			// loginUpAnim.setStartDelay(800);
			// loginUpAnim.start();

		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		position = getIntent().getIntExtra(GuardService.GUARD_SERVICE_WARNING, 0);

	}

}
