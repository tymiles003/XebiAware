package com.android.vantage.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtil {
	private static final String PREF_NAME = "Pioneer_Prefs";
	public static final String LOGIN_KEY = "isUserLoggedIn";

	private SharedPreferences xebiaSharedPrefs;
	private Editor editor;
	private static SharedPreferenceUtil sharedPreferenceUtil;

	private static void initializeNaukriSharedPref(Context context) {
		sharedPreferenceUtil = new SharedPreferenceUtil();
		sharedPreferenceUtil.xebiaSharedPrefs = context.getSharedPreferences(
				PREF_NAME, Activity.MODE_PRIVATE);
		sharedPreferenceUtil.editor = sharedPreferenceUtil.xebiaSharedPrefs
				.edit();
	}

	public static SharedPreferenceUtil getInstance(Context context) {
		if (sharedPreferenceUtil == null) {
			initializeNaukriSharedPref(context);
		}
		return sharedPreferenceUtil;
	}

	private SharedPreferenceUtil() {
		// TODO Auto-generated constructor stub
	}

	public synchronized boolean saveData(String key, String value) {
		editor.putString(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, boolean value) {
		editor.putBoolean(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, long value) {
		editor.putLong(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, float value) {
		editor.putFloat(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, int value) {
		editor.putInt(key, value);
		return editor.commit();
	}
	public static boolean isUserLoggerIn(Context context){
		return SharedPreferenceUtil.getInstance(context).getData(LOGIN_KEY, false);
	}

	/*
	 * public synchronized boolean saveData(String key, Set<String> value) {
	 * //editor.putStringSet(key, value); return editor.commit(); }
	 */

	public synchronized boolean removeData(String key) {
		editor.remove(key);
		return editor.commit();
	}

	public synchronized Boolean getData(String key, boolean defaultValue) {
		return xebiaSharedPrefs.getBoolean(key, defaultValue);
	}

	public synchronized String getData(String key, String defaultValue) {
		return xebiaSharedPrefs.getString(key, defaultValue);
	}

	public synchronized float getData(String key, float defaultValue) {

		return xebiaSharedPrefs.getFloat(key, defaultValue);
	}

	public synchronized int getData(String key, int defaultValue) {
		return xebiaSharedPrefs.getInt(key, defaultValue);
	}

	public synchronized long getData(String key, long defaultValue) {
		return xebiaSharedPrefs.getLong(key, defaultValue);
	}

	/*
	 * public synchronized Set<String> getData(String key, Set<String> defValue)
	 * {
	 * 
	 * // return naukriSharedPreferences.getStringSet(key, defValue); return
	 * null; }
	 */
	public synchronized void deleteAllData() {

		sharedPreferenceUtil = null;
		editor.clear();
		editor.commit();
	}
}