package com.exam.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import jakarta.servlet.http.HttpServletResponse;

@Component
public class ResponseBean {
    
    private HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attributes != null) ? attributes.getResponse() : null;
    }
 
    public ResponseEntity<ApiResponses> AppResponse(String type, String token, Object data) {
    	ApiResponses response = new ApiResponses(); 
       
        HttpServletResponse httpResponse = getHttpServletResponse();
        if (httpResponse == null) {
            throw new IllegalStateException("HttpServletResponse is not available");
        }
        switch (type) {
            case "LoginSuccess":
                response.setCode("200");
                response.setMessage("Login Success");                
                response.setToken(token);
                response.setData(data);
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "OTPSuccess":
                response.setCode("200");
                response.setMessage("OTP has been sent to your email id.");                
                
                response.setData(data);
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "PWDSuccess":
                response.setCode("200");
                response.setMessage("Password has been changed succesfully.");                
                
                response.setData(data);
                return new ResponseEntity<>(response, HttpStatus.OK);
            
            case "RefreshSuccess":
                response.setCode("200");
                response.setMessage("Refresh Success");                
                response.setToken(token);
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "LogoutSuccess":
                response.setCode("200");
                response.setMessage("Logout Success"); 
                return new ResponseEntity<>(response, HttpStatus.OK);
                
            case "OTPError":
                response.setCode("404");
                response.setMessage("Wrong OTP"); 
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            case "Unauothorize":
                response.setCode("401");
                response.setMessage("Unautorized"); 
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            case "SubExists":
                response.setCode("409");
                response.setMessage("You still have remaining interview credits."); 
                response.setData(data);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            case "ReSubscribed":
                response.setCode("200");
                response.setMessage("Subscription renewed successfully."); 
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "DownGradeNotAllowed":
                response.setCode("409");
                response.setMessage("Cannot downgrade while have subscription"); 
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            
            case "TryAgain":
                response.setCode("502");
                response.setMessage("Try Again"); 
                return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
            case "Success":
                response.setCode("200");
                response.setMessage("Success");                
                response.setData(data);                
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "RegSuccess":
                response.setCode("200");
                response.setMessage("Register Succesfully");                    
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "fSuccess":
                response.setCode("200");
                response.setMessage("Feedback Sent");                    
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "SubExp":
                response.setCode("403");
                response.setMessage("Subscription Ended");                    
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            case "Error":
                response.setCode("400");
                response.setMessage("Something went wrong");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            case "TokenValid":
                response.setCode("409");
                response.setMessage("Token is Valid");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            case "Nulltype":
                response.setCode("400");
                response.setMessage("Provide Credentials");
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "Invalid":
                response.setCode("400");
                response.setMessage("Invalid Credentials");
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "Notfound":
                response.setCode("400");
                response.setMessage("No Record Found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            case "SuccessF":
                response.setCode("200");
                return new ResponseEntity<>(response, HttpStatus.OK);
            case "Found":
                response.setCode("400");
                return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
            case "Exists":
                response.setCode("208");
                response.setMessage("Email or Mobile Exists");
                return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
            default:
                response.setCode("400");
                response.setMessage("Unknown type");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
