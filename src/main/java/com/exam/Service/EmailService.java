package com.exam.Service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
@Service
public class EmailService {

    
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String apiKey;
   
    

    
    public String sendOtpEmail(String email, int otp) {

        Map<String, Object> payload = new HashMap<>();

        payload.put("sender", Map.of(
                "name", "PrepX AI",
                "email", "technoid.kolkata@gmail.com"
        ));

        payload.put("to", List.of(
                Map.of("email", email)
        ));

        payload.put("subject", "PrepX AI – OTP Verification");
        payload.put("htmlContent", buildOtpTemplate(otp));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    entity,
                    String.class
            );
            
            

            // 🔍 LOG BREVO RESPONSE
//            System.out.println("Brevo Status Code : " + response.getStatusCode());
//            System.out.println("Brevo Response Body : " + response.getBody());

            return response.getBody(); // contains messageId

        } catch (HttpClientErrorException | HttpServerErrorException ex) {

            throw ex;
        }
    }


    private String buildOtpTemplate(int otp) {
        // We use Inline CSS here because Gmail and Outlook often ignore <style> blocks
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>OTP Verification</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #0f172a; font-family: Arial, sans-serif;">
                
                <div style="background-color: #0f172a; padding: 40px 20px;">
                    <div style="max-width: 520px; margin: 0 auto; background-color: #020617; border-radius: 12px; padding: 30px; text-align: center; border: 1px solid #1e293b;">
                        
                        <div style="font-size: 26px; font-weight: bold; color: #38bdf8; margin-bottom: 20px;">
                            PrepX AI
                        </div>

                        <p style="color: #e5e7eb; font-size: 16px; margin-bottom: 25px;">
                            Use the following OTP to complete the process.
                        </p>

                        <div style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #22c55e; border: 2px dashed #334155; padding: 15px; border-radius: 10px; background-color: #0f172a; margin: 25px auto; width: fit-content;">
                            %d
                        </div>

                        <p style="color: #94a3b8; font-size: 14px; margin-top: 25px;">
                            This OTP is valid for 5 minutes.
                        </p>

                        <p style="color: #f87171; font-size: 13px; margin-top: 10px;">
                            Do not share this OTP with anyone.
                        </p>
                        
                    </div>
                </div>
                
            </body>
            </html>
            """, otp);
    }
}