package com.android.vantage.ModelClasses;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import android.provider.BaseColumns;

@Table(name = "Message", id = BaseColumns._ID)
public class Message extends BaseModel {
	
	public static final String FROM_XEBIA_ID = "fromXebiaId";
	public static final String MESSAGE_TEXT = "messageText";
	public static final String TIME_STAMP = "timeStamp";
	public static final String MESSAGE_TYPE = "messageType";
	
	public static final String INCOMING_MESSAGE = "1";
	public static final String OUTGOING_MESSAGE = "2";

	@Column
	@Expose
	private String fromXebiaId;

	@Column
	@Expose
	private String messageText;
	@Column
	@Expose
	private String timeStamp;
	
	@Column
	private String messageType;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public static Message parseMessageString(String jsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		Message item = gson.fromJson(jsonString, Message.class);
		return item;
	}

	public String getFromXebiaId() {
		return fromXebiaId;
	}

	public void setFromXebiaId(String fromXebiaId) {
		this.fromXebiaId = fromXebiaId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

}
