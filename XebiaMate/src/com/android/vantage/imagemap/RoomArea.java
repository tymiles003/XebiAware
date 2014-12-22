package com.android.vantage.imagemap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.activeandroid.query.Select;
import com.android.vantage.R;
import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantage.utility.Util;
import com.android.vantageLogManager.Logger;

public class RoomArea extends Area implements Serializable{
	public float x, width;
	public float y, height;
	public float _right;
	public float _bottom;
	float currentX, currentY, scaleX, scaleY;
	String assetType;
	private RectF currentRect;

	public static final int EMP_MARKER_DOT = 1, EMP_MARKER_BITMAP = 2;
	public int markerType;
	public ArrayList<EmpMarker> employeeMarkers;

	public RectF getCurrentRect() {
		return currentRect;
	}

	public void setCurrentRect(RectF currentRect) {
		this.currentRect = currentRect;
	}

	Paint paint;

	public static final int BITMAP_TYPE_MARKER = 1;

	public RoomArea(int id, String name, float left, float top, float width,
			float height) {
		this(id, name, left, top, width, height, EMP_MARKER_DOT);
	}

	public RoomArea(int id, String name, float left, float top, float width,
			float height, int empMarkerType) {
		super(id, name);
		x = left;
		y = top;
		_right = x + width;
		_bottom = y + height;
		scaleX = scaleY = 1.0f;
		this.width = width;
		this.height = height;
		markerType = empMarkerType;
		employeeMarkers = new ArrayList<EmpMarker>();
		initEmployeeMarkers();
		paint = new Paint();

		// paint.setAlpha(10);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
	}

	private void initEmployeeMarkers() {
		int count = new Select().from(ParseUserData.class).count();
		List<ParseUserData> emps = new Select().from(ParseUserData.class)
				.execute();
		Logger.info(this.getClass().getSimpleName(), emps.size()+", All Count, "+count+", "+this.get_name());
		for(ParseUserData emp : emps){
			Logger.info(this.getClass().getSimpleName(), "RoomName"+emp.getRoomName());
			float randX = Util.randInt(1, 100)/100;
			float randY = Util.randInt(1, 100)/100;
			EmpMarker marker = new EmpMarker(randX, randY, emp);
			employeeMarkers.add(marker);
		}

	}

	// public RoomArea(int id, String assetNumber, float left, float top,
	// float right, float bottom, String assetType) {
	// super(id, assetNumber);
	// x = left;
	// y = top;
	// _right = right;
	// _bottom = bottom;
	// this.assetType = assetType;
	// }
	//
	// public RoomArea(int id, String assetNumber, float left, float top,
	// float right, float bottom, String assetType, int bitmapType) {
	// super(id, assetNumber);
	// x = left;
	// y = top;
	// _right = right;
	// _bottom = bottom;
	// this.assetType = assetType;
	// this.bitmapType = bitmapType;
	// }

	public boolean isInArea(float x, float y) {
		boolean ret = false;
		RectF rect = new RectF(x, y, x + 2, y + 2);
		if (RectF.intersects(rect, currentRect)) {
			ret = true;
		}
		// if (x > currentX && x < currentX + _right - x) {
		// if (y > currentY && y < currentY + _bottom - y) {
		// ret = true;
		// }
		// }
		Logger.info("Room Area", "Is in Area :" + ret);
		return ret;
	}

	@Override
	public void draw(Canvas canvas, Resources res, float scaleX, float scaleY,
			float transX, float transY) {
		// TODO Auto-generated method stub
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		paint.setColor(res.getColor(R.color.xebia_color));
		float left = currentX = this.x * scaleX + transX;
		float top = currentY = this.y * scaleY + transY;
		float right = left + (this.width * scaleX);
		float bottom = top + (this.height * scaleY);

		// Bitmap bMap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.abs__list_focused_holo);
		// bMap = Bitmap.createScaledBitmap(bMap,
		// (int)(room.width*scaledFloorWidth),
		// (int)(room.height*scaledFloorHeight), true);
		currentRect = new RectF(left, top, right, bottom);

		canvas.drawRoundRect(currentRect, 10.0f, 10.0f, paint);

		// if (bitmapType == BITMAP_TYPE_MARKER) {
		// currentX = getOriginX() * scaleX - (bitmap.getWidth() / 2) + transX;
		// currentY = getOriginY() * scaleY - (bitmap.getHeight() / 2 + 22)
		// + transY;
		// canvas.drawBitmap(bitmap, currentX, currentY, null);
		// } else {
		// canvas.drawBitmap(bitmap, scaleX * getOriginX()+(64*(scaleX-1)) +
		// transX,
		// getOriginY() * scaleY + (scaleY - 1)
		// * (bitmap.getHeight()-100) + transY, null);
		// }
		super.draw(canvas, res, scaleX, scaleY, transX, transY);
	}

	public float getOriginX() {
		return x;
	}

	public float getOriginY() {
		return y;
	}

	@Override
	public void setOriginX(float x) {
		// TODO Auto-generated method stub
		x = x;
	}

	@Override
	public void setOriginY(float y) {
		// TODO Auto-generated method stub

		y = y;
	}
}