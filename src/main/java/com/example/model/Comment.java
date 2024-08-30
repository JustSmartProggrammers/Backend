package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
	private Long id;

	private Long postsId;

	private String content;

	private LocalDateTime createdAt;

	private boolean isDeleted;

}
