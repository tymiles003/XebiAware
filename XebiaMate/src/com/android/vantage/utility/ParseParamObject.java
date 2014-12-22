package com.android.vantage.utility;

public class ParseParamObject {

	public static final String prefixParseURL = "https://api.parse.com/1/classes/";

	private String tableName="", queryJsonString="";

	public ParseParamObject() {

	}

	public ParseParamObject(String tName, String queryString) {
		this.tableName = tName;
		this.queryJsonString = queryString;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getQueryJsonString() {
		return queryJsonString;
	}

	public void setQueryJsonString(String queryJsonString) {
		this.queryJsonString = queryJsonString;
	}

	public String buildURL() {
		StringBuilder builder = new StringBuilder(prefixParseURL);
		builder.append(this.tableName).append("?").append(this.queryJsonString);

		return builder.toString();
	}

}
