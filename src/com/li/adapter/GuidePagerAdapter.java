package com.li.adapter;

import java.util.List;

import com.li.barry.R;
import com.li.barry.UserPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class GuidePagerAdapter extends PagerAdapter {

	private final String TAG = "GuidePagerAdapter";
	// 界面列表
	private List<View> views = null;
	// 上下文内容
	private Context context = null;

	public GuidePagerAdapter(List<View> views, Context context) {
		this.views = views;
		this.context = context;
	}

	/*
	 * 销毁arg1位置的界面(non-Javadoc)
	 */
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	/*
	 * 获得当前界面数(non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return views.size();
	}

	/*
	 * 初始化arg1位置的界面(non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view
	 * .View, int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.i(TAG, "==instantiateItem()");
		((ViewPager) container).addView(views.get(position), 0);
		if (position == views.size() - 1) {
			// 若是最后一个界面
			Log.i(TAG, "==这是最后一个界面");
			Button button_btn = (Button) container
					.findViewById(R.id.button_btn);
			// 设置图片按钮监听,做跳转操作
			button_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences preferences = context
							.getSharedPreferences("first", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					//存入数据
					editor.putBoolean("isFirstIn", false);
					//提交修改
					editor.commit();
					Intent intent = new Intent(context, UserPage.class);
					context.startActivity(intent);
					((Activity)context).finish();
				}
			});
		}
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

}
