// src/app/core/models/chat-history.model.ts

export interface ChatHistoryItem {
  id: number;
  question: string;
  answer: string;
  createdAt: string; // ISO datetime from backend
}