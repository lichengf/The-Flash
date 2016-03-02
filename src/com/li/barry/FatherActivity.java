package com.li.barry;


import com.li.util.AppManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Window;

public class FatherActivity extends Activity{

	/**
	 * ��ʼ��activity��һЩ��Ϣ������û�б��������ѵ�ǰacivity���õ��Զ���Ķ�ջ��ȥ
	 * 
	 * @param activity
	 */
	public void initActivity(Activity activity) {
		AppManager.getAppManager().addActivity(activity);
		/* ����Ϊ�ޱ���������Ϊȫ��ģʽ */
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	/**
	 * ��ȡ����id
	 * @return
	 */
	public String getBgFatherId(){
		SharedPreferences sharedata = getSharedPreferences("data2", 0);
		String data = sharedata.getString("item2", null);
		Log.v("cola","data2="+data);
		return data;
	}
	
	/**
	 * ���ñ��� id
	 * @param bgFatherId
	 */
	public void setBgFatherId(String bgFatherId){
		Editor sharedata = getSharedPreferences("data2", 0).edit();
		sharedata.putString("item2", bgFatherId);
		sharedata.commit();
	}
}
