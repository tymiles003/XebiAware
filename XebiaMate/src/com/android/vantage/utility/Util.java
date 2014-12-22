package com.android.vantage.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.vantage.R;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.Message;
import com.android.vantage.ModelClasses.RoomBeaconMap;
import com.android.vantage.asyncmanager.FindUserService;
import com.android.vantage.components.ParseBroadcastReceiver;
import com.android.vantage.components.SendingPushMessageIntentService;
import com.android.vantageLogManager.Logger;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

public class Util {

	public static float randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static final String JAVA_DATE_PATTERN = "E MMM dd HH:mm:ss";
	public static final String REQUIRE_DATE_PATTERN = "MMM dd, HH:mm";
	public static final String PARSE_CREATED_AT_DATE_PATTERN = "MMM dd, yyyy, HH:mm";

	public static void downloadBeaconMap() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"RoomToBeaconMap");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects != null) {
						new Delete().from(RoomBeaconMap.class).execute();
						for (ParseObject obj : objects) {
							RoomBeaconMap map = new RoomBeaconMap();
							map.setBeaconMacAddress(obj
									.getString(RoomBeaconMap.BEACON_MAC_ADDRESS));
							map.setRoomName(obj
									.getString(RoomBeaconMap.ROOM_NAME));
							map.save();
						}
					}
				} else {

				}
			}

		});
	}

	public interface DialogListener {
		public void onOKPressed(DialogInterface dialog, int which);

		public void onCancelPressed(DialogInterface dialog, int which);
	}

	public static int getQuantityFromEditText(EditText etQty) {
		int qty = 0;
		try {
			qty = Integer.parseInt(etQty.getText().toString());

		} catch (Exception e) {

		}
		return qty;
	}

	public static AlertDialog createAlertDialog(Context context,
			String message, String title, boolean isCancelable, String okText,
			String cancelText, final Util.DialogListener listener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);

		builder.setCancelable(isCancelable);
		builder.setPositiveButton(okText,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						listener.onOKPressed(dialog, which);
					}
				});
		builder.setNegativeButton(cancelText,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						listener.onCancelPressed(dialog, which);
					}
				});

		return builder.create();
	}

	public static boolean isConnectingToInternet(Context ctx) {

		boolean NetConnected = false;
		try {
			ConnectivityManager connectivity = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				Logger.info("tag", "couldn't get connectivity manager");
				NetConnected = false;
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							NetConnected = true;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Logger.error("Connectivity Exception",
					"Exception AT isInternetConnection");
			NetConnected = false;
		}
		return NetConnected;

	}

	public static String getStringFromInputStream(InputStream is) {
		StringBuilder response = new StringBuilder();
		try {
			BufferedReader buReader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 50000);

			String line;

			while ((line = buReader.readLine()) != null) {
				response.append(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response.toString();

	}

	public static void startItemActivity(Context ctx, Class activityClass) {
		Intent i = new Intent(ctx, activityClass);
		ctx.startActivity(i);
	}

	public static String getStringFromHTMLContent(String s) {
		String str = s.replaceAll("<br />", "<br /><br />").replaceAll(
				"&nbsp;", "<br /><br />");
		Log.e("String After", str);
		return str;
	}

	public static String convertDateFormat(String currentDate,
			String reqDateFormat) {
		SimpleDateFormat currentDateFormat = new SimpleDateFormat(
				JAVA_DATE_PATTERN);
		SimpleDateFormat format = new SimpleDateFormat(reqDateFormat);
		try {
			Date d = currentDateFormat.parse(currentDate);
			return format.format(d);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static Object getColumnObject(Cursor c, String columnName) {
		int colIndex = c.getColumnIndex(columnName);
		switch (c.getType(colIndex)) {

		case Cursor.FIELD_TYPE_BLOB:
			return c.getBlob(colIndex);
		case Cursor.FIELD_TYPE_STRING:
			return c.getString(colIndex);
		case Cursor.FIELD_TYPE_FLOAT:
			return c.getFloat(colIndex);
		case Cursor.FIELD_TYPE_INTEGER:
			return c.getInt(colIndex);
		case Cursor.FIELD_TYPE_NULL:
			return null;
		}
		return null;

	}

	public static String getCombinedString(String... strings) {
		StringBuilder builder = new StringBuilder();
		for (String s : strings) {
			builder.append(s);
		}
		return builder.toString();
	}

	public static String DBL_FMT = "%.2f";

	public static String getPriceString(Resources res, String amount) {

		return getCombinedString(res.getString(R.string.Rs), "  ",
				String.format(DBL_FMT, Double.valueOf(amount)));
	}

	public static String getPriceString(Resources res, double amount) {

		return getCombinedString(res.getString(R.string.Rs), "  ",
				String.format(DBL_FMT, amount));
	}

	public static void postNotification(Context ctx, String msg,
			NotificationManager notificationManager, final int NOTIFICATION_ID,
			Class classType) {
		Intent notifyIntent = new Intent(ctx, classType);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivities(ctx, 0,
				new Intent[] { notifyIntent },
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification.Builder(ctx)
				.setSmallIcon(R.drawable.app_logo).setContentTitle("Xebia POC")
				.setContentText(msg).setAutoCancel(true)
				.setContentIntent(pendingIntent).build();
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notificationManager.notify(NOTIFICATION_ID, notification);

	}

	public static Date convertStringToDate(String dateString, String dateFormat)
			throws Exception {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.parse(dateString);
	}

	public static void sendPushMessage(String msg, String to, Context ctx) {
		Intent i = new Intent(ctx, SendingPushMessageIntentService.class);
		i.putExtra(Message.MESSAGE_TEXT, msg);
		i.putExtra(EmpData.EMP_ID, to);
		ctx.startService(i);
		

	}

	public static void hideKeyboard(Context ctx) {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

}
