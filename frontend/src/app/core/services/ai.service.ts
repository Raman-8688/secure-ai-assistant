import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { AIRequest } from '../models/ai-request.model';
import { AIResponse } from '../models/ai-response.model';

@Injectable({
  providedIn: 'root',
})
export class AiService {
  private baseUrl = 'http://localhost:8080/api/ai';

  constructor(private http: HttpClient) {}

  askQuestion(question: string): Observable<AIResponse> {
    const request: AIRequest = { question };
    return this.http.post<AIResponse>(`${this.baseUrl}/ask`, request);
  }
}
