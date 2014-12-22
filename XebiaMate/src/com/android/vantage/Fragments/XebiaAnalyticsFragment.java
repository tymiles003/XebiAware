package com.android.vantage.Fragments;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.android.vantage.MainActivity;
import com.android.vantage.R;
import com.android.vantage.Fragments.BarChartFragment.BarChartItemClickListener;
import com.android.vantage.ModelClasses.EmpRecord;
import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantage.ModelClasses.RoomBeaconMap;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantageLogManager.Logger;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class XebiaAnalyticsFragment extends BaseFragment implements
		OnClickListener, LoaderCallbacks<Cursor> {

	TableLayout table;
	List<ParseUserData> users;
	int totalUserCount, activeUserCount;
	Handler h;
	private final String TAG = "XKEAnalytics";

	private void getAllUsers() {
		ParseQuery<ParseUser> query = ParseUser.getQuery();

		executeTask(AppConstants.TASK_CODES.GET_PARSE_USER_OBJECT, query);

	}

	@Override
	public void onPostExecute(Object response, int taskCode, Object... params) {
		// TODO Auto-generated method stub
		if (getActivity() == null)
			return;
		dialog.dismiss();
		// if (response != null) {
		// updateUI();
		// }

	}

	@Override
	public void onBackgroundError(RestException re, Exception e, int taskCode,
			Object... params) {
		// TODO Auto-generated method stub
		if (getActivity() == null)
			return;
		dialog.dismiss();
	}

	private void setText(View parentView, int id, String text) {
		TextView tv = (TextView) parentView.findViewById(id);
		tv.setText(text);
	}

	private void addUserCountRow() {
		View row = getAnalyticsRow();
		setText(row, R.id.tv_left_key, "Total Users");
		setText(row, R.id.tv_left_value, "" + totalUserCount);

		setText(row, R.id.tv_right_key, "Active Users");
		setText(row, R.id.tv_right_value, "" + activeUserCount);
		setOnClickListener(row, R.id.tv_left_key, R.id.tv_left_value,
				R.id.tv_right_key, R.id.tv_right_value);
		table.addView(row);
	}

	private View getAnalyticsRow() {
		return LayoutInflater.from(getActivity()).inflate(
				R.layout.row_analytics, null);
	}

	private void addUserAreaRow() {
		View row = getAnalyticsRow();
		ParseUser user = ParseUser.getCurrentUser();
		String currentRegion = "OOO";
		String currentStatus = "Offline";
		if (user.getBoolean(EmpRecord.IS_USER_ONLINE)) {
			currentStatus = "Online";
		} else {

		}
		setText(row, R.id.tv_left_key, "Current Region");
		setText(row, R.id.tv_left_value, currentRegion);
		setText(row, R.id.tv_right_key, "Current Status");
		setText(row, R.id.tv_right_value, currentStatus);
		table.addView(row);
	}

	private void updateUI() {
		if (getActivity() == null) {
			return;
		}
		users = new Select().from(ParseUserData.class).execute();
		if(users != null && users.size()>0){
			
		}else{
			return;
		}
		SharedPreferenceUtil.getInstance(getActivity()).saveData(
				LAST_UPDATED_ANALYTICS, new Date().toString());
		setLastUpdated(LAST_UPDATED_ANALYTICS);
		table.removeAllViewsInLayout();
		totalUserCount = users.size();
		activeUserCount = 0;
		for (ParseUserData user : users) {
			Object isOnline = user.getIsUserOnline();
			boolean isUserOnline = false;
			if (isOnline != null) {
				isUserOnline = (Boolean) isOnline;
			}
			if (isUserOnline) {
				activeUserCount++;
			}

		}
		addUserCountRow();
		// addUserAreaRow();
		initChart();
	}

	ProgressDialog dialog;

	private void showDialog() {
		Logger.info(TAG, "Show Dialog");
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Please Wait");
		dialog.setMessage("Loading Data...");
		dialog.setCancelable(false);
		dialog.show();
	}

	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** Button for adding entered data to the current series. */
	private Button mAdd;
	/** Edit text field for entering the slice value. */
	private EditText mValue;
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	private void addSeriesAndRender(String seriesCat, double value, int colorId) {
		mSeries.add(seriesCat, value);
		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(getResources().getColor(colorId));
		mRenderer.addSeriesRenderer(renderer);
		mChartView.repaint();
	}

	@Override
	public void onResume() {
		super.onResume();
		// if (mChartView == null) {
		// LinearLayout layout = (LinearLayout) findView(R.id.chart);
		// mChartView = ChartFactory.getPieChartView(getActivity(), mSeries,
		// mRenderer);
		// mRenderer.setClickEnabled(true);
		// mChartView.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// SeriesSelection seriesSelection = mChartView
		// .getCurrentSeriesAndPoint();
		// if (seriesSelection == null) {
		// } else {
		// for (int i = 0; i < mSeries.getItemCount(); i++) {
		// mRenderer.getSeriesRendererAt(i).setHighlighted(
		// i == seriesSelection.getPointIndex());
		//
		// }
		// String roomName = mSeries.getCategory(seriesSelection
		// .getPointIndex());
		// Logger.error(TAG, "Room Name Cliecked on Chart :"
		// + roomName);
		// ((MainActivity) getActivity()).displayView(roomName);
		// mChartView.repaint();
		//
		// }
		// }
		// });
		// layout.addView(mChartView, new LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// } else {
		// mChartView.repaint();
		// }
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_left_key:

		case R.id.tv_left_value:
			Logger.info(TAG, "Clicked on Total Users");
			((MainActivity) getActivity()).displayView(HomeFragment.ALL_USERS);
			break;
		case R.id.tv_right_key:

		case R.id.tv_right_value:
			((MainActivity) getActivity())
					.displayView(HomeFragment.ACTIVE_USERS);
			break;

		default:
			break;
		}
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		table = (TableLayout) findView(R.id.table_analytics);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setChartTitleTextSize(28f);
		mRenderer.setLabelsColor(getResources().getColor(R.color.text_color));
		mRenderer.setLabelsTextSize(28f);
		mRenderer.setLegendTextSize(28f);
		mRenderer.setShowLegend(false);

		updateUI();
		getActivity().getSupportLoaderManager().initLoader(10, null, this);
		showDialog();
		getAllUsers();
		setActionTitle("Xebia Analytics");
	}

	@Override
	public String getActionTitle() {
		// TODO Auto-generated method stub
		return "Xebia Analytics";
	}

	int colors[] = new int[] { R.color.chart_blue, R.color.chart_green,
			R.color.chart_mehroon, R.color.gray_light };
	HashMap<String, Long> roomToCountMap;

	private void initChart() {
		mSeries.clear();
		roomToCountMap = new HashMap<String, Long>();
		for (ParseUserData user : users) {
			String roomName = user.getRoomName();
			if (roomName != null || roomName != "") {
				if (roomToCountMap.containsKey(roomName)) {
					long count = roomToCountMap.get(roomName);
					roomToCountMap.put(roomName, count + 1);
				} else {
					roomToCountMap.put(roomName, 1L);
				}
			}
		}
		GraphicalView chartView = BarChartFragment.getChartView(getActivity(),
				roomToCountMap, new BarChartItemClickListener() {

					@Override
					public void onBarItemClick(String xLabel) {
						// TODO Auto-generated method stub
						if (!xLabel.isEmpty())
							((MainActivity) getActivity()).displayView(xLabel);
					}
				});

		LinearLayout layout = (LinearLayout) findView(R.id.chart);
		layout.removeAllViews();
		// layout.addView(chartView);
		layout.addView(chartView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		// int count = 0;
		// for (String key : roomToCountMap.keySet()) {
		//
		// addSeriesAndRender(key, roomToCountMap.get(key), colors[count
		// % colors.length]);
		// count++;
		// }
	}

	public void refreshData() {
		showDialog();
		getAllUsers();
	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return R.layout.activity_xkeanalytics;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new android.support.v4.content.CursorLoader(getActivity(),
				ContentProvider.createUri(ParseUserData.class, null), null,
				null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		// TODO Auto-generated method stub
		Logger.info(TAG	, "On Load Finished");
		if (c != null && c.getCount() > 0) {
			updateUI();
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		updateUI();
	}

}
