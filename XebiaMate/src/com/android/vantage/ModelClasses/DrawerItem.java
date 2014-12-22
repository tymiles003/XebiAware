package com.android.vantage.ModelClasses;

import java.util.ArrayList;

import com.android.vantage.R;

public class DrawerItem {

	String ItemName;
	int imgResID;

	public DrawerItem(String itemName, int imgResID) {
		super();
		ItemName = itemName;
		this.imgResID = imgResID;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public int getImgResID() {
		return imgResID;
	}

	public void setImgResID(int imgResID) {
		this.imgResID = imgResID;
	}

	public static ArrayList<DrawerItem> getDrawerList(boolean isUserLoggedIn) {

		ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
		items.add(new DrawerItem("Offers", R.drawable.app_logo));
		items.add(new DrawerItem("Rate Application", R.drawable.app_logo));
		items.add(new DrawerItem("Promote Application", R.drawable.app_logo));
		items.add(new DrawerItem("Previous Orders", R.drawable.app_logo));
		items.add(new DrawerItem("Feedback", R.drawable.app_logo));
		if (isUserLoggedIn) {

			items.add(new DrawerItem("Logout", R.drawable.app_logo));

		} else {

		}

		return items;
	}

}
