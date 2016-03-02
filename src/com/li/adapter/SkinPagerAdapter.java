package com.li.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ���������ݺ�view
 * @author lichengfeng
 *
 */

public class SkinPagerAdapter extends PagerAdapter{

	private final String TAG = "SkinPagerAdapter";
	//�����б�
	private List<View> views = null;
	//����������
	private Context context = null;
	
	public SkinPagerAdapter(List<View> views , Context context) {
		this.views = views;
		this.context = context;
	}
	
	/*
	 * ����arg1λ�õĽ���(non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.View,
	 * int, java.lang.Object)
	 */
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(views.get(position));
		
	}
	
	/*
	 * ��õ�ǰ������(non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(views.get(position));
		return views.get(position);
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

}
