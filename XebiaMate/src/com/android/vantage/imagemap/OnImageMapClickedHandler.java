package com.android.vantage.imagemap;
public interface OnImageMapClickedHandler {
		/**
		 * Area with 'id' has been tapped
		 * 
		 * @param id
		 */
		void onImageMapClicked(int id, TouchImageView imageMap);

		/**
		 * Info bubble associated with area 'id' has been tapped
		 * 
		 * @param id
		 */
		void onBubbleClicked(int id);
	}