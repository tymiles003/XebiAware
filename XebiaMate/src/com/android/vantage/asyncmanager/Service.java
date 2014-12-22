package com.android.vantage.asyncmanager;

import java.sql.SQLException;

import org.json.JSONException;

import com.android.vantage.exceptionhandler.RestException;
import com.parse.ParseException;

public interface Service {

	public Object getData(Object... params)throws JSONException,SQLException,NullPointerException,RestException, ClassCastException, ParseException;
	
	
}
