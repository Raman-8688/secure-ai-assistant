import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-input.component.html',
  styleUrls: ['./chat-input.component.css'],
})
export class ChatInputComponent {
  @Input() isLoading = false;
  @Output() send = new EventEmitter<string>();

  question = '';

  sendMessage(textarea?: HTMLTextAreaElement): void {
    const value = this.question.trim();
    if (!value || this.isLoading) return;

    this.send.emit(value);
    this.question = '';

    setTimeout(() => {
      if (textarea) {
        textarea.style.height = '30px';
      }
    });
  }

  handleEnter(event: Event, textarea: HTMLTextAreaElement): void {
    const keyboardEvent = event as KeyboardEvent;

    if (keyboardEvent.shiftKey) {
      setTimeout(() => this.autoResize(textarea));
      return;
    }

    event.preventDefault();
    this.sendMessage(textarea);
  }
  autoResize(textarea: HTMLTextAreaElement): void {
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
  }
}
