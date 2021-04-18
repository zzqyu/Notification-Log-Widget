package com.dolapps.bank_noti_widget.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dolapps.bank_noti_widget.ui.BrowseAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class NotiDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "BANK_NOTI.db";

	// Posted notifications


	public static class AccountEntry {
		public static final String TABLE_NAME = "account_table";
		public static final String BANK = "bank";
		public static final String ACCOUNT = "account";
		public static final String BALANCE = "balance";
		public static final String ALIAS = "alias";
		public static final String RANK = "rank";
	}

	public static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + AccountEntry.TABLE_NAME + " (" +
					AccountEntry.BANK + " TEXT NOT NULL," +
					AccountEntry.ACCOUNT + " TEXT NOT NULL,"+
					AccountEntry.BALANCE + " INTEGER NOT NULL,"+
					AccountEntry.ALIAS + " TEXT,"+
					AccountEntry.RANK + " INTEGER,"+
					"PRIMARY KEY ( "+AccountEntry.ACCOUNT+"));";

	public static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;

	// Implementation

	public NotiDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	public static void replaceAccountDBItem(Context context, BrowseAdapter.Item item, Integer rank){
		NotiDatabaseHelper dbHelper =  new NotiDatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		Log.i("[SQL_TEST]", "[replaceAccountDBItem]");
		Log.i("[SQL_TEST]", "[Item PackageName] " + item.getPackageName());
		Log.i("[SQL_TEST]", "[Item Account] " + item.getAccount());
		Log.i("[SQL_TEST]", "[Item Balance] " + item.getBalance());
		Log.i("[SQL_TEST]", "[Item Alias] " + item.getAlias());

		if(item.getPackageName()!=null) values.put(AccountEntry.BANK, item.getPackageName());
		if(item.getAccount()!=null) values.put(AccountEntry.ACCOUNT, item.getAccount());
		if(item.getBalance()!=null) values.put(AccountEntry.BALANCE, item.getBalance());
		if(item.getAlias()!=null) {
			values.put(AccountEntry.ALIAS, item.getAlias());
		}
		if(rank!=null) values.put(AccountEntry.RANK, rank);

		//select 해서 데이터 확인
		Cursor cursor = db.query(NotiDatabaseHelper.AccountEntry.TABLE_NAME,
				null,
				AccountEntry.ACCOUNT+" = ? ",
				new String[]{item.getAccount()}, null, null, null, null);
		cursor.moveToFirst();
		if(cursor.getCount()>0){//있으면 update
			db.update(NotiDatabaseHelper.AccountEntry.TABLE_NAME, values,
					AccountEntry.ACCOUNT+" = ?  and "+AccountEntry.BANK+" = ?",
					new String[]{item.getAccount(), item.getPackageName()});
			Log.i("[SQL_TEST]", "있으면 update");
		}
		else {//없으면 insert
			//db.replace(AccountEntry.TABLE_NAME, "null", values);
			db.insert(AccountEntry.TABLE_NAME, "null", values);
			Log.i("[SQL_TEST]", "없으면 insert");
		}

		db.close();
		dbHelper.close();
	}
	public static void deleteAccountDBItem(Context context, String account){
		NotiDatabaseHelper dbHelper =  new NotiDatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(AccountEntry.TABLE_NAME, AccountEntry.ACCOUNT+"=?", new String[]{account});
		db.close();
		dbHelper.close();
	}
	public static void relocationAccountDBItem(Context context, ArrayList<BrowseAdapter.Item> items){
		NotiDatabaseHelper dbHelper =  new NotiDatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(SQL_DELETE_ENTRIES);
		db.execSQL(SQL_CREATE_ENTRIES);
		db.close();
		dbHelper.close();

		for(int i = 0; i< items.size(); i++){
			replaceAccountDBItem(context, items.get(i), i+1);
		}
	}
	public static Long getTotalBalance(Context context){
		NotiDatabaseHelper databaseHelper = new NotiDatabaseHelper(context);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Long result = null;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select sum("+AccountEntry.BALANCE+") from "+AccountEntry.TABLE_NAME+";", null);
			if (cursor != null && cursor.moveToFirst()) {
				result = cursor.getLong(cursor.getColumnIndex("sum("+AccountEntry.BALANCE+")"));

				Log.i("getTotalBalance", "result: "+result);
			}
		}catch (Exception e){
			Log.getStackTraceString(e);
			Log.i("getTotalBalance", e.getLocalizedMessage());
			result=null;
		}
		finally {
			if(cursor!=null)cursor.close();
			db.close();
			databaseHelper.close();
		}
		return result;
	}

	public static ArrayList<BrowseAdapter.Item> getAccountList(Context context, boolean isOrderByRank){
		HashMap<String, String> map = new Gson().fromJson(Const.PACKAGE_APPNAME, new HashMap<String, String>().getClass());
		ArrayList<BrowseAdapter.Item> result =null;
		try {
		NotiDatabaseHelper databaseHelper = new NotiDatabaseHelper(context);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		String orderBy = (isOrderByRank? AccountEntry.RANK+ ", "+ AccountEntry.BANK:NotiDatabaseHelper.AccountEntry.BANK + ", "+ AccountEntry.RANK);

			Cursor cursor = db.query(NotiDatabaseHelper.AccountEntry.TABLE_NAME,
					null, null, null, null, null, orderBy, null);

			String[] tags = new String[]{AccountEntry.ACCOUNT, AccountEntry.BALANCE, AccountEntry.BANK, AccountEntry.ALIAS};
			if(cursor != null) result = new ArrayList<>();
			if (cursor != null && cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					String packageName = cursor.getString(cursor.getColumnIndex(tags[2]));
					String appName = map.get(packageName);
					if (appName == null)
						appName = Util.getAppNameFromPackage(context, packageName, false);
					result.add(new BrowseAdapter.Item(
							packageName, appName,
							cursor.getString(cursor.getColumnIndex(tags[0])),
							cursor.getString(cursor.getColumnIndex(tags[3])),
							cursor.getLong(cursor.getColumnIndex(tags[1]))
					));
					cursor.moveToNext();
				}
				cursor.close();
			}

			db.close();
			databaseHelper.close();
		}
		catch (Exception e){
			Log.getStackTraceString(e);
			result = null;
		}
		return result;
	}
}