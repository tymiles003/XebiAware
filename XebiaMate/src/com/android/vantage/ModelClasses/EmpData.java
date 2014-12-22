package com.android.vantage.ModelClasses;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ConflictAction;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.parse.ParseUser;

@Table(name = "EmpData", id = BaseColumns._ID)
public class EmpData extends Model implements Serializable{

	public static final String EMP_ID = "empId";
	public static final String FULL_NAME = "fullName";
	public static final String DOJ = "doj";
	public static final String PIC_URL = "picUrl";
	public static final String DESIGNATION = "designation";
	public static final String XEBIA_ID = "xebiaID";
	public static final String MOBILE_NUMBER = "mobileNumber";
	public static final String EMAIL = "email";
	public static final String AREA_CODE = "areaCode";

	public EmpData() {

	}

	@Column(name = EMP_ID, unique = true, notNull=true, onUniqueConflict = ConflictAction.REPLACE)
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

	private String RoomName;

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	@Column(name = Message.TIME_STAMP)
	private String timeStamp;

	@Column(name = Message.MESSAGE_TEXT)
	private String latestMessage;

	@Column(name = Message.MESSAGE_TYPE)
	private String messageType;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getLatestMessage() {
		return latestMessage;
	}

	public void setLatestMessage(String latestMessage) {
		this.latestMessage = latestMessage;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * 
	 * @return The empId
	 */
	public String getEmpId() {
		return empId;
	}

	/**
	 * 
	 * @param empId
	 *            The empId
	 */
	public void setEmpId(String empId) {
		this.empId = empId;
	}

	/**
	 * 
	 * @return The doj
	 */
	public String getDoj() {
		return doj;
	}

	/**
	 * 
	 * @param doj
	 *            The doj
	 */
	public void setDoj(String doj) {
		this.doj = doj;
	}

	/**
	 * 
	 * @return The designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * 
	 * @param designation
	 *            The designation
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * 
	 * @return The picUrl
	 */
	public String getPicUrl() {
		return picUrl;
	}

	/**
	 * 
	 * @param picUrl
	 *            The picUrl
	 */
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public static EmpData getUserFromJson(String jsonString)
			throws JSONException {
		// GsonBuilder gsonBuilder = new GsonBuilder();
		// Gson gson = gsonBuilder.create();
		JSONObject obj = new JSONObject(jsonString);
		EmpData data = new EmpData();
		data.empId = obj.optString(EMP_ID);
		data.fullName = obj.optString(FULL_NAME);
		data.areaCode = obj.optString(AREA_CODE);
		data.mobileNumber = obj.optString(MOBILE_NUMBER);
		data.picUrl = obj.optString(PIC_URL);
		data.doj = obj.optString(DOJ);
		data.designation = obj.optString(DESIGNATION);
		data.email = obj.optString(EMAIL);
		// gson.fromJson(jsonString, EmpData.class)
		return data;
	}

	public static EmpData getUserFromParseUser(ParseUser obj) {
		EmpData data = new EmpData();
		data.empId = obj.getUsername();
		data.fullName = obj.getString(FULL_NAME);
		data.areaCode = obj.getString(AREA_CODE);
		data.mobileNumber = obj.getString(MOBILE_NUMBER);
		data.picUrl = obj.getString(PIC_URL);
		data.doj = obj.getString(DOJ);
		data.designation = obj.getString(DESIGNATION);
		data.email = obj.getString(EMAIL);
		// data.RoomName = obj.getString(ROOM_NAME);
		return data;
	}

	public static EmpData getEmpDataFromCursor(Cursor c) {
		EmpData d = new EmpData();
		d.empId = c.getString(c.getColumnIndex(EmpData.EMP_ID));
		d.timeStamp = c.getString(c.getColumnIndex(Message.TIME_STAMP));
		d.latestMessage = c.getString(c.getColumnIndex(Message.MESSAGE_TEXT));
		d.fullName = c.getString(c.getColumnIndex(EmpData.FULL_NAME));
		d.picUrl = c.getString(c.getColumnIndex(EmpData.PIC_URL));
		d.doj = c.getString(c.getColumnIndex(EmpData.DOJ));
		d.designation = c.getString(c.getColumnIndex(EmpData.DESIGNATION));
		d.mobileNumber = c.getString(c.getColumnIndex(EmpData.MOBILE_NUMBER));
		d.areaCode = c.getString(c.getColumnIndex(EmpData.AREA_CODE));
		d.email = c.getString(c.getColumnIndex(EmpData.EMAIL));
		// d.RoomName = c.getString(c.getColumnIndex(EmpData.ROOM_NAME));
		return d;
	}

}