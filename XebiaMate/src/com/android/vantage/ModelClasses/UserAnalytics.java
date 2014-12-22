package com.android.vantage.ModelClasses;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.google.gson.annotations.Expose;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@Table(name = "UserAnalytics", id = BaseColumns._ID)
public class UserAnalytics extends BaseModel implements Comparable<UserAnalytics> {

	public static final String DISTANCE = "Distance";
	public static final String ENTRY_TIME = "EntryTime";
	public static final String EXIT_TIME = "ExitTime";
	public static final String BEACON_MAC_ADDRESS = "beaconMacAddress";
	public static final String ROOM_NAME = "RoomName";
	public static final String IS_ENTERED = "isEntered";

	@Column(name = BEACON_MAC_ADDRESS)
	@Expose
	private String beaconMacAddress;
	@Column(name = DISTANCE)
	@Expose
	private Double Distance;
	@Column(name = ROOM_NAME)
	@Expose
	private String RoomName;

	@Column(name = IS_ENTERED)
	@Expose
	private Boolean isEntered;

	public Boolean getIsEntered() {
		return isEntered;
	}

	public void setIsEntered(Boolean isEntered) {
		this.isEntered = isEntered;
	}

	/**
	 * 
	 * @return The beaconMacAddress
	 */
	public String getBeaconMacAddress() {
		return beaconMacAddress;
	}

	/**
	 * 
	 * @param beaconMacAddress
	 *            The beaconMacAddress
	 */
	public void setBeaconMacAddress(String beaconMacAddress) {
		this.beaconMacAddress = beaconMacAddress;
	}

	/**
	 * 
	 * @return The Distance
	 */
	public Double getDistance() {
		return Distance;
	}

	/**
	 * 
	 * @param Distance
	 *            The Distance
	 */
	public void setDistance(Double Distance) {
		this.Distance = Distance;
	}

	/**
	 * 
	 * @return The EntryTime
	 */

	/**
	 * 
	 * @return The RoomName
	 */
	public String getRoomName() {
		return RoomName;
	}

