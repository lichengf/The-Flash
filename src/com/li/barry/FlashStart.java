package com.li.barry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FlashStart extends Activity{
	Handler handler=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		Runnable runnable=new Runnable(){
			@Override
			public void run() {
				startActivity(new Intent(FlashStart.this,UserPage.class));
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);
				if(version > 5 ){ 
					overridePendingTransition(android.R.anim.slide_in_left,0); 
					}
					finish();
			}
		};
		handler.postDelayed(runnable, 1400);
	}

	
}
