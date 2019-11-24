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

import java.util.ArrayList;

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
		} catch(Exception e) {
			vh.icon.setImageDrawable(context.getDrawable(R.mipmap.ic_launcher_round));
			vh.cb.setVisibility(View.GONE);
		}

		vh.name.setText(item.getAppName());
		vh.packageName.setText(item.getPackageName());
		vh.cb.setChecked(isChecked(item.getPackageName()));

		//Here it is simply write onItemClick listener here
		vh.item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (position != RecyclerView.NO_POSITION) {
					if(vh.cb.getVisibility()==View.GONE){
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


	/*
	신한, 우리, 부산
	com.kebhana.hanapush 하나은행
	com.kbstar.starpush 국민은행
	com.nh.mobilenoti 농협은행
	com.IBK.SmartPush.app 기업은행
	kr.co.citibank.citimobile 씨티은행
	kr.co.dgb.dgbfium DGB대구은행
	SC제일은행 com.scbank.ma30
	케이뱅크 com.kbankwith.smartbank
	카카오뱅크 com.kakaobank.channel
	새마을금고
	산업은행 co.kr.kdb.android.smartkdb
	수협 com.suhyup.pesmb
	전북은행 kr.co.jbbank.smartbank
	경남은행 com.knb.bsp
	신협 com.cu.sbank
	 */

	private void load() {
		String[][] pkgs = {{"com.kebhana.hanapush","하나은행"},
				{"com.kbstar.starpush","국민은행"},
				{"com.nh.mobilenoti","농협"},
				{"com.IBK.SmartPush.app", "기업은행"},
				{"kr.co.citibank.citimobile","씨티은행"},
				{"kr.co.dgb.dgbfium","대구은행"},
				{"com.scbank.ma30","SC제일은행"},
				{"com.kbankwith.smartbank","케이뱅크"},
				{"com.kakaobank.channel", "카카오뱅크"},
				{"com.smg.mgnoti", "새마을금고"},
				{"co.kr.kdb.android.smartkdb", "산업은행"},
				{"com.suhyup.pesmb", "수협"},
				{"kr.co.jbbank.smartbank", "전북은행"},
				{"com.knb.bsp", "경남은행"},
				{"com.cu.sbank", "신협"},
		};
		filteredList = new ArrayList<>();
		boolean isExist = false;
		for(String[] packageName: pkgs){
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName[0]);
			if(intent==null){
				//미설치
				filteredList.add(new AppItem(context, packageName[1], packageName[0]));
			}else{
				//설치
				try {
					ApplicationInfo appinfo = context.getPackageManager().getApplicationInfo(packageName[0], PackageManager.GET_META_DATA);
					String name = context.getPackageManager().getApplicationLabel(appinfo).toString();
					filteredList.add(0, new AppItem(context, name, packageName[0]));
					isExist = true;
				}catch (Exception e){}
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
