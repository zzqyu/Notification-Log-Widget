package com.dolapps.bank_noti_widget.ui;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.dolapps.bank_noti_widget.BuildConfig;
import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.misc.Util;
import com.dolapps.bank_noti_widget.widget.BalanceWidget;
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

	public static final String TAG = SettingsFragment.class.getName();

	private NotiDatabaseHelper dbHelper;
	private BroadcastReceiver updateReceiver;

	private Preference prefStatus;
	private Preference prefBrowse;
	private Preference prefAppList;
	private CheckBoxPreference prefWidgetLock;
	private ColorPreferenceCompat prefColor1;
	private ColorPreferenceCompat prefColor2;
	private SharedPreferences pref;


	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.preferences);
		pref= getContext().getSharedPreferences("bankNotiWidget", getContext().MODE_PRIVATE); // 선언



		PreferenceManager pm = getPreferenceManager();

		prefStatus = pm.findPreference(Const.PREF_STATUS);
		if(prefStatus != null) {
			prefStatus.setOnPreferenceClickListener(preference -> {
				startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
				return true;
			});
		}

		prefAppList = pm.findPreference(Const.PREF_APPLIST);
		if(prefAppList != null) {
			prefAppList.setOnPreferenceClickListener(preference -> {
				startActivity(new Intent(getActivity(), AppListActivity.class));
				return true;
			});
		}

		prefBrowse = pm.findPreference(Const.PREF_BROWSE);
		if(prefBrowse != null) {
			prefBrowse.setOnPreferenceClickListener(preference -> {
				startActivity(new Intent(getActivity(), BrowseActivity.class));
				return true;
			});
		}
		Log.i("pref.getString","!"+pref.getString("password", ""));
		Log.i("pref.getString","!"+!pref.getString("password", "").equals(""));
		prefWidgetLock = pm.findPreference(Const.PREF_WIDGET_LOCK);
		prefWidgetLock.setChecked(!pref.getString("password", "").equals(""));
		prefWidgetLock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Log.i("onPreferenceChange",prefWidgetLock.isChecked()+"");
				//prefWidgetLock.setChecked(!(boolean)newValue);
				Intent it = new Intent(getActivity(), LockActivity.class);
				if(!prefWidgetLock.isChecked()){
					it.putExtra("state", 2);

				}
				prefWidgetLock.setChecked(!prefWidgetLock.isChecked());
				getActivity().startActivityForResult(it, 999);
				return true;
			}
		});
		Preference.OnPreferenceChangeListener cl = new Preference.OnPreferenceChangeListener() {
			@Override public boolean onPreferenceChange(Preference preference, Object newValue) {
				int newColor = (int) newValue;
				SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
				if (Const.PREF_COLOR1.equals(preference.getKey())) {
					editor.putInt(Const.PREF_COLOR1, newColor);
				}
				else{
					editor.putInt(Const.PREF_COLOR2, newColor);
				}
				editor.commit(); //완료한다.
				BalanceWidget.updateAppWidgets(getContext());
				return true;
			}
		};


		prefColor1 = pm.findPreference(Const.PREF_COLOR1);
		prefColor1.setOnPreferenceChangeListener(cl);
		prefColor1.setDefaultValue(Integer.toHexString(pref.getInt(Const.PREF_COLOR1, getContext().getColor(R.color.colorBackgroundWidget))));
		prefColor2 = pm.findPreference(Const.PREF_COLOR2);
		prefColor2.setOnPreferenceChangeListener(cl);
		prefColor2.setDefaultValue(Integer.toHexString(pref.getInt(Const.PREF_COLOR2, getContext().getColor(R.color.colorBackgroundWidget))));



		Preference prefAbout = pm.findPreference(Const.PREF_ABOUT);
		if(prefAbout != null) {
			prefAbout.setOnPreferenceClickListener(preference -> {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://github.com/zzqyu/Notification-Log-Widget"));
				startActivity(intent);
				return true;
			});
		}


		Preference prefVersion = pm.findPreference(Const.PREF_VERSION);
		if(prefVersion != null) {
			prefVersion.setSummary(BuildConfig.VERSION_NAME);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("onActivityResult", requestCode +"/"+resultCode);
		//off
		if(requestCode==111||resultCode==111){
			prefWidgetLock.setChecked(false);
			SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
			editor.putString("password", "");
			editor.commit(); //완료한다.
		}
		//on
		else if(requestCode==222||resultCode==222){
			prefWidgetLock.setChecked(true);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			dbHelper = new NotiDatabaseHelper(getActivity());
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}

		updateReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				update();
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();

		if(Util.isNotificationAccessEnabled(getActivity())) {
			prefStatus.setSummary(R.string.settings_notification_access_enabled);
		} else {
			prefStatus.setSummary(R.string.settings_notification_access_disabled);
			Toast.makeText(getContext(), R.string.settings_notification_access_disabled_more, Toast.LENGTH_LONG).show();
			startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
		}

		update();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateReceiver);
		super.onPause();
	}

	private void update() {
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			long numRowsPosted = DatabaseUtils.queryNumEntries(db, NotiDatabaseHelper.PostedEntry.TABLE_NAME);
			int stringResource = R.string.settings_browse_summary;
			if(stringResource>0) prefBrowse.setSummary(getString(stringResource, numRowsPosted));
			int cnt = (pref.getAll().size()-4);
			if(cnt> 0)
				prefAppList.setSummary(getString(R.string.settings_applist_summary1, cnt));
			if(Util.isNotificationAccessEnabled(getActivity())&&cnt==0){
				Toast.makeText(getContext(), R.string.settings_applist_disabled, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}

	}

}