package com.dolapps.bank_noti_widget.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;
import com.kakao.adfit.ads.AdListener;
import com.kakao.adfit.ads.ba.BannerAdView;


public class AppListActivity extends AppCompatActivity{

	private RecyclerView recyclerView;
	private AppListAdapter adapter;
	private BannerAdView ad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applist);
		getSupportActionBar().setElevation(0);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView = findViewById(R.id.a_list);
		recyclerView.setLayoutManager(layoutManager);

		ad = findViewById(R.id.alist_ad);
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

		update();
	}



	private void update() {
		adapter = new AppListAdapter(this);
		recyclerView.setAdapter(adapter);
	}


}