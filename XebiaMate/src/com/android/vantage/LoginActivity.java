package com.android.vantage;

import java.util.List;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.components.XebiaBeaconService;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.estimote.sdk.BeaconManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends BaseActivity implements OnClickListener, OnQueryTextListener, OnCloseListener {
	private EditText pwd, username;
	private Button register, login;
	private TextView errorView;
	private String errorText = "";
	private ProgressDialog dialog;
	private EmpData user;
	
	private SearchView mSearchView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen_new);
		setActionTitle("");
		boolean isUserLoggedIn = SharedPreferenceUtil.getInstance(
				getBaseContext()).getData(AppConstants.LOGIN_KEY, false);
		if (isUserLoggedIn) {
			startNextActivity(XebiaRoomViewActivity.class);

		} else {
			Util.downloadBeaconMap();
			username = (EditText) findViewById(R.id.et_userName);
			login = (Button) findViewById(R.id.btn_signIN);
			errorView = (TextView) findViewById(R.id.tv_loginError);
			// register.setOnClickListener(this);
			login.setOnClickListener(this);
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

			}
			errorView.setText(errorText);
			errorView.setVisibility(View.VISIBLE);
			break;
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
		startNextActivity(XebiaRoomViewActivity.class);
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.put(EmpData.EMP_ID, user.getEmpId());
		installation.saveInBackground();

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
				user.save();
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
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
       // mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //setupSearchView();

        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView() {

     //   mSearchView.setIconifiedByDefault(true);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            // Try to use the "applications" global search provider
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        showToast("Query = " + newText);
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
    	showToast("Query = " + query + " : submitted");
        return false;
    }

    public boolean onClose() {
    	showToast("Closed!");
        return false;
    }

}
