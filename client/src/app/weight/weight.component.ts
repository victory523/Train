import { Component, OnInit } from '@angular/core';
import { EChartsOption } from 'echarts';
import { NotificationService } from '../common-components/notification.service';
import {
  HttpRequestState,
  initialHttpRequestState,
  requestState,
} from '../utils/request-state';
import { WeightMeasurement, WeightService } from '../weight.service';
import { NgxEchartsDirective } from 'ngx-echarts';

@Component({
  selector: 'app-weight',
  templateUrl: './weight.component.html',
  styleUrls: ['./weight.component.css'],
})
export class WeightComponent implements OnInit {
  chartOptions: EChartsOption | null = null;
  initOpts: NgxEchartsDirective['initOpts'] = {
    renderer: 'svg',
  }

  constructor(
    private weightService: WeightService,
    private notificationService: NotificationService
  ) {}

  weightState: HttpRequestState<WeightMeasurement[]> = initialHttpRequestState;

  ngOnInit(): void {
    requestState(this.weightService.getWeight(), (newState) => {
      this.weightState = newState;

      if (newState.isReady) {
        this.chartOptions = {
          animation: false,
          dataset: {
            source: [
              ['date', 'weight'],
              ...newState.value.map(({ date, weight }) => [date, weight]),
            ],
          },
          xAxis: {
            type: 'time',
          },
          yAxis: {
            max: 'dataMax',
            min: 'dataMin',
          },
          series: [
            {
              type: 'line',
              smooth: true,
            },
          ],
        };
      }

      if (newState.hasFailed) {
        this.notificationService.showNotification(
          'Unable to fetch weight',
          'error'
        );
      }
    });
  }
}
