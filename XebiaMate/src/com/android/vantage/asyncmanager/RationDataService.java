package com.android.vantage.asyncmanager;

import java.sql.SQLException;

import org.json.JSONException;

import android.content.Context;

import com.android.vantage.Network.ClientURLConnection;
import com.android.vantage.Network.NetworkResponse;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.ParseJsonInterface;

public class RationDataService extends GenericService {

	public RationDataService(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public RationDataService() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public Object getData(Object... params) throws JSONException, SQLException,
			NullPointerException, RestException {
		// TODO Auto-generated method stub
		if(params != null && params.length > 1){
			String url = (String) params[0];
			ParseJsonInterface callbackInterface = (ParseJsonInterface) params[1];
			ClientURLConnection conn = new ClientURLConnection(url);
			conn.addRequestHeader(AppConstants.PARSE_APP_KEY_ID, AppConstants.PARSE_APP_KEY_VALUE);
			conn.addRequestHeader(AppConstants.PARSE_REST_KEY_ID, AppConstants.PARSE_REST_KEY_VALUE);
			
			NetworkResponse<String> response = conn.getData();
			String jsonString = response.getData();
			return callbackInterface.parseJsonString(jsonString);
		}
		return null;
	}

}
