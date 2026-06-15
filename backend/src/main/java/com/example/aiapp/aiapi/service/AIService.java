package com.example.aiapp.aiapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")  // ← ADD THIS
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

   /** public String getAIResponse(String prompt) {
        String url = "https://router.huggingface.co/v1/chat/completions";//wokring one



        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", "openai/gpt-oss-120b:fastest",
                "stream", false,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = mapper.readTree(response.getBody());

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (RestClientResponseException ex) {
            return "Hugging Face API Error: " + ex.getStatusCode()
                    + "\n" + ex.getResponseBodyAsString();
        } catch (Exception ex) {
            return "Application Error: " + ex.getMessage();
        }
    }
    **/




    /**
     * AI added to the deep
     * Map<String, Object> body = Map.of(
     *     "model", "gpt-3.5-turbo-16k",  // Alternative 1
     *     // "model", "gpt-4o-mini",     // Alternative 2
     *     // "model", "gpt-4",           // Alternative 3
     *     "messages", List.of(...)
     * );
     * @param prompt
     * @return
     */



   public String getAIResponse(String prompt) {
       // Use the URL from properties
       String url = apiUrl;  // ← CHANGE THIS from hardcoded URL

       HttpHeaders headers = new HttpHeaders();
       headers.setBearerAuth(apiKey);
       headers.setContentType(MediaType.APPLICATION_JSON);

       // Update to OpenAI-compatible format
       Map<String, Object> body = Map.of(
               "model", "gpt-3.5-turbo-16k",  // or "gpt-4o-mini" for better quality gpt-3.5-turbo
               "messages", List.of(
                       Map.of("role", "user", "content", prompt)
               )
       );

       try {
           HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

           ResponseEntity<String> response = restTemplate.exchange(
                   url,
                   HttpMethod.POST,
                   entity,
                   String.class
           );

           JsonNode root = mapper.readTree(response.getBody());

           return root
                   .path("choices")
                   .get(0)
                   .path("message")
                   .path("content")
                   .asText();

       } catch (RestClientResponseException ex) {
           return "API Error: " + ex.getStatusCode() + "\n" + ex.getResponseBodyAsString();
       } catch (Exception ex) {
           return "Application Error: " + ex.getMessage();
       }
   }
}