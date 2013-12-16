package com.igeo.igeolink;

public class FrdLocations {
	private double lat ;
	private double lng;
	
	public FrdLocations(double latitude, double longitude) {
		this.lat = latitude;
		this.lng = longitude;
	}
	
	public double getLatitude() {
		return this.lat;
	}
	
	public double getLongitude() {
		return this.lng;
	}
}
