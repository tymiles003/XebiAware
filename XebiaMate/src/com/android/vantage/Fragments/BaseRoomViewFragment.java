package com.android.vantage.Fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.android.vantage.XebiaRoomViewActivity;
import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantageLogManager.Logger;

public abstract class BaseRoomViewFragment extends BaseFragment implements
		LoaderCallbacks<Cursor> {

	protected HashMap<String, Long> roomToCountMap = new HashMap<String, Long>();
	protected List<ParseUserData> users = new ArrayList<ParseUserData>();
	protected String maxOccupiedRoom, minOccupiedRoom;

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		((XebiaRoomViewActivity) getActivity()).getAllUsers();
	}

	protected void initRoomCountMap() {
		roomToCountMap.clear();
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
		initMaxMinRooms();
	}

	protected void initMaxMinRooms() {
		long max = Long.MAX_VALUE, min = Long.MIN_VALUE;
		for (Entry<String, Long> entry : roomToCountMap.entrySet()) {
			Logger.info(TAG, entry.getKey() + ":" + entry.getValue());
			if (entry.getValue() > max) {
				maxOccupiedRoom = entry.getKey();
				max = entry.getValue();
			}
			if (entry.getValue() <= min) {
				minOccupiedRoom = entry.getKey();
				min = entry.getValue();
			}
		}
	}

	protected void initLoderManager() {
		getLoaderManager().initLoader(1, null, this);
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
		Logger.info(TAG, "On Load Finished");
		if (c != null && c.getCount() > 0) {
			Logger.info(TAG, "Update UI");
			updateUI();
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		updateUI();
	}

	protected void updateUI() {
		users = new Select().from(ParseUserData.class).execute();
		if (users == null)
			return;
		Logger.info(TAG, users.size() + "");
		initRoomCountMap();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return 0;
	}
}
