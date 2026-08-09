// src/app/core/services/ai.service.ts


import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AIRequest } from '../models/ai-request.model';
import { AIResponse } from '../models/ai-response.model';
import { ChatHistoryItem } from '../models/chat-history.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AiService {
  private baseUrl = `${environment.apiUrl}/api/ai`;

  constructor(private http: HttpClient) {}

  askQuestion(question: string, model?: string): Observable<AIResponse> {
    const request: AIRequest = { question, model };
    return this.http.post<AIResponse>(`${this.baseUrl}/ask`, request);
  }

  // NEW: Load user's chat history
  getHistory(): Observable<ChatHistoryItem[]> {
    return this.http.get<ChatHistoryItem[]>(`${this.baseUrl}/history`);
  }

  // NEW: Delete one history item
  deleteHistoryItem(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/history/${id}`);
  }
}