package com.android.vantage;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.EmpRecord;
import com.android.vantage.ModelClasses.UserAnalytics;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class LoginActivity extends BaseActivity implements OnClickListener {
	EditText pwd, username;
	Button register, login;
	TextView errorView;
	String errorText = "";
	ProgressDialog dialog;
	EmpData user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen_new);
		
		ParseUser user = ParseUser.getCurrentUser();
		boolean isUserLoggedIn = SharedPreferenceUtil.getInstance(
				getBaseContext()).getData(AppConstants.LOGIN_KEY, false);
		if (isUserLoggedIn) {
			startNextActivity(MainActivity.class);

		} else {
			Util.downloadBeaconMap();
			// Util.updateMappingTable(getBaseContext());
			username = (EditText) findViewById(R.id.et_userName);
			// pwd = (EditText) findViewById(R.id.et_passWord);
			// register = (Button) findViewById(R.id.btn_register);
			login = (Button) findViewById(R.id.btn_signIN);
			errorView = (TextView) findViewById(R.id.tv_loginError);
			// register.setOnClickListener(this);
			login.setOnClickListener(this);

			// Intent i = getIntent();
			// String userString = i.getStringExtra(AppConstants.USERNAME);
			// String pwdString = i.getStringExtra(CommonConstants.PASSWORD);
			// if (userString != null) {
			// username.setText(userString);
			// }
			// if (pwdString != null) {
			// pwd.setText(pwdString);
			// }

		}

	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_signIN:
			String userName = username.getText().toString();
			// String passWord = pwd.getText().toString();
			if (username.equals("")) {
				errorText = "Username can not be empty";
			} else {
				makeRestCallToFindUser(userName.toUpperCase());
				Util.hideKeyboard(this);
				// validateUser(userName);

			}
			errorView.setText(errorText);
			errorView.setVisibility(View.VISIBLE);
			break;
		// case R.id.btn_register:
		// startNextActivity(RegisterActivity.class);
		// break;
		default:
			break;
		}
	}

	private void loginUser(final EmpData user) {
		final ParseUser parseUser = new ParseUser();
		parseUser.setUsername(user.getEmpId());
		parseUser.setPassword(user.getEmpId());
		Logger.info(TAG, user.getEmpId() + ";;;; User");

		showDialog();

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("username", user.getEmpId());
		// query.whereEqualTo("password", user.getEmpId());
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> paramList, ParseException e) {
				// TODO Auto-generated method stub

				if (paramList != null && paramList.size() > 0) {
					// Username is already taken
					try {
						ParseUser currentUser = ParseUser.logIn(user.getEmpId(), user.getEmpId());

						moveToNext(currentUser);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						Logger.info(TAG, "Exception Logging");
						showToast(e1.getMessage());
						e1.printStackTrace();
					}

				} else {
					try {
						parseUser.signUp();
						moveToNext(parseUser);
					} catch (ParseException e1) {
						Logger.info(TAG, "Exception Sign Up");
						showToast(e1.getMessage());
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				dialog.dismiss();

			}
		});

	}

	private void moveToNext(ParseUser parseUser) {
		parseUser.put(EmpData.DOJ, user.getDoj() + "");
		parseUser.put(EmpData.DESIGNATION, user.getDesignation());
		parseUser.put(EmpData.PIC_URL, user.getPicUrl());
		parseUser.put(EmpData.FULL_NAME, user.getFullName());
		parseUser.put(EmpData.MOBILE_NUMBER, user.getMobileNumber());
		parseUser.put(EmpData.EMAIL, user.getEmail());
		parseUser.put(EmpData.AREA_CODE, user.getAreaCode());
		parseUser.saveEventually();
		SharedPreferenceUtil.getInstance(getBaseContext()).saveData(
				AppConstants.LOGIN_KEY, true);
		startNextActivity(MainActivity.class);
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.put(EmpData.EMP_ID, user.getEmpId());
		installation.saveInBackground();

	}

	private void validateUser(final String xid) {
		ParseQuery<ParseUser> user = ParseUser.getQuery();
		user.whereEqualTo("xebiaID", xid);

		user.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException e) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (e == null) {
					if (users != null && users.size() > 0) {
						ParseUser user = users.get(0);
						try {
							ParseUser.logIn(user.getUsername(), xid);
							SharedPreferenceUtil.getInstance(getBaseContext())
									.saveData(AppConstants.LOGIN_KEY, true);
							startNextActivity(MainActivity.class);
						} catch (ParseException e1) {
							errorView.setText("Unable to Login! Unknown Error");
							errorView.setVisibility(View.VISIBLE);
						}
					}
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							errorView
									.setText("Xebia ID is not found on server");
							errorView.setVisibility(View.VISIBLE);
						}
					});
				}
			}

		});
	}

	private void makeRestCallToFindUser(final String xid) {
		executeTask(AppConstants.TASK_CODES.FIND_USER_SERVICE, xid);

	}

	@Override
	public void onPreExecute(int taskCode) {
		showDialog();
	}

	@Override
	public void onPostExecute(Object response, int taskCode, Object... params) {
		dialog.dismiss();
		switch (taskCode) {
		case AppConstants.TASK_CODES.FIND_USER_SERVICE:
			if (response != null) {

				user = (EmpData) response;
				Logger.info(TAG, "User:" + user + response);
				loginUser(user);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackgroundError(RestException re, Exception e, int taskCode,
			Object... params) {
		// TODO Auto-generated method stub
		dialog.dismiss();
	}

	private void showDialog() {
		Logger.info(TAG, "Show Dialog");
		dialog = new ProgressDialog(this);
		dialog.setTitle("Please Wait");
		dialog.setMessage("Verifying User...");
		dialog.setCancelable(false);
		dialog.show();
	}

	private void startNextActivity(Class actvitiy) {
		Intent i = new Intent(LoginActivity.this, actvitiy);
		startActivity(i);
		finish();
	}

}
