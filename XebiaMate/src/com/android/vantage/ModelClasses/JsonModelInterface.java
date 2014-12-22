package com.android.vantage.ModelClasses;

import java.util.ArrayList;

import org.json.JSONException;

public interface JsonModelInterface {
 public ArrayList<BaseModel> parseModel(String jsonString) throws JSONException;
}
