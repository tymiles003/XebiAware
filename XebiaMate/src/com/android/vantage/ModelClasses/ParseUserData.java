package com.android.vantage.ModelClasses;

import java.io.Serializable;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.annotation.Column.ConflictAction;
import com.google.gson.annotations.Expose;
import com.parse.ParseUser;

@Table(name = "ParseUserData", id = BaseColumns._ID)
public class ParseUserData extends BaseModel implements Serializable{
	
	public static final String IS_USER_ONLINE = "isUserOnline";
	public static final String ROOM_NAME = "RoomName";
	public static final String CURRENT_BEACON_MAC = "CurrentBeaconMac";
	@Column
	@Expose
	private String empId;

	@Column
	@Expose
	private String fullName;
	@Expose
	@Column
	private String doj;

	@Expose
	@Column
	private String designation;
	@Expose
	@Column
	private String picUrl;

	@Expose
	@Column
	private String mobileNumber;

	@Expose
	@Column
	private String email;

	@Expose
	@Column
	private String areaCode;

	@Expose
	@Column
	private String RoomName;
	
	@Expose
	@Column
	private Boolean isUserOnline;
	
	@Expose
	@Column
	private String lastSeenAt;
	
	@Expose
	@Column
	private String CurrentBeaconMac;

	public String getCurrentBeaconMac() {
		return CurrentBeaconMac;
	}

	public void setCurrentBeaconMac(String currentBeaconMac) {
		CurrentBeaconMac = currentBeaconMac;
	}

	public Boolean getIsUserOnline() {
		return isUserOnline;
	}

	public void setIsUserOnline(Boolean isUserOnline) {
		this.isUserOnline = isUserOnline;
	}

	public String getLastSeenAt() {
		return lastSeenAt;
	}

	public void setLastSeenAt(String lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDoj() {
		return doj;
	}

	public void setDoj(String doj) {
		this.doj = doj;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}
	
	public static ParseUserData getUserFromParseUser(ParseUser obj) {
		ParseUserData data = new ParseUserData();
		data.empId = obj.getUsername();
		data.fullName = obj.getString(EmpData.FULL_NAME);
		data.areaCode = obj.getString(EmpData.AREA_CODE);
		data.mobileNumber = obj.getString(EmpData.MOBILE_NUMBER);
		data.picUrl = obj.getString(EmpData.PIC_URL);
		data.doj = obj.getString(EmpData.DOJ);
		data.designation = obj.getString(EmpData.DESIGNATION);
		data.email = obj.getString(EmpData.EMAIL);
		data.isUserOnline = obj.getBoolean(IS_USER_ONLINE);
		data.RoomName = obj.getString(ROOM_NAME);
		data.createdAt = obj.getCreatedAt().toString();
		data.updatedAt = obj.getUpdatedAt().toString();
		data.objectId = obj.getObjectId();
		data.CurrentBeaconMac  = obj.getString(CURRENT_BEACON_MAC);
		return data;
	}

}
