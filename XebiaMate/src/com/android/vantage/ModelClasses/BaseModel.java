package com.android.vantage.ModelClasses;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ConflictAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class BaseModel extends Model implements JsonModelInterface {

	public static final String OBJECT_ID = "objectId";
	@Column
	@Expose
	protected String createdAt;
	@Column
	@Expose
	protected String updatedAt;
	
	//
	
	@Column(name = "objectId", unique = true, onUniqueConflict = ConflictAction.REPLACE)
	@Expose
	protected String objectId;

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Override
	public ArrayList<BaseModel> parseModel(String jsonString) throws JSONException {
		// TODO Auto-generated method stub
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		JSONObject root = new JSONObject(jsonString);

		JSONArray array = root.optJSONArray("results");

		ArrayList<BaseModel> list;
		if (array != null && array.length()>0) {
			list = new ArrayList<BaseModel>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				BaseModel model = getModelFromString(obj.toString(), gson);
				list.add(model);
			}
		} else {
			throw new JSONException("Invalid Json String");
		}

		return null;
	}
	
	public BaseModel getModelFromString(String jsonString, Gson gson){
		return null;
	}

}
