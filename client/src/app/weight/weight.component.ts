import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EChartsOption } from 'echarts';
import { NgxEchartsDirective, NgxEchartsModule } from 'ngx-echarts';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { LoaderComponent } from '../common-components/loader/loader.component';
import { NotificationService } from '../common-components/notification.service';
import { WeightMeasurement, WeightService } from '../services/weight.service';
import {
  HttpRequestState,
  initialHttpRequestState,
  requestState,
} from '../utils/request-state';
import { TextComponent } from '../common-components/text/text.component';
import { PercentageDiffPipe } from '../utils/percentage-diff.pipe';
import { PercentageDiffColorPipe } from '../utils/percentage-diff-color.pipe';
import { MeasurementWithUnitPipe } from '../utils/measurement-with-unit.pipe';

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
    MeasurementWithUnitPipe
  ],
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent implements OnInit {
  initOpts: NgxEchartsDirective['initOpts'] = {
    renderer: 'svg',
  };

  constructor(
    private weightService: WeightService,
    private notificationService: NotificationService,
    private activatedRoute: ActivatedRoute
  ) {}

  weightState: HttpRequestState<WeightMeasurement[]> = initialHttpRequestState;

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) =>
      requestState(this.weightService.getWeight(data['period']), (newState) => {
        this.weightState = newState;

        if (newState.hasFailed) {
          this.notificationService.showNotification(
            'Unable to fetch weight',
            'error'
          );
        }
      })
    );
  }

  get chartOptions(): EChartsOption | null {
    if (!this.weightState.isReady) {
      return null;
    }

    return {
      aria: {
        enabled: true,
      },
      animation: false,
      grid: {
        top: 10,
        right: 10,
        bottom: 10,
        left: 10,
      },
      dataset: {
        source: [
          ['date', 'weight'],
          ...this.weightState.value.map(({ date, weight }) => [
            new Date(date),
            weight,
          ]),
        ],
      },
      xAxis: {
        type: 'time',
        show: false,
      },
      yAxis: {
        max: 'dataMax',
        min: 'dataMin',
        show: false,
      },
      series: [
        {
          type: 'line',
          smooth: true,
          showSymbol: false,
        },
      ],
    };
  }

  get todayWeight(): WeightMeasurement | undefined {
    if (!this.weightState.isReady) {
      return undefined;
    }

    return this.weightService.getTodayWeight(this.weightState.value);
  }

  get diff(): WeightMeasurement | undefined {
    if (!this.weightState.isReady) {
      return undefined;
    }

    return this.weightService.getDiff(this.weightState.value);
  }

}
