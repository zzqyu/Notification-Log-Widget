package com.dolapps.bank_noti_widget.misc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class NotiDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "BankNoti.db";

	// Posted notifications


	public static class PostedEntry {
		public static final String TABLE_NAME = "account_balance";
		public static final String BANK = "bank";
		public static final String ACCOUNT = "account";
		public static final String BALANCE = "balance";
	}

	public static final String SQL_CREATE_ENTRIES_POSTED =
			"CREATE TABLE " + PostedEntry.TABLE_NAME + " (" +
					PostedEntry.BANK + " TEXT NOT NULL," +
					PostedEntry.ACCOUNT + " TEXT NOT NULL,"+
					PostedEntry.BALANCE + " INTEGER,"+
					"PRIMARY KEY ( "+PostedEntry.BANK +", "+PostedEntry.ACCOUNT+"))";

	public static final String SQL_DELETE_ENTRIES_POSTED =
			"DROP TABLE IF EXISTS " + PostedEntry.TABLE_NAME;

	// Implementation

	public NotiDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES_POSTED);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES_POSTED);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}