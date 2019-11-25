package com.dolapps.bank_noti_widget.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.widget.BalanceWidget;
import com.kakao.adfit.ads.AdListener;
import com.kakao.adfit.ads.ba.BannerAdView;


public class MainActivity extends AppCompatActivity {
	private SharedPreferences pref;
	private BannerAdView ad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pref= this.getSharedPreferences("bankNotiWidget", this.MODE_PRIVATE); // 선언
		if(!pref.contains("password")){
			SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
			editor.putInt(Const.PREF_COLOR1, getColor(R.color.colorBackgroundWidget));
			editor.putInt(Const.PREF_COLOR2, getColor(R.color.colorTextWidget));
			editor.putInt("CNT", 0);
			editor.putString("password", "");
			editor.commit(); //완료한다.
		}

		//new NotificationHandler(this).updateBalance(NotiDatabaseHelper.PostedEntry.TABLE_NAME, "com.kebhana.hanapush","하나카드드드 출금 10,000원 잔액 10,000원 잔액10,000원 244-******-12345");
		//new AppListAdapter(this).saveCheck("com.kebhana.hanapush", true);

		if(!pref.getString("password","").equals("")){
			Intent lock = new Intent(this, LockActivity.class);
			lock.putExtra("state", 2);
			lock.putExtra("isMain", true);
			startActivity(lock);
		}

		ad = findViewById(R.id.ad);
		ad.setClientId("DAN-1hrjrgf2qcdpe");
		ad.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				Log.i("AdListener", "onAdLoaded");
			}

			@Override
			public void onAdFailed(int i) {
				Log.i("AdListener", "onAdFailed: "+ i);
			}

			@Override
			public void onAdClicked() {
				Log.i("AdListener", "onAdFailed");
			}
		});
		getLifecycle().addObserver(new LifecycleObserver() {
			@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
			public void onResume() {
				ad.resume();
			}

			@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
			public void onPause() {
				ad.pause();
			}

			@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
			public void onDestroy() {
				ad.destroy();
			}
		});
		ad.loadAd();
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("haint", "onActivityResult in MainActivity, request Code: " + requestCode);
		for (Fragment fragment : getSupportFragmentManager().getFragments()) {
			fragment.onActivityResult(requestCode, resultCode, data);
		}
	}

}