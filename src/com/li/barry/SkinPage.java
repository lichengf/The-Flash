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

	// 左三角图片按钮
	private ImageButton themeLeft;
	// 右三角图片按钮
	private ImageButton themeRight;
	// 文字
	private TextView themeTitle;
	// 页数
	private TextView themePer;
	// 应用按钮
	private Button themeAPP_btn;
	// 用于实现多页面的切换效果
	private ViewPager viewpage;
	// 用来绑定数据的适配器
	private SkinPagerAdapter adapter;
	// 装分页显示的view的数组
	private List<View> views;
	// 记录当前选中位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "==onCreate");
		initActivity(this);
		init();
	}

	// 初始化操作
	private void init() {
		Log.i(TAG, "==init()");
		setContentView(R.layout.activity_skin_page);

		// 创建一个新的用于存放哥哥页面的数组
		views = new ArrayList<View>();

		LayoutInflater inflater = LayoutInflater.from(this);
		// 初始化引导图片列表，添加到视图列表中
		views.add(inflater.inflate(R.layout.slide_bg_view01, null));
		views.add(inflater.inflate(R.layout.slide_bg_view02, null));
		views.add(inflater.inflate(R.layout.slide_bg_view03, null));
		views.add(inflater.inflate(R.layout.slide_bg_view04, null));
		Log.i(TAG, "==views end");
		// 显示在控件位置
		viewpage = (ViewPager) findViewById(R.id.bg_viewpager);
		adapter = new SkinPagerAdapter(views, this);// 初始化adapter
		viewpage.setAdapter(adapter);// 设置适配器
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
		});// 绑定回调

		themeAPP_btn = (Button) findViewById(R.id.theme_app);
		themeLeft = (ImageButton) findViewById(R.id.theme_left);
		themeRight = (ImageButton) findViewById(R.id.theme_right);
		themeTitle = (TextView) findViewById(R.id.theme_title);
		themePer = (TextView) findViewById(R.id.them_per);

		// 设置背景
		themeAPP_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "==onClick()");
				Log.i(TAG, "==根据单击应用按钮，设置相应背景");
				String backgroundId = "bg_help" + (currentIndex + 1);
				Log.i(TAG, "backgroundId==" + backgroundId);
				setBgFatherId(backgroundId);
				Intent intent = new Intent(SkinPage.this, UserPage.class);
				SkinPage.this.startActivity(intent);
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);// 上往下推出效果
			}
		});
		
		//设置左三角按钮监听
		themeLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				viewpage.setCurrentItem(currentIndex - 1); 
			}
		});
		
		Snippet.setButtonFocusChanged(themeLeft);
		
		//设置右三角按钮监听
		themeRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				viewpage.setCurrentItem(currentIndex + 1 ); 
			}
		});
		
		Snippet.setButtonFocusChanged(themeRight);
	}
	/**
	 * 当前引导小点的选中
	 */
	private void setCurDot(int position){
		Log.i(TAG, "==setCurDot()");
		Log.i(TAG, "setCurDot()positon==" + position);
		if(position < 0 || position > 4 || currentIndex == position){
			Log.i(TAG, "==意外退出");
			return;
		}
		
		switch (position) {
		case 0:
			themeLeft.setVisibility(4);
			themeTitle.setText("超凡脱俗");
			themePer.setText("1/4");
			break;
		case 1:
			themeLeft.setVisibility(0);
			themeTitle.setText("清新自然");
			themePer.setText("2/4");
			break;
		case 2:
			themeTitle.setText("少年情怀");
			themePer.setText("3/4");
			themeRight.setVisibility(0);
			break;
		case 3:
			themeTitle.setText("延绵不绝");
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
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);// 上往下推出效果
	}
}
