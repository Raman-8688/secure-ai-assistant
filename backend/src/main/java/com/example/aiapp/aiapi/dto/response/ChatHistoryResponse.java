package com.example.aiapp.aiapi.dto.response;

import java.time.LocalDateTime;

public class ChatHistoryResponse {
    private Long id;
    private String question;
    private String answer;
    private LocalDateTime createdAt;

    public ChatHistoryResponse(Long id, String question, String answer, LocalDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
