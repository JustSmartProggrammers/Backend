package com.example.model;

import lombok.Data;

@Data
public class Spot {
	private Long id;
	
	private String name;
	
	private String address;
	
	private String contact;
	
	private String reservation;
	
	private String regionType;
	
	private String sportsType;
	
	private String weekdayHours;
	
	private String weekendHours;
	
	private String fee;
	
	private String parking;
	
	private String rentalAvailable;

}
