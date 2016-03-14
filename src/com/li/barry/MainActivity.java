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
	/** �ж��Ƿ��ǵ�һ������ */
	private boolean isFirstIn = false;
	/** ��ת��������int�ͱ�� */
	private static final int GO_USER_FARST_PAGE = 1;
	/** ��ת������ҳ��int�ͱ�� */
	private static final int GO_GUIDE = 2;
	/** �����ӳ�ʱ�� */
	private static final long DELAY_MILLIS = 3000;
	/**
	 * ��Ҫ���ܣ���ת����ͬ�Ľ���
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
	 * ��������������Ҫ���ĳ�ʼ������
	 */
	private void init() {
		Log.i(TAG, "==init()");
		setContentView(R.layout.activity_main);
		/* ��ȡSharedPreferences����Ҫ������,�����ж��Ƿ����״����� */
		SharedPreferences preferences = getSharedPreferences("first",
				MODE_PRIVATE);
		/* ȡ����Ӧ��ֵ�����û�и�ֵ��˵����δд�룬��true��ΪĬ��ֵ */
		isFirstIn = preferences.getBoolean("isFirstIn", true);
		/* �ж��Ƿ��ǵ�һ�����У��ǵĻ���ת������ҳ,ʹ��Handler��postDelayed���� */
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
	 * ȥ����ҳ
	 */
	protected void goGuidePage() {
		Log.i(TAG, "==goGuidePage()");
		Intent intent = new Intent(this, GuidePage.class);
		startActivity(intent);
		this.finish();
	}

	/**
	 * ȥ�û���ҳ
	 */
	protected void goUserFirstPage() {
		Log.i(TAG, "==goUserFirstPage()");
		Intent intent = new Intent(this, UserPage.class);
		startActivity(intent);
		this.finish();
	}
	
}
