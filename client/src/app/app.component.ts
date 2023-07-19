import { Component, OnInit } from '@angular/core';
import { subscribeToRequestState, initialHttpRequestState } from './utils';
import { WithingsService } from './withings.service';
import { HttpRequestState } from './types';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(private withingsService: WithingsService) {}

  syncState: HttpRequestState<void> = initialHttpRequestState;

  ngOnInit(): void {
    subscribeToRequestState(this.withingsService.sync(), (newState) => {
      this.syncState = newState;
    });
  }
}
