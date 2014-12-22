package com.android.vantage.asyncmanager;

import java.sql.SQLException;

import org.json.JSONException;

import android.content.Context;

import com.android.vantage.ModelClasses.JsonModelInterface;
import com.android.vantage.Network.ClientURLConnection;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.parse.ParseException;

public class GetBrandsData extends GenericService {

	public GetBrandsData(Context ctx) {
		this.context = ctx;
	}

	@Override
	public Object getData(Object... params) throws JSONException, SQLException,
			NullPointerException, RestException, ClassCastException {
		if (params != null && params.length >= 2) {
			String url = (String) params[0];
			JsonModelInterface ref = (JsonModelInterface) params[1];
			ClientURLConnection conn = new ClientURLConnection(url);
			conn.addRequestHeader(AppConstants.PARSE_APP_KEY_ID,
					AppConstants.PARSE_APP_KEY_VALUE);
			conn.addRequestHeader(AppConstants.PARSE_REST_KEY_ID,
					AppConstants.PARSE_REST_KEY_VALUE);

			String data = conn.getData().getData();
			if (data != null) {
				return ref.parseModel(data);

			} else {
				throw new NullPointerException();
			}
		}

		return null;
	}

}
