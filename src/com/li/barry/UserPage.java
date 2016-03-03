package com.li.barry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.li.adapter.MenuAdapter;
import com.li.adapter.TextAdapter;
import com.li.adapter.UserPageAdapter;
import com.li.util.AppManager;
import com.li.util.Config;
import com.li.util.Constants;
import com.li.util.Eggs;
import com.li.util.Snippet;
import com.li.util.UpdateManager;

public class UserPage extends FatherActivity implements HttpGetDataListener,OnClickListener{

	private final String TAG = "UserPage";
	
	//主界面背景
	private ImageView imageView_user_bg;
	//显示引导页中所有页面的控件
	private ViewPager viewPager_user = null;
	//保存引导页布局
	private List<View> views;
	//适配viewpager的适配器
	private UserPageAdapter userPagerAdapter = null;
	
	/** 搜索界面的其它功能框 */
	private LinearLayout lin_user_search_other = null;
	private Button skin_btn;
	private Button settings_btn;
	private Button copyright_btn;
	private Button about_btn;
	private Button helper_btn;
	
	
	private HttpData httpData;
	long exitTime=-2000;
	private List<ListData> lists;
	private ListView lv;
	private EditText sendtext;
	private Button send_btn, speak_btn,choose_btn;
	private String content_str;
	private TextAdapter adapter;
	private String[] welcome_array;
	private double currentTime = 0, oldTime = 0;
	
	private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;
    
  //menu菜单模块
  	private PopupWindow popupWindow;
  	private ListView lv_menu;
  	private View view;
  	private List<String> Menus;
  	//判断网络
  	public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    
    private Toast mToast;
    
