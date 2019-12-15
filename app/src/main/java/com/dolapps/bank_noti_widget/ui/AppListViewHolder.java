package com.dolapps.bank_noti_widget.ui;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;

class AppListViewHolder extends RecyclerView.ViewHolder {

	public CardView card;
	public LinearLayout item;
	public ImageView icon;
	public TextView name;
	public TextView packageName;
	public CheckBox cb;

	AppListViewHolder(View view) {
		super(view);
		card = view.findViewById(R.id.a_card);
		item = view.findViewById(R.id.a_item);
		icon = view.findViewById(R.id.a_icon);
		name = view.findViewById(R.id.a_name);
		packageName = view.findViewById(R.id.a_package);
		cb = view.findViewById(R.id.a_cb);

	}

}
