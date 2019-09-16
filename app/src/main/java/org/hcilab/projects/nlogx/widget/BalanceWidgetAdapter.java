package org.hcilab.projects.nlogx.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import org.hcilab.projects.nlogx.R;
import org.hcilab.projects.nlogx.misc.Const;
import org.hcilab.projects.nlogx.misc.DatabaseHelper;
import org.hcilab.projects.nlogx.misc.Util;
import org.hcilab.projects.nlogx.ui.BrowseAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.hcilab.projects.nlogx.ui.BrowseAdapter.iconCache;

public class BalanceWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    HashMap<String, HashMap<String, Long>> mItems = new HashMap<>();
    private HashMap<String, Long> item;
    private Intent intent;
    private Cursor cursor;
    private final static String DATA_LIMIT = "10000";



    BalanceWidgetAdapter(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        Toast.makeText(context,"Widget created.",Toast.LENGTH_SHORT).show();
        loadData();
    }

    @Override
    public void onDataSetChanged() {
        if(cursor != null){
            loadData();

        }
    }

    @Override
    public void onDestroy() {
        //close dataSource
        mItems.clear();
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }


    @Override
    public RemoteViews getViewAt(int i) {
        String[] ks = (String[])mItems.keySet().toArray(new String[mItems.keySet().size()]);
        String key = ks[i];
        item = mItems.get(key);
        String appName = key.split("!!!!!!")[0];
        String packageName = key.split("!!!!!!")[1];

        Log.i("dsadasdasd", appName + " " + packageName + " " + ks[i] );

        //TODO: reference list item to set data.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.balance_widget_item);
        views.setTextViewText(R.id.widget_bank_name,appName);//set app name
        long allBalance = 0;
        String itemText = "";
        for(String account:item.keySet()){
            itemText += (account+"        "+ item.get(account) + "원\n");
            allBalance+=item.get(account);
        }
        views.setTextViewText(R.id.widget_bank_balance, allBalance+"원");//set app name
        views.setTextViewText(R.id.widget_item_text,itemText);//set preview/text
        //지지고광운대점/출금/4,000원/잔액/30,440원/2019-09-16/18:33/244-******-39105
        //TODO: set app icon
        if(iconCache.containsKey(packageName) && iconCache.get(packageName) != null) {
            Drawable d = iconCache.get(packageName);
            assert d != null;
            views.setImageViewBitmap(R.id.item_icon,getBitmap(d));
        } else {
            views.setImageViewResource(R.id.item_icon,R.mipmap.ic_launcher_round);
        }


        views.setOnClickFillInIntent(R.id.widget_item_root, intent);

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


//    //TODO: fetch data from data source...
//    private void loadData(){
//        try {
//            DatabaseHelper databaseHelper = new DatabaseHelper(context);
//            SQLiteDatabase db = databaseHelper.getReadableDatabase();
//            mCollection.clear();
//            cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
//                    new String[] {
//                            DatabaseHelper.PostedEntry._ID,
//                            DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
//                    },
//                    DatabaseHelper.PostedEntry._ID + " < ?",
//                    new String[] {""+ Integer.MAX_VALUE},
//                    null,
//                    null,
//                    DatabaseHelper.PostedEntry._ID + " DESC",
//                    DATA_LIMIT);
//
//            if(cursor != null && cursor.moveToFirst()) {
//                for(int i = 0; i < cursor.getCount(); i++) {
//                    BrowseAdapter.DataItem dataItem = new BrowseAdapter.DataItem(context, cursor.getLong(0), cursor.getString(1));
//                    mCollection.add(dataItem);
//                    cursor.moveToNext();
//                }
//                cursor.close();
//            }
//
//            db.close();
//            databaseHelper.close();
//        } catch (Exception e) {
//            if(Const.DEBUG) e.printStackTrace();
//        }
//
//
//    }

    //TODO: fetch data from data source...
    private void loadData(){
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            mItems.clear();
            cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
                    new String[] {
                            DatabaseHelper.PostedEntry._ID,
                            DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
                    },
                    DatabaseHelper.PostedEntry._ID + " < ?",
                    new String[] {""+ Integer.MAX_VALUE},
                    null,
                    null,
                    DatabaseHelper.PostedEntry._ID + " DESC",
                    DATA_LIMIT);

            if(cursor != null && cursor.moveToFirst()) {
                for(int i = 0; i < cursor.getCount(); i++) {
                    JSONObject json = new JSONObject(cursor.getString(1));
                    String packageName = json.getString("packageName");
                    String appName = Util.getAppNameFromPackage(context, packageName, false);
                    String title = json.optString("title");
                    String text = json.optString("text");


                    //if(s==null || (s!=null && appName.contains(s))) {
                    Log.i("sadsadasdasd!", appName);
                    if(appName.contains("하나알리미")) {
                        Log.i("sadsadasdasd!", "!!!!");
                        String[] infoItems = text.split(" ");
                        String account =infoItems[7];
                        long balance =Long.parseLong(infoItems[4].replace(",", "").replace("원", ""));

                        String key = appName+"!!!!!!"+packageName;

                        if(!mItems.containsKey(key))
                            mItems.put(key, new HashMap<>());

                        if(!mItems.get(key).containsKey(account))
                            mItems.get(key).put(account, balance);

                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }

            db.close();
            databaseHelper.close();

            Log.i("sadsadasdasd!", mItems.containsKey("하나알리미")+"");

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