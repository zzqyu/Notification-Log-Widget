package com.dolapps.bank_noti_widget.widget;


import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableCompat;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.ui.BrowseActivity;
import com.dolapps.bank_noti_widget.ui.LockActivity;


/**
 * Implementation of App Widget functionality.
 */




public class BalanceWidget extends AppWidgetProvider {
    static RemoteViews views;
    static private SharedPreferences pref;
    public static final String ACTION_UNLOCK = "com.dolapps.bank_noti_widget.widget.UNLOCK";
    public static final String ACTION_LOCK = "com.dolapps.bank_noti_widget.widget.LOCK";
    public static final String ACTION_UNLOCK_SUCESS = "com.dolapps.bank_noti_widget.widget.UNLOCK_SUCESS";

    public static void updateAppWidgets(Context context){
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName testWidget = new ComponentName(context.getPackageName(), BalanceWidget.class.getName());
        int[] widgetIds = manager.getAppWidgetIds(testWidget);

        Log.i("BalanceWidget", "updateAppWidgets");
        for (int appWidgetId : widgetIds) {
            updateAppWidget(context, manager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.i("BalanceWidget", "updateAppWidget");
        pref= context.getSharedPreferences("bankNotiWidget", context.MODE_PRIVATE); // 선언

        boolean isLock = !pref.getString("password", "").equals("");

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.balance_list_widget);

        if(isLock){
            views.setViewVisibility(R.id.widget_bg, View.GONE);
            views.setViewVisibility(R.id.widget_lock_bg, View.VISIBLE);
        }
        else{
            views.setViewVisibility(R.id.widget_bg, View.VISIBLE);
            views.setViewVisibility(R.id.widget_lock_bg, View.GONE);
        }

        Intent intentUnlock = new Intent(context, BalanceWidget.class);
        intentUnlock.setAction(ACTION_UNLOCK);
        PendingIntent pendingUnlock = PendingIntent.getBroadcast(context,0, intentUnlock, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_lock_bg,pendingUnlock);

        Intent intentLock = new Intent(context, BalanceWidget.class);
        intentLock.setAction(ACTION_LOCK);
        PendingIntent pendingLock = PendingIntent.getBroadcast(context,0, intentLock, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_lock,pendingLock);


        int c1 = pref.getInt(Const.PREF_COLOR1, context.getColor(R.color.colorBackgroundWidget));
        int c2 = pref.getInt(Const.PREF_COLOR2, context.getColor(R.color.colorTextWidget));

        views.setTextViewText(R.id.widget_title, "은행별 잔고");//set app title on widget
        views.setTextColor(R.id.widget_title, c2);//set app title on widget
        views.setInt(R.id.widget_bg, "setBackgroundColor", c1);
        //TODO: set adapter to list
        views.setRemoteAdapter(R.id.widget_balance_list_view,new Intent(context,BalanceWidgetService.class));

        //TODO: launch browser activity
        views.setPendingIntentTemplate(R.id.widget_balance_list_view, PendingIntent.getActivity(context, appWidgetId,
                new Intent(context, BrowseActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));


        //TODO: Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widget_balance_list_view);

    }

    private  void widgetLayout(Context context, RemoteViews views,  AppWidgetManager manager, int widgetId ){

        Intent intentUnlock = new Intent(context, BalanceWidget.class);
        intentUnlock.setAction(ACTION_UNLOCK);
        PendingIntent pendingUnlock = PendingIntent.getBroadcast(context,0, intentUnlock, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_lock_bg,pendingUnlock);

        Intent intentLock = new Intent(context, BalanceWidget.class);
        intentLock.setAction(ACTION_LOCK);
        PendingIntent pendingLock = PendingIntent.getBroadcast(context,0, intentLock, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_lock,pendingLock);

        int c1 = pref.getInt(Const.PREF_COLOR1, context.getColor(R.color.colorBackgroundWidget));
        int c2 = pref.getInt(Const.PREF_COLOR2, context.getColor(R.color.colorTextWidget));

        views.setTextViewText(R.id.widget_title, "은행별 잔고");//set app title on widget
        views.setTextColor(R.id.widget_title, c2);//set app title on widget
        views.setInt(R.id.widget_lock, "setColorFilter", c2);
        views.setInt(R.id.widget_unlock, "setColorFilter", c2);
        views.setInt(R.id.widget_bg, "setBackgroundColor", c1);
        //TODO: set adapter to list
        views.setRemoteAdapter(R.id.widget_balance_list_view,new Intent(context,BalanceWidgetService.class));

        //TODO: launch browser activity
        views.setPendingIntentTemplate(R.id.widget_balance_list_view, PendingIntent.getActivity(context, widgetId,
                new Intent(context, BrowseActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));


        manager.updateAppWidget(widgetId, views);
        manager.notifyAppWidgetViewDataChanged(widgetId,R.id.widget_balance_list_view);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        // There may be multiple widgets active, so update all of them
        Log.i("BalanceWidget", "onUpdate");

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
        Log.i("BalanceWidget", "onEnabled");

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
        Log.i("BalanceWidget", "onDisabled");

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i("BalanceWidget", "onReceive");
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName testWidget = new ComponentName(context.getPackageName(), BalanceWidget.class.getName());
        int widgetId = manager.getAppWidgetIds(testWidget)[0];

        Log.i("onReceive", intent.getAction() );
        if(intent.getAction().equals(ACTION_UNLOCK)){
            Intent lock = new Intent(context, LockActivity.class);
            lock.putExtra("state", 2);
            lock.putExtra("isWidget", true);
            context.startActivity(lock.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
        else if(intent.getAction().equals(ACTION_LOCK)){
            if(pref.getString("password", "").equals("")){
                Toast.makeText(context, "은행잔고위젯에서 잠금기능을 설정하세요.", Toast.LENGTH_LONG).show();
            }
            else{
                views.setViewVisibility(R.id.widget_bg, View.GONE);
                views.setViewVisibility(R.id.widget_lock_bg, View.VISIBLE);
                widgetLayout(context, views, manager, widgetId);
            }
        }
        else if(intent.getAction().equals(ACTION_UNLOCK_SUCESS)){
            views.setViewVisibility(R.id.widget_bg, View.VISIBLE);
            views.setViewVisibility(R.id.widget_lock_bg, View.GONE);
            widgetLayout(context, views, manager, widgetId);
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i("BalanceWidget", "onDeleted");

    }
}

