package com.android.vantage;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.android.vantage.ListAdapters.CustomCursorAdapter;
import com.android.vantage.ListAdapters.CustomCursorAdapter.CustomCursorAdapterInterface;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.Message;
import com.android.vantage.utility.Util;

public class MessageDetailActivity extends BaseActivity implements
		CustomCursorAdapterInterface {

	ListView list;
	CustomCursorAdapter adapter;
	String empId, empName;
	EditText msgBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);
		empId = getIntent().getStringExtra(EmpData.EMP_ID);
		empName = getIntent().getStringExtra(EmpData.FULL_NAME);
		adapter = new CustomCursorAdapter(this, null, true,
				R.layout.row_message_detail, this);
		list = (ListView) findViewById(R.id.lv_model);
		list.setAdapter(adapter);
		list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setAdapter(adapter);
		getSupportLoaderManager().initLoader(0, null, this);
		msgBox = (EditText) findViewById(R.id.et_sendMessage);
		findViewById(R.id.btn_sendMessage).setOnClickListener(this);

		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayUseLogoEnabled(false);
		setActionTitle(empName);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_sendMessage:
			String msg = msgBox.getText().toString().trim();
			if (!msg.isEmpty()) {

				Util.sendPushMessage(msg, empId, MessageDetailActivity.this);
				msgBox.setText("");
				Util.hideKeyboard(this);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(MessageDetailActivity.this,
				ContentProvider.createUri(Message.class, null), null,
				Message.FROM_XEBIA_ID + " = ?", new String[] { empId }, null);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		// TODO Auto-generated method stub
		if (c != null && c.getCount() > 0) {
			adapter.swapCursor(c);
		}
	}

	@Override
	public void bindView(View convertView, Context arg1, Cursor c) {
		ViewHolder holder = new ViewHolder(convertView);
		RelativeLayout rl = (RelativeLayout) convertView
				.findViewById(R.id.rl_messageAlignment);
		String msgType = c.getString(c.getColumnIndex(Message.MESSAGE_TYPE));
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (msgType.equalsIgnoreCase(Message.OUTGOING_MESSAGE)) {

			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			params.setMargins(20, 10, 0, 0);
			holder.msgText.setGravity(Gravity.RIGHT);
			rl.setBackgroundResource(R.drawable.balloon_outgoing_normal);

		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			params.setMargins(0, 10, 20, 0);
			rl.setBackgroundResource(R.drawable.balloon_incoming_focused);
		}
		rl.setLayoutParams(params);
		holder.msgText.setText(c.getString(c
				.getColumnIndex(Message.MESSAGE_TEXT)));
		String timeString = c.getString(c.getColumnIndex(Message.TIME_STAMP));

		holder.msgTime.setText(Util.convertDateFormat(timeString,
				Util.REQUIRE_DATE_PATTERN));

	}

	private class ViewHolder {
		TextView msgText, msgTime;

		public ViewHolder(View v) {
			msgText = (TextView) v.findViewById(R.id.tv_msgText);
			msgTime = (TextView) v.findViewById(R.id.tv_msgTime);
		}
	}
}
