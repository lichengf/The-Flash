package com.li.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
public class UserPageAdapter extends PagerAdapter{

	private final String TAG = "UserPageAdapter";
	//界面列表
	private List<View> views = null;
	//上下文
	private Context context = null;
	
	public UserPageAdapter(List<View> views,Context context) {
		this.views = views;
		this.context = context;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}
	
	/*
	 * 初始化arg1位置的界面(non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view
	 * .View, int)
	 */
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(views.get(position),0);
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return (arg0 == arg1);
	}

}
