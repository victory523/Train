import { Attribute, Component } from '@angular/core';

@Component({
  standalone: true,
  selector: '[app-header]',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  constructor(@Attribute('title') public title = '') {}
}
