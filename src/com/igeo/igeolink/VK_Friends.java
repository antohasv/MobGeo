package com.igeo.igeolink;

import java.util.ArrayList;

public class VK_Friends {
	
	private String firstName;
	private String lastName;
	private String imageUrl;
	private int online;
	private String photoBig;
	
	public VK_Friends(String firstName, String lastName, String imageUrl, String photoBig, int online) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.imageUrl = imageUrl;
		this.online = online;
		this.photoBig = photoBig;
	}
	
	public String getPhotoBig() {
		return photoBig;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getImageUrl() {
		return this.imageUrl;
	}
	
	public int getOnline() {
		return this.online;
	}
	
}
