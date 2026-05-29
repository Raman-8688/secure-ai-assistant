import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AiChatComponent } from './features/ai-chat/ai-chat.component';
import { ChatUiComponent } from './features/chat-ui/chat-ui.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AiChatComponent, ChatUiComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'ai-frontend';
}
