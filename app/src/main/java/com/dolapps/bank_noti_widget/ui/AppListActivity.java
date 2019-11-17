package com.dolapps.bank_noti_widget.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;


public class AppListActivity extends AppCompatActivity{

	private RecyclerView recyclerView;
	private AppListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applist);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView = findViewById(R.id.a_list);
		recyclerView.setLayoutManager(layoutManager);

		update();
	}



	private void update() {
		adapter = new AppListAdapter(this);
		recyclerView.setAdapter(adapter);
	}


}