	/**
	 * 
	 * @param RoomName
	 *            The RoomName
	 */
	public void setRoomName(String RoomName) {
		this.RoomName = RoomName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((RoomName == null) ? 0 : RoomName.hashCode());
		result = prime
				* result
				+ ((beaconMacAddress == null) ? 0 : beaconMacAddress.hashCode());
		result = prime * result
				+ ((isEntered == null) ? 0 : isEntered.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (getClass() != obj.getClass())
			return false;
		UserAnalytics other = (UserAnalytics) obj;
		if (RoomName == null) {
			if (other.RoomName != null)
				return false;
		} else if (!RoomName.equals(other.RoomName))
			return false;
		if (beaconMacAddress == null) {
			if (other.beaconMacAddress != null)
				return false;
		} else if (!beaconMacAddress.equals(other.beaconMacAddress))
			return false;
		if (isEntered == null) {
			if (other.isEntered != null)
				return false;
		} else if (!isEntered.equals(other.isEntered))
			return false;
		return true;
	}

	public static void addEntryToMap(
			HashMap<String, ArrayList<UserAnalytics>> mapStatistics,
			UserAnalytics entry) {
		ArrayList<UserAnalytics> list = null;
		if (mapStatistics.containsKey(entry.RoomName)) {
			list = new ArrayList<UserAnalytics>();

		} else {
			list = mapStatistics.get(entry.RoomName);
		}
		list.add(entry);
		mapStatistics.put(entry.RoomName, list);
	}

	public static void printMapContent(HashMap<String, Long> mapStatistics) {
		Logger.error(UserAnalytics.class.getSimpleName(), "Size of Map stats"
				+ mapStatistics.size() + "");
		for (Entry<String, Long> entry : mapStatistics.entrySet()) {
			Logger.error(UserAnalytics.class.getSimpleName(), entry.getKey()
					+ ":" + entry.getValue() + "");
		}
	}

	public static void testParseLogic() {
		String xId = ParseUser.getCurrentUser().getUsername();
		ParseQuery<ParseObject> userData = new ParseQuery<ParseObject>(xId);
		userData.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					Logger.error("ParseLoading	",
							"Done with downloading User Analytics");
					ArrayList<UserAnalytics> users = new ArrayList<UserAnalytics>(
							list.size());
					for (ParseObject obj : list) {
						users.add(getInstanceFromParseObject(obj));
					}
					getFilteredRoomAnalytics(users);
					// printMapContent(parseUserAnaytics(users));
				} else {
					Logger.error("ParseError",
							"Error while downloading User Analytics");
					e.printStackTrace();
				}
			}
		});
	}
	
	


	public static UserAnalytics getInstanceFromParseObject(ParseObject obj) {
		UserAnalytics user = new UserAnalytics();
		user.beaconMacAddress = obj.getString(BEACON_MAC_ADDRESS);
		user.createdAt = obj.getCreatedAt().toString();
		user.updatedAt = obj.getUpdatedAt().toString();
		user.Distance = obj.getDouble(DISTANCE);
		user.isEntered = obj.getBoolean(IS_ENTERED);
		user.RoomName = obj.getString(ROOM_NAME);
		user.objectId = obj.getObjectId();
		return user;
	}

	private static final String TAG = UserAnalytics.class.getSimpleName();

	private static List<UserAnalytics> removingAdjacentNodes(int currentIndex,
			List<UserAnalytics> list) {
		if (currentIndex >= list.size() - 1) {
			return list;
		} else {
			Logger.info(TAG, "Current Index" + currentIndex);
			boolean entry = list.get(currentIndex).getIsEntered();
			boolean exit = list.get(currentIndex + 1).getIsEntered();
			if (entry == exit) {
				Logger.info(TAG, "Entry == Exit");
				list.remove(currentIndex);
				return removingAdjacentNodes(currentIndex, list);
			} else {
				return removingAdjacentNodes(currentIndex + 1, list);
			}
		}

	}

	public static List<UserAnalytics> getFilteredRoomAnalytics(List<UserAnalytics> list) {
		Iterator<UserAnalytics> it = list.listIterator();
		while (it.hasNext()) {
			if (it.next().getIsEntered()) {
				break;
			} else {
				it.remove();
			}

		}
		printListEntryExit(list);
		// removeSimilarAdjacentNodes(list);
		list = removingAdjacentNodes(0, list);
		Logger.error(TAG, "Printing list  after removal");
		printListEntryExit(list);
		return list;
	}

	private static void printListEntryExit(List<UserAnalytics> list) {
		Iterator<UserAnalytics> it = list.listIterator();
		while (it.hasNext()) {
			Logger.info(UserAnalytics.class.getSimpleName(), it.next()
					.getIsEntered() + "");
		}
	}

	public static HashMap<String, Long> parseUserAnaytics(
			List<UserAnalytics> list) {
		HashMap<String, Long> mapStatistics = new HashMap<String, Long>();

		if (list != null && list.size() > 0) {
			// First node should not be exit node
			if (!list.get(0).isEntered) {
				list.remove(0);
			}
			// Last node should not be entry node
			if (list.get(list.size() - 1).isEntered) {
				list.remove(list.size() - 1);
			}
			Logger.info(UserAnalytics.class.getSimpleName(),
					"ListSize:" + list.size());
			for (int i = 0; i < list.size() - 1; i = i + 2) {
				UserAnalytics entryUserNode = list.get(i);
				UserAnalytics exitUserNode = list.get(i + 1);

				if (entryUserNode.isEntered && !exitUserNode.isEntered)
					try {
						Logger.info(UserAnalytics.class.getSimpleName(),
								"Entry Exit Right");
						Date entryTime = Util
								.convertStringToDate(entryUserNode.updatedAt,
										Util.JAVA_DATE_PATTERN);
						Date exitTime = Util.convertStringToDate(
								exitUserNode.updatedAt, Util.JAVA_DATE_PATTERN);

						long diffMinutes = (exitTime.getTime() - entryTime
								.getTime()) / (60 * 1000) % 60;
						Logger.info(UserAnalytics.class.getSimpleName(),
								"Entry Time:" + entryTime.toString()
										+ ", Exit Time:" + exitTime.toString()
										+ ", Diff:" + diffMinutes);
						if (mapStatistics.containsKey(entryUserNode.RoomName)) {
							long prevTimeSpan = mapStatistics
									.get(entryUserNode.RoomName);
							mapStatistics.put(entryUserNode.RoomName,
									prevTimeSpan + diffMinutes);
						} else {
							mapStatistics.put(entryUserNode.RoomName,
									diffMinutes);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}

			}
		}
		return mapStatistics;
	}

	@Override
	public int compareTo(UserAnalytics another) {
		try {
			Date start = Util.convertStringToDate(this.getUpdatedAt(), Util.JAVA_DATE_PATTERN);
			Date end = Util.convertStringToDate(another.getUpdatedAt(), Util.JAVA_DATE_PATTERN);
			return start.compareTo(end);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
}
