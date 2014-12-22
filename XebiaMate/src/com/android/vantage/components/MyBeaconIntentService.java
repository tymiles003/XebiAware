package com.android.vantage.components;

import android.app.IntentService;
import android.content.Intent;

public class MyBeaconIntentService extends IntentService {

	public MyBeaconIntentService() {
		super("MyBeaconIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getBooleanExtra("StartBeaconService", false)) {
			startService(new Intent(getApplicationContext(),
					XebiaBeaconService.class));
		}else{
			stopService(new Intent(getApplicationContext(),
					XebiaBeaconService.class));
		}
	}

}
