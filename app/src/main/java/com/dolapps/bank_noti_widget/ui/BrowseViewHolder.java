package com.dolapps.bank_noti_widget.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;

class BrowseViewHolder extends RecyclerView.ViewHolder {

	public LinearLayout item;
	public ImageView icon;
	public TextView name;
	public TextView text;

	BrowseViewHolder(View view) {
		super(view);
		item = view.findViewById(R.id.item);
		icon = view.findViewById(R.id.icon);
		name = view.findViewById(R.id.name);
		text = view.findViewById(R.id.text);
	}

}
