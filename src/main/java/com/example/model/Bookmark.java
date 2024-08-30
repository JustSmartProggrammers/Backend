package com.example.model;

import lombok.Data;

@Data
public class Bookmark {
	private Long id;
	
	private Long userId;
	
	private Long spotId;
	
	private boolean isDeleted;

}
