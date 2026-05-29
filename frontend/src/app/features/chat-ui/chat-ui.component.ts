import {
  Component,
  ElementRef,
  ViewChild,
  AfterViewChecked
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { AiService } from '../../core/services/ai.service';
import { ChatMessageComponent, Message } from '../components/chat-message/chat-message.component';
import { ChatInputComponent } from '../components/chat-input/chat-input.component';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-chat-ui',
  standalone: true,
  imports: [CommonModule, ChatMessageComponent, ChatInputComponent],
  templateUrl: './chat-ui.component.html',
  styleUrls: ['./chat-ui.component.css']
})
export class ChatUiComponent  {
  @ViewChild('chatMessagesContainer') messagesContainer!: ElementRef;

  messages: Message[] = [];
  isLoading = false;
  private messageId = 0;

  constructor(
    private aiService: AiService,
  private authService: AuthService,
  private router: Router
  ) {
    this.addBotMessage(
      "Hello! I'm your AI assistant. Ask me anything about programming, technology, or general knowledge."
    );
  }

  // ngAfterViewChecked(): void {
  //   this.scrollToBottom();
  // }

  sendMessage(question: string): void {
    this.addUserMessage(question);
    this.isLoading = true;

    const typingId = this.addTypingIndicator();

    this.aiService.askQuestion(question).subscribe({
      next: (response: any) => {
        this.removeTypingIndicator(typingId);
        this.addBotMessage(response.answer || 'No response received.');
        this.isLoading = false;
      },
      error: () => {
        this.removeTypingIndicator(typingId);
        this.addBotMessage('Sorry, something went wrong. Please try again.');
        this.isLoading = false;
      }
    });
  }

 private addUserMessage(text: string): void {
  this.messages.push({
    id: this.messageId++,
    text,
    isUser: true,
    timestamp: new Date()
  });

  this.scrollToBottomAfterMessage();
}

private addBotMessage(text: string): void {
  this.messages.push({
    id: this.messageId++,
    text,
    isUser: false,
    timestamp: new Date()
  });

  this.scrollToBottomAfterMessage();
}

  private scrollToBottomAfterMessage(): void {
  setTimeout(() => {
    try {
      this.messagesContainer.nativeElement.scrollTop =
        this.messagesContainer.nativeElement.scrollHeight;
    } catch {}
  }, 0);
}


  private addTypingIndicator(): number {
    const id = this.messageId++;
    this.messages.push({
      id,
      text: '',
      isUser: false,
      timestamp: new Date(),
      isTyping: true
    });
    return id;
  }

  private removeTypingIndicator(id: number): void {
    this.messages = this.messages.filter(m => m.id !== id);
  }

  private scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop =
        this.messagesContainer.nativeElement.scrollHeight;
    } catch {}
  }

  logout(): void {
  this.authService.logout();
  this.router.navigate(['/login']);
}
}