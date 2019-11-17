package com.dolapps.bank_noti_widget.misc;

import com.dolapps.bank_noti_widget.BuildConfig;

public class Const {

	public static final boolean DEBUG = BuildConfig.DEBUG;
	public static final long VERSION  = BuildConfig.VERSION_CODE;

	// Feature flags
	public static final boolean ENABLE_ACTIVITY_RECOGNITION = true;
	public static final boolean ENABLE_LOCATION_SERVICE     = true;

	// Preferences shown in the UI
	public static final String PREF_STATUS  = "pref_status";
	public static final String PREF_APPLIST = "pref_applist";
	public static final String PREF_BROWSE  = "pref_browse";
	public static final String PREF_ABOUT   = "pref_about";
	public static final String PREF_VERSION = "pref_version";
	public static final String PREF_COLOR1 = "pref_color1";
	public static final String PREF_COLOR2 = "pref_color2";

	// Preferences not shown in the UI
	public static final String PREF_LAST_ACTIVITY  = "pref_last_activity";
	public static final String PREF_LAST_LOCATION  = "pref_last_location";

}