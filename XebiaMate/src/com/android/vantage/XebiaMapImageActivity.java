package com.android.vantage;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;

import com.android.vantage.imagemap.Area;
import com.android.vantage.imagemap.EmpMarker;
import com.android.vantage.imagemap.FloorArea;
import com.android.vantage.imagemap.RoomArea;
import com.android.vantage.imagemap.RoomManager;
import com.android.vantage.imagemap.TouchImageView;
import com.android.vantage.imagemap.TouchImageView.onTouchImageMapClickHandler;
import com.android.vantage.imagemap.TouchImageView.postImageDrawListener;
import com.android.vantage.utility.AppConstants;
import com.android.vantageLogManager.Logger;

public class XebiaMapImageActivity extends BaseActivity {

	TouchImageView mImageMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_xebia_map_image_view);
		mImageMap = (TouchImageView) findViewById(R.id.img);
		mImageMap.setPostImageDrawListener(new postImageDrawListener() {
			
			@Override
			public void postDraw(Canvas c, TouchImageView v) {
				// TODO Auto-generated method stub
				drawBitmap(c, v);
			}
		});
		mImageMap.setImageResource(R.drawable.balloon_incoming_normal);
		
		mImageMap
		.setonTouchImageMapClickHandler(new onTouchImageMapClickHandler() {

			@Override
			public void onImageMapClicked(Area a) {
				// TODO Auto-generated method stub
				int id = a.getId();
				Log.e("Id clicked", ""+id);
				if (id < 0) {
					return;
				}
				startDetailActivity((RoomArea)a);
			}
		});
		
		// mImageMap.setImageResource(R.drawable.t2_exterior);
	}
	
	private void drawBitmap(Canvas c, TouchImageView view) {
		float width = view.getImageWidth();
		float height = view.getImageHeight();

		float scaleX = width /view.viewWidth;
		float scaleY = height /view.viewHeight;
		float transX, transY;
		float[] n = new float[9];
		view.getCurrentMatrix().getValues(n);
		transX = n[Matrix.MTRANS_X];
		transY = n[Matrix.MTRANS_Y];

		drawRoomRectangle(transX, transY, c, view);
		// for (Area a : mAreaList) {
		// a.draw(c, getContext().getResources(), scaleX, scaleY, transX,
		// transY);
		//
		// }

	}

	RoomManager manager;

	private void drawRoomRectangle(float transX, float transY, Canvas c, TouchImageView view) {

		if (manager == null)
			manager = new RoomManager();
		for (FloorArea f : manager.getFloors()) {
			float scaledFloorWidth = view.getImageWidth() / f.getFloorWidth();
			float scaledFloorHeight = view.getImageHeight() / f.getFloorHeight();
			for (RoomArea room : f.getRooms()) {
				view.mAreaList.add(room);
				room.draw(c, getResources(), scaledFloorWidth,
						scaledFloorHeight, transX, transY);
				for (EmpMarker marker : room.employeeMarkers) {
					marker.drawEmployeeMarker(c, getResources(),
							scaledFloorWidth, scaledFloorHeight, transX, transY, room);
				}
			}
		}

	}
	
	private void startDetailActivity(RoomArea room){
		Intent subActivity = new Intent(this,
                PictureDetailsActivity.class);
        int orientation = getResources().getConfiguration().orientation;
        String PACKAGE = PictureDetailsActivity.PACKAGE_NAME;
        RectF rect = room.getCurrentRect();
        Logger.info("Rect", rect.left+","+rect.right+","+rect.bottom+","+rect.top);
        subActivity.
                putExtra(PACKAGE + ".orientation", orientation).
                putExtra(PACKAGE + ".resourceId", R.drawable.bg_bitmap).
                putExtra(PACKAGE + ".left", (int)rect.left).
                putExtra(PACKAGE + ".top",(int) rect.top).
                putExtra(PACKAGE + ".width", (int)(rect.right-rect.left)).
                putExtra(PACKAGE + ".height", (int)(rect.bottom-rect.top)).
                putExtra(PACKAGE + ".description", room.get_name());
        startActivity(subActivity);
        
        // Override transitions: we don't want the normal window animation in addition
        // to our custom one
        overridePendingTransition(0, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
