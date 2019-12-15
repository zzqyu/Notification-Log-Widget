package com.dolapps.bank_noti_widget.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.widget.BalanceWidget;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseViewHolder>  implements ItemTouchHelperListener{

	private Context context;
	private ArrayList<Item> data = new ArrayList<>();
	private Handler handler = new Handler();

	public BrowseAdapter(Context context) {
		this.context = context;
		loadMore();
	}

	@NonNull
	@Override
	public BrowseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse, parent, false);
		BrowseViewHolder vh = new BrowseViewHolder(view);

		return vh;
	}

	@Override
	public void onBindViewHolder(@NonNull BrowseViewHolder vh, int position) {
		Item item = data.get(position);

		try {
			vh.icon.setImageDrawable(context.getPackageManager().getApplicationIcon(item.getPackageName()));
		}
		catch (Exception e){
			vh.icon.setImageResource(R.mipmap.ic_launcher_round);
		}
		String alias = (item.getAlias()==null||item.getAlias().trim().equals(""))?item.getAccount():item.getAlias();

		vh.name.setText(item.getAppName());
		vh.text.setVisibility(View.VISIBLE);
		vh.text.setText(alias + " " + new DecimalFormat("###,###").format(item.getBalance()) + "원");

		vh.item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("계좌 별칭 설정");
				builder.setMessage("입력란을 비우고 변경하면 계좌번호로 설정됩니다. ");

				// Set up the input
				final EditText input = new EditText(context);
				// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
				input.setInputType(InputType.TYPE_CLASS_TEXT);
				input.setHint("계좌 별칭 입력(ex. 생활비 통장)");
				builder.setView(input);

				// Set up the buttons
				builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!input.getText().toString().trim().equals("")) {
							data.get(position).setAlias(input.getText().toString());
						}
						else {
							data.get(position).setAlias(null);
						}
						NotiDatabaseHelper.replaceAccountDBItem(context, data.get(position), position);
						notifyDataSetChanged();
						BalanceWidget.updateAppWidgets(context);
					}
				});
				builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

				builder.show();
			}
		});
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	private void loadMore() {

		data = NotiDatabaseHelper.getAccountList(context, true);

		handler.post(() -> notifyDataSetChanged());
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		if(fromPosition < 0 || fromPosition >= getItemCount() || toPosition < 0 || toPosition >= getItemCount()){
			return false;
		}

		Item fromItem = data.get(fromPosition);
		data.remove(fromPosition);
		data.add(toPosition, fromItem);

		notifyItemMoved(fromPosition, toPosition);
		NotiDatabaseHelper.relocationAccountDBItem(context, data);
		return true;
	}

	@Override
	public void onItemRemove(int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("삭제");
		builder.setMessage("삭제하시겠습니까?");

		builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				data.remove(position);
				notifyItemRemoved(position);
				NotiDatabaseHelper.relocationAccountDBItem(context, data);
				BalanceWidget.updateAppWidgets(context);
			}
		});

		builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				//Toast.makeText(getApplicationContext(), "NO 클릭", Toast.LENGTH_SHORT).show();
				notifyDataSetChanged();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();


	}

	public static class Item {

		private String packageName;
		private String appName;
		private String account;
		private String alias;
		private long balance;

		public Item(String packageName, String appName, String account, String alias, long balance) {
			this.packageName = packageName;
			this.appName = appName;
			this.account = account;
			this.balance = balance;
			this.alias = alias;

		}

		public String getPackageName() {
			return packageName;
		}

		public String getAppName() {
			return appName;
		}
		public String getAccount() {
			return account;
		}
		public String getAlias() {
			return alias;
		}
		public long getBalance() {
			return balance;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}
	}

}
