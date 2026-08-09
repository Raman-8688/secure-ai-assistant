package com.example.aiapp.aiapi.controller;

import com.example.aiapp.aiapi.dto.response.ChatHistoryResponse;
import com.example.aiapp.aiapi.entity.ChatHistory;
import com.example.aiapp.aiapi.repository.ChatHistoryRepository;
import com.example.aiapp.aiapi.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            String question = body.get("question");
            String model = body.getOrDefault("model", "meta/llama-3.1-8b-instruct");
            if (question == null || question.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Question cannot be empty"));
            }

            // Use AIService with dynamic model selection
            String answer = aiService.getAIResponse(question, model);

            // Save to history (use email from JWT)
            String email = authentication.getName();
            ChatHistory history = new ChatHistory();
            history.setUserEmail(email);
            history.setQuestion(question);
            history.setAnswer(answer);
            chatHistoryRepository.save(history);

            return ResponseEntity.ok(Map.of("answer", answer));

        } catch (Exception e) {
            System.err.println("Error in askQuestion: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "AI service temporarily unavailable. Please try again."));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<ChatHistory> records = chatHistoryRepository
                    .findByUserEmailOrderByCreatedAtDesc(email);

            List<ChatHistoryResponse> response = records.stream()
                    .map(r -> new ChatHistoryResponse(r.getId(), r.getQuestion(), r.getAnswer(), r.getCreatedAt()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getHistory: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Could not load history."));
        }
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteHistory(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ChatHistory item = chatHistoryRepository.findById(id).orElse(null);
            if (item == null || !item.getUserEmail().equals(email)) {
                return ResponseEntity.notFound().build();
            }
            chatHistoryRepository.delete(item);
            return ResponseEntity.ok(Map.of("message", "Deleted"));
        } catch (Exception e) {
            System.err.println("Error in deleteHistory: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Could not delete item."));
        }
    }
}