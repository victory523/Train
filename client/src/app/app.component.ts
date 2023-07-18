import { Component } from '@angular/core';
import { WithingsService } from './withings.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(private withingsService: WithingsService) {}

  $sync = this.withingsService.sync();
}
