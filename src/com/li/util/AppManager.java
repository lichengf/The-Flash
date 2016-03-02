package com.li.util;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

//
//AppManager.getAppManager().addActivity(this);
//
//AppManager.getAppManager().AppExit(this);
public class AppManager {
	private static Stack<Activity> mActivityStack;
	private static AppManager instance;

	private AppManager() {
	}

	public static AppManager getAppManager() {
		if (null == instance) {
			instance = new AppManager();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		if (null == mActivityStack) {
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}

	public Activity currentActivity() {
		Activity activity = mActivityStack.lastElement();
		return activity;
	}

	public void finishActivity() {
		Activity activity = mActivityStack.lastElement();
		if (activity != null) {
			activity.finish();
			activity = null;
		}
	}

	public void finishActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	public void finishActivity(Class<?> cls) {
		for (Activity activity : mActivityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	public void finishAllActivity() {
		for (int i = 0, size = mActivityStack.size(); i < size; i++) {
			if (null != mActivityStack.get(i)) {
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}

	@SuppressWarnings("deprecation")
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}
}
