import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgxEchartsModule } from 'ngx-echarts';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { LoaderComponent } from '../common-components/loader/loader.component';
import { NotificationService } from '../common-components/notification.service';
import { TextComponent } from '../common-components/text/text.component';
import { RideService, RideStats } from '../services/ride.service';
import { MeasurementWithUnitPipe } from '../utils/measurement-with-unit.pipe';
import { PercentageDiffColorPipe } from '../utils/percentage-diff-color.pipe';
import { PercentageDiffPipe } from '../utils/percentage-diff.pipe';
import {
  HttpRequestState,
  initialHttpRequestState,
  requestState,
} from '../utils/request-state';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    HeadingComponent,
    LoaderComponent,
    NgxEchartsModule,
    TextComponent,
    PercentageDiffPipe,
    PercentageDiffColorPipe,
    MeasurementWithUnitPipe,
  ],
  selector: 'app-ride',
  templateUrl: './ride.component.html',
  styleUrls: ['./ride.component.css'],
})
export class RideComponent {
  @Input() set period(value: number | undefined) {
    requestState(this.rideService.getRideStats(value), (newState) => {
      this.periodRideStatsState = newState;

      if (newState.hasFailed) {
        this.notificationService.showNotification(
          'Unable to fetch ride stats',
          'error'
        );
      }
    });
  }

  constructor(
    private rideService: RideService,
    private notificationService: NotificationService
  ) {
    requestState(this.rideService.getRideStats(1), (newState) => {
      this.todayRideStatsState = newState;

      if (newState.hasFailed) {
        this.notificationService.showNotification(
          'Unable to fetch ride stats',
          'error'
        );
      }
    });
  }

  periodRideStatsState: HttpRequestState<RideStats> = initialHttpRequestState;
  todayRideStatsState: HttpRequestState<RideStats> = initialHttpRequestState;
}
