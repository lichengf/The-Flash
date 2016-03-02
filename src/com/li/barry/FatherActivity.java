package com.li.barry;


import com.li.util.AppManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Window;

public class FatherActivity extends Activity{

	/**
	 * 初始化activity的一些信息，包括没有标题栏，把当前acivity设置到自定义的堆栈中去
	 * 
	 * @param activity
	 */
	public void initActivity(Activity activity) {
		AppManager.getAppManager().addActivity(activity);
		/* 设置为无标题栏并且为全屏模式 */
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	/**
	 * 获取背景id
	 * @return
	 */
	public String getBgFatherId(){
		SharedPreferences sharedata = getSharedPreferences("data2", 0);
		String data = sharedata.getString("item2", null);
		Log.v("cola","data2="+data);
		return data;
	}
	
	/**
	 * 设置背景 id
	 * @param bgFatherId
	 */
	public void setBgFatherId(String bgFatherId){
		Editor sharedata = getSharedPreferences("data2", 0).edit();
		sharedata.putString("item2", bgFatherId);
		sharedata.commit();
	}
}
