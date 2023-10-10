import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgxEchartsModule } from 'ngx-echarts';
import { Observable, combineLatest, map, switchMap } from 'rxjs';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { LoaderComponent } from '../common-components/loader/loader.component';
import { TextComponent } from '../common-components/text/text.component';
import { RideService, RideStats } from './ride.service';
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
  private readonly $periodRideStats = this.route.data.pipe(
    switchMap((data) => this.rideService.getRideStats(data['period']))
  );
  private readonly $todayRideStats = this.rideService.getRideStats(1);

  constructor(
    private readonly rideService: RideService,
    private readonly route: ActivatedRoute
  ) {}

  $vm = combineLatest([this.$todayRideStats, this.$periodRideStats]).pipe(
    map(([todayRideStats, periodRideStats]) => ({
      todayRideStats,
      periodRideStats,
    }))
  );
}
