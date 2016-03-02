package com.li.barry;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service {

	private Context mContext;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = getApplicationContext();
        Intent intent = new Intent();
        intent.putExtra("guest_fp_delete", true);
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}



	public void test() {
		
	}

}
