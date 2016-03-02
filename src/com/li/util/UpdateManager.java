package com.li.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.li.util.ParseXmlService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager {

	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	
	//检测中
	private static final int TESTING = 1;
	//检测完毕
	private static final int TEST_COMPLIT = 2;
	
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	private Context context;
	
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			//正在下载
			case DOWNLOAD:
				//设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				//安装文件
				installApk();
				break;
			}
		}
	};
	
	private Handler tHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TESTING:
				Toast.makeText(context,"检测中...", Toast.LENGTH_LONG).show();
				break;
			case TEST_COMPLIT:
				if(isUpdate()){
					// 显示提示对话框
					showNoticeDialog();
				}else{
					Toast.makeText(context,com.li.barry.R.string.soft_update_no, Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
		
	};
	
	/**
	 * 安装apk文件
	 * @param context
	 */
	private void installApk() {
		File apkFile = new File(mSavePath,mHashMap.get("name"));
		if(!apkFile.exists()){
			return;
		}
		//通过Intent安装apk文件
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://"+apkFile.toString()), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	public UpdateManager(Context context) {
		this.context = context;
	}
	
	//检测软件更新
	public void checkUpdate(){
		tHandler.sendEmptyMessage(TESTING);
		new getVersionFromNet().start();
	}
	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		//构造对话框
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(com.li.barry.R.string.soft_update_title);
		builder.setMessage(com.li.barry.R.string.soft_update_info);
		//更新
		builder.setPositiveButton(com.li.barry.R.string.soft_update_updatebtn, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//显示下载对话框
				showDownloadDialog();
			}
		});
		//稍后更新
		builder.setNegativeButton(com.li.barry.R.string.soft_update_later, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	
	/**
	 * 显示软件下载对话框
	 * @return
	 */
	private void showDownloadDialog(){
		//构造软件下载对话框
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(com.li.barry.R.string.soft_updating);
		//给下载对话框添加进度条
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(com.li.barry.R.layout.softupdate_progress, null);
		mProgress = (ProgressBar)v.findViewById(com.li.barry.R.id.update_progress);
		builder.setView(v);
		//取消更新
		builder.setNegativeButton(com.li.barry.R.string.soft_update_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//设置取消状态
				cancelUpdate = true;
			}
		});
		
		mDownloadDialog = builder.create();
		/*add by lichengfeng for fix bug #1 @20160302 begin*/
		mDownloadDialog.setCanceledOnTouchOutside(false);
		/*add by lichengfeng for fix bug #1 @20160302 end */
		mDownloadDialog.show();
		//下载文件
		downloadApk();
	}
	
	private void downloadApk() {
		//启动新线程下载软件
		new downloadApkThread().start();
	}
	
	
	/**
	 * 下载文件线程
	 * 
	 * @return
	 */
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	}
	
	
	private class getVersionFromNet extends Thread{
		@Override
		public void run() {
			try {
				String path = context.getResources().getString(com.li.barry.R.string.serverUrl);
				URL version_url = new URL(path);
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) version_url.openConnection();
				conn.connect();
				// 创建输入流
				InputStream inStream = conn.getInputStream();
				ParseXmlService service = new ParseXmlService();
				mHashMap = service.parseXml(inStream);
				tHandler.sendEmptyMessage(DOWNLOAD_FINISH);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//检测软件是否有新版本
	private boolean isUpdate(){
		//获取当前软件版本
		int versionCode = getVersionCode(context);
		//把version.xml放到网络上，然后获取文件信息
		if(mHashMap!=null){
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
			//版本判断
			if(serviceCode > versionCode){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取当前版本号
	 */
	private int getVersionCode(Context context){
		int versionCode = 0;
		try {
			//获取软件版本号，对应AndroidMainfest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo("com.li.barry", 0).versionCode;
		} catch (Exception e) {
		}
		return versionCode;
	}
	
}
