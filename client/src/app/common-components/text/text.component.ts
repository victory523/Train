import { Component, HostBinding, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: '[app-text]',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './text.component.html',
  styleUrls: ['./text.component.css'],
})
export class TextComponent {
  @Input({alias: 'app-text-type'})
  type?: 'green' | 'red' | 'white' | 'blue';

  @HostBinding('attr.app-text-type')
  get textType() {
    return this.type
  }
}
