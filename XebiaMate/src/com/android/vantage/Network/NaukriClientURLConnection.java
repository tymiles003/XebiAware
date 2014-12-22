package com.android.vantage.Network;

import org.json.JSONObject;

public class NaukriClientURLConnection extends ClientURLConnection {
	public  <T extends JSONObject> NetworkResponse<T> getJsonData() {
		//JSONObject jsonObject = new JSONObject(getData().getData());
		return new NetworkResponse<T>();

	}
}
