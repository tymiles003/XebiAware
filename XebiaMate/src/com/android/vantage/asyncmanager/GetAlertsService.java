package com.android.vantage.asyncmanager;

import org.json.JSONException;
import android.content.Context;

public class GetAlertsService extends GetParseJsonService {

	public GetAlertsService(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Object parseJsonData(String jsonStr) throws JSONException {
//		JSONObject rootObj = new JSONObject(jsonStr);
//Log.e("JSON Alerts Data", jsonStr+"");
//		JSONArray array = rootObj.getJSONArray("results");
//		if (array != null) {
//			ArrayList<ExamDetails> alertsList = new ArrayList<ExamDetails>(
//					array.length());
//			for (int i = 0; i < array.length(); i++) {
//				JSONObject obj = array.getJSONObject(i);
//				ExamDetails alert = ExamDetails.parseJsonString(obj);
//				DBAdapter adapter = new DBAdapter(context);
//				adapter.open();
//				alertsList.add(alert);
//				long index = adapter.insertSerializableObject(
//						DBConstants.Table_BLOB_EXAM_DETAILS, alert);
//				adapter.close();
//				Logger.info(GetAlertsService.class.getSimpleName(),
//						"Value inserted or not" + index);
//
//			}
//			return alertsList;
//		}
//
		return null;

	}

}
