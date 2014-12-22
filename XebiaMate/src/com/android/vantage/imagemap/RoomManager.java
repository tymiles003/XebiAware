package com.android.vantage.imagemap;

import java.util.ArrayList;

public class RoomManager {
	float windowWidth, windowHeight;
	
	ArrayList<FloorArea> floors;
	
	public RoomManager() {
		floors = new ArrayList<FloorArea>();
		initRoomManager();
	}
	public float getWindowWidth() {
		return windowWidth;
	}
	public void setWindowWidth(float windowWidth) {
		this.windowWidth = windowWidth;
	}
	public float getWindowHeight() {
		return windowHeight;
	}
	public void setWindowHeight(float windowHeight) {
		this.windowHeight = windowHeight;
	}
	public ArrayList<FloorArea> getFloors() {
		return floors;
	}
	public void setFloors(ArrayList<FloorArea> floors) {
		this.floors = floors;
	}
	public void initRoomManager(){
		FloorArea f = FloorArea.getFloorPlanForNoidaFlat();
		
		floors.add(f);
	}

}
