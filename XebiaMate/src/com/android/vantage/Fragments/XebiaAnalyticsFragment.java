package com.android.vantage.Fragments;

import java.util.Date;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.vantage.FragmentHolderActivity;
import com.android.vantage.R;
import com.android.vantage.Fragments.BarChartFragment.BarChartItemClickListener;
import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.SharedPreferenceUtil;

public class XebiaAnalyticsFragment extends BaseRoomViewFragment implements
		OnClickListener {

	TableLayout table;

	int totalUserCount, activeUserCount;
	Handler h;
	private final String TAG = "XKEAnalytics";

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

	protected void updateUI() {
		if (getActivity() == null) {
			return;
		}
		super.updateUI();
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
		// addUserCountRow();
		// addUserAreaRow();
		initChart();
	}

	ProgressDialog dialog;

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
		initLoderManager();
		updateUI();
		setActionTitle("Xebia Analytics");
	}

	@Override
	public String getActionTitle() {
		// TODO Auto-generated method stub
		return "Xebia Analytics";
	}

	int colors[] = new int[] { R.color.chart_blue, R.color.chart_green,
			R.color.chart_mehroon, R.color.gray_light };

	private void initChart() {
		mSeries.clear();

		GraphicalView chartView = BarChartFragment.getChartView(getActivity(),
				roomToCountMap, new BarChartItemClickListener() {

					@Override
					public void onBarItemClick(String xLabel) {
						// TODO Auto-generated method stub
						if (!xLabel.isEmpty())

							startNextActivity(xLabel);
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

	private void startNextActivity(String roomName) {
		Bundle b = new Bundle();
		b.putString(AppConstants.BUNDLE_KEYS.KEY_ROOM_NAME, roomName);
		b.putInt(AppConstants.BUNDLE_KEYS.FRAGMENT_TYPE,
				FragmentHolderActivity.FRAGMENT_TYPE_HOME);
		Intent i = new Intent(getActivity(), FragmentHolderActivity.class);
		i.putExtras(b);
		startActivity(i);
	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return R.layout.activity_xkeanalytics;
	}

}
