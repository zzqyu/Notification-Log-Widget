package org.hcilab.projects.nlogx.widget;


import android.content.Intent;
import android.widget.RemoteViewsService;


public class BalanceWidgetService extends RemoteViewsService  {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BalanceWidgetAdapter(getApplicationContext(),intent);
    }


}
