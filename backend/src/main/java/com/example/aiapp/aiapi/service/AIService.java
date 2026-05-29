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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String getAIResponse(String prompt) {
        String url = "https://router.huggingface.co/v1/chat/completions";
//        String url = "https://router.huggingface.co/v1/chat/bloom";not working

        //  Hugging Face Inference API endpoint for Mistral
        //String url = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct";
//        String url = "https://api-inference.huggingface.co/models/google/flan-t5-base";
//        String url = "https://api-inference.huggingface.co/models/google/flan-t5-small";
//        String url = "https://api-inference.huggingface.co/models/bigscience/bloom";

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
}