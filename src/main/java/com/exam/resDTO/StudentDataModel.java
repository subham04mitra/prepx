package com.exam.resDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDataModel {

	private String student_name;
	private String student_roll;
	private int tot_qs ;
	private int tot_crct ;
	private int tot_attm ;
	private int tot_wrng ;
	private int tot_marks ;
	private int each_mrk ;
	private int mrk_obtn ;
}
