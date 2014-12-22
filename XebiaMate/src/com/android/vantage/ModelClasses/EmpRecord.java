package com.android.vantage.ModelClasses;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONException;

import com.activeandroid.Model;
import com.android.vantage.asyncmanager.GetParseUserData;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantageLogManager.Logger;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EmpRecord extends Model {

	public static final String ENTRY_TIME = "EntryTime";
	public static final String EXIT_TIME = "ExitTime";
	public static final String DISTANCE = "Distance";
	public static final String BLE_STATUS = "BLEStatus";

	public static final String BLE_ON = "ON";
	public static final String BLE_OFF = "OFF";
	public static final String IS_ENTERED = "isEntered";

	public static final String BEACON_MAJOR = "Major";
	public static final String BEACON_MINOR = "Minor";
	public static final String BEACON_PROXIMITY_UUID = "ProximityUUID";
	public static final String BEACON_NAME = "beaconName";
	public static final String BEACON_MAC_ADDRESS = "beaconMacAddress";
	public static final String BEACON_RSSI = "beaconRSSI";
	public static final String BEACON_IDENTIFIER = "beaconIdentifier";

	public static final String CURRENT_REGION_MAC = "CurrentBeaconMac";
	public static final String IS_USER_ONLINE = "isUserOnline";
	public static final String LAST_SEEN = "lastSeenAt";

	public static ParseUser getCurrentUser() {
		return ParseUser.getCurrentUser();
	}

	public  static void updateRegion(Beacon b) {
		ParseUser user = getCurrentUser();
		if (user != null) {
			user.put(CURRENT_REGION_MAC, b.getMacAddress());
			user.saveEventually();
			updateData();
		}

	}

	public  static void updateRegion(Beacon b, String roomName) {
		
		ParseUser user = getCurrentUser();
		if (user != null) {
			if (b != null)
				user.put(CURRENT_REGION_MAC, b.getMacAddress());
			user.put(RoomBeaconMap.ROOM_NAME, roomName);
			user.saveEventually();
			updateData();
		}

	}

	static ExecutorService executor = Executors.newFixedThreadPool(1);

	public static void updateData() {
			Logger.info("Updating Data from Service", "Starting Executor");
			executor.submit(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					ParseQuery<ParseUser> query = ParseUser.getQuery();
					GetParseUserData data = new GetParseUserData();
					try {
						data.getData(query);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
	}

	public static synchronized void updateOnlineStatus(boolean isOnline) {

		ParseUser user = getCurrentUser();
		Date d = new Date();
		user.put(LAST_SEEN, d.toString());
		user.put(IS_USER_ONLINE, isOnline);
		user.saveEventually();
	}

	public synchronized static void updateBLEStatus(String onOrOff) {
		ParseUser user = getCurrentUser();
		ParseObject obj = new ParseObject(user.getUsername());
		obj.put(BLE_STATUS, onOrOff);
		obj.saveEventually();
	}

	public static void updateInTimeAnalytics(Beacon b,
			String roomName) {
		ParseUser user = getCurrentUser();
		if (user != null) {

			saveUserAnalytics(true, user, b, roomName);
		}
	}

	private static void saveUserAnalytics(boolean isEntered, ParseUser user,
			Beacon b, String roomName) {
		Date d = new Date();
		ParseObject obj = new ParseObject(user.getUsername());
		ParseObject roomObj = new ParseObject(roomName);
		
		obj.put(IS_ENTERED, isEntered);
		double distance = Utils.computeAccuracy(b);
		obj.put(DISTANCE, distance);
		obj.put(RoomBeaconMap.ROOM_NAME, roomName);
		obj.put(BEACON_MAC_ADDRESS, b.getMacAddress() + "");
		obj.saveEventually();
		
		roomObj.put(EmpData.EMP_ID, user.getUsername());
		roomObj.put(RoomBeaconMap.ROOM_NAME, roomName);
		roomObj.put(IS_ENTERED, isEntered);
		roomObj.put(DISTANCE, distance);
		roomObj.put(BEACON_MAC_ADDRESS, b.getMacAddress() + "");
		roomObj.saveEventually();
		
	}

	public static void updateOutTimeAnalytics(Beacon b,
			String roomName) {
		ParseUser user = getCurrentUser();
		if (user != null) {
			saveUserAnalytics(false, user, b, roomName);
		}
	}

}
