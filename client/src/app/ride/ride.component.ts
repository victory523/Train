import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgxEchartsModule } from 'ngx-echarts';
import { combineLatest, map } from 'rxjs';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { LoaderComponent } from '../common-components/loader/loader.component';
import { TextComponent } from '../common-components/text/text.component';
import { RideService } from './ride.service';
import { MeasurementWithUnitPipe } from '../utils/measurement-with-unit.pipe';
import { PercentageDiffColorPipe } from '../utils/percentage-diff-color.pipe';
import { PercentageDiffPipe } from '../utils/percentage-diff.pipe';
import { ActivatedRoute } from '@angular/router';

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
  constructor(
    private readonly rideService: RideService,
    route: ActivatedRoute
  ) {
    route.data.subscribe((data) => rideService.selectPeriod(data['period']));
  }

  $vm = combineLatest([
    this.rideService.$todayRideStats,
    this.rideService.$periodRideStats,
  ]).pipe(
    map(([todayRideStats, periodRideStats]) => ({
      todayRideStats,
      periodRideStats,
    }))
  );
}
