package com.android.vantage.asyncmanager;

import java.sql.SQLException;

import org.json.JSONException;

import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.Network.ClientURLConnection;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.parse.ParseException;

public class FindUserService extends GenericService {

	public static final String URL = "http://default-environment-hgkgm8yf2y.elasticbeanstalk.com/getEmployee/";

	@Override
	public Object getData(Object... params) throws JSONException, SQLException,
			NullPointerException, RestException, ClassCastException,
			ParseException {
		if (params != null && params.length == 1) {

			String xid = (String) params[0];
			String restUrl = Util.getCombinedString(URL, xid);
			ClientURLConnection conn = new ClientURLConnection(restUrl);
			conn.addRequestHeader(AppConstants.PARSE_APP_KEY_ID,
					AppConstants.PARSE_APP_KEY_VALUE);
			conn.addRequestHeader(AppConstants.PARSE_REST_KEY_ID,
					AppConstants.PARSE_REST_KEY_VALUE);

			String data = conn.getData().getData();
			if (data != null && !data.isEmpty()) {
				EmpData user = EmpData.getUserFromJson(data);
				Logger.info(this.getClass().getCanonicalName(), "User"+user);
				
				return user;

			} else {
				throw new NullPointerException(
						"User Not Found, Please enter valid Xebia ID");
			}
		} else {
			throw new NullPointerException("Invalid Arguments");
		}

	}

}
