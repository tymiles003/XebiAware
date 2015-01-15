package com.android.vantage.asyncmanager;

import android.content.Context;

import com.android.vantage.utility.AppConstants;

public class ServiceFactory {

	public static Service getInstance(Context context, int taskCode) {
		Service service = null;
		switch (taskCode) {


		case AppConstants.TASK_CODES.GET_PARSE_USER_OBJECT:
			service = new GetParseUserData();
			break;
		case AppConstants.TASK_CODES.FIND_USER_SERVICE:
			service = new FindUserService();
			break;
		case AppConstants.TASK_CODES.GET_ALL_PARSE_USERS:
			service = new GetAllParseUsers(context);
			break;

		}
		return service;
	}

}
