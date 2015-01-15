package com.android.vantage.Fragments;

import java.util.List;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.vantage.MessageDetailActivity;
import com.android.vantage.R;
import com.android.vantage.ListAdapters.CustomListAdapter;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantage.imagedownloadutil.DownLoadImageLoader;
import com.android.vantage.utility.CustomListAdapterInterface;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;
import com.fortysevendeg.swipelistview.SwipeListView;

public class SearchFragment extends BaseFragment implements TextWatcher,
		CustomListAdapterInterface, OnItemClickListener {

	private EditText search;
	private SwipeListView listView;
	private CustomListAdapter<ParseUserData> adapter;

	private List<ParseUserData> parseUsers, tempList;
	private DownLoadImageLoader loader;

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		search = (EditText) findView(R.id.et_search);
		search.addTextChangedListener(this);
		tempList = new Select().from(ParseUserData.class).execute();
		parseUsers = new Select().from(ParseUserData.class).execute();
		listView = (SwipeListView) findView(R.id.lv_swipe);
		loader = new DownLoadImageLoader(getActivity());
		setAdapter();
		listView.setOnItemClickListener(this);
	}

	private void setAdapter() {
		adapter = null;
		listView.removeAllViewsInLayout();
		adapter = new CustomListAdapter<ParseUserData>(getActivity(),
				R.layout.beacon_list_search_row, parseUsers, this);
		listView.setAdapter(adapter);

	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return R.layout.activity_search;
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		performFilterAndSetAdapter(arg0.toString());
	}

	private void performFilterAndSetAdapter(String filterText) {
		filterText = filterText.toLowerCase();

		parseUsers.clear();
		for (ParseUserData user : tempList) {
			String empId = user.getEmpId().toLowerCase();
			String empName = user.getFullName().toLowerCase();
			String empDesignation = user.getDesignation().toLowerCase();
			if (empId.contains(filterText) || empName.contains(filterText)
					|| empDesignation.contains(filterText)) {
				parseUsers.add(user);
			}
		}
		setAdapter();
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
		holder.empLocation.setText(user.getRoomName());
		String lastSeenString = user.getLastSeenAt();
		Logger.info(TAG, "Last Seen:" + lastSeenString);
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
			this.lastSeen = (TextView) v.findViewById(R.id.tv_lastSeen);
			this.empImage = (ImageView) v.findViewById(R.id.iv_empIm);
			this.empLocation = (TextView) v.findViewById(R.id.tv_roomName);

			this.tvCall = (TextView) v.findViewById(R.id.tv_call);
			this.tvPushMessage = (TextView) v.findViewById(R.id.tv_pushMessage);
			this.tvEmail = (TextView) v.findViewById(R.id.tv_email);
			this.frontLayout = (RelativeLayout) v.findViewById(R.id.front);

		}
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
		// show Dialog box
		showListDialog(user);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final AlertDialog dialog = showListDialog(parseUsers.get(arg2));
		dialog.show();
	}

}
