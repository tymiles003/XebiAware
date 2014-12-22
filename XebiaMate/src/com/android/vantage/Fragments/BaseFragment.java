package com.android.vantage.Fragments;

import java.util.Date;

import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.vantage.BaseActivity;
import com.android.vantage.R;
import com.android.vantage.Fragments.TaskFragment.AsyncTaskListener;
import com.android.vantage.asyncmanager.Service;
import com.android.vantage.asyncmanager.ServiceFactory;
import com.android.vantage.exceptionhandler.ExceptionHandler;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;

public abstract class BaseFragment extends Fragment implements
		View.OnClickListener, AsyncTaskListener {

	View v;
	int layoutId;
	boolean retainFlag = false;
	public static String LAST_UPDATED = "Last Updated: ";
	public static String LAST_UPDATED_ANALYTICS = "Last Updated Analytics: ";

	protected String TAG = getClass().getSimpleName();

	public String getActionTitle() {
		return null;
	}

	public void setActionTitle(String actionTitle) {
		((BaseActivity) getActivity()).setActionTitle(actionTitle);
	}

	public void lastUpdatedClicked(View v) {
		findView(R.id.progressBar1).setVisibility(View.VISIBLE);
		refreshData();
	}

	public void refreshData() {
	}
	
	protected void setOnClickListener(int...viewIds ){
		for(int i : viewIds){
			findView(i).setOnClickListener(this);
		}
	}
	protected void setOnClickListener(View v, int...viewIds ){
		for(int id : viewIds){
			v.findViewById(id).setOnClickListener(this);
		}
	}

	public void setLastUpdated(String lastUpdatedKey) {
		findView(R.id.progressBar1).setVisibility(View.GONE);
		TextView lastUpdated = (TextView) findView(R.id.tv_lastUpdated);

		String lastUpdatedTime = SharedPreferenceUtil
				.getInstance(getActivity()).getData(lastUpdatedKey,
						new Date().toString());
		lastUpdatedTime = Util.convertDateFormat(lastUpdatedTime, Util.REQUIRE_DATE_PATTERN);
		lastUpdated.setText(LAST_UPDATED + lastUpdatedTime);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		retainFlag = true;
		Log.e("onCreate", "savedInstanceState:" + savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		v = inflater.inflate(getViewID(), null);
		Log.e("Retain Flag", retainFlag + "");
		initViews();
		return v;

	}

	public void scrollToBottom(final ScrollView scrollView) {
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.fullScroll(View.FOCUS_DOWN);
				// scrollView.scrollTo(0, scrollView.getBottom());
			}
		});
	}

	protected AsyncTask executeTask(int taskCode, Object... params) {

		if (Util.isConnectingToInternet(getActivity())) {
			AsyncManager task = new AsyncManager(taskCode, this);
			task.execute(params);
			return task;
		} else {
			Logger.info("Base Activity", "Not Connected to internet");
			Util.createAlertDialog(getActivity(), "Please Connect to Internet",
					"Not Connected to internet", false, "OK", "Cancel",
					BaseActivity.internetDialogListener).show();
		}
		return null;
	}

	public void scrollToTop(final ScrollView scrollView) {
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.fullScroll(View.FOCUS_UP);
				// scrollView.scrollTo(0, scrollView.getBottom());
			}
		});
	}

	protected void initActionBar(String text) {
		// ((BaseActivity) getActivity()).initActionBar(text);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	public void registerClickListeners(int... viewIds) {
		for (int id : viewIds) {
			v.findViewById(id).setOnClickListener(this);
		}
	}

	public void registerClickListeners(View... views) {
		for (View view : views) {
			view.setOnClickListener(this);
		}
	}

	public View findView(int id) {
		return v.findViewById(id);
	}

	public abstract void initViews();

	public abstract int getViewID();

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	protected void setText(int textViewID, String text) {
		TextView textView = (TextView) findView(textViewID);
		textView.setText(text);
	}

	protected void setText(int textViewID, String text, View row) {
		TextView textView = (TextView) row.findViewById(textViewID);
		textView.setText(text);
	}

	public void hideKeyboard() {
		/*
		 * getActivity().getWindow().setSoftInputMode(
		 * WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		 */

		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

	}

	public class AsyncManager extends AsyncTask<Object, Object, Object> {

		public static final String TAG = "XebiaAsyncManage";

		private int taskCode;
		private Object[] params;
		private Exception e;
		private long startTime;
		AsyncTaskListener asyncTaskListener;

		public AsyncManager(int taskCode, AsyncTaskListener ref) {

			this.taskCode = taskCode;
			asyncTaskListener = ref;
		}

		@Override
		protected void onPreExecute() {
			startTime = System.currentTimeMillis();
			Logger.info(TAG, "On Preexecute AsyncTask");
			if (asyncTaskListener != null) {
				asyncTaskListener.onPreExecute(this.taskCode);
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			Object response = null;
			Service service = ServiceFactory.getInstance(getActivity(),
					taskCode);
			Logger.info(TAG, "DoinBackGround");
			try {
				this.params = params;
				response = service.getData(params);
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof JSONException) {
					String exceptionMessage = "APIMANAGEREXCEPTION : ";

					exceptionMessage += ExceptionHandler.getStackString(e,
							asyncTaskListener.getClass().getName());
					/*
					 * ServiceFactory.getDBInstance(context).putStackTrace(
					 * exceptionMessage);
					 */
				}
				this.e = e;
			}
			return response;
		}

		@Override
		protected void onPostExecute(Object result) {

			if (getActivity() != null) {

				Logger.error(
						"servicebenchmark",
						asyncTaskListener.getClass().getName()
								+ " , time taken in ms: "
								+ (System.currentTimeMillis() - startTime));

				if (e != null) {

					if (e instanceof RestException) {
						asyncTaskListener.onBackgroundError((RestException) e,
								null, this.taskCode, this.params);
					} else {
						asyncTaskListener.onBackgroundError(null, e,
								this.taskCode, this.params);
					}
				} else {
					asyncTaskListener.onPostExecute(result, this.taskCode,
							this.params);
				}
				super.onPostExecute(result);
			}
		}

		public int getCurrentTaskCode() {
			return this.taskCode;
		}

	}

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
			((BaseActivity) getActivity()).showToast(re.getMessage() + "");
		} else if (e != null) {
			((BaseActivity) getActivity()).showToast(re.getMessage() + "");
		}
	}

}
