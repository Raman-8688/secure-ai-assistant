import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { marked } from 'marked';

@Component({
  selector: 'app-markdown-renderer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './markdown-renderer.component.html',
  styleUrls: ['./markdown-renderer.component.css']
})
export class MarkdownRendererComponent {
  htmlContent: SafeHtml = '';
  private codeBlocks: string[] = [];

  copiedIndex: number | null = null;

  constructor(private sanitizer: DomSanitizer) {}

  @Input() set content(value: string) {
    this.codeBlocks = [];

    const renderer = new marked.Renderer();

    renderer.code = ({ text, lang }) => {
      const index = this.codeBlocks.length;
      this.codeBlocks.push(text);

      return `
        <div class="code-wrapper">
          <div class="code-header">
            <span class="code-language">${lang || 'code'}</span>
            <button class="copy-btn" data-code-index="${index}">
              Copy
            </button>
          </div>
          <pre><code>${this.escapeHtml(text)}</code></pre>
        </div>
      `;
    };

    const rawHtml = marked(value || '', { renderer }) as string;
    this.htmlContent = this.sanitizer.bypassSecurityTrustHtml(rawHtml);
  }

  async handleClick(event: MouseEvent): Promise<void> {
    const target = event.target as HTMLElement;
    const button = target.closest('.copy-btn') as HTMLElement;

    if (!button) return;

    const index = Number(button.dataset['codeIndex']);
    const code = this.codeBlocks[index];

    if (!code) return;

    await navigator.clipboard.writeText(code);

    this.copiedIndex = index;
    button.innerText = 'Copied!';

    setTimeout(() => {
      button.innerText = 'Copy';
      this.copiedIndex = null;
    }, 1200);
  }

  private escapeHtml(value: string): string {
    return value
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
  }
}