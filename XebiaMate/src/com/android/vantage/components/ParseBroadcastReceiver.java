package com.android.vantage.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.vantageLogManager.Logger;
import com.parse.PushService;

public class ParseBroadcastReceiver extends BroadcastReceiver {

	private final String TAG = this.getClass().getSimpleName();
	public static final String ACTION = "PushActionSentMessage";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		// TODO Auto-generated method stub

		Logger.info(TAG, "Broadcast Received");
		Intent i = new Intent(ctx, GCMIntentService.class);
		Logger.info(TAG, "Intent " + intent + "," + intent.getAction() + ","
				+ intent.getDataString() + "," + intent.getExtras());
		Bundle b = intent.getExtras();
		if (b == null)
			return;
		for (String key : b.keySet()) {
			Logger.info(TAG, key + "," + b.getString(key));
		}

		i.putExtras(intent.getExtras());
		ctx.startService(i);
	}

}
