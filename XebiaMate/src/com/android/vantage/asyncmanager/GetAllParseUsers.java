package com.android.vantage.asyncmanager;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantageLogManager.Logger;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GetAllParseUsers extends GenericService {

	public GetAllParseUsers(Context ctx) {
		this.context = ctx;
	}

	@Override
	public Object getData(Object... params) throws JSONException, SQLException,
			NullPointerException, RestException, ClassCastException,
			ParseException {
		Logger.info(this.getClass().getSimpleName(), "FindAllUserService GetData()");
		List<ParseUser> objects;
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		objects = query.find();
		new Delete().from(EmpData.class).execute();
		for (ParseUser user : objects) {
			EmpData data = EmpData.getUserFromParseUser(user);
			data.save();
		}
		return objects;
	}
}
