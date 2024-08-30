package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {
	private Long id;
	
	private Long userId;
	
	private String content;
	
	private LocalDateTime createdAt;
	
	private boolean isDeleted;

}
