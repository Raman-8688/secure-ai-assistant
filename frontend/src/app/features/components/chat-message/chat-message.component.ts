import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MarkdownRendererComponent } from '../markdown-renderer/markdown-renderer.component';

export interface Message {
  id: number;
  text: string;
  isUser: boolean;
  timestamp: Date;
  isTyping?: boolean;
}

@Component({
  selector: 'app-chat-message',
  standalone: true,
  imports: [CommonModule, MarkdownRendererComponent],
  templateUrl: './chat-message.component.html',
  styleUrls: ['./chat-message.component.css']
})
export class ChatMessageComponent {
  @Input() message!: Message;

  formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}