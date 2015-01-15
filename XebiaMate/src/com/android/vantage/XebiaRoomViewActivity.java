package com.android.vantage;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vantage.Fragments.RoomListViewFragment;
import com.android.vantage.Fragments.XebiaAnalyticsFragment;
import com.android.vantage.ListAdapters.CustomPagerAdapter;
import com.android.vantage.ListAdapters.CustomPagerAdapter.PagerAdapterInterface;
import com.android.vantage.components.XebiaBeaconService;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.utility.AppConstants;
import com.android.vantageLogManager.Logger;
import com.estimote.sdk.BeaconManager;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class XebiaRoomViewActivity extends BaseActivity implements
		PagerAdapterInterface<String>, OnPageChangeListener {

	private ViewPager pager;
	private ArrayList<String> tabArray = new ArrayList<String>();
	private CustomPagerAdapter<String> pagerAdapter;

	private static final String LIST_VIEW = "List View";
	private static final String CHART_VIEW = "Chart View";

	TextView listTabButton, chartTabButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xebia_room_view);
		listTabButton = (TextView) findViewById(R.id.tv_tab1);
		chartTabButton = (TextView) findViewById(R.id.tv_tab2);

		tabArray.add(LIST_VIEW);
		tabArray.add(CHART_VIEW);
		pager = (ViewPager) findViewById(R.id.pager);
		pagerAdapter = new CustomPagerAdapter<String>(
				getSupportFragmentManager(), tabArray, this);
		initActionBar();
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(this);
		getAllUsers();
		onTabButtonClicked(listTabButton);
		beaconManager = new BeaconManager(this);
		if (beaconManager.isBluetoothEnabled()) {
			startService(new Intent(this, XebiaBeaconService.class));
		}
	}

	private View getActionBarView() {
		return LayoutInflater.from(this).inflate(R.layout.custom_action_bar,
				null);
	}

	public void lastUpdatedClicked(View v) {
		fragment.lastUpdatedClicked(v);
	}

	public void onTabButtonClicked(View v) {
		switch (v.getId()) {
		case R.id.tv_tab1:
			selectListButton();
			pager.setCurrentItem(0, true);
			break;
		case R.id.tv_tab2:
			pager.setCurrentItem(1, true);
			selectChartButton();
			break;

		default:
			break;
		}

	}

	private void selectChartButton() {
		chartTabButton.setBackgroundColor(getResources().getColor(
				R.color.text_dark_xebia));
		listTabButton.setBackgroundColor(getResources().getColor(
				R.color.text_white));
		chartTabButton
				.setTextColor(getResources().getColor(R.color.text_white));
		listTabButton.setTextColor(getResources().getColor(
				R.color.text_light_grey));
	}

	BeaconManager beaconManager;

	@Override
	protected void onStart() {
		super.onStart();

		// Check if device supports Bluetooth Low Energy.

		if (!beaconManager.hasBluetooth()) {
			Toast.makeText(this, "Device does not have Bluetooth Low Energy",
					Toast.LENGTH_LONG).show();
			return;
		}

		// If Bluetooth is not enabled, let user enable it.
		if (!beaconManager.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {

		}
	}

	private void selectListButton() {
		listTabButton.setBackgroundColor(getResources().getColor(
				R.color.text_dark_xebia));
		chartTabButton.setBackgroundColor(getResources().getColor(
				R.color.text_white));
		listTabButton.setTextColor(getResources().getColor(R.color.text_white));
		chartTabButton.setTextColor(getResources().getColor(
				R.color.text_light_grey));
	}

	private static final int REQUEST_ENABLE_BT = 1234;

	private void initActionBar() {
		// Specify that tabs should be displayed in the action bar.
		bar.setCustomView(getActionBarView());
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayUseLogoEnabled(false);
		bar.setDisplayShowCustomEnabled(true);
	}

	private View getTabView(String text) {
		TextView tv = (TextView) LayoutInflater.from(this).inflate(
				R.layout.tab_buttons, null);
		tv.setText(text);
		return tv;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getAllUsers() {
		ParseQuery<ParseUser> query = ParseUser.getQuery();

		executeTask(AppConstants.TASK_CODES.GET_PARSE_USER_OBJECT, query);

	}

	@Override
	public void onPostExecute(Object response, int taskCode, Object... params) {
		// TODO Auto-generated method stub
		if (dialog != null)
			dialog.dismiss();

	}

	@Override
	public void onBackgroundError(RestException re, Exception e, int taskCode,
			Object... params) {
		// TODO Auto-generated method stub
		if (dialog != null)
			dialog.dismiss();
	}

	@Override
	public void onPreExecute(int taskCode) {
		showDialog();
	}

	private void showDialog() {
		Logger.info(TAG, "Show Dialog");
		dialog = new ProgressDialog(this);
		dialog.setTitle("Please Wait");
		dialog.setMessage("Loading Data...");
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public Fragment getFragmentItem(int position, String listItem) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			// Get the List View Fragment
			fragment = new RoomListViewFragment();
			break;
		case 1:
			// Get the chart View Fragment
			fragment = new XebiaAnalyticsFragment();
			break;

		default:
			break;
		}
		return fragment;
	}

	@Override
	public CharSequence getPageTitle(int position, String listItem) {
		// TODO Auto-generated method stub

		return tabArray.get(position);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		if (position == 0) {
			selectListButton();
		} else {
			selectChartButton();
		}
	}
}
