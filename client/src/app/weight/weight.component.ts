import { Component, OnInit } from '@angular/core';
import { EChartsOption } from 'echarts';
import { NotificationService } from '../common-components/notification.service';
import {
  HttpRequestState,
  initialHttpRequestState,
  requestState,
} from '../utils/request-state';
import { NgxEchartsDirective, NgxEchartsModule } from 'ngx-echarts';
import { WeightMeasurement, WeightService } from '../services/weight.service';
import { NgIf } from '@angular/common';
import { HeadingComponent } from '../common-components/heading/heading.component';
import { LoaderComponent } from '../common-components/loader/loader.component';

type Period = {
  label: string;
  value?: number;
};

@Component({
  standalone: true,
  imports: [NgIf, NgxEchartsModule, HeadingComponent, LoaderComponent],
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent implements OnInit {
  initOpts: NgxEchartsDirective['initOpts'] = {
    renderer: 'svg',
  };
  periods: Period[] = [
    { label: 'Week', value: 7 },
    { label: 'Month', value: 30 },
    { label: 'Year', value: 365 },
    { label: 'All time' },
  ];
  selectedPeriod = this.periods[0];

  constructor(
    private weightService: WeightService,
    private notificationService: NotificationService
  ) {}

  weightState: HttpRequestState<WeightMeasurement[]> = initialHttpRequestState;

  ngOnInit(): void {
    this.fetchPeriod();
  }

  fetchPeriod() {
    requestState(this.weightService.getWeight(this.selectedPeriod.value), (newState) => {
      this.weightState = newState;

      if (newState.hasFailed) {
        this.notificationService.showNotification(
          'Unable to fetch weight',
          'error'
        );
      }
    });
  }

  selectPeriod(newPeriod: Period) {
    this.selectedPeriod = newPeriod;
    this.fetchPeriod();
  }

  get chartOptions(): EChartsOption | null {
    if (!this.weightState.isReady) {
      return null;
    }

    return {
      aria: {
        enabled: true
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
        show: false
      },
      yAxis: {
        max: 'dataMax',
        min: 'dataMin',
        show: false
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

  get todayWeight(): number | undefined {
    if (!this.weightState.isReady) {
      return undefined
    }

    return this.weightService.getTodayWeight(this.weightState.value);
  }
}
