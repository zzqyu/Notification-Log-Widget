package com.dolapps.bank_noti_widget.misc;

import com.dolapps.bank_noti_widget.BuildConfig;

import java.util.HashMap;

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
	public static final String PREF_WIDGET_LOCK = "pref_lock";
	public static final String PREF_COLOR1 = "pref_color1";
	public static final String PREF_COLOR2 = "pref_color2";

	// Preferences not shown in the UI
	public static final String PREF_LAST_ACTIVITY  = "pref_last_activity";
	public static final String PREF_LAST_LOCATION  = "pref_last_location";

	public static final String PACKAGE_APPNAME = "{'com.kebhana.hanapush': '하나은행',	'com.kbstar.starpush': '국민은행',	'com.wr.alrim': '우리은행',	'com.nh.mobilenoti': '농협',"+
	"'com.IBK.SmartPush.app': '기업은행',	'kr.co.citibank.citimobile': '씨티은행',	'kr.co.dgb.dgbfium': '대구은행',	'com.scbank.ma30': 'SC제일은행',"+
	"'com.kbankwith.smartbank': '케이뱅크',	'com.kakaobank.channel': '카카오뱅크',	'com.smg.mgnoti': '새마을금고',	'co.kr.kdb.android.smartkdb': '산업은행',"+
	"'com.suhyup.pesmb': '수협',	'kr.co.jbbank.smartbank': '전북은행',	'com.knb.bsp': '경남은행',	'com.cu.sbank': '신협'}";
}