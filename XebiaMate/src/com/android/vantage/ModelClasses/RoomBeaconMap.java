package com.android.vantage.ModelClasses;

import java.util.HashMap;
import java.util.List;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "RoomToBeaconMap", id = BaseColumns._ID)
public class RoomBeaconMap extends BaseModel {

	public static final String ROOM_NAME = "RoomName";
	public static final String BEACON_MAC_ADDRESS = "BeaconMacAddress";
	@Column
	private String RoomName;

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}

	public String getBeaconMacAddress() {
		return BeaconMacAddress;
	}

	public void setBeaconMacAddress(String beaconMacAddress) {
		BeaconMacAddress = beaconMacAddress;
	}

	@Column
	private String BeaconMacAddress;

	public static HashMap<String, String> getBeaconToRoomMap() {
		List<RoomBeaconMap> list = new Select().from(RoomBeaconMap.class)
				.execute();
		if (list != null && list.size() > 0) {
			HashMap<String, String> beaconToRoom = new HashMap<String, String>(
					list.size());
			for (RoomBeaconMap obj : list) {
				beaconToRoom.put(obj.getBeaconMacAddress(), obj.getRoomName());
			}
			return beaconToRoom;
		}
		return null;
	}
	public static String getRoomNameFromBeaconMac(String beaconMac){
		HashMap<String, String> beaconToRoom = getBeaconToRoomMap();
		if(beaconToRoom != null && beaconToRoom.containsKey(beaconMac)){
			return beaconToRoom.get(beaconMac);
		}
		return "Invalid Room";
	}

}
