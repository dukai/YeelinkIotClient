package net.yeelink.yeelinkiotclient;

public class GpsPoint {
	private double lng;
	private double lat;
	public GpsPoint(double lng, double lat){
		this.lng = lng;
		this.lat = lat;
	}
	
	public GpsPoint(){
		
	}
	
	public double getLat(){
		return this.lat;
	}
	
	public double getLng(){
		return this.lng;
	}
	
	public void setLat(double value){
		lat = value;
	}
	public void setLng(double value){
		lng = value;
	}
}
