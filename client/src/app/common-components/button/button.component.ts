import { Component, HostBinding, Input } from '@angular/core';

export type ButtonColor = 'blue' | 'green' | 'red' | 'yellow';

@Component({
  standalone: true,
  selector: '[app-button]',
  template: '<ng-content></ng-content>',
  styleUrls: ['./button.component.css']
})
export class ButtonComponent {
  @Input() color: ButtonColor = 'blue';

  @HostBinding('class')
  get class() {
    return this.color;
  }
}
