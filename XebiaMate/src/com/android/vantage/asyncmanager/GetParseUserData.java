package com.android.vantage.asyncmanager;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantage.exceptionhandler.RestException;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GetParseUserData extends GenericService {

	@Override
	public Object getData(Object... params) throws JSONException, SQLException,
			NullPointerException, RestException, ClassCastException,
			ParseException {
		List<ParseUser> objects;
		if (params != null && params.length > 0) {
			ParseQuery<ParseUser> query = (ParseQuery<ParseUser>) params[0];
			objects = query.find();
			for(ParseUser obj : objects){
				ParseUserData data = ParseUserData.getUserFromParseUser(obj);
				data.save();
			}
		} else {
			throw new NullPointerException();
		}
		return objects;
	}

}
