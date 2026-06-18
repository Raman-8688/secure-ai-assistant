import {
  Component, ElementRef, ViewChild,
  OnInit, OnDestroy
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { AiService } from '../../core/services/ai.service';
import { NetworkService } from '../../core/services/network.service';
import { ChatMessageComponent, Message } from '../components/chat-message/chat-message.component';
import { ChatInputComponent } from '../components/chat-input/chat-input.component';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { ChatHistoryItem } from '../../core/models/chat-history.model';
import { Subscription } from 'rxjs';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-chat-ui',
  standalone: true,
  imports: [CommonModule, ChatMessageComponent, ChatInputComponent],
  templateUrl: './chat-ui.component.html',
  styleUrls: ['./chat-ui.component.css'],
})
export class ChatUiComponent implements OnInit, OnDestroy {
  @ViewChild('chatMessagesContainer') messagesContainer!: ElementRef;

  messages: Message[] = [];
  isLoading = false;
  isLoadingHistory = false;

  toastMessage = '';
  toastType: 'error' | 'warning' | 'info' = 'error';
  toastVisible = false;
  private toastTimer: any;

  historyItems: ChatHistoryItem[] = [];
  showHistory = false;
  isDeletingId: number | null = null;

  isOnline = true;
  private networkSub!: Subscription;
  private messageId = 0;

  get isDark(): boolean { return this.themeService.isDark; }

  constructor(
    private aiService: AiService,
    private authService: AuthService,
    private networkService: NetworkService,
    private router: Router,
    private themeService: ThemeService,
  ) {}

  ngOnInit(): void {
    this.networkSub = this.networkService.isOnline$.subscribe(online => {
      this.isOnline = online;
      if (!online) {
        this.showToast('No internet connection. Please check your network.', 'warning');
      } else if (this.toastType === 'warning') {
        this.hideToast();
      }
    });

    this.loadHistory();
    this.addBotMessage("Hello! I'm your AI assistant. Ask me anything about programming, technology, or general knowledge.");
  }

  ngOnDestroy(): void {
    this.networkSub?.unsubscribe();
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }

  loadHistory(): void {
    this.isLoadingHistory = true;
    this.aiService.getHistory().subscribe({
      next: (items) => { this.historyItems = items; this.isLoadingHistory = false; },
      error: (err) => { this.isLoadingHistory = false; console.warn('History load failed:', err); },
    });
  }

  loadHistoryChat(item: ChatHistoryItem): void {
    this.messages = [];
    this.messageId = 0;
    this.addUserMessage(item.question);
    this.addBotMessage(item.answer);
    this.showHistory = false;
  }

  deleteHistoryItem(id: number, event: Event): void {
    event.stopPropagation();
    this.isDeletingId = id;
    this.aiService.deleteHistoryItem(id).subscribe({
      next: () => { this.historyItems = this.historyItems.filter(h => h.id !== id); this.isDeletingId = null; },
      error: (err) => { this.isDeletingId = null; this.showToast(err?.userMessage || 'Could not delete item.', 'error'); },
    });
  }

  sendMessage(question: string): void {
    if (!this.isOnline) {
      this.showToast('No internet connection. Please check your network and try again.', 'warning');
      return;
    }
    this.addUserMessage(question);
    this.isLoading = true;
    const typingId = this.addTypingIndicator();

    this.aiService.askQuestion(question).subscribe({
      next: (response: any) => {
        this.removeTypingIndicator(typingId);
        this.addBotMessage(response.answer || 'No response received.');
        this.isLoading = false;
        this.loadHistory();
      },
      error: (err) => {
        this.removeTypingIndicator(typingId);
        const msg = err?.userMessage || 'Something went wrong. Please try again.';
        this.addBotMessage(msg);
        this.showToast(msg, 'error');
        this.isLoading = false;
      },
    });
  }

  showToast(message: string, type: 'error' | 'warning' | 'info' = 'error'): void {
    clearTimeout(this.toastTimer);
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;
    if (type !== 'warning') {
      this.toastTimer = setTimeout(() => this.hideToast(), 5000);
    }
  }

  hideToast(): void { this.toastVisible = false; this.toastMessage = ''; }

  private addUserMessage(text: string): void {
    this.messages.push({ id: this.messageId++, text, isUser: true, timestamp: new Date() });
    this.scrollToBottom();
  }

  private addBotMessage(text: string): void {
    this.messages.push({ id: this.messageId++, text, isUser: false, timestamp: new Date() });
    this.scrollToBottom();
  }

  private addTypingIndicator(): number {
    const id = this.messageId++;
    this.messages.push({ id, text: '', isUser: false, timestamp: new Date(), isTyping: true });
    return id;
  }

  private removeTypingIndicator(id: number): void {
    this.messages = this.messages.filter(m => m.id !== id);
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      try { this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight; } catch {}
    }, 50);
  }

  logout(): void { this.authService.logout(); this.router.navigate(['/login']); }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
  }
}