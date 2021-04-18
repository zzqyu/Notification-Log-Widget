package com.dolapps.bank_noti_widget.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.kakao.adfit.ads.AdListener;
import com.kakao.adfit.ads.ba.BannerAdView;

public class BrowseActivity extends AppCompatActivity{

	private RecyclerView recyclerView;
	private BrowseAdapter adapter;
	private BannerAdView ad;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);
		getSupportActionBar().setElevation(0);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView = findViewById(R.id.list);

		update();
		recyclerView.setLayoutManager(layoutManager);

		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
		itemTouchHelper.attachToRecyclerView(recyclerView);

		ad = findViewById(R.id.b_ad);
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

		/*Log.i("[SQL_TEST]", "!!!!");
		NotiDatabaseHelper.replaceAccountDBItem(this,
				new BrowseAdapter.Item(
						"com.kebhana.hanapush",
						"하나은행",
						"244-******-39105",
						null,
						new Long(13951)),0);*/
	}


	private void update() {
		adapter = new BrowseAdapter(this);
		recyclerView.setAdapter(adapter);
	}

}