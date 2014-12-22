package com.android.vantage;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.android.vantage.utility.AppConstants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

public class VantageApplication extends com.activeandroid.app.Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ActiveAndroid.initialize(this);
		Parse.initialize(getApplicationContext(),
				AppConstants.PARSE_APP_KEY_VALUE,
				AppConstants.PARSE_CLIENT_KEY_VALUE);
		PushService.setDefaultPushCallback(this, MessageListActivity.class);
		PushService.subscribe(this, "", MessageListActivity.class);
		// configureDefaultImageLoader(getApplicationContext());
	}

	public static void configureDefaultImageLoader(Context ctx) {
		// ImageLoaderConfiguration config = new
		// ImageLoaderConfiguration.Builder(ctx).build();
		// ImageLoader.getInstance().init(config);
	}
}
