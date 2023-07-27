import { Attribute, Component, HostBinding, Input } from '@angular/core';

@Component({
  selector: 'app-heading',
  templateUrl: './heading.component.html',
  styleUrls: ['./heading.component.css'],
})
export class HeadingComponent {
  constructor(
    @Attribute('level') public level: string,
    @Attribute('class') public className: string
  ) {}

  @HostBinding('class')
  get class(): string {
    return [`level${this.level ?? 1}`, this.className].join(' ');
  }
}
