package com.android.vantage.components;

import com.android.vantage.utility.SharedPreferenceUtil;
import com.parse.ParseUser;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
	String TAG = this.getClass().getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (SharedPreferenceUtil.isUserLoggerIn(context)){
			if (BluetoothAdapter.ACTION_STATE_CHANGED
					.equals(intent.getAction())) {
				int state = intent
						.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				Intent i;
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					if (XebiaBeaconService.isStarted) {
						i = new Intent(context, MyBeaconIntentService.class);
						i.putExtra("StartBeaconService", false);
						context.startService(i);
					}
					Log.i(TAG, "Bluetooth State : OFF");
					break;

				case BluetoothAdapter.STATE_ON:
					Log.i(TAG, "Bluetooth State : ON");
					if (!XebiaBeaconService.isStarted) {
						i = new Intent(context, MyBeaconIntentService.class);
						i.putExtra("StartBeaconService", true);
						context.startService(i);
					}
					break;

				}
			}
		}else{
			Log.e(TAG, "User is NUll");
		}
	}
}
