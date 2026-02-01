package com.exam.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "leaderboard")
public class Leaderboard {

	@Id
	private String id;

	@Field("uuid")
	private String uuid;

	@Field("score")
	private int score;
}
