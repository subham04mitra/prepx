package com.exam.resDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashResModel {

	private Object tot_teacher;
	private Object tot_paper;
	private Object tot_scan;
	private Object avg_marks;
	private Object tot_branch;
	private Object tot_ques;
}
