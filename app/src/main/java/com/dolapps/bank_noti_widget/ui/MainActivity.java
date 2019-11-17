package com.dolapps.bank_noti_widget.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.widget.BalanceWidget;

public class MainActivity extends AppCompatActivity {
	private SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pref= this.getSharedPreferences("bankNotiWidget", this.MODE_PRIVATE); // 선언
		if(!pref.contains(Const.PREF_COLOR1)){
			SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
			editor.putInt(Const.PREF_COLOR1, getColor(R.color.colorBackgroundWidget));
			editor.putInt(Const.PREF_COLOR2, getColor(R.color.colorTextWidget));
			editor.putInt("CNT", 0);
			editor.commit(); //완료한다.
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_delete:
				confirm();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void confirm() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
		builder.setTitle(R.string.dialog_delete_header);
		builder.setMessage(R.string.dialog_delete_text);
		builder.setNegativeButton(R.string.dialog_delete_no, (dialogInterface, i) -> {});
		builder.setPositiveButton(R.string.dialog_delete_yes, (dialogInterface, i) -> truncate());
		builder.show();
	}

	private void truncate() {
		try {
			NotiDatabaseHelper dbHelper = new NotiDatabaseHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(NotiDatabaseHelper.SQL_DELETE_ENTRIES_POSTED);
			db.execSQL(NotiDatabaseHelper.SQL_CREATE_ENTRIES_POSTED);
			BalanceWidget.updateAppWidgets(this);
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}
	}


}