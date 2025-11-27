package com.exam.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Invalid Input");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<Map<String, Object>> BadSqlGrammarException(BadSqlGrammarException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Statement Error");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(org.springframework.jdbc.UncategorizedSQLException.class)
    public ResponseEntity<Map<String, Object>> UncategorizedSQLException(BadSqlGrammarException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Parameter mismatch");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    // Handle DataAccessException (e.g., database errors)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseException(DataAccessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Application Error");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

   @ExceptionHandler(GlobalExceptionHandler.ExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(GlobalExceptionHandler.ExpiredException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Session Expired");
        response.put("status", HttpStatus.REQUEST_TIMEOUT.value());
        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);
    }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
       Map<String, Object> res = new HashMap<>();
       res.put("timestamp", LocalDateTime.now());
       res.put("status", 400);
       res.put("error", "Validation Error");

//       List<String> errors = ex.getBindingResult()
//           .getFieldErrors()
//           .stream()
//           .map(e -> e.getField() + ": " + e.getDefaultMessage())
//           .collect(Collectors.toList());
//
//       res.put("message", errors);
       return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
   }
   @ExceptionHandler(GlobalExceptionHandler.DatabaseUnavailableException.class)
   public ResponseEntity<Object> handleDatabaseDown(GlobalExceptionHandler.DatabaseUnavailableException ex) {
       Map<String, Object> body = new HashMap<>();
       body.put("error", "Database Unavailable");
       body.put("message", ex.getMessage());
       body.put("timestamp", LocalDateTime.now());
       return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
   }


   public static class DatabaseUnavailableException extends RuntimeException {
	    public DatabaseUnavailableException(String message) {
	        super(message);
	    }
	}

    public static class ExpiredException extends RuntimeException {
        public ExpiredException() {
            
        }
    }
   
    

}
