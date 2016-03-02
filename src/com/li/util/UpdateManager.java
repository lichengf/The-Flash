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

	/* ������ */
	private static final int DOWNLOAD = 1;
	/* ���ؽ��� */
	private static final int DOWNLOAD_FINISH = 2;
	
	//�����
	private static final int TESTING = 1;
	//������
	private static final int TEST_COMPLIT = 2;
	
	/* ���������XML��Ϣ */
	HashMap<String, String> mHashMap;
	private Context context;
	
	/* �Ƿ�ȡ������ */
	private boolean cancelUpdate = false;
	/* ���ر���·�� */
	private String mSavePath;
	/* ��¼���������� */
	private int progress;
	/* ���½����� */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			//��������
			case DOWNLOAD:
				//���ý�����λ��
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				//��װ�ļ�
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
				Toast.makeText(context,"�����...", Toast.LENGTH_LONG).show();
				break;
			case TEST_COMPLIT:
				if(isUpdate()){
					// ��ʾ��ʾ�Ի���
					showNoticeDialog();
				}else{
					Toast.makeText(context,com.li.barry.R.string.soft_update_no, Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
		
	};
	
	/**
	 * ��װapk�ļ�
	 * @param context
	 */
	private void installApk() {
		File apkFile = new File(mSavePath,mHashMap.get("name"));
		if(!apkFile.exists()){
			return;
		}
		//ͨ��Intent��װapk�ļ�
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://"+apkFile.toString()), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	public UpdateManager(Context context) {
		this.context = context;
	}
	
	//����������
	public void checkUpdate(){
		tHandler.sendEmptyMessage(TESTING);
		new getVersionFromNet().start();
	}
	/**
	 * ��ʾ������¶Ի���
	 */
	private void showNoticeDialog() {
		//����Ի���
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(com.li.barry.R.string.soft_update_title);
		builder.setMessage(com.li.barry.R.string.soft_update_info);
		//����
		builder.setPositiveButton(com.li.barry.R.string.soft_update_updatebtn, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//��ʾ���ضԻ���
				showDownloadDialog();
			}
		});
		//�Ժ����
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
	 * ��ʾ������ضԻ���
	 * @return
	 */
	private void showDownloadDialog(){
		//����������ضԻ���
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(com.li.barry.R.string.soft_updating);
		//�����ضԻ�����ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(com.li.barry.R.layout.softupdate_progress, null);
		mProgress = (ProgressBar)v.findViewById(com.li.barry.R.id.update_progress);
		builder.setView(v);
		//ȡ������
		builder.setNegativeButton(com.li.barry.R.string.soft_update_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//����ȡ��״̬
				cancelUpdate = true;
			}
		});
		
		mDownloadDialog = builder.create();
		/*add by lichengfeng for fix bug #1 @20160302 begin*/
		mDownloadDialog.setCanceledOnTouchOutside(false);
		/*add by lichengfeng for fix bug #1 @20160302 end */
		mDownloadDialog.show();
		//�����ļ�
		downloadApk();
	}
	
	private void downloadApk() {
		//�������߳��������
		new downloadApkThread().start();
	}
	
	
	/**
	 * �����ļ��߳�
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
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do
					{
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
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
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	}
	
	
	private class getVersionFromNet extends Thread{
		@Override
		public void run() {
			try {
				String path = context.getResources().getString(com.li.barry.R.string.serverUrl);
				URL version_url = new URL(path);
				// ��������
				HttpURLConnection conn = (HttpURLConnection) version_url.openConnection();
				conn.connect();
				// ����������
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
	
	//�������Ƿ����°汾
	private boolean isUpdate(){
		//��ȡ��ǰ����汾
		int versionCode = getVersionCode(context);
		//��version.xml�ŵ������ϣ�Ȼ���ȡ�ļ���Ϣ
		if(mHashMap!=null){
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
			//�汾�ж�
			if(serviceCode > versionCode){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ��ȡ��ǰ�汾��
	 */
	private int getVersionCode(Context context){
		int versionCode = 0;
		try {
			//��ȡ����汾�ţ���ӦAndroidMainfest.xml��android:versionCode
			versionCode = context.getPackageManager().getPackageInfo("com.li.barry", 0).versionCode;
		} catch (Exception e) {
		}
		return versionCode;
	}
	
}
