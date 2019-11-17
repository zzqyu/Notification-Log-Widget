package com.dolapps.bank_noti_widget.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.dolapps.bank_noti_widget.R;
import com.dolapps.bank_noti_widget.misc.Const;
import com.dolapps.bank_noti_widget.ui.BrowseActivity;


/**
 * Implementation of App Widget functionality.
 */




public class BalanceWidget extends AppWidgetProvider {
    static RemoteViews views;
    static private SharedPreferences pref;

    public static void updateAppWidgets(Context context){
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName testWidget = new ComponentName(context.getPackageName(), BalanceWidget.class.getName());
        int[] widgetIds = manager.getAppWidgetIds(testWidget);
        for (int appWidgetId : widgetIds) {
            updateAppWidget(context, manager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.i("BalanceWidget", "updateAppWidget");
        pref= context.getSharedPreferences("bankNotiWidget", context.MODE_PRIVATE); // 선언

        int c1 = pref.getInt(Const.PREF_COLOR1, context.getColor(R.color.colorBackgroundWidget));
        int c2 = pref.getInt(Const.PREF_COLOR2, context.getColor(R.color.colorTextWidget));

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.balance_list_widget);

        views.setTextViewText(R.id.widget_title, "은행별 잔고");//set app title on widget
        views.setTextColor(R.id.widget_title, c2);//set app title on widget
        views.setInt(R.id.widget_bg, "setBackgroundColor", c1);
        //TODO: set adapter to list
        views.setRemoteAdapter(R.id.widget_balance_list_view,new Intent(context,BalanceWidgetService.class));

        //TODO: launch browser activity
        views.setPendingIntentTemplate(R.id.widget_balance_list_view, PendingIntent.getActivity(context, appWidgetId,
                new Intent(context, BrowseActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));


        //TODO: launch main activity on title widget clicked
        // views.setOnClickPendingIntent(R.id.appwidget_text, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));

        //TODO: Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widget_balance_list_view);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}

