package com.li.barry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.li.adapter.MenuAdapter;
import com.li.adapter.TextAdapter;
import com.li.adapter.UserPageAdapter;
import com.li.util.AppManager;
import com.li.util.Config;
import com.li.util.Constants;
import com.li.util.Snippet;
import com.li.util.UpdateManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
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

public class UserPage extends FatherActivity implements HttpGetDataListener,OnClickListener{

	private final String TAG = "UserPage";
	
	//�����汳��
	private ImageView imageView_user_bg;
	//��ʾ����ҳ������ҳ��Ŀؼ�
	private ViewPager viewPager_user = null;
	//��������ҳ����
	private List<View> views;
	//����viewpager��������
	private UserPageAdapter userPagerAdapter = null;
	
	/** ����������������ܿ� */
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
    
  //menu�˵�ģ��
  	private PopupWindow popupWindow;
  	private ListView lv_menu;
  	private View view;
  	private List<String> Menus;
  	//�ж�����
  	public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    
    private Toast mToast;
	
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
		//��ʼ�����ֿռ估����
		viewPager_user = (ViewPager)findViewById(R.id.viewPager_user);
		imageView_user_bg = (ImageView)findViewById(R.id.imageView_user_bg);
		//��ʼ������ͼƬ�б�
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.user_first_page, null));
		views.add(inflater.inflate(R.layout.user_second_page, null));
		//��ʼ��Adapter
		userPagerAdapter = new UserPageAdapter(views, this);
		viewPager_user.setAdapter(userPagerAdapter);
	}
	//�ж������Ƿ����
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

			if((System.currentTimeMillis()-exitTime) > 2000)  //System.currentTimeMillis()���ۺ�ʱ���ã��϶�����2000  
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
		return true; // ���һ��Ҫ�����Ժ󷵻� true�������ڵ����˵��󷵻�true������������super����������Ĭ��

	}
	
	private void initView() {
		View firstView = views.get(0);//�õ���һ��ҳ��view
		lv = (ListView) firstView.findViewById(R.id.lv);
		send_btn = (Button) firstView.findViewById(R.id.send_btn);
		speak_btn = (Button) firstView.findViewById(R.id.speak_btn);
		choose_btn= (Button) firstView.findViewById(R.id.choose_btn);
		sendtext = (EditText) firstView.findViewById(R.id.sendText);
		
		View secondView = views.get(1);//�õ��ڶ���ҳ��view
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
					int version = Integer.valueOf(android.os.Build.VERSION.SDK);//activity�л�Ч��
					if(version > 5 ){ 
						overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out); 
					}
					break;
				case R.id.copyright_btn:
//					Toast.makeText(getApplicationContext(), "�汾���", 1000).show();
					UpdateManager manager = new UpdateManager(UserPage.this);
					//�����������
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
			
			// ��������  
            Menus = new ArrayList<String>();  
            Menus.add("��������");  
            Menus.add("ͨ������");
            Menus.add("����װ��");
            Menus.add("ʹ�ý���");
            Menus.add("����");
            Menus.add("�˳�");
			
            MenuAdapter groupAdapter = new MenuAdapter(this, Menus);  
            lv_menu.setAdapter(groupAdapter);  
            // ����һ��PopuWidow����  
            popupWindow = new PopupWindow(view,windowManager.getDefaultDisplay().getWidth()*3/7,windowManager.getDefaultDisplay().getHeight()*5/13);
		}
		
		// ʹ��ۼ�  
        popupWindow.setFocusable(true);  
        // ����������������ʧ  
        popupWindow.setOutsideTouchable(true);    
        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı���  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
        //WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);  
        int xPos = windowManager.getDefaultDisplay().getWidth() / 20-80;
        int yPos = windowManager.getDefaultDisplay().getHeight()/20-21;
        popupWindow.showAsDropDown(parent, xPos, yPos);
        
        lv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,  
                    int position, long id) {
				if(Menus.get(position)=="��������"){
					startActivity(new Intent(UserPage.this,FlashYuyin.class));
					int version = Integer.valueOf(android.os.Build.VERSION.SDK);//activity�л�Ч��
					if(version > 5 ){
						overridePendingTransition(android.R.anim.fade_in,0); 
					}
				}
				if(Menus.get(position)=="ͨ������"){
					startActivity(new Intent(UserPage.this,SettingActivity.class));
					int version = Integer.valueOf(android.os.Build.VERSION.SDK);//activity�л�Ч��
					if(version > 5 ){ 
						overridePendingTransition(android.R.anim.fade_in,0); 
					}
				}
				if(Menus.get(position)=="����װ��"){
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