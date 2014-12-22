package com.android.vantage.components;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;

import com.activeandroid.query.Select;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.Message;
import com.android.vantage.asyncmanager.FindUserService;
import com.android.vantageLogManager.Logger;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SendingPushMessageIntentService extends IntentService {

	private final static String TAG = "SendingPushMessageIntentService";
	public SendingPushMessageIntentService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String msg = intent.getStringExtra(Message.MESSAGE_TEXT);
		String to = intent.getStringExtra(EmpData.EMP_ID);
		ParseUser user = ParseUser.getCurrentUser();
		JSONObject data = new JSONObject();
		try {
			data.put(Message.FROM_XEBIA_ID, user.getUsername());
			Date d = new Date();
			data.put(Message.TIME_STAMP, d.toString());
			data.put(Message.MESSAGE_TEXT, msg);
			data.put("action", ParseBroadcastReceiver.ACTION);

			Logger.info(TAG, ""+data.toString()+" ----TO:"+to);
			ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
			query.whereEqualTo(EmpData.EMP_ID, to);

			
			Message msgObj = new Message();
			msgObj.setFromXebiaId(to);
			msgObj.setTimeStamp(d.toString());
			msgObj.setMessageText(msg);
			msgObj.setMessageType(Message.OUTGOING_MESSAGE);
			msgObj.save();
			
			
			
			
			ParsePush push = new ParsePush();
			// push.setQuery(query); // Set our Installation query
			// push.setData(data);

			push.setQuery(query);
			push.setData(data);
			push.send();

			Logger.error(TAG, "Push Sent");
			
			EmpData empData = new Select().from(EmpData.class)
					.where(EmpData.EMP_ID + " = ?", to).executeSingle();
			if (empData == null) {
				FindUserService service = new FindUserService();
				empData = (EmpData) service.getData(to);

			}
			empData.setEmpId(to);
			empData.setMessageType(Message.OUTGOING_MESSAGE);
			empData.setTimeStamp(d.toString());
			empData.setLatestMessage(msg);
			empData.save();
			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.error(TAG, "JSON Exception While Sending Push");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Logger.error(TAG, "Parse Exception While Sending Push");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.error(TAG, "Exception While Sending Push");
			e.printStackTrace();
		}
	}

}
