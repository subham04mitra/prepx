package com.exam.resDTO;

import java.sql.Date;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

	private String name;
	private String email;
	private String mobile;
	private String institute;
	private String city;
	private String stream;
	private String subType;
	private String subName;
	private String ref;
	private int refCount;
	private Instant creationData;
	private int intCount;
	private int tCount;
}
