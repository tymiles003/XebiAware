package com.android.vantage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vantage.Fragments.BaseFragment;
import com.android.vantage.Fragments.TaskFragment;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;

public class BaseActivity extends ActionBarActivity implements
		TaskFragment.AsyncTaskListener, OnClickListener,
		LoaderCallbacks<Cursor> {

	ActionBar bar;
	public TaskFragment mTaskFragment;
	ProgressDialog dialog;
	protected BaseFragment fragment;

	protected final String TAG = getTag();

	public static boolean isInternetDialogVisible = false;

	public String getActionTitle() {
		return getResources().getString(R.string.app_name);
	}

	protected String getTag() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		bar = getSupportActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
				R.color.xebia_color)));
		FragmentManager fm = getSupportFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag("task");

		// If the Fragment is non-null, then it is currently being
		// retained across a configuration change.
		if (mTaskFragment == null) {
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, "task").commit();
		}
		// fm.beginTransaction().add(mTaskFragment, "task").commit();
	}

	public void onActionItemClicked(View v) {
		switch (v.getId()) {
		case R.id.iv_refresh:

			if (fragment != null) {
				fragment.refreshData();
			}

			break;
		case R.id.iv_search:
			Intent searchActivity = new Intent(this,
					FragmentHolderActivity.class);
			Bundle b = new Bundle();
			b.putInt(AppConstants.BUNDLE_KEYS.FRAGMENT_TYPE,
					FragmentHolderActivity.FRAGMENT_TYPE_SEARCH);
			searchActivity.putExtras(b);
			startActivity(searchActivity);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// do nothing
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setFullScreenWindow() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		WindowManager.LayoutParams attrs = this.getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		this.getWindow().setAttributes(attrs);
	}

	public void initDefaultActionBar(String customTitle) {
		bar = getSupportActionBar();
		bar.setDisplayUseLogoEnabled(false);

		// bar.setDisplayShowTitleEnabled(true);

		// bar.setIcon(R.drawable.back_action_button);
		bar.setTitle(customTitle);
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		// bar.setLogo(R.drawable.back_action_button);
		bar.show();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onBackIconClicked(View v) {
		super.onBackPressed();
	}

	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	protected AsyncTask executeTask(int taskCode, Object... params) {
		// AsyncTask task = mTaskFragment
		// .createAsyncTaskManagerObject(taskCode);
		// task.execute(params);
		// Logger.info("Base Activity", "Connected to internet");
		// return task;
		if (Util.isConnectingToInternet(this)) {
			AsyncTask task = mTaskFragment
					.createAsyncTaskManagerObject(taskCode);
			task.execute(params);
			Logger.info("Base Activity", "Connected to internet");
			return task;
		} else {
			Logger.info("Base Activity", "Not Connected to internet");
			if (isInternetDialogVisible) {
				Util.createAlertDialog(this, "Please Connect to Internet",
						"Not Connected to internet", false, "OK", "Cancel",
						internetDialogListener).show();
				isInternetDialogVisible = true;
			}
		}
		return null;
	}

	public static Util.DialogListener internetDialogListener = new Util.DialogListener() {

		@Override
		public void onOKPressed(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			isInternetDialogVisible = false;
		}

		@Override
		public void onCancelPressed(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			isInternetDialogVisible = false;
		}
	};

	@Override
	public void onPreExecute(int taskCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostExecute(Object response, int taskCode, Object... params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackgroundError(RestException re, Exception e, int taskCode,
			Object... params) {

		if (re != null) {
			showToast(re.getMessage() + "");
		} else if (e != null) {
			showToast(e.getMessage());
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void setText(String text, int textViewId) {
		TextView view = (TextView) findViewById(textViewId);
		view.setText(text);
	}

	public boolean isUserLoggedIn() {
		return SharedPreferenceUtil.getInstance(getApplicationContext())
				.getData(AppConstants.PREF_KEYS.KEY_LOGIN, false);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	public void setActionTitle(String actionTitle) {
		// TODO Auto-generated method stub
		bar.setTitle(actionTitle);
	}

}
