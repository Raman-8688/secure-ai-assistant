package com.example.aiapp.aiapi.repository;


import com.example.aiapp.aiapi.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}
