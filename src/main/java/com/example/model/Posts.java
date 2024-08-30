package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Posts {
	
	private Long id;
	
	private Long userId;
	
	private String title;
	
	private String content;
	
	private LocalDateTime createdAt;
	
	private boolean isDeleted;
}
