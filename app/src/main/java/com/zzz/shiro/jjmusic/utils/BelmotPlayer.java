package com.zzz.shiro.jjmusic.utils;
//Download by http://www.codefans.net

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.zzz.shiro.jjmusic.BottomBar;

public class BelmotPlayer extends Application {
	public static String TAG = "BelmotPlayer";

	private IPlayerEngine playerEngine;

	private static BelmotPlayer instance;

	private static BottomBar bottomBar;

	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
	}

	public IPlayerEngine getPlayerEngine() {
		if (null == playerEngine) {
			playerEngine = new PlayerEngineImpl();
		}
		return playerEngine;
	}

	public BottomBar getBottomBar(Activity activity){
		if(null == bottomBar){
			bottomBar = BottomBar.getInstance(activity);
		}
		return bottomBar;
	}

	public static BelmotPlayer getInstance() {
		return instance;
	}

	/**
	 * Retrieves application's version number from the manifest
	 * 
	 * @return
	 */
	public String getVersion() {
		String version = "0.0.0";

		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}
}
