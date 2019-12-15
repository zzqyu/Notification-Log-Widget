package com.dolapps.bank_noti_widget.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.ui.BrowseAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class BalanceWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private ArrayList<BrowseAdapter.Item> mItems;
    private HashMap<String, ArrayList<Integer>> indexMap;
    private HashMap<String, Pair<Long, String>> balances;
    private Intent intent;
    private DecimalFormat df= new DecimalFormat("###,###");
    private SharedPreferences pref;
    private long total = 0;


    BalanceWidgetAdapter(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        Log.i("BalanceWidgetAdapter", "BalanceWidgetAdapter");
        pref= context.getSharedPreferences("bankNotiWidget", context.MODE_PRIVATE); // 선언

    }

    @Override
    public void onCreate() {
        Log.i("BalanceWidgetAdapter", "onCreate");
        Toast.makeText(context,"Widget created.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataSetChanged() {
        Log.i("BalanceWidgetAdapter", "onDataSetChanged");
        loadData();
    }

    @Override
    public void onDestroy() {
        Log.i("BalanceWidgetAdapter", "onDestroy");

        //close dataSource
        mItems.clear();
        indexMap.clear();
        balances.clear();
    }

    @Override
    public int getCount() {
        return indexMap != null ? indexMap.size() : 0;
    }


    @Override
    public RemoteViews getViewAt(int i) {
        Log.i("BalanceWidgetAdapter", "getViewAt");
        if(indexMap!=null){
            String[] keys = ((String[])indexMap.keySet().toArray(new String[indexMap.keySet().size()]));
            String packageName = keys[i];
            String appName = mItems.get(indexMap.get(packageName).get(0)).getAppName();

            //TODO: reference list item to set data.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.balance_widget_item);
            views.setTextViewText(R.id.widget_bank_name, appName);//set app name
            Pair<Long, String> tmp = balances.get(packageName);


            views.setTextViewText(R.id.widget_bank_balance, df.format(tmp.first) + "원");//set app name
            views.setTextViewText(R.id.widget_item_text, tmp.second);//set preview/text


            //TODO: set app icon
            Drawable d = null;
            try {
                d = context.getPackageManager().getApplicationIcon(packageName);
            } catch (Exception e) {
                d = context.getDrawable(R.mipmap.ic_launcher_round);
            }
            views.setImageViewBitmap(R.id.item_icon, getBitmap(d));
            views.setOnClickFillInIntent(R.id.widget_item_root, intent);

            int c2 = pref.getInt(Const.PREF_COLOR2, context.getColor(R.color.colorTextWidget));
            views.setTextColor(R.id.widget_bank_name, c2);
            views.setTextColor(R.id.widget_bank_balance, c2);
            views.setTextColor(R.id.widget_item_text, c2);
            return views;
        }
        else return null;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



    //TODO: fetch data from data source...
    private void loadData(){
        Log.i("BalanceWidgetAdapter", "loadData");
        total = 0 ;
        indexMap = new HashMap<>();
        balances = new HashMap<>();
        mItems = NotiDatabaseHelper.getAccountList(context, false);
        for(int i =0; i < mItems.size(); i++){
            BrowseAdapter.Item item = mItems.get(i);
            total += item.getBalance();
            String alias = (item.getAlias()==null||item.getAlias().trim().equals(""))?item.getAccount():item.getAlias();
            if(!indexMap.containsKey(item.getPackageName())){
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(i);
                indexMap.put(item.getPackageName(), tmp);
                balances.put(item.getPackageName(),
                        new Pair<>(item.getBalance(), (alias+"        "+ df.format(item.getBalance()) + "원\n")));
            }
            else{
                Pair<Long, String> tmp = balances.get(item.getPackageName());
                indexMap.get(item.getPackageName()).add(i);
                balances.put(item.getPackageName(),
                        new Pair<>(item.getBalance()+tmp.first, tmp.second+(alias+"        "+ df.format(item.getBalance()) + "원\n")));
            }
        }

    }

    /**
     * converts drawable to bitmap using ARGB_8888
     * @param drawable : the drawable to draw into bitmap
     * @return bitmap
     */
    private Bitmap getBitmap(Drawable drawable){
        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error here
            return null;
        }
    }

}