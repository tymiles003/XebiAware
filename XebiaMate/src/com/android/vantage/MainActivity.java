package com.android.vantage;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.android.vantage.Fragments.BaseFragment;
import com.android.vantage.Fragments.HomeFragment;
import com.android.vantage.Fragments.XebiaAnalyticsFragment;
import com.android.vantage.ListAdapters.CustomCursorAdapter;
import com.android.vantage.ListAdapters.CustomCursorAdapter.CustomCursorAdapterInterface;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.RoomBeaconMap;
import com.android.vantage.ModelClasses.UserAnalytics;
import com.android.vantage.components.XebiaBeaconService;
import com.android.vantage.imagedownloadutil.DownLoadImageLoader;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.estimote.sdk.BeaconManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.parse.ParseUser;

public class MainActivity extends BaseActivity implements
		CustomCursorAdapterInterface {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	BaseFragment fragment;

	private static final int REQUEST_ENABLE_BT = 1234;

	boolean isDrawerOpen;

	CustomCursorAdapter loaderAdapter;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	Cursor c;

	// slide menu items

	int selectedPosition = 0;

	public void lastUpdatedClicked(View v) {
		if (fragment != null) {
			fragment.lastUpdatedClicked(v);
		}
	}

	private void runTestMethod() {
		UserAnalytics.testParseLogic();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
		Util.downloadBeaconMap();
		runTestMethod();

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mDrawerList.addHeaderView(getHeaderView());
		loaderAdapter = new CustomCursorAdapter(this, null, true,
				R.layout.item_nav_drawer, this);
		// setting the nav drawer list adapter

		mDrawerList.setAdapter(loaderAdapter);
		getSupportLoaderManager().initLoader(0, null, this);
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// enabling action bar app icon and behaving it as toggle button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				// getSupportActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
				isDrawerOpen = false;
			}

			public void onDrawerOpened(View drawerView) {
				// getSupportActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
				isDrawerOpen = true;
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(selectedPosition);
		}
		beaconManager = new BeaconManager(this);
		if (beaconManager.isBluetoothEnabled()) {
			startService(new Intent(this, XebiaBeaconService.class));
		}
	}

	public void onBackPressed() {
		if (isDrawerOpen) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else if (!(fragment instanceof XebiaAnalyticsFragment)) {
			displayView(0);
		} else {
			super.onBackPressed();
		}
	};

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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub

		return new CursorLoader(this, ContentProvider.createUri(
				RoomBeaconMap.class, null), null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		// TODO Auto-generated method stub
		if (c != null && c.getCount() > 0) {
			this.c = c;
			loaderAdapter.swapCursor(this.c);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		this.c = null;
		loaderAdapter.swapCursor(this.c);
	}

	private View getHeaderView() {
		View headerView = LayoutInflater.from(this).inflate(
				R.layout.header_drawer, null);

		ParseUser user = ParseUser.getCurrentUser();
		ImageView empImage = (CircularImageView) headerView
				.findViewById(R.id.iv_empIm);
		DownLoadImageLoader loader = new DownLoadImageLoader(this);
		loader.DisplayImage(user.getString(EmpData.PIC_URL), empImage);
		TextView empName = (TextView) headerView.findViewById(R.id.tv_empName);
		TextView empDesignation = (TextView) headerView
				.findViewById(R.id.tv_empDesignation);
		empName.setText(user.getString(EmpData.FULL_NAME));
		empDesignation.setText(user.getString(EmpData.DESIGNATION));
		return headerView;
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			Logger.info(TAG, "List Item Clicked");
			displayView(position);
		}
	}

	@Override
	public String getActionTitle() {
		// TODO Auto-generated method stub
		return super.getActionTitle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.add_to_cart:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.add_to_cart).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */

	public void displayView(String roomName) {
		fragment = (HomeFragment) HomeFragment.getInstance(roomName);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void displayView(int position) {
		// update the main content by replacing fragments

		switch (position) {
		case 0:
			// fragment = (HomeFragment) HomeFragment.getInstance("HI");
			fragment = new XebiaAnalyticsFragment();
			break;

		default:
			if (this.c != null) {
				fragment = (HomeFragment) HomeFragment.getInstance(c
						.getString(c.getColumnIndex(RoomBeaconMap.ROOM_NAME)));
			}

			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			// mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		if (fragment != null) {
			mTitle = fragment.getActionTitle();
		}
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void bindView(View convertView, Context arg1, Cursor c) {

		convertView.setBackgroundResource(R.drawable.list_selector);
		TextView tv = (TextView) convertView.findViewById(R.id.tv_roomName);
		tv.setText(c.getString(c.getColumnIndex(RoomBeaconMap.ROOM_NAME)));
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) v;
				String text = tv.getText().toString().trim();
				displayView(text);
			}
		});
	}

}
