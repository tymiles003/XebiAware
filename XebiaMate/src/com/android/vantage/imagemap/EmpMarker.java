package com.android.vantage.imagemap;

import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.android.vantage.ModelClasses.EmpData;
import com.android.vantage.ModelClasses.ParseUserData;
import com.android.vantageLogManager.Logger;

public class EmpMarker implements Serializable {

	public float x, y;
	public Bitmap bMap;
	ParseUserData data;

	Paint p;

	private Paint getPaint() {
		if (p != null)
			return p;
		p = new Paint();
		p.setColor(Color.BLACK);
		p.setStyle(Paint.Style.FILL);
		return p;
	}

	public EmpMarker(float x, float y, Bitmap bMap, ParseUserData d) {
		this.x = x;
		this.y = y;
		this.bMap = bMap;
		this.data = d;
	}

	public EmpMarker(float x, float y, ParseUserData d) {
		this(x, y, null, d);
	}

	public void drawEmployeeMarker(Canvas c, Resources resources,
			float scaledFloorWidth, float scaledFloorHeight, float transX,
			float transY, RoomArea room) {
		Logger.info(this.getClass().getSimpleName(), "DrawingEmployeeMarker");
		float cx = (room.x + x * room.width) * scaledFloorWidth;
		float cy = (room.y + y * room.height) * scaledFloorHeight;
		c.drawCircle(cx + transX, cy + transY, 7, getPaint());
	}

}
