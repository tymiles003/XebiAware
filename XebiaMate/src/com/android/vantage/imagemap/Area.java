package com.android.vantage.imagemap;

import java.io.Serializable;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class Area implements Serializable{
	int _id;
	String _name;
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	HashMap<String, String> _values;
	Bitmap bitmap = null;
	protected int bitmapType = RoomArea.BITMAP_TYPE_MARKER;

	public Area(int id, String name) {
		_id = id;
		if (name != null) {
			_name = name;
		}
	}

	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	// all xml values for the area are passed to the object
	// the default impl just puts them into a hashmap for
	// retrieval later
	public void addValue(String key, String value) {
		if (_values == null) {
			_values = new HashMap<String, String>();
		}
		_values.put(key, value);
	}

	public String getValue(String key) {
		String value = null;
		if (_values != null) {
			value = _values.get(key);
		}
		return value;
	}

	// a method for setting a simple decorator for the area
	public void setBitmap(Bitmap b) {
		bitmap = b;
	}

	// an onDraw is set up to provide an extensible way to
	// decorate an area. When drawing remember to take the
	// scaling and translation into account
	float prevScaleX, prevScaleY;
	boolean isFirstTime = true;

	public void draw(Canvas canvas, Resources res, float scaleX,
			float scaleY, float transX, float transY) {
		// Log.e("Drawing Area", "" + _decoration);

	}

	abstract boolean isInArea(float x, float y);

	abstract float getOriginX();

	public abstract void setOriginX(float x);

	public abstract void setOriginY(float y);

	abstract float getOriginY();

}