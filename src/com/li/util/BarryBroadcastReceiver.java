package com.li.util;
/**
*add by lichengfeng start barry from dialer use *#*#7758521#*#*
*/
import com.li.barry.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class BarryBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "BarryBroadcastReceiver";

	private static final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
	private final Uri mEmUri = Uri.parse("android_secret_code://7758521");
	@Override
	public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            Log.i(TAG, "Null action");
            return;
        }
        if (intent.getAction().equals(SECRET_CODE_ACTION)) {
            Uri uri = intent.getData();
            Log.i(TAG, "getIntent success in if");
            if (uri.equals(mEmUri)) {
                Intent intentEm = new Intent(context, MainActivity.class);
                intentEm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i(TAG, "Before start EM activity");
                context.startActivity(intentEm);
            } else {
                Log.i(TAG, "No matched URI!");
            }
        } else {
            Log.i(TAG, "Not SECRET_CODE_ACTION!");
        }
	}

}
