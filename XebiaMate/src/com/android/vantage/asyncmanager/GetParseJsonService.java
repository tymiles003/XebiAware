package com.android.vantage.asyncmanager;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.vantage.Network.JSONParser;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.ParseParamObject;
import com.android.vantageLogManager.Logger;

public class GetParseJsonService extends GenericService {
	int parseType = -1;
	
	public GetParseJsonService(Context ctx){
		context = ctx;
	}
	@Override
	public Object getData(Object... params) throws JSONException, SQLException,
			NullPointerException, RestException {
		// TODO Auto-generated method stub
		if(params != null && params.length > 0){
			
			String url = ((ParseParamObject) params[0]).buildURL();
			Logger.info("URL :", ""+url);
//			ClientURLConnection conn = new ClientURLConnection(url);
//			Logger.info("URL", url);
//			conn.addRequestHeader(PARSE_APPLICATION_API_KEY_STRING, PARSE_APPLICATION_API_KEY_VALUE);
//			conn.addRequestHeader(PARSE_REST_API_KEY_STRING, PARSE_REST_API_KEY_VALUE);
//			
//			NetworkResponse<String> response = conn.getData();
//			
//			Logger.info("Response Received	", ""+response.getData()+"@@esponse"+response);
//			handleResponse(response);
//			Logger.info("Response Received	", ""+response.getData());
//			//validateResponse(response.get)
			
			return parseJsonData(JSONParser.getJSONFromUrl(url));
		}
		
		
		return null;
	}
	
	protected Object parseJsonData(String jsonStr) throws JSONException{
		
		return null;
	}
	
	@Override
	protected void validateResponse(JSONObject rootJson) throws JSONException,
			NumberFormatException, RestException {
		// TODO Auto-generated method stub
		super.validateResponse(rootJson);
	}
	
	@Override
	protected void parseJsonResponse(JSONObject json) {
		
		
	}
	
	

}
