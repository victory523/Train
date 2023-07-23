import { Component, OnInit } from '@angular/core';
import { HttpRequestState } from '../types';
import { initialHttpRequestState, subscribeToRequestState } from '../utils';
import { WeightService } from '../weight.service';
import { NotificationService } from '../common-components/notification.service';

@Component({
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent implements OnInit {
  constructor(private weightService: WeightService, private notificationService: NotificationService) {}

  weightState: HttpRequestState<number | undefined> = initialHttpRequestState;

  ngOnInit(): void {
    subscribeToRequestState(this.weightService.getWeight(), newState => {
      this.weightState = newState

      if (newState.hasFailed) {
        this.notificationService.showNotification('Unable to fetch weight', 'error')
      }
    })
  }
}
