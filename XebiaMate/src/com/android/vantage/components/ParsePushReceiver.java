package com.android.vantage.components;

import com.android.vantageLogManager.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ParsePushReceiver extends com.parse.ParseBroadcastReceiver{

	private final String TAG = this.getClass().getName();
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Logger.info(TAG, "Broadcast Received");
		Intent i = new Intent(context, GCMIntentService.class);
		Logger.info(TAG, "Intent "+intent+","+intent.getAction()+","+intent.getDataString()+","+intent.getExtras());
		Bundle b = intent.getExtras();
		if(b == null) return;
		for(String key : b.keySet()){
			Logger.info(TAG, key+","+b.getString(key));
		}
		
		
		Bundle extras = intent.getExtras();
        String message = extras != null ? extras.getString("com.parse.Data") : "";  
        
		
		
		com.parse.GcmBroadcastReceiver r;
		i.putExtras(intent.getExtras());
		context.startService(i);
	}

}
