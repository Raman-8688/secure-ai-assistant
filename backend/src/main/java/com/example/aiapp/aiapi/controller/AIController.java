package com.example.aiapp.aiapi.controller;

import com.example.aiapp.aiapi.dto.AIRequest;
import com.example.aiapp.aiapi.dto.AIResponse;
import com.example.aiapp.aiapi.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/ask")
    public AIResponse askAI(@RequestBody AIRequest request) {
        String answer = aiService.getAIResponse(request.getQuestion());
        return new AIResponse(answer);
    }
}