    /*add eggs by lichengfeng @20160303 begin */
	public static final int EGG_ARRAY_NUM = 1;
	public static int egg_object = 0;
	/*add eggs by lichengfeng @20160303 begin */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActivity(this);
		if(isNetWorkConnected()){
			initPager();
			initView();
		}else{
			/*add by lichengfeng to show toast immediately @20160219 begin*/
			if (mToast == null) {
				mToast = Toast.makeText(UserPage.this,R.string.network_unuseable,Toast.LENGTH_SHORT);
			} else {
				mToast.setText(R.string.network_unuseable);
			}
			mToast.show();
			/*add by lichengfeng to show toast immediately @20160219 end*/
			
			//send_btn.setActivated(false);
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			
		}
	}
	
	private void initPager(){
		setContentView(R.layout.activity_user_page);
		//初始化布局空间及监听
		viewPager_user = (ViewPager)findViewById(R.id.viewPager_user);
		imageView_user_bg = (ImageView)findViewById(R.id.imageView_user_bg);
		//初始化引导图片列表
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.user_first_page, null));
		views.add(inflater.inflate(R.layout.user_second_page, null));
		//初始化Adapter
		userPagerAdapter = new UserPageAdapter(views, this);
		viewPager_user.setAdapter(userPagerAdapter);
	}
	//判断网络是否可用
	public boolean isNetWorkConnected(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	

	
	@Override
	protected void onDestroy() {
		if (mDialog != null) {
            mDialog.dismiss();
        }
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)  
		{  

			if((System.currentTimeMillis()-exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000  
			{
				/*add by lichengfeng to show toast immediately @20160219 begin*/
				if (mToast == null) {
					mToast = Toast.makeText(UserPage.this,R.string.press_again_to_quit,Toast.LENGTH_SHORT);
				} else {
					mToast.setText(R.string.press_again_to_quit);
				}
				mToast.show();
				/*add by lichengfeng to show toast immediately @20160219 end*/
				exitTime = System.currentTimeMillis();  
			}  
			else  
			{  
				this.finish();  
				AppManager.getAppManager().AppExit(this);
				System.exit(0);  
			}  

			return true;  
		}  
		return true; // 最后，一定要做完以后返回 true，或者在弹出菜单后返回true，其他键返回super，让其他键默认

	}
	
	private void initView() {
		View firstView = views.get(0);//得到第一个页面view
		lv = (ListView) firstView.findViewById(R.id.lv);
		send_btn = (Button) firstView.findViewById(R.id.send_btn);
		speak_btn = (Button) firstView.findViewById(R.id.speak_btn);
		choose_btn= (Button) firstView.findViewById(R.id.choose_btn);
		sendtext = (EditText) firstView.findViewById(R.id.sendText);
		
		View secondView = views.get(1);//得到第二个页面view
		lin_user_search_other = (LinearLayout)secondView.findViewById(R.id.lin_user_search_other);
		skin_btn = (Button)secondView.findViewById(R.id.skin_btn);
		settings_btn = (Button)secondView.findViewById(R.id.settings_btn);
		copyright_btn = (Button)secondView.findViewById(R.id.copyright_btn);
		helper_btn = (Button)secondView.findViewById(R.id.helper_btn);
		about_btn = (Button)secondView.findViewById(R.id.about_btn);
		
		Snippet.setButtonFocusChanged(skin_btn);
		Snippet.setButtonFocusChanged(settings_btn);
		Snippet.setButtonFocusChanged(copyright_btn);
		Snippet.setButtonFocusChanged(helper_btn);
		Snippet.setButtonFocusChanged(about_btn);
		
		OnClickListener myClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int vId = v.getId();
				switch (vId) {
				case R.id.skin_btn:
					UserPage.this.startActivity(new Intent(UserPage.this,SkinPage.class));
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					break;
				case R.id.settings_btn:
					startActivity(new Intent(UserPage.this,SettingActivity.class));
					int version = Integer.valueOf(android.os.Build.VERSION.SDK);//activity切换效果
					if(version > 5 ){ 
						overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out); 
					}
					break;
				case R.id.copyright_btn:
//					Toast.makeText(getApplicationContext(), "版本检测", 1000).show();
					UpdateManager manager = new UpdateManager(UserPage.this);
					//检查软件更新
					manager.checkUpdate();
					break;
				case R.id.about_btn:
					/*add by lichengfeng to show toast immediately @20160219 begin*/
					if (mToast == null) {
						mToast = Toast.makeText(UserPage.this,R.string.text_about,Toast.LENGTH_SHORT);
					} else {
						mToast.setText(R.string.text_about);
					}
					mToast.show();
					/*add by lichengfeng to show toast immediately @20160219 end*/
					break;
				case R.id.helper_btn:
					/*add by lichengfeng to show toast immediately @20160219 begin*/
					if (mToast == null) {
						mToast = Toast.makeText(UserPage.this,R.string.text_help,Toast.LENGTH_SHORT);
					} else {
						mToast.setText(R.string.text_help);
					}
					mToast.show();
					/*add by lichengfeng to show toast immediately @20160219 end*/
					break;

				}
			}
		};
		
		skin_btn.setOnClickListener(myClickListener);
		settings_btn.setOnClickListener(myClickListener);
		copyright_btn.setOnClickListener(myClickListener);
		helper_btn.setOnClickListener(myClickListener);
		about_btn.setOnClickListener(myClickListener);
		
		String backgroudId = getBgFatherId();
		imageView_user_bg.setBackgroundResource(this.getImageId1(backgroudId));
		
		mRecognitionListener = new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                	sendtext.setText(rs.get(0));
                }
            }
        };
        
        
		lists = new ArrayList<ListData>();
		adapter = new TextAdapter(lists, this);
		lv.setAdapter(adapter);
		ListData listData;
		listData = new ListData(getRandomWelcomeTips(), ListData.RECEIVER,
				getTime());
		lists.add(listData);
	}

	public int getImageId1(String id){
		Class<R.drawable> cls = R.drawable.class;
		try {
			return cls.getDeclaredField(id).getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
			return R.drawable.bg_help1;
		}
	}
	
	private String getRandomWelcomeTips() {
		String welcome_tip = null;
		welcome_array = this.getResources()
				.getStringArray(R.array.welcome_tips);
		int index = (int) (Math.random() * (welcome_array.length - 1));
		welcome_tip = welcome_array[index];
		return welcome_tip;
	}

	public void parseText(String str) {
		try {
			JSONObject jb = new JSONObject(str);
			ListData listData;
			listData = new ListData(jb.getString("text"), ListData.RECEIVER,
					getTime());
			lists.add(listData);
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String getTime() {
		currentTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date curDate = new Date();
		String str = format.format(curDate);
		if (currentTime - oldTime >= 500) {
			oldTime = currentTime;
			return str;
		} else {
			return "";
		}

	}
	
	/*add eggs by lichengfeng begin @20160303 */
	Runnable eggThreadRunnable = new Runnable() {
		
		@Override
		public void run() {
			Intent intent = new Intent();
			intent.setClass(UserPage.this, Eggs.class);
			startActivity(intent);
		}
	};
	
	public String[] getEggsStrings(int egg_object) {
		String[] eggsStrings = null;
		if (egg_object == 0) {
			eggsStrings = getResources().getStringArray(R.array.egg_kiss);
		}
		return eggsStrings;
	}
	
	public boolean matcherEggsStrings(CharSequence s) {
		String[] eggsStrings;
		if (s == null) {
			return false;
		}
		for (int i = 0 ; i < EGG_ARRAY_NUM ; i++) {
			eggsStrings = getEggsStrings(i);
			for (int j = 0 ; j < eggsStrings.length ; j++) {
				Pattern p = Pattern.compile(eggsStrings[j]);
				Matcher m = p.matcher(s);
				if (m.find()) {
					egg_object = i;
					return true;
				}
			}
		}
		return false;
	}
	/*add eggs by lichengfeng end @20160303 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_btn:
			getTime();
			content_str = sendtext.getText().toString();
			if (content_str == null || content_str.length() <= 0) {
				/*add by lichengfeng to show toast immediately @20160219 begin*/
				if (mToast == null) {
					mToast = Toast.makeText(UserPage.this,R.string.input_cannot_empty,Toast.LENGTH_SHORT);
				} else {
					mToast.setText(R.string.input_cannot_empty);
				}
				mToast.show();
				/*add by lichengfeng to show toast immediately @20160219 end*/
			} else {
				sendtext.setText("");
				String dropk = content_str.replace(" ", "");
				/*add eggs by lichengfeng begin @20160303 */
				if (matcherEggsStrings((CharSequence) content_str)) {
					Thread eggThreads = new Thread(eggThreadRunnable);
					eggThreads.start();
				}
				/*add eggs by lichengfeng end @20160303 */
				String droph = dropk.replace("\n", "");
				ListData listData;
				listData = new ListData(content_str, ListData.SEND, getTime());
				lists.add(listData);
				if (lists.size() > 30) {
					for (int i = 0; i < lists.size(); i++) {
						lists.remove(i);
					}
				}
				adapter.notifyDataSetChanged();
				httpData = (HttpData) new HttpData(
						"http://www.tuling123.com/openapi/api?key=6af9822f5491fadfc142b53818bbd63a&info="
								+ droph, this).execute();
			}
			break;
		case R.id.speak_btn:
			Toast.makeText(getApplicationContext(), "sidiaole", 1000);
			sendtext.setText(null);
//          if (mDialog == null || mCurrentTheme != Config.DIALOG_THEME) {
              mCurrentTheme = Config.DIALOG_THEME;
              if (mDialog != null) {
                  mDialog.dismiss();
              }
              Bundle params = new Bundle();
              params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
              params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
              params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
              mDialog = new BaiduASRDigitalDialog(UserPage.this, params);
              mDialog.setDialogRecognitionListener(mRecognitionListener);
//          }
          mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
          mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                  Config.getCurrentLanguage());
          Log.e("DEBUG", "Config.PLAY_START_SOUND = "+Config.PLAY_START_SOUND);
          mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
          mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
          mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
          mDialog.show();
			break;
		case R.id.choose_btn:
			showMenu(v);
			break;
		}
		
	}

	private void showMenu(View parent) {
		WindowManager windowManager =  (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		if(popupWindow == null){
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.menu_list, null);
			lv_menu = (ListView) view.findViewById(R.id.lvMenu);
			
			// 加载数据  
            Menus = new ArrayList<String>();  
            Menus.add("语音聊天");  
            Menus.add("通用设置");
            Menus.add("个性装扮");
            Menus.add("使用介绍");
            Menus.add("关于");
            Menus.add("退出");
			
            MenuAdapter groupAdapter = new MenuAdapter(this, Menus);  
            lv_menu.setAdapter(groupAdapter);  
            // 创建一个PopuWidow对象  
            popupWindow = new PopupWindow(view,windowManager.getDefaultDisplay().getWidth()*3/7,windowManager.getDefaultDisplay().getHeight()*5/13);
		}
		
		// 使其聚集  
        popupWindow.setFocusable(true);  
        // 设置允许在外点击消失  
        popupWindow.setOutsideTouchable(true);    
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
        //WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        int xPos = windowManager.getDefaultDisplay().getWidth() / 20-80;
        int yPos = windowManager.getDefaultDisplay().getHeight()/20-21;
        popupWindow.showAsDropDown(parent, xPos, yPos);
        
        lv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,  
                    int position, long id) {
				if(Menus.get(position)=="语音聊天"){
					startActivity(new Intent(UserPage.this,FlashYuyin.class));
					int version = Integer.valueOf(android.os.Build.VERSION.SDK);//activity切换效果
					if(version > 5 ){
						overridePendingTransition(android.R.anim.fade_in,0); 
					}
				}
				if(Menus.get(position)=="通用设置"){
					startActivity(new Intent(UserPage.this,SettingActivity.class));
					int version = Integer.valueOf(android.os.Build.VERSION.SDK);//activity切换效果
					if(version > 5 ){ 
						overridePendingTransition(android.R.anim.fade_in,0); 
					}
				}
				if(Menus.get(position)=="个性装扮"){
					startActivity(new Intent(UserPage.this, SkinPage.class));
					overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
					
				}
			}
		});
	}

	@Override
	public void getDataUrl(String data) {
		parseText(data);
	}
}
