package com.li.util;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Eggs extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "Eggs Activity...", Toast.LENGTH_SHORT);
	}
}
