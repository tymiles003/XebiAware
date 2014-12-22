package com.android.vantage.ListAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CustomCursorAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private int resID;
	private CustomCursorAdapterInterface ref;

	public CustomCursorAdapter(Context context, Cursor c, boolean autoRequery,
			int resourceID, CustomCursorAdapterInterface ref) {
		super(context, c, autoRequery);

		inflater = LayoutInflater.from(context);
		resID = resourceID;
		this.ref = ref;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View convertView, Context arg1, Cursor c) {
		// TODO Auto-generated method stub
		ref.bindView(convertView, arg1, c);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return inflater.inflate(resID, null);
	}

	public interface CustomCursorAdapterInterface {
		public void bindView(View convertView, Context arg1, Cursor c);
	}

}
