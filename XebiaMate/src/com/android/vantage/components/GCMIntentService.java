package com.android.vantage.components;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.android.vantage.MessageListActivity;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.Message;
import com.android.vantage.asyncmanager.FindUserService;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;

public class GCMIntentService extends IntentService {

	NotificationManager notificationManager;
	private final String TAG = this.getClass().getSimpleName();

	public GCMIntentService() {
		super("GCMIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle b = intent.getExtras();

		String messageJson = b.getString("com.parse.Data");

		Message msg = Message.parseMessageString(messageJson);
		Logger.info(TAG, "Message String JSON " + messageJson);
		Logger.info(TAG, "Message Object" + msg);
		EmpData user = new Select().from(EmpData.class)
				.where(EmpData.EMP_ID + " = ?", msg.getFromXebiaId())
				.executeSingle();

		try {
			if (user == null)
				user = (EmpData) new FindUserService().getData(msg
						.getFromXebiaId());
			user.setLatestMessage(msg.getMessageText());
			user.setTimeStamp(msg.getTimeStamp());
			user.setMessageType(Message.INCOMING_MESSAGE);
			user.save();

			msg.setMessageType(Message.INCOMING_MESSAGE);
			msg.save();
			if (SharedPreferenceUtil.isUserLoggerIn(getApplicationContext()))
				Util.postNotification(getApplicationContext(),
						"Message Received", notificationManager, 1,
						MessageListActivity.class);
		} catch (Exception e) {
			Logger.error(GCMIntentService.class.getSimpleName(),
					"Exception Caught");
			e.printStackTrace();
		}

	}

}