package com.exam.resDTO;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QsPaperListModel {
	
	private String paper_id;
	private String exam_name;
	private String exam_type;
	private String user_name;
	private String branch;
	private String inst;
	private String paper_name;
	private int tot_qs;
	private int tot_mrks;
	private int parer_duration;
	private Object subject;
	private Object chapter;
	private Object topic;
	private Date exam_date;
	
}
