import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService } from '../../core/services/ai.service';

@Component({
  selector: 'app-ai-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-chat.component.html',
  styleUrls: ['./ai-chat.component.css']
})
export class AiChatComponent {

  question = '';
  answer = '';
  loading = false;
  errorMessage = '';

  constructor(private aiService: AiService) {}

  askAI(): void {
    if (!this.question.trim()) {
      this.errorMessage = 'Please enter a question.';
      return;
    }

    this.loading = true;
    this.answer = '';
    this.errorMessage = '';

    this.aiService.askQuestion(this.question).subscribe({
      next: (response: any) => {
        this.answer = response.answer;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Something went wrong. Please try again.';
        this.loading = false;
      }
    });
  }
}