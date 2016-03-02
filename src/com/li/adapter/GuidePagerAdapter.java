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
	// �����б�
	private List<View> views = null;
	// ����������
	private Context context = null;

	public GuidePagerAdapter(List<View> views, Context context) {
		this.views = views;
		this.context = context;
	}

	/*
	 * ����arg1λ�õĽ���(non-Javadoc)
	 */
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
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

	/*
	 * ��ʼ��arg1λ�õĽ���(non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view
	 * .View, int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.i(TAG, "==instantiateItem()");
		((ViewPager) container).addView(views.get(position), 0);
		if (position == views.size() - 1) {
			// �������һ������
			Log.i(TAG, "==�������һ������");
			Button button_btn = (Button) container
					.findViewById(R.id.button_btn);
			// ����ͼƬ��ť����,����ת����
			button_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences preferences = context
							.getSharedPreferences("first", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					//��������
					editor.putBoolean("isFirstIn", false);
					//�ύ�޸�
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
