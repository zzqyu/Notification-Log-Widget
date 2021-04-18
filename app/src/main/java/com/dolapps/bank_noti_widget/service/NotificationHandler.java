package com.dolapps.bank_noti_widget.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.misc.Util;
import com.dolapps.bank_noti_widget.ui.BrowseAdapter;
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
		updateBalance(NotiDatabaseHelper.AccountEntry.TABLE_NAME, no.toString());
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
						NotiDatabaseHelper.replaceAccountDBItem(context,
								//						packageName, appname,    	account, 	alias		, balance,				rank
								new BrowseAdapter.Item(packageName, null, info[0], null, Long.parseLong(info[1])), null);
						BalanceWidget.updateAppWidgets(context);
					}
				}

			}
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}
	}


}
