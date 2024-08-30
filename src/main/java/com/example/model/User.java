package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
	private int id;
	
	private String email;
	
	private String password;
	
	private String name;
	
	private boolean isDeleted;
	
	private LocalDateTime createdAt;
	

}
