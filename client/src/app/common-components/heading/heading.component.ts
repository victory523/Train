import { Component, HostBinding, Input } from '@angular/core';

@Component({
  selector: 'app-heading',
  templateUrl: './heading.component.html',
  styleUrls: ['./heading.component.css'],
  host: {
    '[class]': "'level' + level",
  },
})
export class HeadingComponent {
  @Input() level = 1;
}
