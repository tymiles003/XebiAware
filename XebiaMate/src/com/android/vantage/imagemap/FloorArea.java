package com.android.vantage.imagemap;

import java.util.ArrayList;

public class FloorArea {

	private float floorWidth, floorHeight;
	private ArrayList<RoomArea> rooms;
	private String floorName;

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}

	public float getFloorWidth() {
		return floorWidth;
	}

	public void setFloorWidth(float floorWidth) {
		this.floorWidth = floorWidth;
	}

	public float getFloorHeight() {
		return floorHeight;
	}

	public void setFloorHeight(float floorHeight) {
		this.floorHeight = floorHeight;
	}

	public ArrayList<RoomArea> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<RoomArea> rooms) {
		this.rooms = rooms;
	}

	public FloorArea() {
		rooms = new ArrayList<RoomArea>();
	}

	public FloorArea(String floorName, float floorWidth, float floorHeight) {
		this.floorHeight = floorHeight;
		this.floorWidth = floorWidth;
		this.floorName = floorName;
		rooms = new ArrayList<RoomArea>();
	}

	public void addRoom(RoomArea room) {
		rooms.add(room);
	}

	public static FloorArea getFloorPlanForNoidaFlat() {
		FloorArea f = new FloorArea("FirstFloor", 113, 75);
		RoomArea room1 = new RoomArea(0,"Perl", 32, 3, 25, 20);
		RoomArea room2 = new RoomArea(1,"Python", 58, 3, 25, 20);
		RoomArea room3 = new RoomArea(2,"Conference", 84, 3, 25, 20);
		
		
		
		RoomArea room4 = new RoomArea(3,"Zorg Domain", 2, 54, 35, 20);
		RoomArea room5 = new RoomArea(4,"HI", 38, 54, 35, 20);
		RoomArea room6 = new RoomArea(5,"Mobile", 74, 54, 35, 20);
		f.addRoom(room1);
		f.addRoom(room2);
		f.addRoom(room3);
		f.addRoom(room4);
		f.addRoom(room5);
		f.addRoom(room6);
		return f;
	}

}
