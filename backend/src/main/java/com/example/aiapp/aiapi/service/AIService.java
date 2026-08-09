package com.example.aiapp.aiapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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

    @Value("${ai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        System.out.println("=================================================");
        System.out.println(">>> DEBUG: SPRING BOOT LOADED AI CONFIGURATION:");
        System.out.println(">>> AI_API_URL = " + apiUrl);
        System.out.println(">>> AI_API_KEY = " + (apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : apiKey));
        System.out.println("=================================================");
    }

   public String getAIResponse(String prompt) {
       return getAIResponse(prompt, "meta/llama-3.1-8b-instruct");
   }

   public String getAIResponse(String prompt, String model) {
       String url = apiUrl;
       String key = apiKey;

       // Safety Check & Logging
       if (url == null || url.isBlank() || url.contains("chatanywhere")) {
           url = "https://integrate.api.nvidia.com/v1/chat/completions";
       }

       if (key == null || key.isBlank()) {
           return "Application Error: AI_API_KEY configuration is missing. Please configure AI_API_KEY in secrets.properties or system environment variables.";
       }

       String modelToUse = (model != null && !model.isBlank()) ? model : "meta/llama-3.1-8b-instruct";
       System.out.println(">>> [AIService Executing Request] Endpoint: " + url + " | Model: " + modelToUse + " | Key: " + (key.length() > 10 ? key.substring(0, 10) + "..." : key));

       HttpHeaders headers = new HttpHeaders();
       headers.setBearerAuth(key);
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.setAccept(List.of(MediaType.APPLICATION_JSON));

       Map<String, Object> body = Map.of(
               "model", modelToUse,
               "messages", List.of(
                       Map.of("role", "user", "content", prompt)
               ),
               "max_tokens", 1024,
               "temperature", 0.7,
               "stream", false
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

           if (root.has("choices") && root.path("choices").isArray() && root.path("choices").size() > 0) {
               JsonNode firstChoice = root.path("choices").get(0);
               JsonNode messageNode = firstChoice.path("message");
               if (messageNode.has("content") && !messageNode.path("content").isNull()) {
                   return messageNode.path("content").asText();
               }
           }

           return "AI Response: " + response.getBody();

       } catch (RestClientResponseException ex) {
           return "API Error (" + ex.getStatusCode() + "): " + ex.getResponseBodyAsString();
       } catch (Exception ex) {
           return "Application Error: " + ex.getMessage();
       }
   }
}