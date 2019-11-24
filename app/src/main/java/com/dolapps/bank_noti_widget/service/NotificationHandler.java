package com.dolapps.bank_noti_widget.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.misc.Util;
import com.dolapps.bank_noti_widget.widget.BalanceWidget;

import org.json.JSONObject;

public class NotificationHandler {

	public static final String LOCK = "lock";

	private Context context;
	private SharedPreferences pref;

	public NotificationHandler(Context context) {
		this.context = context;
		pref= context.getSharedPreferences("bankNotiWidget", context.MODE_PRIVATE); // 선언
	}

	void handlePosted(StatusBarNotification sbn) {
		Log.i("NotificationHandler", "handlePosted");
		NotificationObject no = new NotificationObject(context, sbn, true, -1);
		updateBalance(NotiDatabaseHelper.PostedEntry.TABLE_NAME, no.toString());
	}

	void handleRemoved(StatusBarNotification sbn, int reason) {
        Log.i("NotificationHandler", "handleRemoved");
		//NotificationObject no = new NotificationObject(context, sbn, true, -1);
        //updateBalance(NotiDatabaseHelper.PostedEntry.TABLE_NAME, no.toString());
	}

	private void updateBalance(String tableName,  String content) {
		try {
			if(content != null) {
				JSONObject json = new JSONObject(content);
				String packageName = json.getString("packageName");
				if(!pref.getBoolean(packageName, false))return ;
				String text = json.optString("text");
				String[] info = Util.getAccountBalance(context, packageName, text);
				if(info!=null) {
					synchronized (LOCK) {
						NotiDatabaseHelper dbHelper = new NotiDatabaseHelper(context);
						SQLiteDatabase db = dbHelper.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(NotiDatabaseHelper.PostedEntry.BANK, packageName);
						values.put(NotiDatabaseHelper.PostedEntry.ACCOUNT, info[0]);
						values.put(NotiDatabaseHelper.PostedEntry.BALANCE, Long.parseLong(info[1]));
						db.replace(tableName, "null", values);
						db.close();
						dbHelper.close();
						Log.i("NotificationHandler", "updateBalance");
						BalanceWidget.updateAppWidgets(context);
					}
				}

			}
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}
	}
	public void updateBalance(String tableName,  String packageName, String text) {
		try {
			//if(!pref.getBoolean(packageName, false))return ;
			String[] info = Util.getAccountBalance(context, packageName, text);
			if(info!=null) {
				synchronized (LOCK) {
					NotiDatabaseHelper dbHelper = new NotiDatabaseHelper(context);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put(NotiDatabaseHelper.PostedEntry.BANK, packageName);
					values.put(NotiDatabaseHelper.PostedEntry.ACCOUNT, info[0]);
					values.put(NotiDatabaseHelper.PostedEntry.BALANCE, Long.parseLong(info[1]));
					db.replace(tableName, "null", values);
					db.close();
					dbHelper.close();
					Log.i("NotificationHandler", "updateBalance");
					BalanceWidget.updateAppWidgets(context);
				}
			}
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}
	}

}
