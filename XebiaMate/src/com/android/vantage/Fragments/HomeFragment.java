package com.android.vantage.Fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vantage.MessageDetailActivity;
import com.android.vantage.R;
import com.android.vantage.ListAdapters.CustomCursorAdapter;
import com.android.vantage.ListAdapters.CustomCursorAdapter.CustomCursorAdapterInterface;
import com.android.vantage.ListAdapters.CustomListAdapter;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.EmpRecord;
import com.android.vantage.ModelClasses.RoomBeaconMap;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.imagedownloadutil.DownLoadImageLoader;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.CustomListAdapterInterface;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class HomeFragment extends BaseFragment implements
		CustomCursorAdapterInterface, LoaderCallbacks<Cursor>,
		CustomListAdapterInterface, Callback, OnItemClickListener {

	DownLoadImageLoader loader;
	GridView grid;

	CustomCursorAdapter adapter;

	CustomListAdapter<ParseUser> listAdapter;

	String roomName;

	public static final String ALL_USERS = "All Users";
	public static final String ACTIVE_USERS = "Active Users";
	public static final String OFFLINE_USERS = "Offline Users";
	public static final String OUT_OF_RANGE = "Out Of Range";

	List<ParseUser> parseUsers = new ArrayList<ParseUser>();
	Handler h;

	public static Fragment getInstance(String roomName) {
		Fragment f = new HomeFragment();
		Bundle b = new Bundle();
		b.putString(AppConstants.BUNDLE_KEYS.KEY_ROOM_NAME, roomName);
		f.setArguments(b);
		return f;
	}

	@Override
	public String getActionTitle() {
		// TODO Auto-generated method stub
		return roomName;
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		roomName = getArguments().getString(
				AppConstants.BUNDLE_KEYS.KEY_ROOM_NAME);
		TextView empty = (TextView) findView(android.R.id.empty);

		grid = (GridView) findView(R.id.gridEmployees);
		grid.setEmptyView(empty);
		adapter = new CustomCursorAdapter(getActivity(), null, true,
				R.layout.row_emp_grid, this);
		listAdapter = new CustomListAdapter<ParseUser>(getActivity(),
				R.layout.row_emp_grid, parseUsers, this);
		grid.setAdapter(listAdapter);
		grid.setOnItemClickListener(this);
		h = new Handler(this);

		loader = new DownLoadImageLoader(getActivity());
		downloadUserDetails(roomName);
		setActionTitle(roomName);
		setLastUpdated(LAST_UPDATED);

	}

	public void refreshData() {
		downloadUserDetails(roomName);
	}

	private void setAdapter() {
		listAdapter = new CustomListAdapter<ParseUser>(getActivity(),
				R.layout.row_emp_grid, parseUsers, this);

		grid.setAdapter(listAdapter);
	}

	@Override
	public void onBackgroundError(RestException re, Exception e, int taskCode,
			Object... params) {
		if (getActivity() != null) {
			dialog.dismiss();
		}
		super.onBackgroundError(re, e, taskCode, params);
	}

	@Override
	public void onPreExecute(int taskCode) {
		showDialog();
		findView(R.id.rl_progressBar).setVisibility(View.VISIBLE);
		Logger.info(TAG, "On pre execute" + taskCode);

	}

	@Override
	public void onPostExecute(Object response, int taskCode, Object... params) {
		Logger.info(TAG, "On post execute" + taskCode);
		if (dialog != null)
			dialog.dismiss();
		if (getActivity() == null) {
			return;
		}
		findView(R.id.rl_progressBar).setVisibility(View.GONE);
		switch (taskCode) {
		case AppConstants.TASK_CODES.GET_PARSE_USER_OBJECT:
			SharedPreferenceUtil.getInstance(getActivity()).saveData(
					LAST_UPDATED, new Date().toString());
			setLastUpdated(LAST_UPDATED);

			if (response != null) {
				parseUser((List<ParseUser>) response);
			}
			break;

		default:
			break;
		}

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

	private void parseUser(List<ParseUser> pUsers) {
		parseUsers.clear();
		for (ParseUser user : pUsers) {
			Logger.info(TAG, "RoomName" + user.getString("RoomName"));
		}
		parseUsers = pUsers;
		setAdapter();
	}

	private void downloadUserDetails(String roomName) {

		ParseQuery<ParseUser> users = ParseUser.getQuery();

		if (roomName.equals(ALL_USERS)) {

		} else if (roomName.equals(OFFLINE_USERS)) {
			users.whereEqualTo(EmpRecord.IS_USER_ONLINE, false);
		} else if (roomName.equals(ACTIVE_USERS)) {
			users.whereEqualTo(EmpRecord.IS_USER_ONLINE, true);
		} else {
			users.whereEqualTo(RoomBeaconMap.ROOM_NAME, roomName);
		}
		executeTask(AppConstants.TASK_CODES.GET_PARSE_USER_OBJECT, users);
	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return R.layout.fragment_home;
	}

	@Override
	public void bindView(View convertView, Context arg1, Cursor c) {
		// TODO Auto-generated method stub

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

	@Override
	public View getView(int position, View convertView, ViewGroup parent,
			int resourceID) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(getActivity()).inflate(
					resourceID, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		ParseUser user = parseUsers.get(position);
		loader.DisplayImage(user.getString(EmpData.PIC_URL) + "",
				holder.empImage);

		if (user.getBoolean(EmpRecord.IS_USER_ONLINE)) {
			holder.iconOnline.setVisibility(View.VISIBLE);
		} else {
			holder.iconOnline.setVisibility(View.GONE);
		}

		holder.empName
				.setText(user.getString(EmpData.FULL_NAME) == null ? "Name : NA"
						: user.getString(EmpData.FULL_NAME));
		holder.empDesignation
				.setText(user.getString(EmpData.DESIGNATION) == null ? "Designation : NA"
						: user.getString(EmpData.DESIGNATION));
		String lastSeenString = user.getString(EmpRecord.LAST_SEEN);
		holder.lastSeen.setText(Util.convertDateFormat(lastSeenString,
				Util.REQUIRE_DATE_PATTERN));

		return convertView;
	}

	private class ViewHolder {
		TextView empName, empStatus, empLocation, empDesignation, lastSeen;
		ImageView empImage, iconOnline;

		public ViewHolder(View v) {
			this.empName = (TextView) v.findViewById(R.id.tv_empName);
			this.empDesignation = (TextView) v
					.findViewById(R.id.tv_empDesignation);
			this.iconOnline = (ImageView) v.findViewById(R.id.iv_icon_online);

			this.lastSeen = (TextView) v.findViewById(R.id.tv_lastSeen);
			this.empImage = (ImageView) v.findViewById(R.id.iv_empIm);

		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		findView(R.id.rl_progressBar).setVisibility(View.GONE);
		switch (msg.what) {
		case 0:
			setAdapter();
			break;

		default:
			break;
		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final AlertDialog dialog = showListDialog(parseUsers.get(arg2));
		dialog.show();
		
		// Intent i = new Intent(getActivity(), MessageDetailActivity.class);
		// i.putExtra(EmpData.EMP_ID, parseUsers.get(arg2).getUsername());
		// i.putExtra(EmpData.FULL_NAME,
		// parseUsers.get(arg2).getString(EmpData.FULL_NAME));
		// startActivity(i);
	}

	private void startMessageDetailActivity(ParseUser user) {
		Intent i = new Intent(getActivity(), MessageDetailActivity.class);
		i.putExtra(EmpData.EMP_ID, user.getUsername());
		i.putExtra(EmpData.FULL_NAME, user.getString(EmpData.FULL_NAME));
		startActivity(i);
	}

	private void callUser(ParseUser user) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_CALL);
		i.setData(Uri.parse(Util.getCombinedString("tel:",
				user.getString(EmpData.AREA_CODE),
				user.getString(EmpData.MOBILE_NUMBER))));
		startActivity(i);
	}

	private void emailUser(ParseUser user) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("*/*");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { user.getString(EmpData.EMAIL) });
		i.putExtra(Intent.EXTRA_SUBJECT, "From XebiAware");
		i.putExtra(Intent.EXTRA_TEXT, "Hi");

		startActivity(createEmailOnlyChooserIntent(i, "Send via email"));
	}

	private AlertDialog showListDialog(final ParseUser user) {
		final CharSequence[] items = new CharSequence[3];
		items[0] = Util.getCombinedString("Call : ",
				user.getString(EmpData.AREA_CODE), " - ",
				user.getString(EmpData.MOBILE_NUMBER));
		items[1] = "Send Push Message";
		items[2] = Util.getCombinedString("Email : "
				+ user.getString(EmpData.EMAIL));

		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(user.getString(EmpData.FULL_NAME)).setItems(items,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							callUser(user);
							break;
						case 1:
							startMessageDetailActivity(user);
							break;
						case 2:
							emailUser(user);
							break;

						default:
							break;
						}

					}

				});

		AlertDialog dialog = builder.create();
	
		return dialog;
		

	}

	public Intent createEmailOnlyChooserIntent(Intent source,
			CharSequence chooserTitle) {
		Stack<Intent> intents = new Stack<Intent>();
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
				"info@domain.com", null));
		List<ResolveInfo> activities = getActivity().getPackageManager()
				.queryIntentActivities(i, 0);

		for (ResolveInfo ri : activities) {
			Intent target = new Intent(source);
			target.setPackage(ri.activityInfo.packageName);
			intents.add(target);
		}

		if (!intents.isEmpty()) {
			Intent chooserIntent = Intent.createChooser(intents.remove(0),
					chooserTitle);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					intents.toArray(new Parcelable[intents.size()]));

			return chooserIntent;
		} else {
			return Intent.createChooser(source, chooserTitle);
		}
	}

}
