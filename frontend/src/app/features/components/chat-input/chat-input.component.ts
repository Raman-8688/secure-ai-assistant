import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, OnDestroy, NgZone } from '@angular/core';
import { FormsModule } from '@angular/forms';

// Extend Window to include SpeechRecognition
declare global {
  interface Window {
    SpeechRecognition: any;
    webkitSpeechRecognition: any;
  }
}

@Component({
  selector: 'app-chat-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-input.component.html',
  styleUrls: ['./chat-input.component.css'],
})
export class ChatInputComponent implements OnDestroy {
  @Input() isLoading = false;
  @Output() send = new EventEmitter<string>();

  question = '';

  // Mic state
  isListening = false;
  micError = '';
  private recognition: any = null;
  private interimTranscript = '';

  constructor(private ngZone: NgZone) {}

  // ─── Send message ────────────────────────────────────────────────────────────
  sendMessage(textarea?: HTMLTextAreaElement): void {
    const value = this.question.trim();
    if (!value || this.isLoading) return;

    // Stop mic if it's running when user sends
    if (this.isListening) this.stopListening();

    this.send.emit(value);
    this.question = '';

    setTimeout(() => {
      if (textarea) textarea.style.height = '30px';
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

  // ─── Mic toggle ──────────────────────────────────────────────────────────────
  toggleMic(): void {
    if (this.isListening) {
      this.stopListening();
    } else {
      this.startListening();
    }
  }

  private startListening(): void {
    this.micError = '';

    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
      this.micError = 'Speech recognition is not supported in this browser. Try Chrome or Edge.';
      return;
    }

    this.recognition = new SpeechRecognition();
    this.recognition.lang = 'en-US';
    this.recognition.continuous = true;       // keep listening until stopped
    this.recognition.interimResults = true;   // show live partial results
    this.recognition.maxAlternatives = 1;

    // Capture existing text so we append to it
    const baseText = this.question;

    this.recognition.onstart = () => {
      this.ngZone.run(() => {
        this.isListening = true;
        this.interimTranscript = '';
      });
    };

    this.recognition.onresult = (event: any) => {
      this.ngZone.run(() => {
        let interim = '';
        let finalText = '';

        for (let i = event.resultIndex; i < event.results.length; i++) {
          const transcript = event.results[i][0].transcript;
          if (event.results[i].isFinal) {
            finalText += transcript + ' ';
          } else {
            interim += transcript;
          }
        }

        // Build the displayed text: baseText + committed finals + live interim
        const committed = baseText + finalText;
        this.interimTranscript = interim;
        this.question = committed + interim;
      });
    };

    this.recognition.onerror = (event: any) => {
      this.ngZone.run(() => {
        this.isListening = false;
        if (event.error === 'not-allowed') {
          this.micError = 'Microphone access denied. Please allow microphone in browser settings.';
        } else if (event.error === 'no-speech') {
          this.micError = 'No speech detected. Try again.';
        } else if (event.error === 'network') {
          this.micError = 'Network error. Check your connection.';
        } else {
          this.micError = `Mic error: ${event.error}`;
        }
      });
    };

    this.recognition.onend = () => {
      this.ngZone.run(() => {
        this.isListening = false;
        this.interimTranscript = '';
      });
    };

    try {
      this.recognition.start();
    } catch (e) {
      this.micError = 'Could not start microphone. Please try again.';
      this.isListening = false;
    }
  }

  private stopListening(): void {
    if (this.recognition) {
      this.recognition.stop();
      this.recognition = null;
    }
    this.isListening = false;
    this.interimTranscript = '';
  }

  isMicSupported(): boolean {
    return !!(window.SpeechRecognition || window.webkitSpeechRecognition);
  }

  ngOnDestroy(): void {
    this.stopListening();
  }
}