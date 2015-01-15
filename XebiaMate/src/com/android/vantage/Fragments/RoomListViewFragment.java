package com.android.vantage.Fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.vantage.FragmentHolderActivity;
import com.android.vantage.R;
import com.android.vantage.ListAdapters.CustomListAdapter;
import com.android.vantage.ModelClasses.RoomBeaconObject;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.CustomListAdapterInterface;
import com.mikhaellopez.circularimageview.DottedImageView;

public class RoomListViewFragment extends BaseRoomViewFragment implements
		CustomListAdapterInterface, OnItemClickListener {

	private ListView listView;
	private CustomListAdapter<RoomBeaconObject> adapter;
	private List<RoomBeaconObject> roomsList;

	private int[] colorArray = new int[] { R.color.beacon_row_color_1,
			R.color.beacon_row_color_2, R.color.beacon_row_color_3,
			R.color.beacon_row_color_4, R.color.beacon_row_color_5 };

	@Override
	public void initViews() {

		listView = (ListView) findView(R.id.lv_model);
		initLoderManager();
		setAdapter();
		listView.setOnItemClickListener(this);
		TextView emptyView = (TextView) findView(android.R.id.empty);
		listView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
	}

	private void setAdapter() {
		adapter = null;

		int count = 0;
		roomsList = new ArrayList<RoomBeaconObject>();
		List<RoomBeaconObject> tempList = new Select().from(
				RoomBeaconObject.class).execute();
		Iterator<RoomBeaconObject> it = tempList.iterator();
		while (it.hasNext()) {
			RoomBeaconObject b = it.next();
			if (roomToCountMap.containsKey(b.getRoomName())) {
				roomsList.add(b);
			}

		}
		tempList.clear();
		adapter = new CustomListAdapter(getActivity(), R.layout.beacon_row,
				roomsList, this);
		listView.setAdapter(adapter);
	}

	@Override
	public int getViewID() {
		return R.layout.list_view_model;
	}

	@Override
	protected void updateUI() {
		super.updateUI();
		setAdapter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent,
			int resourceID) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getActivity()).inflate(
					R.layout.beacon_row, null);
			holder = new ViewHolder(convertView);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setBackgroundColor(getResources().getColor(
				colorArray[position % colorArray.length]));
		String roomNameString = roomsList.get(position).getRoomName();
		holder.roomName.setText(roomNameString);
		Long count = roomToCountMap.get(roomNameString);
		if (count == null) {
			holder.roomCount.setText("Empty");
		} else {
			holder.backImage.setCircleCount(count);
			holder.roomCount.setText(roomToCountMap.get(roomNameString)
					.toString());
		}

		return convertView;
	}

	private class ViewHolder {
		TextView roomName, roomCount;
		ImageView minMaxImage;
		DottedImageView backImage;

		public ViewHolder(View v) {
			roomName = (TextView) v.findViewById(R.id.tv_roomName);
			roomCount = (TextView) v.findViewById(R.id.tv_roomCount);
			minMaxImage = (ImageView) v.findViewById(R.id.imgMinMax);
			backImage = (DottedImageView) v.findViewById(R.id.iv_back);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Bundle b = new Bundle();
		b.putString(AppConstants.BUNDLE_KEYS.KEY_ROOM_NAME, roomsList.get(arg2)
				.getRoomName());
		b.putInt(AppConstants.BUNDLE_KEYS.FRAGMENT_TYPE,
				FragmentHolderActivity.FRAGMENT_TYPE_HOME);
		Intent i = new Intent(getActivity(), FragmentHolderActivity.class);
		i.putExtras(b);
		startActivity(i);

	}

}
