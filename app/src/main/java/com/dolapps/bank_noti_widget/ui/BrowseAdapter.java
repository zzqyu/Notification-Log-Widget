package com.dolapps.bank_noti_widget.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.misc.Util;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseViewHolder> {

	private Activity context;
	private ArrayList<Item> data = new ArrayList<>();
	public static HashMap<String, Drawable> iconCache = new HashMap<>();
	private Handler handler = new Handler();

	public BrowseAdapter(Activity context) {
		this.context = context;
		loadMore();
	}

	@NonNull
	@Override
	public BrowseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse, parent, false);
		BrowseViewHolder vh = new BrowseViewHolder(view);

		return vh;
	}

	@Override
	public void onBindViewHolder(@NonNull BrowseViewHolder vh, int position) {
		Item item = data.get(position);

		if(iconCache.containsKey(item.getPackageName()) && iconCache.get(item.getPackageName()) != null) {
			vh.icon.setImageDrawable(iconCache.get(item.getPackageName()));
		} else {
			vh.icon.setImageResource(R.mipmap.ic_launcher_round);
		}

		vh.name.setText(item.getAppName());
		vh.date.setVisibility(View.GONE);
		vh.preview.setVisibility(View.GONE);
		vh.text.setVisibility(View.VISIBLE);
		vh.text.setText(item.getAccount() + " " + new DecimalFormat("###,###").format(item.getBalance()) + "원");
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	private void loadMore() {

		try {
			HashMap<String, String> map = new Gson().fromJson(Const.PACKAGE_APPNAME, new HashMap<String, String>().getClass());
			NotiDatabaseHelper databaseHelper = new NotiDatabaseHelper(context);
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			Cursor cursor = db.query(NotiDatabaseHelper.PostedEntry.TABLE_NAME,
					new String[] {
							NotiDatabaseHelper.PostedEntry.BANK,
							NotiDatabaseHelper.PostedEntry.ACCOUNT,
							NotiDatabaseHelper.PostedEntry.BALANCE
					},
					null,
					null,
					null,
					null,
					NotiDatabaseHelper.PostedEntry.BANK);

			if(cursor != null && cursor.moveToFirst()) {
				for(int i = 0; i < cursor.getCount(); i++) {
					String packageName = cursor.getString(0);
					String appName = map.get(packageName);
					if(appName==null)appName = Util.getAppNameFromPackage(context, packageName, false);
					String account =cursor.getString(1);
					long balance =cursor.getLong(2);
					data.add(new Item(packageName, appName, account, balance));
					Drawable icon = context.getPackageManager().getApplicationIcon(packageName);
					iconCache.put(packageName, icon);
					cursor.moveToNext();
				}
				cursor.close();
			}

			db.close();
			databaseHelper.close();
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}

		handler.post(() -> notifyDataSetChanged());
	}

	public static class Item {

		private String packageName;
		private String appName;
		private String account;
		private long balance;

		public Item(String packageName, String appName, String account, long balance) {
			this.packageName = packageName;
			this.appName = appName;
			this.account = account;
			this.balance = balance;

		}

		public String getPackageName() {
			return packageName;
		}

		public String getAppName() {
			return appName;
		}
		public String getAccount() {
			return account;
		}
		public long getBalance() {
			return balance;
		}



	}

}
