package org.church.rockmobile.model;

public class GroupsFeaturedListModel{
	private String clubName = "";
	private String thumbnail = "";
	private String banner = "";
	private String description = "";
	private String street = "";
	private String distance="";
	private String url = "";
	private String facebook = "";
	private String phoneNumber = "";
	private double latitude = 0;
	private double longtitude = 0;
	
	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getClubName() {
		return this.clubName;
	}
	
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getThumbnail() {
		return this.thumbnail;
	}
	
	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getBanner() {
		return this.banner;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
	
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getFacebook() {
		return this.facebook;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet() {
		return this.street;
	}
	
	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDistance() {
		return this.distance;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return this.latitude;
	}
	
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public double getLongtitude() {
		return this.longtitude;
	}
}
