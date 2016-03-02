package com.li.barry;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.li.util.Config;
import com.li.util.Constants;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FlashYuyin extends Activity implements HttpGetDataListener,OnClickListener ,SpeechSynthesizerListener{
	
	private HttpData httpData;
	long exitTime=-2000;
	private Button buttonOne;
	private ImageView imageView;
	private AnimationDrawable animationDrawable;
	
	//录音
	private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;
    
    //播放
    private SpeechSynthesizer speechSynthesizer;
    private Handler uiHandler;
    
    private String return_text;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yuyin);
		
		buttonOne = (Button) findViewById(R.id.myButtonOne);
		imageView = (ImageView) findViewById(R.id.imageView);
		
		speechSynthesizer = new SpeechSynthesizer(getApplicationContext(),
                "holder", this);
        // 此处需要将setApiKey方法的两个参数替换为你在百度开发者中心注册应用所得到的apiKey和secretKey
		speechSynthesizer.setApiKey("0gOvSMi54CWlArSTOZaP6aqA", "593a0dbac0475dc94d430f4bf2e0e321");
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mRecognitionListener = new DialogRecognitionListener() {

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                	httpData = (HttpData) new HttpData(
              				"http://www.tuling123.com/openapi/api?key=6af9822f5491fadfc142b53818bbd63a&info="
              						+ rs.get(0),FlashYuyin.this).execute();
                }
                
           }
        };
	}
	
	@Override
	public void getDataUrl(String str) {
		try {
			JSONObject jb = new JSONObject(str);
			return_text = jb.getString("text");
			Toast.makeText(FlashYuyin.this, return_text, Toast.LENGTH_SHORT)
			.show();
			
			imageView.setImageResource(R.drawable.speakanimation);
            animationDrawable = (AnimationDrawable)imageView.getDrawable();
            animationDrawable.start();
            new Handler().postDelayed(new Runnable(){
            	public void run() {
            		imageView.setImageResource(R.drawable.moveanimation);
            		animationDrawable = (AnimationDrawable)imageView.getDrawable();
	                animationDrawable.start();
            		}
            	}, 4000);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					setParams();
					int ret = speechSynthesizer.speak(return_text.toString());
                    if (ret != 0) {
                    	Toast.makeText(FlashYuyin.this, "开始合成器失败：", Toast.LENGTH_SHORT)
            			.show();
                    }
					
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
    private void setParams() {
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, SpeechSynthesizer.AUDIO_ENCODE_AMR);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85);
//        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_LANGUAGE, SpeechSynthesizer.LANGUAGE_ZH);
//        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_NUM_PRON, "0");
//        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_ENG_PRON, "0");
//        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PUNC, "0");
//        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_BACKGROUND, "0");
//        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_STYLE, "0");
//        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TERRITORY, "0");
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myButtonOne:
//          if (mDialog == null || mCurrentTheme != Config.DIALOG_THEME) {
              mCurrentTheme = Config.DIALOG_THEME;
              if (mDialog != null) {
                  mDialog.dismiss();
              }
              Bundle params = new Bundle();
              params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
              params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
              params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
              mDialog = new BaiduASRDigitalDialog(this, params);
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

		default:
			break;
		}
		
	}

	@Override
	public void onBufferProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(SpeechSynthesizer arg0, SpeechError arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewDataArrive(SpeechSynthesizer arg0, byte[] arg1,
			boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechFinish(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechPause(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechResume(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechStart(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStartWorking(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSynthesizeFinish(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
