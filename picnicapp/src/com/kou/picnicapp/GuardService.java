package com.kou.picnicapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kou.picnicapp.model.BeaconBluetoothDevice;
import com.kou.picnicapp.model.TargetData;
import com.kou.picnicapp.utils.LogWrapper;
import com.kou.picnicapp.utils.Utils;

public class GuardService extends Service {

	// 20초에 한 번, 10초간 스캔 후 판정.
	// 발견되지 않으면 알람과 소리.
	// -95 이하면 발견 안된걸로 간주.
	// 스캔 결과를 수시로 저장

	private final String TAG = GuardService.class.getSimpleName();

	public final static String INTENT_GUARD_SERVICE_START = "com.kou.picnicapp.GuardService.start";
	public final static String INTENT_GUARD_SERVICE_CHECK_COMPLETE = "com.kou.picnicapp.GuardService.checkcomplete";
	public final static String GUARD_SERVICE_WARNING = "GUARD_SERVICE_WARNING";

	public final static String FILENAME_IS_GUARD_START = "isGuardStart.dat";
	public final static String FILENAME_GUARD_LIST = "guardList.dat";

	private static final int REQUEST_CODE_RESTART = 20000;
	private static final int NOTIFICATION_CODE = 20202;

	private static final long CHECK_INTERVAL = 20 * 1000;// 2 * 60 * 1000; // 2min
	private static final long SCAN_PERIOD = 10 * 1000;// 60 * 1000;

	private BluetoothAdapter bluetoothAdapter;
	private ArrayList<TargetData> targetDataList;
	private HashMap<String, Long> timeList = new HashMap<String, Long>();

	private boolean isScanning;
	private Handler handler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		LogWrapper.d("TAG", "DEBUG AppService onBind()");
		setRegisterAlarmManager(REQUEST_CODE_RESTART, CHECK_INTERVAL);
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogWrapper.d("TAG", "DEBUG AppService onCreate()");
		setRegisterAlarmManager(REQUEST_CODE_RESTART, CHECK_INTERVAL);

		if (bluetoothAdapter == null) {
			final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();
		}

		doWork();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogWrapper.d("TAG", "DEBUG AppService onStartCommand()");

		if (bluetoothAdapter == null) {
			final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();
		}

		doWork();

