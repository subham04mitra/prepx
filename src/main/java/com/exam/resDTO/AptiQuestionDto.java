package com.exam.resDTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AptiQuestionDto {

	private String id; // question code / number
	private String category;
	private String text; // question text
	private List<String> options;
}
