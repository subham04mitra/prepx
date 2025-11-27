package com.exam.resDTO;

import java.util.List;

/**
 * Data Transfer Object (DTO) to hold the structured JSON response from Gemini.
 * The model will generate a JSON object: {"questions": ["Q1", "Q2", ...]}
 */
public record QuestionList(List<String> questions) {
    // This simple record automatically provides a constructor, getters, and toString().
}