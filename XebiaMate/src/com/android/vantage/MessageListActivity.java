package com.android.vantage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.android.vantage.ListAdapters.CustomCursorAdapter;
import com.android.vantage.ListAdapters.CustomCursorAdapter.CustomCursorAdapterInterface;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.Message;
import com.android.vantage.imagedownloadutil.DownLoadImageLoader;
import com.android.vantage.utility.Util;
import com.parse.ParseUser;

public class MessageListActivity extends BaseActivity implements
		CustomCursorAdapterInterface {

	ListView list;
	CustomCursorAdapter adapter;

	DownLoadImageLoader loader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_model);
		list = (ListView) findViewById(R.id.lv_model);
		adapter = new CustomCursorAdapter(this, null, true,
				R.layout.row_message, this);
		list.setAdapter(adapter);
		loader = new DownLoadImageLoader(this);
		getSupportLoaderManager().initLoader(0, null, this);
		ParseUser user = ParseUser.getCurrentUser();
		if(user != null){
			setActionTitle(user.getString(EmpData.FULL_NAME));
			getSupportActionBar().setSubtitle(user.getString(EmpData.DESIGNATION));
		}else{
			setActionTitle("Messages");
		}
		

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(MessageListActivity.this, ContentProvider.createUri(EmpData.class,
				null), null, Message.MESSAGE_TEXT + " IS NOT NULL OR "+Message.MESSAGE_TEXT + " != ''", null, null);
	}

	private void saveNewMessage() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_list, menu);
		return true;
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

	@Override
	public void bindView(View convertView, Context arg1, Cursor c) {
		ViewHolder holder = new ViewHolder(convertView);
		EmpData data = EmpData.getEmpDataFromCursor(c);
		String xebiaId = c.getString(c.getColumnIndex(EmpData.EMP_ID));
		String timeStamp = c.getString(c.getColumnIndex(Message.TIME_STAMP));
		String message = c.getString(c.getColumnIndex(Message.MESSAGE_TEXT));
		String name = c.getString(c.getColumnIndex(EmpData.FULL_NAME));
		String url = c.getString(c.getColumnIndex(EmpData.PIC_URL));

		holder.empName.setText(name);
		holder.timeStamp.setText(Util.convertDateFormat(timeStamp, Util.REQUIRE_DATE_PATTERN));
		holder.empMessage.setText(message);

		loader.DisplayImage(url, holder.empImage);
		
		convertView.setTag(data);
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EmpData c = (EmpData) v.getTag();
				
						// (String) v.getTag();
				Intent i = new Intent(MessageListActivity.this, MessageDetailActivity.class);
				i.putExtra(EmpData.EMP_ID, c.getEmpId());
				i.putExtra(EmpData.FULL_NAME, c.getFullName());
				startActivity(i);
			}
		});

	}

	private class ViewHolder {
		TextView empName, empMessage, timeStamp, currentRoom;
		ImageView empImage;

		public ViewHolder(View v) {
			empName = (TextView) v.findViewById(R.id.tv_empName);
			empMessage = (TextView) v.findViewById(R.id.tv_empMessage);
			timeStamp = (TextView) v.findViewById(R.id.tv_msgTime);
			currentRoom = (TextView) v.findViewById(R.id.tv_currentRoom);
			empImage = (ImageView) v.findViewById(R.id.iv_empImage);
		}
	}
}
