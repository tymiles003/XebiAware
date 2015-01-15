package com.android.vantage.Fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import android.app.AlertDialog;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.android.vantage.MessageDetailActivity;
import com.android.vantage.R;
import com.android.vantage.ListAdapters.CustomCursorAdapter;
import com.android.vantage.ListAdapters.CustomCursorAdapter.CustomCursorAdapterInterface;
import com.android.vantage.ListAdapters.CustomListAdapter;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.EmpRecord;
import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantage.ModelClasses.RoomBeaconObject;
import com.android.vantage.exceptionhandler.RestException;
import com.android.vantage.imagedownloadutil.DownLoadImageLoader;
import com.android.vantage.utility.AppConstants;
import com.android.vantage.utility.CustomListAdapterInterface;
import com.android.vantage.utility.SharedPreferenceUtil;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class HomeFragment extends BaseFragment implements
		CustomCursorAdapterInterface, LoaderCallbacks<Cursor>,
		CustomListAdapterInterface, OnItemClickListener {

	DownLoadImageLoader loader;
	SwipeListView listView;

	CustomListAdapter<ParseUserData> listAdapter;

	String roomName;

	public static final String ALL_USERS = "All Users";
	public static final String ACTIVE_USERS = "Active Users";
	public static final String OFFLINE_USERS = "Offline Users";
	public static final String OUT_OF_RANGE = "Out Of Range";

	List<ParseUserData> parseUsers = new ArrayList<ParseUserData>();
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
		headerView = (TextView) findView(R.id.tv_roomHeader);
		listView = (SwipeListView) findView(R.id.lv_swipe);
		listView.setEmptyView(empty);
		// adapter = new CustomCursorAdapter(getActivity(), null, true,
		// R.layout.row_list_room_user, this);
		//
		// listAdapter = new CustomListAdapter<ParseUserData>(getActivity(),
		// R.layout.row_list_room_user, parseUsers, this);
		// listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

		loader = new DownLoadImageLoader(getActivity());
		downloadUserDetails(roomName);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		// setLastUpdated(LAST_UPDATED);

	}

	private String getHeaderString(int count) {
		return roomName + " Room Users (" + count + ")";
	}

	public void refreshData() {
		downloadUserDetails(roomName);
	}

	private void setAdapter() {
		listAdapter = null;
		listView.removeAllViewsInLayout();
		parseUsers = new Select().from(ParseUserData.class)
				.where(ParseUserData.ROOM_NAME + "  = ?", roomName).execute();
		listAdapter = new CustomListAdapter<ParseUserData>(getActivity(),
				R.layout.beacon_list_search_row, parseUsers, this);
		int count = parseUsers.size();
		if (count == 0) {
			headerView.setText("");
		} else {
			headerView.setText(getHeaderString(count));
		}
		headerView.setText(getHeaderString(parseUsers.size()));
		listView.setAdapter(listAdapter);
		
		listView.setOnItemClickListener(this);

	}

	@Override
	public void onBackgroundError(RestException re, Exception e, int taskCode,
			Object... params) {
		if (getActivity() != null && dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	public void onPreExecute(int taskCode) {
		showDialog();
		findView(R.id.rl_progressBar).setVisibility(View.VISIBLE);
		Logger.info(TAG, "On pre execute" + taskCode);

	}

	TextView headerView;

	private void setHeaderString() {

		headerView.setText(getHeaderString(10));
	}

	@Override
	public void onPostExecute(Object response, int taskCode, Object... params) {
		Logger.info(TAG, "On post execute" + taskCode);

		if (getActivity() == null) {
			return;
		}
		dialog.dismiss();
		findView(R.id.rl_progressBar).setVisibility(View.GONE);
		switch (taskCode) {
		case AppConstants.TASK_CODES.GET_PARSE_USER_OBJECT:
			SharedPreferenceUtil.getInstance(getActivity()).saveData(
					LAST_UPDATED, new Date().toString());
			// setLastUpdated(LAST_UPDATED);

			// if (response != null) {
			// parseUser((List<ParseUser>) response);
			// }
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
			parseUsers.add(ParseUserData.getUserFromParseUser(user));
		}
		setAdapter();
	}

	private void downloadUserDetails(String roomName) {

		ParseQuery<ParseUser> users = ParseUser.getQuery();
		//
		// if (roomName.equals(ALL_USERS)) {
		// parseUsers = new Select().from(ParseUserData.class).execute();
		// } else if (roomName.equals(OFFLINE_USERS)) {
		// parseUsers = new Select().from(ParseUserData.class)
		// .where(ParseUserData.IS_USER_ONLINE + " = ?", false)
		// .execute();
		// users.whereEqualTo(EmpRecord.IS_USER_ONLINE, false);
		// } else if (roomName.equals(ACTIVE_USERS)) {
		// parseUsers = new Select().from(ParseUserData.class)
		// .where(ParseUserData.IS_USER_ONLINE + " = ?", true)
		// .execute();
		// users.whereEqualTo(EmpRecord.IS_USER_ONLINE, true);
		// } else {
		// parseUsers = new Select().from(ParseUserData.class)
		// .where(ParseUserData.ROOM_NAME + " = ?", roomName)
		// .execute();
		// users.whereEqualTo(RoomBeaconObject.ROOM_NAME, roomName);
		// }
		setAdapter();
		executeTask(AppConstants.TASK_CODES.GET_PARSE_USER_OBJECT, users);
	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return R.layout.fragment_home;
	}

	@Override
	public void bindView(View convertView, Context arg1, Cursor c) {

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new android.support.v4.content.CursorLoader(getActivity(),
				ContentProvider.createUri(ParseUserData.class, null), null,
				ParseUserData.ROOM_NAME + " = ?", new String[] { roomName },
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		// TODO Auto-generated method stub
		Logger.info(TAG, "On Load Finished");
		if (c != null && c.getCount() > 0) {
			Logger.info(TAG, "Update UI");
		}

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
		ParseUserData user = parseUsers.get(position);
		loader.DisplayImage(user.getPicUrl() + "", holder.empImage);

		// if (user.getIsUserOnline()) {
		// holder.iconOnline.setVisibility(View.VISIBLE);
		// } else {
		// holder.iconOnline.setVisibility(View.GONE);
		// }

		holder.empName.setText(user.getFullName() == null ? "Name : NA" : user
				.getFullName());
		holder.empDesignation
				.setText(user.getDesignation() == null ? "Designation : NA"
						: user.getDesignation());
		 String lastSeenString = user.getLastSeenAt();
		 holder.lastSeen.setText(Util.convertDateFormat(lastSeenString,
		 Util.REQUIRE_DATE_PATTERN));

		// holder.tvCall.setTag(position);
		// holder.tvEmail.setTag(position);
		// holder.tvPushMessage.setTag(position);

		setTagAndListener(position, holder.tvCall, holder.tvEmail,
				holder.tvPushMessage, holder.frontLayout);

		return convertView;
	}

	private void setTagAndListener(int position, View... views) {
		for (View v : views) {
			v.setTag(position);
			v.setOnClickListener(swipeOptionListener);
		}
	}

	private View.OnClickListener swipeOptionListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			int position = (Integer) v.getTag();
			TextView view = (TextView) v;
			Logger.info(TAG,
					"Clicked on :" + position + " : String :" + view.getText());

			onSwipeOptionsClicked(v.getId(), parseUsers.get(position));
		}
	};

	private class ViewHolder {
		TextView empName, empStatus, empLocation, empDesignation, lastSeen;
		ImageView empImage, iconOnline;

		TextView tvCall, tvPushMessage, tvEmail;
		RelativeLayout frontLayout;

		public ViewHolder(View v) {
			this.empName = (TextView) v.findViewById(R.id.tv_empName);
			this.empDesignation = (TextView) v
					.findViewById(R.id.tv_empDesignation);
			// this.iconOnline = (ImageView)
			// v.findViewById(R.id.iv_icon_online);
			//
			this.empLocation = (TextView) v.findViewById(R.id.tv_roomName);
			this.empLocation.setVisibility(View.GONE);
			this.lastSeen = (TextView) v.findViewById(R.id.tv_lastSeen);
			this.empImage = (ImageView) v.findViewById(R.id.iv_empIm);

			this.tvCall = (TextView) v.findViewById(R.id.tv_call);
			this.tvPushMessage = (TextView) v.findViewById(R.id.tv_pushMessage);
			this.tvEmail = (TextView) v.findViewById(R.id.tv_email);
			this.frontLayout = (RelativeLayout) v.findViewById(R.id.front);

		}
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

	private void startMessageDetailActivity(ParseUserData user) {
		Intent i = new Intent(getActivity(), MessageDetailActivity.class);
		i.putExtra(EmpData.EMP_ID, user.getEmpId());
		i.putExtra(EmpData.FULL_NAME, user.getFullName());
		startActivity(i);
	}

	private void callUser(ParseUserData user) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_CALL);
		i.setData(Uri.parse(Util.getCombinedString("tel:", user.getAreaCode(),
				user.getMobileNumber())));
		startActivity(i);
	}

	private void emailUser(ParseUserData user) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("*/*");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { user.getEmail() });
		i.putExtra(Intent.EXTRA_SUBJECT, "From XebiAware");
		i.putExtra(Intent.EXTRA_TEXT, "Hi");

		startActivity(createEmailOnlyChooserIntent(i, "Send via email"));
	}

	public void onSwipeOptionsClicked(int id, ParseUserData user) {
		switch (id) {
		case R.id.tv_call:
			callUser(user);
			break;
		case R.id.tv_pushMessage:
			startMessageDetailActivity(user);
			break;
		case R.id.tv_email:
			emailUser(user);
			break;
		case R.id.front:
			showUserProfile(user);
			break;
		default:
			break;

		}
	}

	private void showUserProfile(ParseUserData user) {

	}

	private AlertDialog showListDialog(final ParseUserData user) {
		final CharSequence[] items = new CharSequence[3];
		items[0] = Util.getCombinedString("Call : ", user.getAreaCode(), " - ",
				user.getMobileNumber());
		items[1] = "Send Push Message";
		items[2] = Util.getCombinedString("Email : " + user.getEmail());

		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(user.getFullName()).setItems(items,
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
