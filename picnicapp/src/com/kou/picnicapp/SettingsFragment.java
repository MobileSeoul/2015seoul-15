package com.kou.picnicapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kou.picnicapp.excel.ReadExcel;
import com.kou.picnicapp.model.TargetData;
import com.kou.picnicapp.utils.LogWrapper;
import com.kou.picnicapp.utils.PreferenceUtils;

public class SettingsFragment extends Fragment implements OnClickListener {
	private static final String TAG = SettingsFragment.class.getSimpleName();
	private static final String ARG_POSITION = "position";

	private int position;
	private View mainView;

	private Button btnLoadCheckList;
	private Button btnClearCheckList;
	private Button btnLoadGuardList;
	private Button btnClearGuardList;

	private String[] mFileList;
	private File mPath = null;
	private String mChosenFile = null;
	private static final String FTYPE = ".xls";
	private static final int DIALOG_LOAD_CHECK_FILE = 1000;
	private static final int DIALOG_LOAD_GUARD_FILE = 2000;

	private Handler handler = new Handler();

	public static SettingsFragment newInstance(int position) {
		SettingsFragment f = new SettingsFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
		mPath = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mainView = inflater.inflate(R.layout.fragment_setting, null);

		btnLoadCheckList = (Button) mainView.findViewById(R.id.btnLoadCheckList);
		btnClearCheckList = (Button) mainView.findViewById(R.id.btnClearCheckList);
		btnLoadGuardList = (Button) mainView.findViewById(R.id.btnLoadGuardList);
		btnClearGuardList = (Button) mainView.findViewById(R.id.btnClearGuardList);

		btnLoadCheckList.setOnClickListener(this);
		btnClearCheckList.setOnClickListener(this);
		btnLoadGuardList.setOnClickListener(this);
		btnClearGuardList.setOnClickListener(this);

		return mainView;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLoadCheckList: {
			loadFileList();
			Dialog d = createDialog(DIALOG_LOAD_CHECK_FILE);
			d.show();
		}
			break;

		case R.id.btnClearCheckList:
			clearCheckList();
			break;

		case R.id.btnLoadGuardList: {
			loadFileList();
			Dialog d = createDialog(DIALOG_LOAD_GUARD_FILE);
			d.show();
		}
			break;

		case R.id.btnClearGuardList:
			clearGuardList();
			break;

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

	private void clearCheckList() {
		PreferenceUtils.setCheckList(getActivity(), "");
		Toast.makeText(getActivity(), getString(R.string.check_list_cleared), Toast.LENGTH_SHORT).show();
	}

	private void clearGuardList() {
		writeFileIsGuardStart(false);
		writeFileGuardData("");
		Toast.makeText(getActivity(), getString(R.string.guard_list_cleared), Toast.LENGTH_SHORT).show();
	}

	protected Dialog createDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(getActivity());

		switch (id) {
		case DIALOG_LOAD_CHECK_FILE: {
			String defaultTitle = getString(R.string.choose_file);
			builder.setTitle(defaultTitle);

			if (mFileList == null//
					|| mFileList.length == 0//
					|| (mFileList.length == 1 && mFileList[0].equals("sample.xls"))) {
				LogWrapper.d(TAG, "Showing file picker before loading the file list");
				builder.setTitle(getString(R.string.choose_file_nofile) + getString(R.string.app_name));
				dialog = builder.create();
				return dialog;
			}

			List<String> list = new ArrayList<String>();
			for (String file : mFileList) {
				if (!file.equals("sample.xls")) {
					list.add(file);
				}
			}
			mFileList = list.toArray(new String[list.size()]);

			builder.setItems(mFileList, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mChosenFile = mFileList[which];
				}
			});
			builder.setOnDismissListener(onCheckDismissListener);
		}
			break;

		case DIALOG_LOAD_GUARD_FILE: {
			String defaultTitle = getString(R.string.choose_file);
			builder.setTitle(defaultTitle);

			if (mFileList == null//
					|| mFileList.length == 0//
					|| (mFileList.length == 1 && mFileList[0].equals("sample.xls"))) {
				LogWrapper.d(TAG, "Showing file picker before loading the file list");
				builder.setTitle(getString(R.string.choose_file_nofile) + getString(R.string.app_name));
				dialog = builder.create();
				return dialog;
			}

			List<String> list = new ArrayList<String>();
			for (String file : mFileList) {
				if (!file.equals("sample.xls")) {
					list.add(file);
				}
			}
			mFileList = list.toArray(new String[list.size()]);

			builder.setItems(mFileList, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mChosenFile = mFileList[which];
				}
			});
			builder.setOnDismissListener(onGuardDismissListener);
		}
			break;
		}

		dialog = builder.show();
		return dialog;
	}

	private DialogInterface.OnDismissListener onCheckDismissListener = new DialogInterface.OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {

			ArrayList<TargetData> targetDataList = null;

			targetDataList = ReadExcel.read(mPath + "/" + mChosenFile);

			if (targetDataList == null || targetDataList.size() == 0) {
				Toast.makeText(getActivity(), getString(R.string.check_list_failed), Toast.LENGTH_SHORT).show();
			} else {
				Gson gson = new Gson();
				String targetDataStr = gson.toJson(targetDataList);
				PreferenceUtils.setCheckList(getActivity(), targetDataStr);
				Toast.makeText(getActivity(), getString(R.string.check_list_saved), Toast.LENGTH_SHORT).show();
			}
		}
	};

	private DialogInterface.OnDismissListener onGuardDismissListener = new DialogInterface.OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {

			ArrayList<TargetData> targetDataList = null;

			targetDataList = ReadExcel.read(mPath + "/" + mChosenFile);

			if (targetDataList == null || targetDataList.size() == 0) {
				Toast.makeText(getActivity(), getString(R.string.guard_list_failed), Toast.LENGTH_SHORT).show();
			} else {
				Gson gson = new Gson();
				String targetDataStr = gson.toJson(targetDataList);

				java.lang.reflect.Type listType = new TypeToken<List<TargetData>>() {
				}.getType();

				targetDataList = gson.fromJson(targetDataStr, listType);

				writeFileIsGuardStart(true);
				writeFileGuardData(targetDataStr);
				// PreferenceUtils.setGuardList(getActivity(), targetDataStr);
				// PreferenceUtils.setIsGuardStart(getActivity(), true);
				Toast.makeText(getActivity(), getString(R.string.guard_list_saved), Toast.LENGTH_SHORT).show();
			}

		}

	};

	private void writeFileIsGuardStart(boolean state) {
		Gson gson = new Gson();
		String strToSave = gson.toJson(state);
		writeToFile(strToSave, GuardService.FILENAME_IS_GUARD_START);
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

}