package com.li.barry;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends FatherActivity{

	private final String TAG = "MainActivity";
	/** 判断是否是第一次运行 */
	private boolean isFirstIn = false;
	/** 跳转到主界面int型标记 */
	private static final int GO_USER_FARST_PAGE = 1;
	/** 跳转到引导页面int型标记 */
	private static final int GO_GUIDE = 2;
	/** 设置延迟时间 */
	private static final long DELAY_MILLIS = 3000;
	/**
	 * 主要功能，跳转到不同的界面
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Log.i(TAG, "==handleMessage()");
			switch (msg.what) {
			case GO_USER_FARST_PAGE:
				goUserFirstPage();
				break;
			case GO_GUIDE:
				goGuidePage();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "==onCreate()");
		initActivity(this); 
		init();
	}

	/**
	 * 软件在这个界面中要做的初始化操作
	 */
	private void init() {
		Log.i(TAG, "==init()");
		setContentView(R.layout.activity_main);
		/* 读取SharedPreferences中需要的数据,用于判断是否是首次运行 */
		SharedPreferences preferences = getSharedPreferences("first",
				MODE_PRIVATE);
		/* 取得相应的值，如果没有该值，说明还未写入，用true作为默认值 */
		isFirstIn = preferences.getBoolean("isFirstIn", true);
		/* 判断是否是第一次运行，是的话跳转到引导页,使用Handler的postDelayed方法 */
		if (isFirstIn) {
			setBgFatherId("bg_help1");
			/// add by lichengfeng for fix bug #4 add shortcut in launcher begin @20160314{
			createShortcut();
			///@20160314}
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, 0);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_USER_FARST_PAGE, DELAY_MILLIS);
		}

	}
	/*add by lichengfeng for fix bug #4 add shortcut in launcher begin @20160314*/
	private void createShortcut() {
		Log.i(TAG, "==createShortcut()");
		Intent shortCut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
		shortCut.putExtra("duplicate", false);
		ComponentName comp = new ComponentName("com.li.barry","com.li.barry.MainActivity");
		shortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.launcher);
		shortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		this.sendBroadcast(shortCut);
	}
	/*add by lichengfeng for fix bug #4 add shortcut in launcher end @20160314*/
	/**
	 * 去引导页
	 */
	protected void goGuidePage() {
		Log.i(TAG, "==goGuidePage()");
		Intent intent = new Intent(this, GuidePage.class);
		startActivity(intent);
		this.finish();
	}

	/**
	 * 去用户首页
	 */
	protected void goUserFirstPage() {
		Log.i(TAG, "==goUserFirstPage()");
		Intent intent = new Intent(this, UserPage.class);
		startActivity(intent);
		this.finish();
	}
	
}