		return START_STICKY;
	}

	protected void doWork() {

		setRegisterAlarmManager(REQUEST_CODE_RESTART, CHECK_INTERVAL);
		LogWrapper.d("TAG", "DEBUG AppService doWork");

		boolean isGuardStart = readFileIsGuardStart();
		if (isGuardStart) {
			LogWrapper.d("TAG", "DEBUG AppService doWork isGuardStart:true");
			setTargetData();
			scanLeDevice(true);
		} else {
			LogWrapper.d("TAG", "DEBUG AppService doWork isGuardStart:false");
		}
	}

	private boolean readFileIsGuardStart() {

		String localPath = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name);
		BufferedReader br = null;
		String readData = "";
		try {
			br = new BufferedReader(new FileReader(localPath + "/" + FILENAME_IS_GUARD_START));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			readData = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (readData.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	private String readFileGuardData() {
		String localPath = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name);
		BufferedReader br = null;
		String readData = "";
		try {
			br = new BufferedReader(new FileReader(localPath + "/" + FILENAME_GUARD_LIST));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			readData = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return readData;
	}

	private void setTargetData() {
		String strData = readFileGuardData();

		if (strData == null || strData.length() == 0) {
			// Do nothing
		} else {
			Gson gson = new Gson();
			java.lang.reflect.Type listType = new TypeToken<List<TargetData>>() {
			}.getType();

			strData = strData.replace("\\", "");
			strData = strData.replace("\"[", "[");
			strData = strData.replace("]\"", "]");

			targetDataList = gson.fromJson(strData, listType);
			timeList.clear();
			for (TargetData t : targetDataList) {
				timeList.put(t.getUuid() + t.getMajor() + t.getMinor(), 0L);
			}
		}
	}

	private void setRegisterAlarmManager(int requestCode, long interval) {
		LogWrapper.d("TAG", "DEBUG AppService setRegisterAlarmManager()");
		cancelRegisterAlarmManager(requestCode);
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

		Intent requestIntent = new Intent(INTENT_GUARD_SERVICE_START);
		PendingIntent registrationPendingIntent = PendingIntent.getBroadcast(this, requestCode, requestIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, registrationPendingIntent);

	}

	private void cancelRegisterAlarmManager(int requestCode) {
		LogWrapper.d("TAG", "DEBUG AppService cancelRegisterAlarmManager()");
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Intent regIntent = new Intent(INTENT_GUARD_SERVICE_START);
		alarmManager.cancel(PendingIntent.getBroadcast(this, requestCode, regIntent, PendingIntent.FLAG_UPDATE_CURRENT));

	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					isScanning = false;
					bluetoothAdapter.stopLeScan(leScanCallback);
					checkAndAlarm();
				}

			}, SCAN_PERIOD);

			isScanning = true;
			bluetoothAdapter.startLeScan(leScanCallback);
		} else {
			isScanning = false;
			bluetoothAdapter.stopLeScan(leScanCallback);
		}
	}

	private void checkAndAlarm() {

		int foundCount = 0;
		// for (HashMap.Entry<String, Long> entry : timeList.entrySet()) {
		// String key = entry.getKey();
		// long value = entry.getValue();
		// }

		for (TargetData t : targetDataList) {
			t.setCheckState(TargetData.CHECK_STATE.UNKNOWN);
		}

		for (String key : timeList.keySet()) {
			for (TargetData t : targetDataList) {
				String listkey = t.getUuid() + t.getMajor() + t.getMinor();

				if (!key.equals(listkey)) {
					continue;
				}

				foundCount++;
				t.setLastCheckedTime(timeList.get(key));
				t.setCheckState(TargetData.CHECK_STATE.FOUND);
			}
		}

		timeList.clear();

		Gson gson = new Gson();
		String targetDataStr = gson.toJson(targetDataList);
		writeFileGuardData(targetDataStr);

		Intent intent = new Intent(INTENT_GUARD_SERVICE_CHECK_COMPLETE);
		sendBroadcast(intent);

		if (foundCount != targetDataList.size()) {
			Toast.makeText(this, getString(R.string.guard_warning), Toast.LENGTH_SHORT).show();

			Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			v.vibrate(5000);

			Utils.playWarningSound(this);

			notification();
		}

	}

	private void writeFileGuardData(String targetDataStr) {
		Gson gson = new Gson();
		String strToSave = gson.toJson(targetDataStr);
		writeToFile(strToSave, GuardService.FILENAME_GUARD_LIST);
	}

	private void writeToFile(String stringData, String filename) {
		String localPath = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name);
		try {
			BufferedWriter bos = new BufferedWriter(new FileWriter(localPath + "/" + filename));
			bos.write(stringData);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notification() {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, SplashActivity.class);
		intent.putExtra(GUARD_SERVICE_WARNING, 1);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Notification.Builder mBuilder = new Notification.Builder(this);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setTicker(getString(R.string.guard_warning));
		mBuilder.setWhen(System.currentTimeMillis());
		mBuilder.setContentTitle(getString(R.string.guard_warning));
		mBuilder.setContentText(getString(R.string.guard_warning));
		mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setAutoCancel(true);

		mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

		nm.notify(NOTIFICATION_CODE, mBuilder.build());
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

			final BeaconBluetoothDevice beacon = new BeaconBluetoothDevice(device, rssi, scanRecord);

			if (targetDataList == null) {
				return;
			}

			for (TargetData t : targetDataList) {

				if (!t.getUuid().equals(beacon.getUUID())) {
					continue;
				}

				if (t.getMajor() != beacon.getMajor()) {
					continue;
				}

				if (t.getMinor() != beacon.getMinor()) {
					continue;
				}
				long currentTime = System.currentTimeMillis();
				timeList.put(beacon.getUUID() + beacon.getMajor() + beacon.getMinor(), currentTime);

				LogWrapper.d(TAG, "DEBUG found :" + beacon.getUUID() + beacon.getMajor() + beacon.getMinor());
			}
		}
	};

}