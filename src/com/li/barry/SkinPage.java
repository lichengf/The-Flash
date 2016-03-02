package com.li.barry;

import java.util.ArrayList;
import java.util.List;

import com.li.adapter.SkinPagerAdapter;
import com.li.util.Snippet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SkinPage extends FatherActivity {
	private final String TAG = "SkinPage";

	// ������ͼƬ��ť
	private ImageButton themeLeft;
	// ������ͼƬ��ť
	private ImageButton themeRight;
	// ����
	private TextView themeTitle;
	// ҳ��
	private TextView themePer;
	// Ӧ�ð�ť
	private Button themeAPP_btn;
	// ����ʵ�ֶ�ҳ����л�Ч��
	private ViewPager viewpage;
	// ���������ݵ�������
	private SkinPagerAdapter adapter;
	// װ��ҳ��ʾ��view������
	private List<View> views;
	// ��¼��ǰѡ��λ��
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "==onCreate");
		initActivity(this);
		init();
	}

	// ��ʼ������
	private void init() {
		Log.i(TAG, "==init()");
		setContentView(R.layout.activity_skin_page);

		// ����һ���µ����ڴ�Ÿ��ҳ�������
		views = new ArrayList<View>();

		LayoutInflater inflater = LayoutInflater.from(this);
		// ��ʼ������ͼƬ�б���ӵ���ͼ�б���
		views.add(inflater.inflate(R.layout.slide_bg_view01, null));
		views.add(inflater.inflate(R.layout.slide_bg_view02, null));
		views.add(inflater.inflate(R.layout.slide_bg_view03, null));
		views.add(inflater.inflate(R.layout.slide_bg_view04, null));
		Log.i(TAG, "==views end");
		// ��ʾ�ڿؼ�λ��
		viewpage = (ViewPager) findViewById(R.id.bg_viewpager);
		adapter = new SkinPagerAdapter(views, this);// ��ʼ��adapter
		viewpage.setAdapter(adapter);// ����������
		viewpage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				setCurDot(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});// �󶨻ص�

		themeAPP_btn = (Button) findViewById(R.id.theme_app);
		themeLeft = (ImageButton) findViewById(R.id.theme_left);
		themeRight = (ImageButton) findViewById(R.id.theme_right);
		themeTitle = (TextView) findViewById(R.id.theme_title);
		themePer = (TextView) findViewById(R.id.them_per);

		// ���ñ���
		themeAPP_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "==onClick()");
				Log.i(TAG, "==���ݵ���Ӧ�ð�ť��������Ӧ����");
				String backgroundId = "bg_help" + (currentIndex + 1);
				Log.i(TAG, "backgroundId==" + backgroundId);
				setBgFatherId(backgroundId);
				Intent intent = new Intent(SkinPage.this, UserPage.class);
				SkinPage.this.startActivity(intent);
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);// �������Ƴ�Ч��
			}
		});
		
		//���������ǰ�ť����
		themeLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				viewpage.setCurrentItem(currentIndex - 1); 
			}
		});
		
		Snippet.setButtonFocusChanged(themeLeft);
		
		//���������ǰ�ť����
		themeRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				viewpage.setCurrentItem(currentIndex + 1 ); 
			}
		});
		
		Snippet.setButtonFocusChanged(themeRight);
	}
	/**
	 * ��ǰ����С���ѡ��
	 */
	private void setCurDot(int position){
		Log.i(TAG, "==setCurDot()");
		Log.i(TAG, "setCurDot()positon==" + position);
		if(position < 0 || position > 4 || currentIndex == position){
			Log.i(TAG, "==�����˳�");
			return;
		}
		
		switch (position) {
		case 0:
			themeLeft.setVisibility(4);
			themeTitle.setText("��������");
			themePer.setText("1/4");
			break;
		case 1:
			themeLeft.setVisibility(0);
			themeTitle.setText("������Ȼ");
			themePer.setText("2/4");
			break;
		case 2:
			themeTitle.setText("�����黳");
			themePer.setText("3/4");
			themeRight.setVisibility(0);
			break;
		case 3:
			themeTitle.setText("���಻��");
			themePer.setText("4/4");
			themeRight.setVisibility(4);
			break;
		}
		currentIndex = position;
		Log.i(TAG, "setCurDot()==end");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);// �������Ƴ�Ч��
	}
}
