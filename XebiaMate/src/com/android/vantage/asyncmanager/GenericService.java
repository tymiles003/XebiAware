package com.android.vantage.asyncmanager;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.vantage.ModelClasses.BaseModel;
import com.android.vantage.Network.ClientURLConnection;
import com.android.vantage.Network.NetworkResponse;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants.ERROR_CODES;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

abstract public class GenericService implements Service {

	public static final String VALIDATION_ERROR = "999";
	public static final String HTTP_OK = "200";
	public static final int HTTP_RESPONSE_OK = 200;
	public static final int HTTP_RESPONSE_VALIDATION_ERROR = 999;
	public static final int HTTP_RESPONSE_VALIDATION_ERROR_500 = 500;
	private static final String RESPONSE_TYPE = "json";
	public static final String STATUS_CODE = "statuscode";

	public static final String PARSE_APPLICATION_API_KEY_STRING = "X-Parse-Application-Id";
	public static final String PARSE_REST_API_KEY_STRING = "X-Parse-REST-API-Key";

	public static final String PARSE_APPLICATION_API_KEY_VALUE = "ffkLGugChiz4rxWuS0lYBgHCqvAAu7h8OrUcp4ed";
	public static final String PARSE_REST_API_KEY_VALUE = "8bOK1EyACceLxPaR2x8zqcm250zLT30TKbqEPf4X";

	protected Context context;

	protected GenericService() {

	}

	public void parseResults(String jsonString, Class<? extends BaseModel> classType)
			throws JSONException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		JSONObject root = new JSONObject(jsonString);

		JSONArray array = root.optJSONArray("results");

		ArrayList<? extends BaseModel> list;
		if (array != null) {
			list = new ArrayList<BaseModel>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Class model = gson.fromJson(obj.toString(), classType.getClass());
				
			}
		}
		else{
			throw new JSONException("Invalid Json String");
		}

	}

	protected GenericService(Context context) {
		this.context = context;
	}

	protected final NetworkResponse<String> doPost(String url,
			StringBuilder postBody) throws RestException, JSONException {
		NetworkResponse<String> nwResponse = new ClientURLConnection(url,
				postBody.toString(), ClientURLConnection.POST_METHOD).getData();
		handleResponse(nwResponse);

		return nwResponse;
	}

	protected final NetworkResponse<String> doGet(String url,
			StringBuilder queryString) throws RestException, JSONException {

		NetworkResponse<String> nwResponse = new ClientURLConnection(url,
				queryString.toString()).getData();
		handleResponse(nwResponse);
		return nwResponse;
	}

	protected final NetworkResponse<InputStream> doGetForInputStrem(String url,
			StringBuilder queryString) {
		NetworkResponse<InputStream> nwResponse = new ClientURLConnection(url,
				queryString.toString()).getInputStream();
		return nwResponse;
	}

	protected final NetworkResponse<String> doGet(String url)
			throws RestException, JSONException {

		NetworkResponse<String> nwResponse = new ClientURLConnection(url)
				.getData();
		handleResponse(nwResponse);
		return nwResponse;
	}

	protected RestException getRestException(int code) {
		return new RestException(code, "");
	}

	protected void validateResponse(JSONObject rootJson) throws JSONException,
			NumberFormatException, RestException {

		if (rootJson.has("error")) {
			// Inform stack trace
			throw new RestException(ERROR_CODES.DEVELOPMENT_ERROR,
					"JSON String:" + rootJson);
		} else {
			parseJsonResponse(rootJson);
		}
		/*
		 * String statusCode = rootJson.has("statuscode") ? rootJson
		 * .getString("statuscode") : rootJson.getString("statusCode"); if
		 * (VALIDATION_ERROR.equals(statusCode)) {
		 * 
		 * throw new RestException(ERROR_CODES.VALIDATION_ERROR,
		 * rootJson.has("description") ? rootJson .getString("description") :
		 * "");
		 * 
		 * } else if (!HTTP_OK.equals(statusCode)) {
		 * 
		 * String errorMsg = rootJson.has("description") ? rootJson
		 * .getString("description") : ""; throw new
		 * RestException(Integer.parseInt(statusCode), errorMsg); }
		 */
	}

	protected void parseJsonResponse(JSONObject json) {

	}

	protected void handleResponse(NetworkResponse<String> response)
			throws JSONException, RestException {

		if (response == null) {
			throw new RestException(ERROR_CODES.UNKNOWN_ERROR,
					"Some technical error occured !!");
		} else {

			if (response.isSuccess()) {
				/*
				 * Logger.error("Server Response", response.getData());
				 * JSONObject rootJson = new JSONObject(response.getData());
				 * 
				 * validateResponse(rootJson);
				 */
				return;

			} else if (!response.isConnectedToNetwork()) {
				throw new RestException(ERROR_CODES.NO_INTERNET_ERROR,
						"No network");
			} else if (response.isTimeout()) {
				throw new RestException(ERROR_CODES.NETWORK_SLOW_ERROR,
						"Connection timed out");

			} else if (!response.isURLValid()) {
				throw new RestException(ERROR_CODES.URL_INVALID,
						"Possible reasong URL Not Valid");
			} else {

				throw new RestException(ERROR_CODES.UNKNOWN_ERROR,
						"Unknown Error");
			}

		}

	}
}
