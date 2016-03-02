package com.li.barry;

import java.util.ArrayList;
import java.util.List;

import com.li.adapter.GuidePagerAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;

public class GuidePage extends FatherActivity{
	public final String TAG = "GuidePage";
	
	//��ʾ����ҳ������ҳ��Ŀռ�
	private ViewPager viewPager_guidance = null;
	//��������ҳ����
	private List<View> views;
	//����viewpager��������
	private GuidePagerAdapter guidePagerAdapter = null;
	//ҳ���еĵ�
	private LinearLayout linear_guidance_dots = null;
	//�ײ�ͼƬ��Բ���ͼƬ
	private ImageView[] dots;
	//��¼��ǰѡ��λ��
	private int currentIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		initActivity(this);
		initViews();
		initDots();
	}
	
	/**
	 * ��ʼ���ײ�С��
	 */
	private void initDots() {
		Log.i(TAG, "==initDots()");

		LinearLayout linear_guidance_dots = (LinearLayout) findViewById(R.id.linear_guidance_dots);
		dots = new ImageView[views.size()];
		for (int i = 0; i < views.size(); i++) {// ѭ��ȡ��С��ͼƬ
			dots[i] = (ImageView) linear_guidance_dots.getChildAt(i);
			dots[i].setEnabled(true);// ����Ϊ��ɫ
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// ����Ϊ��ɫ����ѡ��״̬
	}
	
	//��ʼ������
	private void initViews(){
		Log.i(TAG, "==initViews()");
		setContentView(R.layout.activity_guide);
		//��ʼ�����ֿؼ�������
		viewPager_guidance = (ViewPager) findViewById(R.id.viewPager_guidance);
		//��ʼ������ͼƬ�б�
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.guide_one, null));
		views.add(inflater.inflate(R.layout.guide_two, null));
		views.add(inflater.inflate(R.layout.guide_three, null));
		views.add(inflater.inflate(R.layout.guide_four, null));
		
		//��ʼ��Adapter
		guidePagerAdapter = new GuidePagerAdapter(views, this);
		viewPager_guidance.setAdapter(guidePagerAdapter);
		viewPager_guidance.setOnPageChangeListener(new OnPageChangeListener() {
			
			/*
			 * �µ�ҳ�汻ѡ��ʱ����(non-Javadoc)
			 * 
			 * @see
			 * android.support.v4.view.ViewPager.OnPageChangeListener
			 * #onPageSelected(int)
			 */
			@Override
			public void onPageSelected(int position) {
				if(position < 0 || position > views.size() - 1 || currentIndex == position){
					return ;
				}
					dots[position].setEnabled(false);
					dots[currentIndex].setEnabled(true);
					currentIndex = position;
			}
			
			
			/*
			 * ��ǰҳ�汻����ʱ����(non-Javadoc)
			 * 
			 * @see
			 * android.support.v4.view.ViewPager.OnPageChangeListener
			 * #onPageScrolled(int, float, int)
			 */
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			/*
			 * ����״̬�ı�ʱ����(non-Javadoc)
			 * 
			 * @see
			 * android.support.v4.view.ViewPager.OnPageChangeListener
			 * #onPageScrollStateChanged(int)
			 */
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
	}
	

}
