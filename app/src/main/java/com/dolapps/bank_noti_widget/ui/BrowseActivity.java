package com.dolapps.bank_noti_widget.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;

public class BrowseActivity extends AppCompatActivity{

	private RecyclerView recyclerView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView = findViewById(R.id.list);
		recyclerView.setLayoutManager(layoutManager);

		update();
	}


	private void update() {
		BrowseAdapter adapter = new BrowseAdapter(this);
		recyclerView.setAdapter(adapter);
	}

}