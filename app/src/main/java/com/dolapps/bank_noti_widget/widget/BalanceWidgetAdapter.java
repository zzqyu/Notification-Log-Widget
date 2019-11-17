package com.dolapps.bank_noti_widget.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.misc.NotiDatabaseHelper;
import com.dolapps.bank_noti_widget.misc.Util;

import java.text.DecimalFormat;
import java.util.HashMap;

import static com.dolapps.bank_noti_widget.ui.BrowseAdapter.iconCache;

public class BalanceWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    HashMap<String, HashMap<String, Long>> mItems = new HashMap<>();
    private HashMap<String, Long> item;
    private Intent intent;
    private Cursor cursor;
    private DecimalFormat df= new DecimalFormat("###,###");
    private SharedPreferences pref;


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
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }


    @Override
    public RemoteViews getViewAt(int i) {
        Log.i("BalanceWidgetAdapter", "getViewAt");

        String[] ks = (String[])mItems.keySet().toArray(new String[mItems.keySet().size()]);
        String key = ks[i];
        item = mItems.get(key);
        String appName = key.split("!!!!!!")[0];
        String packageName = key.split("!!!!!!")[1];

        //TODO: reference list item to set data.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.balance_widget_item);
        views.setTextViewText(R.id.widget_bank_name,appName);//set app name
        long allBalance = 0;
        String itemText = "";
        for(String account:item.keySet()){
            itemText += (account+"        "+ df.format(item.get(account)) + "원\n");
            allBalance+=item.get(account);
        }

        views.setTextViewText(R.id.widget_bank_balance, df.format(allBalance)+"원");//set app name
        views.setTextViewText(R.id.widget_item_text,itemText);//set preview/text

        //TODO: set app icon
        Drawable d = null;
        if(iconCache.containsKey(packageName) && iconCache.get(packageName) != null) {
            d = iconCache.get(packageName);
            assert d != null;
        } else {
            try {
                d = context.getPackageManager().getApplicationIcon(packageName);
            }catch (Exception e){
                d = context.getDrawable(R.mipmap.ic_launcher_round);
            }
        }
        views.setImageViewBitmap(R.id.item_icon, getBitmap(d));
        views.setOnClickFillInIntent(R.id.widget_item_root, intent);

        int c2 = pref.getInt(Const.PREF_COLOR2, context.getColor(R.color.colorTextWidget));
        views.setTextColor(R.id.widget_bank_name,c2);
        views.setTextColor(R.id.widget_bank_balance,c2);
        views.setTextColor(R.id.widget_item_text,c2);

        return views;
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

        try {
            NotiDatabaseHelper databaseHelper = new NotiDatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            mItems.clear();
            cursor = db.query(NotiDatabaseHelper.PostedEntry.TABLE_NAME,
                    new String[] {
                            NotiDatabaseHelper.PostedEntry.BANK,
                            NotiDatabaseHelper.PostedEntry.ACCOUNT,
                            NotiDatabaseHelper.PostedEntry.BALANCE
                    },null,null, null, null, NotiDatabaseHelper.PostedEntry.BANK+ " asc", null);

            if(cursor != null && cursor.moveToFirst()) {
                for(int i = 0; i < cursor.getCount(); i++) {
                    String packageName = cursor.getString(0);
                    if(pref.getBoolean(packageName, false)){
                        String appName = Util.getAppNameFromPackage(context, packageName, false);
                        String account = cursor.getString(1);
                        long balance = cursor.getLong(2);

                        String key = appName + "!!!!!!" + packageName;

                        if (!mItems.containsKey(key))
                            mItems.put(key, new HashMap<>());

                        if (!mItems.get(key).containsKey(account))
                            mItems.get(key).put(account, balance);

                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }

            db.close();
            databaseHelper.close();

        } catch (Exception e) {
            if(Const.DEBUG) e.printStackTrace();
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