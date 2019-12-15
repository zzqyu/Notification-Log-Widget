package com.dolapps.bank_noti_widget.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppListAdapter extends RecyclerView.Adapter<AppListViewHolder> {

	private Activity context;
	private ArrayList<AppItem> filteredList;
	private SharedPreferences pref;
	public AppListAdapter(Activity context) {
		this.context = context;
		pref= context.getSharedPreferences("bankNotiWidget", context.MODE_PRIVATE); // 선언
		load();
	}

	@NonNull
	@Override
	public AppListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applist, parent, false);
		AppListViewHolder vh = new AppListViewHolder(view);

		return vh;
	}

	public void onBindViewHolder(@NonNull AppListViewHolder vh, int position) {
		AppItem item = filteredList.get(position);

		try {
			vh.icon.setImageDrawable(context.getPackageManager().getApplicationIcon(item.getPackageName()));
			vh.cb.setVisibility(View.VISIBLE);
		} catch(Exception e) {
			vh.icon.setImageDrawable(context.getDrawable(R.mipmap.ic_launcher_round));
			vh.cb.setVisibility(View.INVISIBLE);
			vh.card.setBackgroundColor(0xaaaaaa);
			SharedPreferences.Editor editor = pref.edit();
			editor.remove(vh.packageName.getText().toString());
			editor.commit();
		}

		vh.name.setText(item.getAppName());
		vh.packageName.setText(item.getPackageName());
		vh.cb.setChecked(isChecked(item.getPackageName()));

		//Here it is simply write onItemClick listener here
		vh.item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (position != RecyclerView.NO_POSITION) {
					if(vh.cb.getVisibility()==View.INVISIBLE){
						try {
							context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + item.getPackageName())));
						} catch (android.content.ActivityNotFoundException anfe) {
							context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + item.getPackageName())));
						}
					}
					else{
						vh.cb.setChecked(!vh.cb.isChecked());
						saveCheck(vh.packageName.getText().toString(), vh.cb.isChecked());
						load();
					}

				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return filteredList.size();
	}


	private void load() {
		filteredList = new ArrayList<>();
		boolean isExist = false;
		HashMap<String, String> map = new Gson().fromJson(Const.PACKAGE_APPNAME, new HashMap<String, String>().getClass());

		for(String packageName: new ArrayList<String>(map.keySet())){
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			if(intent==null){
				//미설치
				filteredList.add(new AppItem(context,map.get(packageName), packageName));
			}else{
				//설치
				filteredList.add(0, new AppItem(context, map.get(packageName), packageName));
				isExist = true;
			}

		}
		if(!isExist){
			Toast.makeText(context, R.string.settings_applist_not, Toast.LENGTH_LONG).show();
		}
	}

	public void saveCheck(String packageName, boolean isCk){
		SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
		int cnt = pref.getInt("CNT", 0);
		if(isCk) {
			editor.putBoolean(packageName, true);
			Toast.makeText(context, R.string.settings_applist_notice, Toast.LENGTH_SHORT).show();
		}
		else{
			editor.remove(packageName);
			editor.putInt("CNT", --cnt);
		}
		editor.commit(); //완료한다.
	}

	private boolean isChecked(String packageName){
		return pref.getBoolean(packageName, false);
	}

	public static class AppItem {

		private String packageName;
		private String appName;

		public AppItem(Context context, String name, String packageName) {
			this.appName = name;
			this.packageName = packageName;
		}
		public String getPackageName() {
			return packageName;
		}

		public String getAppName() {
			return appName;
		}

	}

}
